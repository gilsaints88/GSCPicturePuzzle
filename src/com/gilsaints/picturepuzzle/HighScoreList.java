package com.gilsaints.picturepuzzle;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class HighScoreList {
	private String highTimes;
	private String highNames;
	private String highMoves;
	private int highScoreIndex;

	public void setHighScoreIndex(int highScoreIndex) {
		this.highScoreIndex = highScoreIndex;
	}

	public HighScoreList(String highTimes, String highNames, String highMoves) {
		this.highTimes = highTimes;
		this.highNames = highNames;
		this.highMoves = highMoves;
	}
	
	public String getHighTimes() {
		return highTimes;
	}

	public void setHighTimes(String highTimes) {
		this.highTimes = highTimes;
	}

	public String getHighNames() {
		return highNames;
	}

	public void setHighNames(String highNames) {
		this.highNames = highNames;
	}

	public String getHighMoves() {
		return highMoves;
	}

	public void setHighMoves(String highMoves) {
		this.highMoves = highMoves;
	}

	public void renameHighScorer(String newName) {
		StringTokenizer parseTokenizer = new StringTokenizer(
				highNames, ",");
		int tokenIndex = 1;
		String renamedNamesList = new String();
		while (parseTokenizer.hasMoreElements()) {
			if (tokenIndex == highScoreIndex) {
//				if (highScoreIndex > 1) {
					parseTokenizer.nextToken();
					String nextNameAfterReplace = new String();
					try {
						nextNameAfterReplace = parseTokenizer.nextToken();
					}
					catch(NoSuchElementException e) {
					}
					renamedNamesList = renamedNamesList + "," + newName
							+ "," + nextNameAfterReplace;
//				} else {
//					parseTokenizer.nextToken();
//					String nextNameAfterReplace = parseTokenizer.nextToken();
//					renamedNamesList = renamedNamesList + ","
//							+ nextNameAfterReplace;
//				}
			}
			else {
				renamedNamesList = renamedNamesList + ","
				+ parseTokenizer.nextToken();
			}
			tokenIndex++;
		}
		this.highNames = renamedNamesList;
	}
}
