package com.kevinmessier;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

/**
 * This program retrieves a list of keywords from a CSV file and searches for
 * the in a specified text file.
 * 
 * @author Kevin Messier
 *
 */
public class StringMatcher {

	private static final Logger logger = LogManager.getLogger("StringMatcher");

	@Option(name = "-i", aliases = "--input-file", usage = "Path to the input file containing the text to be searched", required = true)
	private String inputFilePath;

	@Option(name = "-k", aliases = "--keywords-file", usage = "Path to a CSV file containing the keywords to be matched", required = true)
	private String keywordsFilePath;

	@Option(name = "-l", aliases = "--lines-per-chunk", usage = "Optional - Number of lines processed per thread")
	private int chunkSize = 1000;

	@Option(name = "-c", aliases = "--case-sensitive", usage = "Optional - Perform case sensitive search")
	private boolean caseSensitveSearch = false;

	@Option(name = "-p", aliases = "--partial-matches", usage = "Optional - Match partial words (default is whole words)")
	private boolean matchPartialWords = false;

	private String[] keywords;

	/**
	 * Main method
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		logger.debug("======================================");
		logger.debug("========== START OF NEW RUN ==========");
		logger.debug("======================================");

		try {
			new StringMatcher().doMain(args);
		} catch (CsvValidationException e) {
			logger.error("Keywords file does not contain valid CVS", e);
		} catch (IOException e) {
			logger.error("The program encountered an IOException", e);
		} catch (InterruptedException e) {
			logger.error("A thread was interrupted", e);
		} catch (ExecutionException e) {
			logger.error("Error attempting to retrieve the result of a task that aborted by throwing an exception", e);
		}
	}

	/**
	 * Parses arguments and keywords file. Reads input file in chunks and starts
	 * threads to perform matching. Call aggregator and prints results.
	 * 
	 * @param args
	 * @throws CsvValidationException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private void doMain(String[] args)
			throws CsvValidationException, IOException, InterruptedException, ExecutionException {
		// Parse arguments
		CmdLineParser parser = new CmdLineParser(this);
		try {
			parser.parseArgument(args);

		} catch (CmdLineException e) {
			logger.error(e.getMessage());
			parser.printUsage(System.err);
			return;
		}

		// Log the matching options that are being used
		if (caseSensitveSearch)
			logger.debug("Matches will be case sensitive");
		else
			logger.debug("Matches will be case insensitive");

		if (matchPartialWords)
			logger.debug("Matching partial words");
		else
			logger.debug("Matching whole words only");

		// Log chunk size
		logger.debug("Lines per chunk: " + chunkSize);

		// Parse keywords file
		keywords = getKeywordsFromCSVFile(keywordsFilePath);
		logger.debug("Keywords: " + Arrays.toString(keywords));

		// Count the number of chunks
		int chunkCounter = 0;
		// Count the number of characters in each chunk
		int charCounter = 0;
		// Count the number of lines so we know when to create a new chunk
		int lineCounter = 0;

		// Store the number of characters in each chunk
		HashMap<Integer, Integer> charsPerChunk = new HashMap<Integer, Integer>();

		// Store the matches from all chunks
		List<FutureTask<List<Match>>> results = new ArrayList<FutureTask<List<Match>>>();

		logger.debug("Parsing input file: " + inputFilePath);

		// Read the input file
		FileInputStream inputStream = new FileInputStream(new File(inputFilePath));
		Scanner sc = new Scanner(inputStream, "UTF-8");

		// Holds the lines for the current chunk
		List<String> chunk = new ArrayList<String>();

		while (sc.hasNextLine()) {
			lineCounter++;
			String line = sc.nextLine();
			charCounter = charCounter + line.length();
			chunk.add(line);

			// When we hit chunkSize lines, start a new thread to look for matches
			if (lineCounter == chunkSize || !sc.hasNext()) {

				// Create new Matcher
				Matcher matcher = new Matcher(keywords, caseSensitveSearch, matchPartialWords, chunk, chunkCounter);

				// Start new thread
				FutureTask<List<Match>> ft = new FutureTask<List<Match>>(matcher);
				results.add(ft);
				Thread t = new Thread(ft);
				t.start();

				// Increment chunk counter and store the number of characters in the current
				// chunk
				chunkCounter++;
				charsPerChunk.put(chunkCounter, charCounter);

				// Clear counters for next chunk
				charCounter = 0;
				lineCounter = 0;

				// Reinitialize chunk ArrayList for next chunk
				chunk = new ArrayList<>();
			}
		}

		// Close Scanner and FileInputStream
		sc.close();
		inputStream.close();
		
		// Scanner suppresses exceptions
		if (sc.ioException() != null) {
			throw sc.ioException();
		}

		// Put the matches from all the threads into an ArrayList
		List<List<Match>> allMatches = new ArrayList<List<Match>>();
		for (FutureTask<List<Match>> f : results) {
			allMatches.add(f.get());
		}

		// Send all matches to the aggregator
		Aggregator a = new Aggregator();
		HashMap<String, List<Match>> aggreagatedMatches = a.aggregateMatches(allMatches);

		// Print out aggregated matches
		for (String keyword : aggreagatedMatches.keySet()) {
			StringBuilder offsetListString = new StringBuilder();
			offsetListString.append("[");
			String comma = "";

			// Build the output string
			for (Match m : aggreagatedMatches.get(keyword)) {
				// Calculate absolute line offset
				int lineOffset = (m.getChunkNum() * chunkSize) + m.getLineOffset();

				// Calculate absolute character offset
				int charsInPreviousChunks = 0;

				// Add all matches to output string
				for (int i = 1; i < m.getChunkNum(); i++) { // Chunk numbers in charsPerChunk start with 1
					charsInPreviousChunks = charsInPreviousChunks + charsPerChunk.get(i);
				}

				// Add character offset to the number of characters in the previous chunks to
				// get the absolute offset
				int charOffset = charsInPreviousChunks + m.getCharOffset();

				offsetListString.append(comma + "[lineOffset=" + lineOffset + ", charOffset=" + charOffset + "]");
				comma = ",";
			}

			offsetListString.append("]");

			// Capitalize first letter of keywords if we're doing a case sensitive search
			if (caseSensitveSearch) {
				keyword = keyword.substring(0, 1).toUpperCase() + keyword.substring(1);
			}

			logger.info(keyword + " --> " + offsetListString);
		}
	}

	/**
	 * Parses CSV file for keywords
	 * 
	 * @param filePath The CVS file containing the keywords
	 * @return An array of keywords
	 * @throws CsvValidationException
	 * @throws IOException
	 */
	private String[] getKeywordsFromCSVFile(String filePath) throws CsvValidationException, IOException {
		logger.debug("Parsing keywords file: " + filePath);
		List<String> allValues = new ArrayList<String>();

		CSVReader csvReader = new CSVReader(new FileReader(filePath));
		String[] values = null;
		while ((values = csvReader.readNext()) != null) {
			for (String value : values) {
				allValues.add(value);
			}
		}

		String[] allValuesArray = new String[allValues.size()];
		return allValues.toArray(allValuesArray);
	}
}