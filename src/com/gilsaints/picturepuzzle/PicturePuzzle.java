package com.gilsaints.picturepuzzle;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextPaint;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.AbsoluteLayout.LayoutParams;
import android.widget.EditText;
import android.widget.TextView;

public class PicturePuzzle extends Activity {

	public static final Level[] LEVELS = new Level[] { new Level("Easy", 3, 3),
			new Level("Medium", 4, 4), new Level("Hard", 5, 5),
			new Level("Very Hard", 6, 6) };

	private int locations[] = new int[] { 0, 0 };

	private List<Block> blocks = null;

	private AbsoluteLayout absoluteLayout = null;
	private Block selectedBlock = null;

	// timer addition (start)
	protected static final int DEFAULTSECONDS = 60 * 12; // 12 MInutes
	/*
	 * The value of these IDs is random! they are just needed to be recognized
	 */
	protected static final int DECREASINGTIMEIDENTIFIER = 0x1337;
	protected static final int INCREASINGTIMEIDENTIFIER = 0x101;
	protected static final int PIZZA_NOTIFICATION_ID = 0x1991;

	/** is the countdown running at the moment ? */
	protected boolean running = false;

	private boolean isContinueSavedGame = false;
	private boolean savedGamePlayedAlready = false;

	/** Seconds passed so far */
	protected int mySecondsPassed = 0;
	/** Seconds to be passed totally */
	protected int mySecondsTotal = DEFAULTSECONDS;

	private int screenWidth = 0;
	private int screenHeight = 0;

	Boolean isFlipped = false;

	private Bitmap bitmap;
	private Level level;
	private PuzzleObjectsContainer puzzleContainer;
	private int gameLevel;
	private String imagePath;

	private int moves;

	private boolean isRandomPicture;
	private boolean isTryAgain = false;

	private boolean isDecreasingTime = false;
	SharedPreferences prefs;

	private final String HIGH_SCORE_SETTINGS = "HighScores";

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

	private String levelHighTimes;
	private String levelHighNames;
	private String levelHighMoves;

	private static final int YOU_LOSE_DIALOG = 1;
	private static final int HIGHSCORE_DIALOG = 2;
	private static final int YOU_WIN_DIALOG = 3;
	private static final int NO_BITMAP_DIALOG = 4;
	private static final int FIRST_TIME_DIALOG = 5;

	private static final int BLOCK_PUZZLE_TYPE = 0;
	private static final int SLIDING_PUZZLE_TYPE = 1;

	private Block emptyBlock;
	
	/*
	 * Thread that sends a message to the handler every second
	 */
	Thread myRefreshThread = null;

	Handler timerHandler = null;

	// timer addition (end)

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// blocks.clear();

		absoluteLayout = new AbsoluteLayout(this);
		mTimeLabel = new TextView(this);
		mTimeLabel.setTypeface(Typeface.DEFAULT_BOLD);

		if (getLastNonConfigurationInstance() != null) {
			puzzleContainer = (PuzzleObjectsContainer) getLastNonConfigurationInstance();
		}

		setupPuzzle();
	}

	public void setupPuzzle() {
		blocks = new ArrayList<Block>();

		absoluteLayout.removeAllViews();
		blocks.clear();

		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null) {
			if (bundle.getBoolean("continueSavedGame")
					&& !savedGamePlayedAlready) {
				imagePath = bundle.getString("imagePath");
				bitmap = BitmapFactory.decodeFile("/mnt/sdcard/gsc_saved.jpg");
				// isContinueSavedGame = true;
			} else if (!isRandomPicture) {
				imagePath = bundle.getString("imagePath");
				bitmap = BitmapFactory.decodeFile(imagePath);

				prefs = getSharedPreferences(HIGH_SCORE_SETTINGS, 0);
				editor = prefs.edit();
				editor.putString("previousImage", imagePath);
				editor.commit();
			}

			gameLevel = bundle.getInt("gameLevel");
			isDecreasingTime = bundle.getBoolean("isDecreasingTime");
			mStartTime = (long) bundle.getInt("startTime", 0);

			prefs = getSharedPreferences(HIGH_SCORE_SETTINGS, 0);
			if (bundle.getBoolean("continueSavedGame")
					&& !savedGamePlayedAlready) {
				mStartTime = prefs.getLong("saved_game_time", mStartTime);

				long millis = mStartTime;
				int seconds = (int) (millis / 1000);
				int minutes = seconds / 60;
				seconds = seconds % 60;

				mTimeLabel
						.setText(String.format("%02d:%02d", minutes, seconds));
			} else {
				mStartTime = 0L;
			}
		}

		if ((isRandomPicture || (imagePath == null || imagePath.length() <= 0) || bitmap == null)
				&& !isTryAgain) {
			String[] projection = { MediaStore.Images.Media.DATA };
			prefs = getSharedPreferences(HIGH_SCORE_SETTINGS, 0);
			editor = prefs.edit();

			try {
				Cursor cursor = managedQuery(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
						projection, // Which columns to return
						null, // Return all rows
						null, MediaStore.Images.Media.DATA);

				String previousImage = prefs.getString("previousImage",
						imagePath);
				boolean isImagePathTheSame = true;
				while (isImagePathTheSame) {
					Random rand = new Random();
					int randomIndex = rand.nextInt(cursor.getCount());
					// int columnIndex = cursor
					// .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

					cursor.moveToPosition(randomIndex);
					if (!previousImage.equals(cursor.getString(0))) {
						isImagePathTheSame = false;

						if ("/mnt/sdcard/gsc_saved.jpg".equals(cursor
								.getString(0))) {
							isImagePathTheSame = true;
						}
					}
				}

				bitmap = BitmapFactory.decodeFile(cursor.getString(0));
				editor.putString("previousImage", cursor.getString(0));

				editor.commit();
			} catch (Exception iae) {
				imagePath = bundle.getString("imagePath");
				bitmap = BitmapFactory.decodeFile(imagePath);

				editor.putString("previousImage", imagePath);

				editor.commit();
			}
		}
		if (isTryAgain) {
			imagePath = prefs.getString("previousImage",
					bundle.getString("imagePath"));
			bitmap = BitmapFactory.decodeFile(imagePath);
			isTryAgain = false;
		}

		level = LEVELS[gameLevel];
		setLevelHighs(gameLevel);

		Display display = getWindowManager().getDefaultDisplay();

		screenHeight = display.getHeight();
		screenWidth = display.getWidth();

		if (!isBitmapExists(bitmap)) {
			// PicturePuzzle.this.showDialog(YOU_LOSE_DIALOG);
			finish();
		}

		bitmap = bitmap.createScaledBitmap(bitmap, LayoutMessure.windowW,
				LayoutMessure.windowH, false);

		if (!bundle.getBoolean("continueSavedGame") || savedGamePlayedAlready) {
			putBorders(bundle);
		}

		int width = LayoutMessure.windowW / level.getColumns();
		int height = LayoutMessure.windowH / level.getRows();

		int count = 0;

		ArrayList<PicLocation> picLocations = new ArrayList<PicLocation>();
		ArrayList<PicLocation> alreadySetLocations = new ArrayList<PicLocation>();
		ArrayList<Sequence> orderOfSequenceList = new ArrayList<Sequence>();

		// this is where the parts/location of the picture is cropped and
		// then
		// added to picLocations
		if (bundle.getBoolean("continueSavedGame") && !savedGamePlayedAlready) {
			String savedSequences = prefs.getString("saved_sequence_array",
					new String());

			String delims = ",";
			String[] sequences = savedSequences.split(delims);

			int savedCount = 0;
			for (int row = 0; row < level.getRows(); row++) {

				for (int column = 0; column < level.getColumns(); column++) {
					Block block = new Block(this, column * width, row * height,
							width, height, savedCount++, bitmap,
							column * width, row * height,
							Integer.parseInt(sequences[savedCount]));
					blocks.add(block);
					
					if(prefs.getInt("gameType", 0) == SLIDING_PUZZLE_TYPE) {
						if( !(row == level.getRows()-1) || !(column == level.getColumns()-1) ) {
							blocks.add(block);
						}
						else if( (row == level.getRows()-1) && (column == level.getColumns()-1)) {
							block.setImageBitmap(null);
							blocks.add(block);
							emptyBlock = block;
						}
					}
					
					absoluteLayout.addView(block, block.getViewLayoutParams());
				}

			}
			savedGamePlayedAlready = true;
		} else {
			int index = 0;
			for (int rowRandom = 0; rowRandom < level.getRows(); rowRandom++) {
				for (int columnRandom = 0; columnRandom < level.getColumns(); columnRandom++) {
					//
					PicLocation location = new PicLocation(
							columnRandom * width, rowRandom * height);
					picLocations.add(location);

					// we can determine sequence here
					// determine proper sequence (start)
					int sequenceCol = picLocations.get(index).getLeft();
					// sequenceCol = sequenceCol / width;
					log("[gil]sequenceCol:  " + sequenceCol);

					int sequenceRow = picLocations.get(index).getTop();
					// sequenceRow = sequenceRow / height;
					log("[gil]sequenceRow:  " + sequenceRow);

					orderOfSequenceList.add(new Sequence(sequenceCol,
							sequenceRow));
					// determine proper sequence (end)
					index++;
				}
			}

			Random rand = new Random();
			for (int row = 0; row < level.getRows(); row++) {

				int randomPick = 0;
				for (int column = 0; column < level.getColumns(); column++) {
					boolean foundUnique = false;
					while (foundUnique == false) {
						// randomRow = rand.nextInt(3);
						// randomCol = rand.nextInt(3);
						randomPick = rand.nextInt(picLocations.size());

						// int sum = randomRow + randomCol;
						if (!alreadySetLocations.contains(picLocations
								.get(randomPick))) {
							foundUnique = true;
							alreadySetLocations.add(picLocations
									.get(randomPick));
						}
					}

					int seqNum = orderOfSequenceList.indexOf(new Sequence(
							picLocations.get(randomPick).getLeft(),
							picLocations.get(randomPick).getTop()));
					Block block = new Block(this, column * width, row * height,
							width, height, count++, bitmap, picLocations.get(
									randomPick).getLeft(), picLocations.get(
									randomPick).getTop(), seqNum);
					blocks.add(block);

					log("[gil]randomLeft:  "
							+ picLocations.get(randomPick).getLeft()
							+ " [gil]randomRight: "
							+ picLocations.get(randomPick).getTop());
					log("[gil]Left:  " + column * width + " [gil]Right: " + row
							* height);
					log("[gil]width:  " + width + " [gil]height: " + height);

					log("[gil]orderOfSequenceList:  " + orderOfSequenceList);
					log("[gil]blocks: " + blocks);

					
					if(prefs.getInt("gameType", 0) == SLIDING_PUZZLE_TYPE) {
						if( !(row == level.getRows()-1) || !(column == level.getColumns()-1) ) {
							blocks.add(block);
						}
						else if( (row == level.getRows()-1) && (column == level.getColumns()-1)) {
							block.setImageBitmap(null);
							blocks.add(block);
							emptyBlock = block;
						}
					}
					
					absoluteLayout.addView(block, block.getViewLayoutParams());
				}
			}
		}

		timerHandler = new Handler() {

			public void handleMessage(Message msg) {
				Bundle bundle = msg.getData();
				boolean isTimeOver = bundle.getBoolean("isTimeOver");
				if (isTimeOver) {
					isRandomPicture = true;

					PicturePuzzle.this.showDialog(YOU_LOSE_DIALOG);
				}
			}
		};

		mUpdateTimeTask = new Runnable() {
			public void run() {
				if (isDecreasingTime) {
					mStartTime = mStartTime - 100;
				} else {
					mStartTime = mStartTime + 100;
				}

				long millis = mStartTime;
				int seconds = (int) (millis / 1000);
				int minutes = seconds / 60;
				seconds = seconds % 60;

				mTimeLabel
						.setText(String.format("%02d:%02d", minutes, seconds));

				if (isDecreasingTime && millis <= 0) {
					timerHandler.removeCallbacks(mUpdateTimeTask);
					Message msg = new Message();

					Bundle data = new Bundle();
					data.putBoolean("isTimeOver", true);
					msg.setData(data);
					timerHandler.sendMessage(msg);
				}

				if ((isDecreasingTime && millis > 0) || !isDecreasingTime) {
					timerHandler.postDelayed(mUpdateTimeTask, 100);
				}

			}
		};

		timerHandler.removeCallbacks(mUpdateTimeTask, 0);
		timerHandler.postDelayed(mUpdateTimeTask, 100);

		mTimeLabel.setText("00:00");
		mTimeLabel.setTextColor(-65536);
		TextPaint textPaint = mTimeLabel.getPaint();
		textPaint.getTextSize();
		int textWidth = (int) textPaint.getTextSize() * 4;
		int textHeight = (int) (textPaint.descent() - textPaint.ascent()) * 2;

		absoluteLayout.addView(
				mTimeLabel,
				new LayoutParams(LayoutMessure.windowW - 100,
						LayoutMessure.windowH - 100, (width * level
								.getColumns()) - textWidth, (height * level
								.getColumns()) - textHeight));
		// timer (end)

		this.setContentView(absoluteLayout);
	}

	private void putBorders(Bundle bundle) {
		if (bundle != null && bundle.getBoolean("hasBorder", false)) {
			Canvas canvas = new Canvas(bitmap);

			Paint p = new Paint();
			p.setColor(Color.RED);
			p.setStrokeWidth(12f);
			canvas.drawLine(0, 0, bitmap.getWidth(), 0, p);
			canvas.drawLine(0, 0, 0, bitmap.getHeight(), p);
			canvas.drawLine(0, bitmap.getHeight(), bitmap.getWidth(),
					bitmap.getHeight(), p);
			canvas.drawLine(bitmap.getWidth(), 0, bitmap.getWidth(),
					bitmap.getHeight(), p);
		}
	}

	private boolean isBitmapExists(Bitmap bitmap) {
		if (bitmap == null) {
			// AlertDialog.Builder dlgAlert = new AlertDialog.Builder(
			// PicturePuzzle.this);
			//
			// dlgAlert.setPositiveButton("New Game",
			// new OnClickListener() {
			//
			// public void onClick(DialogInterface arg0,
			// int arg1) {
			// setupPuzzle();
			// }
			// });
			// dlgAlert.setNegativeButton("Quit", new OnClickListener() {
			//
			// public void onClick(DialogInterface arg0, int arg1) {
			// finish();
			// // System.runFinalizersOnExit(true);
			// // System.exit(0);
			// }
			// });
			// dlgAlert.setCancelable(true);
			// dlgAlert.create().show();

			// PicturePuzzle.this.showDialog(YOU_LOSE_DIALOG);
			return false;
		}
		return true;
	}

	private long mStartTime = 0L;
	private TextView mTimeLabel = null;

	private Runnable mUpdateTimeTask;

	private void setLevelHighs(int level) {
		if (level == 0) {
			this.levelHighTimes = HIGH_TIMES_3x3;
			this.levelHighNames = HIGH_NAMES_3x3;
			this.levelHighMoves = HIGH_MOVES_3x3;
		} else if (level == 1) {
			this.levelHighTimes = HIGH_TIMES_4x4;
			this.levelHighNames = HIGH_NAMES_4x4;
			this.levelHighMoves = HIGH_MOVES_4x4;
		} else if (level == 2) {
			this.levelHighTimes = HIGH_TIMES_5x5;
			this.levelHighNames = HIGH_NAMES_5x5;
			this.levelHighMoves = HIGH_MOVES_5x5;
		} else if (level == 3) {
			this.levelHighTimes = HIGH_TIMES_6x6;
			this.levelHighNames = HIGH_NAMES_6x6;
			this.levelHighMoves = HIGH_MOVES_6x6;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		absoluteLayout.getLocationInWindow(locations);

		switch (prefs.getInt("gameType", 0)) {
		case BLOCK_PUZZLE_TYPE:
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				selectedBlock = getBlock(event.getX() - locations[0],
						event.getY() - locations[1]);
				log("Selected " + blocks.indexOf(selectedBlock));

				mTimeLabel.bringToFront();
				return true;

			case MotionEvent.ACTION_MOVE:
				if (selectedBlock != null) {

					absoluteLayout.removeView(selectedBlock);
					absoluteLayout.invalidate();
					LayoutParams layoutParams = selectedBlock
							.getViewLayoutParams();
					int x = (int) (event.getX() - locations[0]);
					int y = (int) (event.getY() - locations[1]);

					layoutParams.x = x - (layoutParams.width / 2);
					layoutParams.y = y - (layoutParams.height / 2);

					absoluteLayout.addView(selectedBlock, layoutParams);
					absoluteLayout.refreshDrawableState();
					log("Moving " + blocks.indexOf(selectedBlock) + " to "
							+ layoutParams.x + "," + layoutParams.y);

					mTimeLabel.bringToFront();
				}
				return true;
			case MotionEvent.ACTION_UP:
				boolean swapped = false;
				if (selectedBlock != null) {
					for (Block endingBlock : blocks) {
						if (!swapped
								&& endingBlock.isTapped(event.getX(),
										(event.getY() - locations[1]))) {
							// If swapping with itself, restore the position
							if (endingBlock.equals(selectedBlock)) {
								restoreSelected();
							} else {
								LayoutParams newSelectedBlockLayout = endingBlock
										.getViewLayoutParams();
								LayoutParams newEndingBlockLayout = selectedBlock
										.getViewLayoutParams();

								absoluteLayout.removeView(selectedBlock);
								absoluteLayout.removeView(endingBlock);

								absoluteLayout.addView(selectedBlock,
										newSelectedBlockLayout);
								absoluteLayout.addView(endingBlock,
										newEndingBlockLayout);

								selectedBlock.setLeft2(newSelectedBlockLayout.x);
								selectedBlock.setTop2(newSelectedBlockLayout.y);
								endingBlock.setLeft2(newEndingBlockLayout.x);
								endingBlock.setTop2(newEndingBlockLayout.y);
								log("Swapped " + blocks.indexOf(selectedBlock)
										+ " with "
										+ blocks.indexOf(endingBlock));
								log("Swapped " + selectedBlock.getSequence()
										+ " "
										+ selectedBlock.getSequenceOrder()
										+ " with " + endingBlock.getSequence()
										+ " " + endingBlock.getSequenceOrder());
								Collections.swap(blocks,
										blocks.indexOf(selectedBlock),
										blocks.indexOf(endingBlock));
								absoluteLayout.invalidate();
								swapped = true;
							}
						} else {
							restoreSelected();
						}
					}

					log("[gil] Block Order = " + blocks.toString());

					mTimeLabel.bringToFront();
					moves++;
					determineIfWon(blocks);
				}
				return true;

			default:
				restoreSelected();
				return super.onTouchEvent(event);
			}
		case SLIDING_PUZZLE_TYPE:
			absoluteLayout.getLocationInWindow(locations);
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				selectedBlock = getBlock(event.getX() - locations[0], event.getY()
						- locations[1]);
				log("Selected " + blocks.indexOf(selectedBlock));

				mTimeLabel.bringToFront();
				
				// check above if empty
				Log.i(this.toString(), "selectedBlock.getLocLeft(): " + selectedBlock.getLocLeft());
				Log.i(this.toString(), "emptyBlock.getLocLeft(): " + emptyBlock.getLocLeft());
				Log.i(this.toString(), "selectedBlock.getLocTop(): " + selectedBlock.getLocTop());
				Log.i(this.toString(), "emptyBlock.getLocTop(): " + emptyBlock.getLocTop());
				
				Log.i(this.toString(), "selectedBlock.getImageHeight(): " + selectedBlock.getImageHeight());
				Log.i(this.toString(), "selectedBlock.getImageWidth(): " + selectedBlock.getImageWidth());
				
				boolean isValid = false;
				
				if(selectedBlock != null && emptyBlock != null) {
					if( (selectedBlock.getLocLeft() == emptyBlock.getLocLeft()) &&
							selectedBlock.getLocTop() + selectedBlock.getImageHeight() == emptyBlock.getLocTop()) {
						isValid = true;
					} // check below if empty
					else if(selectedBlock.getLocLeft() == emptyBlock.getLocLeft() &&
							selectedBlock.getLocTop() - selectedBlock.getImageHeight() == emptyBlock.getLocTop()) {
						isValid = true;
					} // check left if empty
					else if(selectedBlock.getLocLeft() - selectedBlock.getImageWidth() == emptyBlock.getLocLeft() &&
							selectedBlock.getLocTop() == emptyBlock.getLocTop()) {
						isValid = true;
					}
					else if(selectedBlock.getLocLeft() + selectedBlock.getImageWidth() == emptyBlock.getLocLeft() &&
							selectedBlock.getLocTop() == emptyBlock.getLocTop()) {
						isValid = true;
					}
				}

				if (selectedBlock != null && emptyBlock != selectedBlock && isValid) {
					LayoutParams newSelectedBlockLayout = emptyBlock.getViewLayoutParams();
					LayoutParams newEndingBlockLayout = selectedBlock.getViewLayoutParams();
					
					absoluteLayout.removeView(selectedBlock);
					absoluteLayout.removeView(emptyBlock);
					
					absoluteLayout.addView(selectedBlock,
							newSelectedBlockLayout);
					absoluteLayout.addView(emptyBlock,
							newEndingBlockLayout);

					selectedBlock.setLeft2(newSelectedBlockLayout.x);
					selectedBlock.setTop2(newSelectedBlockLayout.y);
					emptyBlock.setLeft2(newEndingBlockLayout.x);
					emptyBlock.setTop2(newEndingBlockLayout.y);
		
					Collections.swap(blocks,
							blocks.indexOf(selectedBlock),
							blocks.indexOf(emptyBlock));
					
					absoluteLayout.invalidate();

					log("[gil] Block Order = " + blocks.toString());
		
					mTimeLabel.bringToFront();
					determineIfWon(blocks);
				}
				return true;

			case MotionEvent.ACTION_MOVE:
				return false;
			case MotionEvent.ACTION_UP:
				if (selectedBlock != null && emptyBlock != selectedBlock) {
				
					log("[gil] Block Order = " + blocks.toString());

					mTimeLabel.bringToFront();
					determineIfWon(blocks);
				}
				return true;

			default:
				restoreSelected();
				return super.onTouchEvent(event);
			}
		}
		return super.onTouchEvent(event);
	}

	private void restoreSelected() {
		LayoutParams originalBlockLayout = selectedBlock.getViewLayoutParams();

		absoluteLayout.removeView(selectedBlock);

		absoluteLayout.addView(selectedBlock, originalBlockLayout);
		selectedBlock.setLeft2(originalBlockLayout.x);
		selectedBlock.setTop2(originalBlockLayout.y);
	}

	private Block getBlock(float x, float y) {

		log("Block Order = " + blocks.toString());
		for (Block block : blocks) {
			if (block.isTapped(x, y)) {
				return block;
			}
		}
		return null;

	}

	static void log(String msg) {
		Log.d("Picture Puzzle", msg);
	}

	private void determineIfWon(List<Block> blocks) {
		boolean won = false;
		int[] sequenceArray = new int[blocks.size()];
		for (int i = 0; i < blocks.size(); i++) {
			Block block = (Block) blocks.get(i);
			sequenceArray[i] = block.getSequenceOrder();
		}

		for (int y = 0; y < sequenceArray.length; y++) {

			if (y >= 1 && y < sequenceArray.length - 1) {
				if ((sequenceArray[y - 1] < sequenceArray[y])
						&& (sequenceArray[y] < sequenceArray[y + 1])) {
					won = true;
				} else {
					won = false;
					return;
				}
			}
		}

		if (won) {
			timerHandler.removeCallbacks(mUpdateTimeTask);
			mUpdateTimeTask = null;
			isRandomPicture = true;

			manageHighScore(levelHighTimes, levelHighNames, levelHighMoves);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onStop() {
		timerHandler.removeCallbacks(mUpdateTimeTask);
		super.onStop();
	}

	HighScoreManager highScoreManager;
	Intent intent;
	SharedPreferences.Editor editor;

	private void manageHighScore(String timeExtra, String nameExtra,
			String movesExtra) {
		prefs = getSharedPreferences(HIGH_SCORE_SETTINGS, 0);
		editor = prefs.edit();

		String existingTimes = prefs.getString(timeExtra, "");
		String existingNames = prefs.getString(nameExtra, "");
		String existingMoves = prefs.getString(movesExtra, "");

		String dummyNewName = "Anonymous";

		highScoreManager = new HighScoreManager(existingTimes, existingNames,
				existingMoves, mTimeLabel.getText().toString(), dummyNewName,
				String.valueOf(moves));

		intent = new Intent(PicturePuzzle.this, HighScoreMain.class);
		if (highScoreManager.isANewHighScore() && !isDecreasingTime) {
			showDialog(HIGHSCORE_DIALOG);
		} else {
			PicturePuzzle.this.showDialog(YOU_WIN_DIALOG);
		}

		// startActivity(intent);
		// finish();
	}

	private void finishPuzzle() {
		finish();
	}

	@Override
	public Dialog onCreateDialog(int dialogId) {
		Dialog dialog = null;
		switch (dialogId) {
		case YOU_LOSE_DIALOG:
			// CustomDialog.Builder customBuilder = new CustomDialog.Builder(
			// PicturePuzzle.this);
			// customBuilder.setTitle("You Lose!!");
			// customBuilder.setNegativeButton("Quit",
			// new DialogInterface.OnClickListener() {
			// public void onClick(DialogInterface dialog, int which) {
			// if (!isFirstTimeUser()) {
			// finishPuzzle();
			// dialog.dismiss();
			// } else {
			// dialog.dismiss();
			// PicturePuzzle.this
			// .showDialog(FIRST_TIME_DIALOG);
			// }
			// isFirstTimeUser();
			// }
			// });
			// customBuilder.setPositiveButton("New Game",
			// new DialogInterface.OnClickListener() {
			// public void onClick(DialogInterface dialog, int which) {
			// cleanUpForNextGame();
			// setupPuzzle();
			// dialog.dismiss();
			// if (isFirstTimeUser()) {
			// PicturePuzzle.this
			// .showDialog(FIRST_TIME_DIALOG);
			// }
			// }
			// });
			//
			// dialog = customBuilder.create();

			YouLoseDialog.Builder customBuilder = new YouLoseDialog.Builder(
					PicturePuzzle.this);
			customBuilder.setTitle("You Lose!!");
			customBuilder.setNewGameButton("New Game",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							cleanUpForNextGame();
							setupPuzzle();
							dialog.dismiss();
							if (isFirstTimeUser()) {
								PicturePuzzle.this
										.showDialog(FIRST_TIME_DIALOG);
							}
						}
					});
			customBuilder.setTryAgainButton("Try Again",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							isTryAgain = true;
							cleanUpForNextGame();
							setupPuzzle();
							dialog.dismiss();
							if (isFirstTimeUser()) {
								PicturePuzzle.this
										.showDialog(FIRST_TIME_DIALOG);
							}
						}
					});
			customBuilder.setQuitButton("Quit",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							if (!isFirstTimeUser()) {
								finishPuzzle();
								dialog.dismiss();
							} else {
								dialog.dismiss();
								PicturePuzzle.this
										.showDialog(FIRST_TIME_DIALOG);
							}
							isFirstTimeUser();
						}
					});

			dialog = customBuilder.create();

			return dialog;
		case HIGHSCORE_DIALOG:
			// AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
			CustomDialog.Builder winBuilder = new CustomDialog.Builder(
					PicturePuzzle.this);

			winBuilder.setTitle("New High Score!!");
			// winBuilder.setMessage("Enter your name");
			final EditText txtName = new EditText(this);
			final TextView txtView = new TextView(this);
			txtName.setMinWidth(200);
			txtName.setGravity(Gravity.CENTER_HORIZONTAL);

			txtView.setText("Enter your name");
			winBuilder.setContentView(txtView);
			winBuilder.setContentView(txtName);

			winBuilder.setPositiveButton("Ok", new OnClickListener() {

				public void onClick(DialogInterface dialog, int arg1) {
					if (txtName.getText().toString().trim().length() == 0) {
						txtName.setText("Anonymous");
					}
					highScoreManager.getHighScoreList().renameHighScorer(
							txtName.getText().toString());
					intent.putExtra(levelHighTimes, highScoreManager
							.getHighScoreList().getHighTimes());
					intent.putExtra(levelHighNames, highScoreManager
							.getHighScoreList().getHighNames());
					intent.putExtra(levelHighMoves, highScoreManager
							.getHighScoreList().getHighMoves());

					editor.putString(levelHighTimes, highScoreManager
							.getHighScoreList().getHighTimes()); // Activity //
																	// doesn't
																	// get its /
																	// value
																	// here
					editor.putString(levelHighNames, highScoreManager
							.getHighScoreList().getHighNames());
					editor.putString(levelHighMoves, highScoreManager
							.getHighScoreList().getHighMoves());
					editor.commit();

					highScoreManager = null;
					intent = null;
					editor = null;

					CustomDialog.Builder newGame = new CustomDialog.Builder(
							PicturePuzzle.this);

					newGame.setTitle("New Game?");
					newGame.setPositiveButton("Yeah!", new OnClickListener() {

						public void onClick(DialogInterface dialog, int arg1) {
							if (!isFirstTimeUser()) {
								cleanUpForNextGame();
								setupPuzzle();
								dialog.dismiss();
							} else {
								dialog.dismiss();
								PicturePuzzle.this
										.showDialog(FIRST_TIME_DIALOG);
							}
						}
					});
					newGame.setNegativeButton("Quit", new OnClickListener() {

						public void onClick(DialogInterface dialog, int arg1) {
							if (!isFirstTimeUser()) {
								finish();
								dialog.dismiss();
							} else {
								dialog.dismiss();
								PicturePuzzle.this
										.showDialog(FIRST_TIME_DIALOG);
							}
						}
					});
					newGame.create().show();
					dialog.dismiss();
				}
			});

			dialog = winBuilder.create();
			return dialog;
		case YOU_WIN_DIALOG:
			CustomDialog.Builder newGame = new CustomDialog.Builder(
					PicturePuzzle.this);

			newGame.setTitle("You Win!!");
			newGame.setMessage("New Game?");
			newGame.setPositiveButton("Yeah!", new OnClickListener() {

				public void onClick(DialogInterface dialog, int arg1) {
					if (!isFirstTimeUser()) {
						cleanUpForNextGame();
						setupPuzzle();
						dialog.dismiss();
					} else {
						dialog.dismiss();
						PicturePuzzle.this.showDialog(FIRST_TIME_DIALOG);
					}
				}
			});
			newGame.setNegativeButton("Quit", new OnClickListener() {

				public void onClick(DialogInterface dialog, int arg1) {
					if (!isFirstTimeUser()) {
						finish();
						dialog.dismiss();
					} else {
						dialog.dismiss();
						PicturePuzzle.this.showDialog(FIRST_TIME_DIALOG);
					}
				}
			});

			dialog = newGame.create();
			return dialog;
		case NO_BITMAP_DIALOG:
			CustomDialog.Builder noImageDialog = new CustomDialog.Builder(
					PicturePuzzle.this);

			noImageDialog.setTitle("Image Problem");
			noImageDialog
					.setMessage("Sorry, There was a problem in the image selection.\nPlease choose another image.");
			noImageDialog.setPositiveButton("OK", new OnClickListener() {

				public void onClick(DialogInterface dialog, int arg1) {
					finish();
					dialog.dismiss();
				}
			});
			dialog = noImageDialog.create();
			return dialog;
		case FIRST_TIME_DIALOG:
			CustomDialog.Builder firstTimeDialog = new CustomDialog.Builder(
					PicturePuzzle.this);

			firstTimeDialog.setTitle("Thank You!");
			firstTimeDialog
					.setMessage("Your Rating and Feedback are much appreciated for this game. ^_^\n(After clicking Ok you won't see this again ^_^)");
			firstTimeDialog.setPositiveButton("OK", new OnClickListener() {

				public void onClick(DialogInterface dialog, int arg1) {
					Intent intent = new Intent(
							Intent.ACTION_VIEW,
							Uri.parse("market://details?id=com.gscreatives.picturepuzzle"));
					// Intent intent = new Intent(Intent.ACTION_VIEW);
					// intent.setData(Uri.parse("market://details?id=com.gscreatives.picturepuzzle"));
					// intent.setData(Uri.parse("market://search?q=pname:com.gscreatives.picturepuzzle"));
					startActivity(intent);
				}
			});
			firstTimeDialog.setNegativeButton("Later", new OnClickListener() {
				public void onClick(DialogInterface dialog, int arg1) {
					prefs = getSharedPreferences(HIGH_SCORE_SETTINGS, 0);
					editor = prefs.edit();
					editor.putString("firstTime13", "");
					editor.commit();

					finish();
					dialog.dismiss();
				}
			});
			dialog = firstTimeDialog.create();
			return dialog;
		}
		return null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		CreateMenu(menu);
		return true;
	}

	private void CreateMenu(Menu menu) {
		menu.setQwertyMode(true);
		MenuItem mnu1 = menu.add(0, 0, 0, "New Game");
		{
			mnu1.setAlphabeticShortcut('n');
		}
		MenuItem mnu2 = menu.add(0, 1, 1, "Quit");
		{
			mnu2.setAlphabeticShortcut('q');
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return MenuChoice(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);
		CreateMenu(menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		return MenuChoice(item);
	}

	private boolean MenuChoice(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			cleanUpForNextGame();
			setupPuzzle();
			return true;
		case 1:
			timerHandler.removeCallbacks(mUpdateTimeTask);
			finish();
			return true;
		}
		return false;
	}

	private void cleanUpForNextGame() {
		timerHandler.removeCallbacks(mUpdateTimeTask);
		this.imagePath = "";
		isRandomPicture = true;
	}

	private boolean isFirstTimeUser() {
		prefs = getSharedPreferences(HIGH_SCORE_SETTINGS, 0);
		String firstTime = prefs.getString("firstTime13", "");
		if (firstTime != null && firstTime.length() > 0) {
			return false;
		} else {
			editor = prefs.edit();
			editor.putString("firstTime13", "firstTime");
			editor.commit();
			return true;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// ArrayList<String> savedRowLocations = new ArrayList<String>();
		// Iterator iter = blocks.iterator();

		// while(iter.hasNext()) {
		// Block block = (Block) iter.next();
		//
		//
		// }
		// SharedPreferences prefs = getSharedPreferences("GridViewDemoPrefs",
		// 0);
		// SharedPreferences.Editor editor = prefs.edit();

		// int index = 0;
		// for (int row = 0; row < level.getRows(); row++) {
		// for (int column = 0; column < level.getColumns(); column++) {
		// Block block = blocks.get(index);
		//
		// outState.putSerializable("savedBlock"+String.valueOf(index), block);
		// }
		// }
		//
		//
		//
		//
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		savedInstanceState.getSerializable("savedBlock" + 0);

		super.onRestoreInstanceState(savedInstanceState);
	}

	private void saveGame(AbsoluteLayout layout) {
		Paint paint = new Paint();
		String sequenceArray = new String();

		Bitmap returnedBitmap = Bitmap.createBitmap(LayoutMessure.windowW,
				LayoutMessure.windowH, Bitmap.Config.ARGB_8888);
		Canvas myCanvas = new Canvas(returnedBitmap);

		GSCAbsoluteLayout gscLayout = new GSCAbsoluteLayout(this);
		for (Block block : blocks) {
			Block imageBlock = block.clone(PicturePuzzle.this, this.bitmap);
			gscLayout.addView(imageBlock);
			myCanvas.drawBitmap(imageBlock.getScaledBitmap(),
					imageBlock.getLocLeft(), imageBlock.getLocTop(), paint);
			sequenceArray = sequenceArray + ","
					+ String.valueOf(imageBlock.getSequenceOrder());
		}

		File myDir = new File("/sdcard");
		myDir.mkdirs();

		File file = new File(myDir, "gsc_saved.jpg");
		if (file.exists())
			file.delete();

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			// Take the bitmap of the view and write it out as a jpeg.
			returnedBitmap.compress(CompressFormat.JPEG, 95, fos);

			prefs = getSharedPreferences(HIGH_SCORE_SETTINGS, 0);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putBoolean("has_saved_game", true);
			editor.putLong("saved_game_time", mStartTime);
			editor.putString("saved_sequence_array", sequenceArray);
			editor.commit();
		} catch (Throwable ex) {
			Log.i(PicturePuzzle.this.toString(), ex.getMessage());
		}
	}

	@Override
	protected void onPause() {
		saveGame(this.absoluteLayout);

		super.onPause();
	}
}