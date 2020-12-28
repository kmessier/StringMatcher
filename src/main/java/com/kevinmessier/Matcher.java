package com.kevinmessier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;
import org.ahocorasick.trie.Trie.TrieBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Search for a list of keywords in text
 * 
 * @author Kevin Messier
 *
 */
public class Matcher implements Callable<List<Match>> {
	private static final Logger logger = LogManager.getLogger("Matcher");
	private Trie trie;
	private List<String> lines;
	private int chunkNumber;

	/**
	 * Creates a new Matcher object
	 * 
	 * @param keywords            Array of keywords
	 * @param caseSensitiveSearch
	 * @param matchPartialWords
	 * @param lines               Text lines to search through
	 * @param chunkNumber         The number of the chunk containing the lines
	 */
	public Matcher(String[] keywords, boolean caseSensitiveSearch, boolean matchPartialWords, List<String> lines,
			int chunkNumber) {
		TrieBuilder tBuilder = Trie.builder();

		if (!caseSensitiveSearch)
			tBuilder.ignoreCase();

		if (!matchPartialWords)
			tBuilder.onlyWholeWords();

		for (String keyword : keywords) {
			tBuilder.addKeyword(keyword);
		}

		this.trie = tBuilder.build();

		this.lines = lines;
		this.chunkNumber = chunkNumber;
	}

	/**
	 * Called when the thread is started
	 */
	@Override
	public List<Match> call() throws Exception {
		return getMatches();
	}

	/**
	 * Searches for matches
	 * 
	 * @return A list of Match objects
	 */
	public List<Match> getMatches() {
		logger.debug("Searching for matches in chunk " + chunkNumber);

		// Holds all matches for current chunk
		List<Match> matches = new ArrayList<Match>();

		// Holds the running count of the characters in the chunk
		int characterCount = 0;

		// Search for matches in each line
		for (int i = 0; i < lines.size(); i++) {
			Collection<Emit> emits = this.trie.parseText(lines.get(i));

			for (Emit emit : emits) {
				String keyword = emit.getKeyword();
				int lineNum = i + 1; // line number should start at 1
				int matchStart = emit.getStart() + 1; // character numbers should start at 1
				int charOffset = characterCount + matchStart;
				Match m = new Match(keyword, chunkNumber, lineNum, charOffset);
				matches.add(m);
			}

			characterCount = characterCount + lines.get(i).length();
		}

		logger.debug("Found " + matches.size() + " matches in chunk " + chunkNumber);
		return matches;
	}
}
