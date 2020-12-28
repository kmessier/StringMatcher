package com.kevinmessier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Aggregates matches based on keyword
 * 
 * @author Kevin Messier
 *
 */
public class Aggregator {
	private static final Logger logger = LogManager.getLogger("Aggregator");

	/**
	 * Aggregates matches based on keyword
	 * 
	 * @param allMatches a List<List<Match>> containing all matches
	 * @return A map of aggregated matches
	 */
	public HashMap<String, List<Match>> aggregateMatches(List<List<Match>> allMatches) {
		logger.debug("Aggregating matches");

		// Holds the aggregated matches
		HashMap<String, List<Match>> aggregatedMatches = new HashMap<>();

		// Loop through all the matches and aggregate based on keyword
		for (List<Match> matchList : allMatches) {
			for (Match m : matchList) {
				String keyword = m.getKeyword();
				List<Match> newMatchList;

				// If the map already contains the keyword, add the match to the list.
				// Otherwise, create a new list to store matches.
				if (aggregatedMatches.containsKey(keyword)) {
					newMatchList = aggregatedMatches.get(keyword);
				} else {
					newMatchList = new ArrayList<Match>();
				}
				newMatchList.add(m);
				aggregatedMatches.put(keyword, newMatchList);
			}
		}

		return aggregatedMatches;
	}
}
