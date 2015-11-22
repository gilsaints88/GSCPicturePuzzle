package com.gilsaints.picturepuzzle;

import com.gilsaints.picturepuzzle.R;

import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class HighScoreMain extends TabActivity {
	private final String HIGH_TIMES_3x3 = "highTimes3x3";
	private final String HIGH_NAMES_3x3 = "highNames3x3";
	private final String HIGH_MOVES_3x3 = "highMoves3x3";

	private final String HIGH_TIMES_4x4 = "highTimes4x4";
	private final String HIGH_NAMES_4x4 = "highNames4x4";
	private final String HIGH_MOVES_4x4 = "highMoves4x4";

	private final String HIGH_TIMES_5x5 = "highTimes5x5";
	private final String HIGH_NAMES_5x5 = "highNames5x5";
	private final String HIGH_MOVES_5x5 = "highMoves5x5";

	private final String HIGH_TIMES_6x6 = "highTimes6x6";
	private final String HIGH_NAMES_6x6 = "highNames6x6";
	private final String HIGH_MOVES_6x6 = "highMoves6x6";

	private final String HIGH_SCORE_SETTINGS = "HighScores";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.high_score);

		/* TabHost will have Tabs */
		TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);

		/*
		 * TabSpec used to create a new tab. By using TabSpec only we can able
		 * to setContent to the tab. By using TabSpec setIndicator() we can set
		 * name to tab.
		 */

		/* tid1 is firstTabSpec Id. Its used to access outside. */
		SharedPreferences settings = getSharedPreferences(HIGH_SCORE_SETTINGS,
				0);
		String highTimes3 = settings.getString(HIGH_TIMES_3x3, "");
		String highNames3 = settings.getString(HIGH_NAMES_3x3, "");
		String highMoves3 = settings.getString(HIGH_MOVES_3x3, "");

		String highTimes4 = settings.getString(HIGH_TIMES_4x4, "");
		String highNames4 = settings.getString(HIGH_NAMES_4x4, "");
		String highMoves4 = settings.getString(HIGH_MOVES_4x4, "");
		
		String highTimes5 = settings.getString(HIGH_TIMES_5x5, "");
		String highNames5 = settings.getString(HIGH_NAMES_5x5, "");
		String highMoves5 = settings.getString(HIGH_MOVES_5x5, "");
		
		String highTimes6 = settings.getString(HIGH_TIMES_6x6, "");
		String highNames6 = settings.getString(HIGH_NAMES_6x6, "");
		String highMoves6 = settings.getString(HIGH_MOVES_6x6, "");

		TabSpec firstTabSpec = tabHost.newTabSpec("tid1");
		TabSpec secondTabSpec = tabHost.newTabSpec("tid1");
		TabSpec thirdTabSpec = tabHost.newTabSpec("tid1");
		TabSpec fourthTabSpec = tabHost.newTabSpec("tid1");

		/* TabSpec setIndicator() is used to set name for the tab. */
		/* TabSpec setContent() is used to set content for a particular tab. */
		Intent firstIntent = new Intent(this, HighScore3x3.class);
		firstIntent.putExtra(HIGH_TIMES_3x3, highTimes3);
		firstIntent.putExtra(HIGH_NAMES_3x3, highNames3);
		firstIntent.putExtra(HIGH_MOVES_3x3, highMoves3);

		firstTabSpec.setIndicator("", getResources().getDrawable(R.drawable.tab_3x3))
				.setContent(firstIntent);
//////////////////////
		Intent secondIntent = new Intent(this, HighScore4x4.class);
		secondIntent.putExtra(HIGH_TIMES_4x4, highTimes4);
		secondIntent.putExtra(HIGH_NAMES_4x4, highNames4);
		secondIntent.putExtra(HIGH_MOVES_4x4, highMoves4);

		secondTabSpec.setIndicator("", getResources().getDrawable(R.drawable.tab_4x4))
				.setContent(secondIntent);
/////////////////////
		Intent thirdIntent = new Intent(this, HighScore5x5.class);
		thirdIntent.putExtra(HIGH_TIMES_5x5, highTimes5);
		thirdIntent.putExtra(HIGH_NAMES_5x5, highNames5);
		thirdIntent.putExtra(HIGH_MOVES_5x5, highMoves5);

		thirdTabSpec.setIndicator("", getResources().getDrawable(R.drawable.tab_5x5))
				.setContent(thirdIntent);
/////////////////////
		Intent fourthIntent = new Intent(this, HighScore6x6.class);
		fourthIntent.putExtra(HIGH_TIMES_6x6, highTimes6);
		fourthIntent.putExtra(HIGH_NAMES_6x6, highNames6);
		fourthIntent.putExtra(HIGH_MOVES_6x6, highMoves6);

		fourthTabSpec.setIndicator("", getResources().getDrawable(R.drawable.tab_6x6))
				.setContent(fourthIntent);
		
//////////////////////
		/* Add tabSpec to the TabHost to display. */
		tabHost.addTab(firstTabSpec);
		tabHost.addTab(secondTabSpec);
		tabHost.addTab(thirdTabSpec);
		tabHost.addTab(fourthTabSpec);

	}
}
