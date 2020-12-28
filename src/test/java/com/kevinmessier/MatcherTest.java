package com.kevinmessier;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class MatcherTest {
	private static final Logger logger = LogManager.getLogger("MatcherTest");

	@Test
	public void testGetMatches() {
		logger.debug("Running testGetMatches()");
		// Setup parametres
		String[] keywords = new String[] { "three", "one", "four" };
		boolean caseSensitive = false;
		boolean partialMatches = false;
		int chunkNum = 0;
		List<String> lines = new ArrayList<String>();
		lines.add("one two three four five six seven eight nine ten");
		lines.add("The quick brown fox jumps over the lazy dog");
		lines.add("one three five seven nine");
		
		// Expected Match values
		String[] expectedValues = new String[5];
		expectedValues[0] = "one | 1 | 1 | 0";
		expectedValues[1] = "three | 1 | 9 | 0";
		expectedValues[2] = "four | 1 | 15 | 0";
		expectedValues[3] = "one | 3 | 92 | 0";
		expectedValues[4] = "three | 3 | 96 | 0";

		// Create new Matcher and get matches
		Matcher m = new Matcher(keywords, caseSensitive, partialMatches, lines, chunkNum);
		List<Match> matches = m.getMatches();

		// Compare actual values to expected values
		for (int i = 0; i < matches.size(); i++) {
			String matchValues = matches.get(i).getKeyword() + " | " + matches.get(i).getLineOffset() + " | "
					+ matches.get(i).getCharOffset() + " | " + matches.get(i).getChunkNum();

			assertEquals(matchValues, expectedValues[i]);
		}
	}

}
