package com.kevinmessier;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class AggregatorTest {
	private static final Logger logger = LogManager.getLogger("AggregatorTest");

	@Test
	public void testAggregateMatches() {
		logger.debug("Running testAggregateMatches()");
		// Create Matches to aggregate
		List<List<Match>> allMatches = new ArrayList<List<Match>>();
		List<Match> matchList1 = new ArrayList<Match>();
		matchList1.add(new Match("one", 0, 2, 10));
		matchList1.add(new Match("two", 1, 4, 20));
		matchList1.add(new Match("three", 2, 6, 30));
		List<Match> matchList2 = new ArrayList<Match>();
		matchList2.add(new Match("two", 1, 3, 11));
		matchList2.add(new Match("four", 3, 5, 21));
		matchList2.add(new Match("six", 5, 7, 31));
		List<Match> matchList3 = new ArrayList<Match>();
		matchList3.add(new Match("one", 2, 7, 12));
		matchList3.add(new Match("two", 4, 8, 22));
		matchList3.add(new Match("four", 6, 9, 32));
		allMatches.add(matchList1);
		allMatches.add(matchList2);
		allMatches.add(matchList3);
		
		// Expected aggregated values
		HashMap<String, String> expectedValues = new HashMap<String, String>();
		expectedValues.put("one", "[[lineOffset=2, charOffset=10],[lineOffset=7, charOffset=12]]");
		expectedValues.put("two", "[[lineOffset=4, charOffset=20],[lineOffset=3, charOffset=11],[lineOffset=8, charOffset=22]]");
		expectedValues.put("three", "[[lineOffset=6, charOffset=30]]");
		expectedValues.put("four", "[[lineOffset=5, charOffset=21],[lineOffset=9, charOffset=32]]");
		expectedValues.put("six", "[[lineOffset=7, charOffset=31]]");
		
		// Create new Aggregator and aggregate Matches
		Aggregator a = new Aggregator();
		HashMap<String, List<Match>> aggrgatedMatches = a.aggregateMatches(allMatches);
		
		// Compare actual values to expected values
		for(String keyword : aggrgatedMatches.keySet()) {
			StringBuilder offsetListString = new StringBuilder();
			offsetListString.append("[");
			String comma = "";
			for(Match m : aggrgatedMatches.get(keyword)) {
				offsetListString.append(comma + "[lineOffset=" + m.getLineOffset() + ", charOffset=" + m.getCharOffset() + "]");
				comma = ",";
			}
			offsetListString.append("]");
			
			assertEquals(offsetListString.toString(), expectedValues.get(keyword));
		}
		
		assertTrue(true);
	}

}
