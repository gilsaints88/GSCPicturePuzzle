package com.gilsaints.picturepuzzle;

import java.util.StringTokenizer;

import com.gilsaints.picturepuzzle.R;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class HighScore5x5 extends Activity {
	private final String HIGH_TIMES_5x5 = "highTimes5x5";
	private final String HIGH_NAMES_5x5 = "highNames5x5";
	private final String HIGH_MOVES_5x5 = "highMoves5x5";
	
	private TableLayout tableLayout;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.tabs);
		
		String highTimes = getIntent().getStringExtra(HIGH_TIMES_5x5);
		String highNames = getIntent().getStringExtra(HIGH_NAMES_5x5);
		String highMoves = getIntent().getStringExtra(HIGH_MOVES_5x5);
		
		LayoutInflater inflater = (LayoutInflater)   getLayoutInflater();
		tableLayout = (TableLayout) inflater.inflate(R.layout.tabs, null);
		
		if(highTimes != null & highTimes.length() > 0) {
			StringTokenizer timeTokenizer = new StringTokenizer(highTimes, ",");
			StringTokenizer nameTokenizer = new StringTokenizer(highNames, ",");
			StringTokenizer moveTokenizer = new StringTokenizer(highMoves, ",");
			
			while(timeTokenizer.hasMoreElements()) {
				String time = timeTokenizer.nextToken();
				String name = nameTokenizer.nextToken();
				String move = moveTokenizer.nextToken();
				
				TextView txtTime = new TextView(this);
				txtTime.setGravity(Gravity.CENTER);
				txtTime.setTextColor(Color.BLACK);
				txtTime.setText(time);
				
				TextView txtName = new TextView(this);
				txtName.setGravity(Gravity.LEFT);
				txtName.setTextColor(Color.BLACK);
				txtName.setText(name);
				
				TextView txtMove = new TextView(this);
				txtMove.setGravity(Gravity.RIGHT);
				txtMove.setTextColor(Color.BLACK);
				txtMove.setText(move);
				
				TableRow row = new TableRow(this);
				
				row.addView(txtName);
				row.addView(txtTime);
				row.addView(txtMove);
				
				tableLayout.addView(row, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			}
		}
		
		
		setContentView(tableLayout);
	}
}
