package com.kevinmessier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class to hold information about matches
 * 
 * @author Kevin Messier
 *
 */
public class Match {
	private static final Logger logger = LogManager.getLogger("Match");
	private String keyword;
	private int chunkNum;
	private int lineOffset;
	private int charOffset;

	/**
	 * Creates a new Match Object
	 * 
	 * @param keyword    The keyword of the match
	 * @param chunkNum   The number of the chunk that contains this match
	 * @param lineOffset The line offset relative to the chunk
	 * @param charOffset The character offset relative to the chunk
	 */
	public Match(String keyword, int chunkNum, int lineOffset, int charOffset) {
		this.keyword = keyword;
		this.chunkNum = chunkNum;
		this.lineOffset = lineOffset;
		this.charOffset = charOffset;
	}

	/**
	 * Get the number of the chunk that contains the match
	 * 
	 * @return The chunk number
	 */
	public int getChunkNum() {
		return chunkNum;
	}

	/**
	 * Set the number of the chunk that contains the match
	 * 
	 * @param chunkNum The chunk number
	 */
	public void setChunkNum(int chunkNum) {
		this.chunkNum = chunkNum;
	}

	/**
	 * Get the match keyword
	 * 
	 * @return The match keyword
	 */
	public String getKeyword() {
		return keyword;
	}

	/**
	 * Set the match keyword
	 * 
	 * @param keyword The match keyword
	 */
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	/**
	 * Gets the line offset
	 * 
	 * @return The line offset relative to the chunk
	 */
	public int getLineOffset() {
		return lineOffset;
	}

	/**
	 * Sets the line offset
	 * 
	 * @param lineOffset The line offset relative to the chunk
	 */
	public void setLineOffset(int lineOffset) {
		this.lineOffset = lineOffset;
	}

	/**
	 * Gets the character offset
	 * 
	 * @return The character offset relative to the chunk
	 */
	public int getCharOffset() {
		return charOffset;
	}

	/**
	 * Sets the character offset
	 * 
	 * @param charOffset The character offset relative to the chunk
	 */
	public void setCharOffset(int charOffset) {
		this.charOffset = charOffset;
	}
}
