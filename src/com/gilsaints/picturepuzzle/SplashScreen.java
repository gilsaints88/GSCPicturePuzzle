package com.gilsaints.picturepuzzle;

/**
 * Created by Evgeni Shafran, http://evgeni-shafran.blogspot.com/, 12/01/2011
 * Created for common use, and learning.
 * 
 * This demo demonstrated how to get the exact size on current screen
 * 
 * This is the first screen (main).
 * This screen gets the main layout which have LayoutMessure view, so when we load this one
 * we also load the LayoutMessure.
 */
import com.gilsaints.picturepuzzle.R;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class SplashScreen extends Activity {
    /** Called when the activity is first created. */
	final Handler mHandler = new Handler();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        
        setContentView(R.layout.main);
        // we do not have to do anything else because the layout is invoked automatically
        
        //you can call it without delay and it will appear instantly.
        // 	loadScreenIntent();
        mHandler.postDelayed(startPro, 3000);	
    }
    final Runnable startPro = new Runnable() {
    	public void run() {
    		loadScreenIntent();
    	}
    	};
    void loadScreenIntent()
    {
    	startInSettingsPage();
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	super.onTouchEvent(event);
    	startInSettingsPage();
    	return false;
    }
    
    private void startInSettingsPage() {
    	mHandler.removeCallbacks(startPro);
    	Intent myIntent = new Intent(SplashScreen.this, PictureMain.class);
    	myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(myIntent);
		finish();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    
    // not support in Android 1.5
//    public void onBackPressed() {
////    	super.onBackPressed();
//    	mHandler.removeCallbacks(startPro);
//    	finish();
//    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	mHandler.removeCallbacks(startPro);
        	finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}