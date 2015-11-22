package com.gilsaints.picturepuzzle;

import java.util.StringTokenizer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class HighScoreManager {
	
	private HighScoreList highScoreList;
	private boolean isANewHighScore = false;
	private String highScorerName;
	
	private String highScorerTime;
	private String highScorerMove;
	private int highScorerIndex;
	
	public HighScoreList getHighScoreList() {
		return highScoreList;
	}
	
	public boolean isANewHighScore() {
		return isANewHighScore;
	}

	public String getHighScorerName() {
		return highScorerName;
	}

	public String getHighScorerTime() {
		return highScorerTime;
	}

	public String getHighScorerMove() {
		return highScorerMove;
	}

	public int getHighScorerIndex() {
		return highScorerIndex;
	}

	public HighScoreManager(String existingTimes, String existingNames, String existingMoves, String newTime, String newName, String newMove) {
		this.highScorerTime = newTime;
		this.highScorerName = newName;
		this.highScorerMove = newMove;
		
		insertHighScores(newTime, existingTimes, newName, existingNames, newMove, existingMoves);
	}

	private void insertHighScores(String newTime, String existingTimes, String newName, String existingNames, String newMoves, String existingMoves) {
		highScoreList = parseHighScore(existingTimes, newTime, existingNames, newName, existingMoves, newMoves);
	}

	private HighScoreList parseHighScore(String existingTimes, String newTime, String existingNames, String newName, String existingMoves, String newMove) {
		
		String parsedHighTimes = existingTimes;
		String parsedHighNames = existingNames;
		String parsedHighMoves = existingMoves;
		
		if (existingTimes.length() > 0) {
			StringTokenizer newTimeTokenizer = new StringTokenizer(newTime,
					":");
			int newMinutes = Integer.parseInt(newTimeTokenizer.nextToken());
			int newSeconds = Integer.parseInt(newTimeTokenizer.nextToken());
			int newPlace = -1;
			isANewHighScore = false;
//			boolean isExceedsList = false;

			StringTokenizer tokenizer = new StringTokenizer(existingTimes, ",");

			int index = 1;
			int numOfEntries = tokenizer.countTokens();
			while (tokenizer.hasMoreElements()) {
				String currentTime = tokenizer.nextToken();

				// compare the minutes
				StringTokenizer timeTokenizer = new StringTokenizer(
						currentTime, ":");
				int minutes = Integer.parseInt(timeTokenizer.nextToken());
				int seconds = Integer.parseInt(timeTokenizer.nextToken());

				if (newMinutes < minutes) {
					newPlace = index;
					isANewHighScore = true;
					break;
				} else if (newMinutes == minutes && newSeconds < seconds) {
					newPlace = index;
					isANewHighScore = true;
					break;
				} else if (newMinutes == minutes && newSeconds == seconds) {
					isANewHighScore = isLessMoves(existingMoves, newMove, index);
					if(!isANewHighScore) {
						index++;
						isANewHighScore = true;
						break;
					}
				}
				else {
					index++;
				}
			}
			
			// if newScore is not higher among current high scorers
			// check if high score list is less than 10
			if (index <= 10) {
				isANewHighScore = true;
				newPlace = index++;
			}
			// cut off the last one
			if(numOfEntries == 10 && isANewHighScore) {
				existingTimes = existingTimes.substring(0, existingTimes.lastIndexOf(","));
			}

			// construct the new highscore if any
//			int insertIndex = 1;
			if (isANewHighScore) {
				this.highScorerIndex = newPlace;
				parsedHighTimes = insertInNewPlace(existingTimes, newTime, newPlace);
				parsedHighNames = insertInNewPlace(existingNames, newName, newPlace);
				parsedHighMoves = insertInNewPlace(existingMoves, newMove, newPlace);
			}
		}
		else { // no existing high score
			parsedHighTimes = newTime + ",";
			parsedHighNames = newName + ",";
			parsedHighMoves = newMove + ",";
			isANewHighScore = true;
			this.highScorerIndex = 1;
		}

		HighScoreList highScoreList = new HighScoreList(parsedHighTimes, parsedHighNames, parsedHighMoves);
		highScoreList.setHighScoreIndex(highScorerIndex);
		return highScoreList;
		// return existingValues;
		// remove excess of 10 high scorers if any
	}
	
	private boolean isLessMoves(String existingMoves, String newMove, int indexToBeReplaced) {
		StringTokenizer tokenizer = new StringTokenizer(existingMoves, ",");

		int index = 1;
		while (tokenizer.hasMoreElements()) {
			String currentMove = tokenizer.nextToken();

			// compare the moves for the index to be replaced
			if(index == indexToBeReplaced) {
				if(Integer.parseInt(currentMove) > Integer.parseInt(newMove)) {
					return true;
				} else {
					return false;
				}
			}
			else {
				index++;
			}
		}
		return false;
	}
	
	private String insertInNewPlace(String existingValues, String newValue, int newPlace) {
		StringTokenizer parseTokenizer = new StringTokenizer(
				existingValues, ",");
		int tokenIndex = 1;
		boolean insertOk = false;
		String parsedHighTimes = new String();
		while (parseTokenizer.hasMoreElements()) {
			if (tokenIndex == newPlace) {
				if (newPlace > 1) {
					parsedHighTimes = parsedHighTimes + "," + newValue
							+ "," + parseTokenizer.nextToken();
					insertOk = true;
				} else {
					parsedHighTimes = parsedHighTimes + newValue + ","
							+ parseTokenizer.nextToken();
					insertOk = true;
				}
			} else {
				parsedHighTimes = parsedHighTimes + ","
						+ parseTokenizer.nextToken();
			}
			tokenIndex++;
		}
		
		if(!insertOk) {
			parsedHighTimes = parsedHighTimes + "," + newValue;
		}
		
		return parsedHighTimes;
	}
}
