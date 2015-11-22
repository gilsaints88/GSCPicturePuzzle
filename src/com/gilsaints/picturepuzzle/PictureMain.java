package com.gilsaints.picturepuzzle;

import java.util.Random;

import com.gilsaints.picturepuzzle.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

public class PictureMain extends Activity {
	Button btnBeginPuzzle = null;
	Button btnSelectPic = null;
	TextView txtImagePath = null;
	SeekBar seekLevel = null;
	CheckBox chkDecreasingTime = null;
	SeekBar seekStartTime = null;
	Button btnShowHighScores = null;

	private int gameLevel;
	private int startTime;
	private boolean isDecreasingTime = false;
	// private String imagePath;

	private static final int SELECT_PICTURE = 1;
	private String selectedImagePath;

	CheckBox chkRandomImages = null;
	CheckBox chkHasBorder = null;

	Spinner spinnerGameType = null;

	SharedPreferences settings;

	private boolean continueSavedGame = false;
	private final int SAVED_GAME_DIALOG = 12345;
	private final String HIGH_SCORE_SETTINGS = "HighScores";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		setContentView(R.layout.settings);
		settings = getSharedPreferences(HIGH_SCORE_SETTINGS, 0);

		if (settings.getBoolean("has_saved_game", false)) {
			savedGameDialog();
			// if (continueSavedGame) {
			// selectedImagePath = settings.getString("imagePath", "");
			// startGame(true);
			// }
		}

		// Restore preferences
		String[] gameTypes = { "Block Puzzle", "Sliding Puzzle" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, gameTypes);
		spinnerGameType = (Spinner) findViewById(R.id.spinnerGameType);
		spinnerGameType.setAdapter(adapter);
		
		spinnerGameType.setSelection(settings.getInt("gameType", 0));
		spinnerGameType.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				int gameTypeIndex = spinnerGameType.getSelectedItemPosition();
				
				Editor editor = settings.edit();

				editor.putInt("gameType", gameTypeIndex);
				editor.commit();
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		
		selectedImagePath = settings.getString("imagePath", "");

		chkRandomImages = (CheckBox) findViewById(R.id.chkRandomImages);
		chkRandomImages
				.setChecked(settings.getBoolean("isRandomImages", false));

		txtImagePath = (TextView) findViewById(R.id.txtImagePath);
		chkHasBorder = (CheckBox) findViewById(R.id.chkHasBorder);
		btnBeginPuzzle = (Button) findViewById(R.id.btnBegin);
		btnSelectPic = (Button) findViewById(R.id.btnSelectPic);
		seekLevel = (SeekBar) findViewById(R.id.seekLevel);
		chkDecreasingTime = (CheckBox) findViewById(R.id.chkDecreasingTime);
		btnShowHighScores = (Button) findViewById(R.id.btnShowHighScores);
		seekStartTime = (SeekBar) findViewById(R.id.seekStartTime);

		// testing purpose only start
		// savedInstanceState.getSerializable("getsavedBlock"+"0");
		// testing purpose only end

		if (selectedImagePath == null || selectedImagePath.length() <= 0) {
			noImageAlert();
		}

		txtImagePath.setText(selectedImagePath);

		chkHasBorder.setChecked(settings.getBoolean("hasBorder", false));

		if (chkRandomImages.isChecked()) {
			txtImagePath.setTextColor(Color.GRAY);
		}

		btnBeginPuzzle.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				if ((txtImagePath.getText() == null || txtImagePath.getText()
						.length() <= 0) && !chkRandomImages.isChecked()) {
					noImageAlert();
					return;
				} else {
					String[] projection = { MediaStore.Images.Media.DATA };
					Editor editor = settings.edit();

					try {
						Cursor cursor = managedQuery(
								MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
								projection, // Which columns to return
								null, // Return all rows
								null, MediaStore.Images.Media.DATA);

						String previousImage = settings.getString(
								"previousImage", txtImagePath.getText()
										.toString());
						boolean isImagePathTheSame = true;
						while (isImagePathTheSame) {
							Random rand = new Random();
							int randomIndex = rand.nextInt(cursor.getCount());

							cursor.moveToPosition(randomIndex);
							if (!previousImage.equals(cursor.getString(0))) {
								isImagePathTheSame = false;
							}
						}

						selectedImagePath = cursor.getString(0);
						editor.putString("previousImage", cursor.getString(0));

						editor.commit();
					} catch (Exception iae) {
						noImageAlert();

						editor.putString("previousImage", txtImagePath
								.getText().toString());

						editor.commit();
					}
				}

				if (!chkRandomImages.isChecked()) {
					selectedImagePath = txtImagePath.getText().toString();
				}
				startGame(false);
			}
		});

		btnSelectPic.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				openGalleryBrowser();
			}
		});

		chkRandomImages
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton arg0,
							boolean value) {
						if (!value) {
							txtImagePath.setTextColor(Color.BLUE);
						} else {
							txtImagePath.setTextColor(Color.GRAY);
						}
					}
				});

		// Restore preferences
		gameLevel = settings.getInt("gameLevel", 0);
		seekLevel.setProgress(gameLevel);
		seekLevel.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar bar) {

			}

			public void onStartTrackingTouch(SeekBar bar) {
				// TODO Auto-generated method stub
			}

			public void onProgressChanged(SeekBar bar, int progress,
					boolean fromUser) {
				Log.i(this.toString(), "Progress Level: " + progress);
				gameLevel = progress;
			}
		});

		chkDecreasingTime.setChecked(settings.getBoolean("isDecreasingTime",
				false));
		isDecreasingTime = chkDecreasingTime.isChecked();

		chkDecreasingTime
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton arg0,
							boolean value) {
						isDecreasingTime = value;
						if (!value) {
							seekStartTime.setEnabled(false);
						} else {
							seekStartTime.setEnabled(true);
						}
					}
				});

		startTime = settings.getInt("startTime", 0);
		int progressStartTime = (int) startTime / 60000;
		seekStartTime.setProgress(progressStartTime);
		seekStartTime.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar bar) {

			}

			public void onStartTrackingTouch(SeekBar bar) {
				// TODO Auto-generated method stub
			}

			public void onProgressChanged(SeekBar bar, int progress,
					boolean fromUser) {
				Log.i(this.toString(), "Progress Start Time: " + progress);
				progress = progress + 1;
				startTime = progress * 60000;
			}
		});

		seekStartTime.setEnabled(isDecreasingTime);

		btnShowHighScores.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				Intent intent = new Intent(PictureMain.this,
						HighScoreMain.class);
				startActivity(intent);
			}
		});
	}

	private void startGame(boolean continueSavedGame) {
		Intent i = new Intent(PictureMain.this, PicturePuzzle.class);
		i.putExtra("imagePath", selectedImagePath);
		i.putExtra("gameLevel", gameLevel);
		i.putExtra("isDecreasingTime", isDecreasingTime);
		i.putExtra("startTime", startTime);
		i.putExtra("hasBorder", chkHasBorder.isChecked());

		i.putExtra("continueSavedGame", continueSavedGame);

		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == SELECT_PICTURE) {
				Uri selectedImageUri = data.getData();
				selectedImagePath = getPath(selectedImageUri);

				Log.i("SuperMain", "selectedImageUri: " + selectedImageUri);

				txtImagePath.setText(selectedImagePath);
			}
		}
	}

	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	@Override
	protected void onStop() {
		super.onStop();

		SharedPreferences settings = getSharedPreferences(HIGH_SCORE_SETTINGS,
				0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("imagePath", selectedImagePath);

		editor.putInt("gameLevel", gameLevel);
		editor.putBoolean("isDecreasingTime", isDecreasingTime);
		editor.putInt("startTime", startTime - 1);
		editor.putBoolean("isRandomImages", chkRandomImages.isChecked());
		editor.putBoolean("hasBorder", chkHasBorder.isChecked());

		editor.commit();
	}

	private void noImageAlert() {
		if (!chkRandomImages.isChecked()) {
			AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
			alertbox.setMessage("You do not have an image!");
			alertbox.setNeutralButton("Ok",
					new DialogInterface.OnClickListener() {

						// click listener on the alert box
						public void onClick(DialogInterface arg0, int arg1) {
							openGalleryBrowser();
						}
					});

			alertbox.show();
		} else {

		}

	}

	private void openGalleryBrowser() {
		// To open up a gallery browser
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"),
				SELECT_PICTURE);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
	}

	private void savedGameDialog() {
		showDialog(SAVED_GAME_DIALOG);
	}

	@Override
	public Dialog onCreateDialog(int dialogId) {
		switch (dialogId) {
		case SAVED_GAME_DIALOG:
			CustomDialog.Builder savedGame = new CustomDialog.Builder(
					PictureMain.this);

			savedGame.setTitle("Saved Game");
			savedGame.setMessage("Continue Saved Game?");
			savedGame.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int arg1) {
							SharedPreferences.Editor editor = settings.edit();
							editor.putBoolean("has_saved_game", false);
							editor.commit();

							continueSavedGame = true;

							if (continueSavedGame) {
								selectedImagePath = settings.getString(
										"imagePath", "");
								startGame(true);
							}

							dialog.dismiss();
						}
					});

			savedGame.setNegativeButton("No",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int arg1) {
							SharedPreferences.Editor editor = settings.edit();
							editor.putBoolean("has_saved_game", false);
							editor.commit();

							continueSavedGame = false;

							dialog.dismiss();
						}
					});

			Dialog dialog = savedGame.create();
			return dialog;
		}
		return null;
	}
}
