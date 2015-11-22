package com.gilsaints.picturepuzzle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.widget.ImageView;

public class PictureTimer extends ImageView {
	protected final Paint myArcSecondPaint = new Paint();
	protected final Paint myArcMinutePaint = new Paint();
	protected final Paint myCountDownTextPaint = new Paint();
	protected final Paint myPizzaTimeTextPaint = new Paint();

	// ===========================================================
	// Constructors
	// ===========================================================

	public PictureTimer(Context context) {
		super(context);

		// Our minute-arc-paint fill be a lookthrough-red.
		this.myArcMinutePaint.setARGB(150, 170, 0, 0);
		this.myArcMinutePaint.setAntiAlias(true);
		this.myArcMinutePaint.setStyle(Style.STROKE);
		this.myArcMinutePaint.setStrokeWidth(50);

		// Our minute-arc-paint fill be a less lookthrough-orange.
		this.myArcSecondPaint.setARGB(200, 255, 130, 20);
		this.myArcSecondPaint.setAntiAlias(true);
		this.myArcSecondPaint.setStyle(Style.STROKE);
		this.myArcSecondPaint.setStrokeWidth(50 / 3);
	}

	// ===========================================================
	// onXYZ(...) - Methods
	// ===========================================================

	@Override
	protected void onDraw(Canvas canvas) {
//		/*
//		 * Calculate the time left, until our pizza is finished.
//		 */
//		int secondsLeft = this.mySecondsTotal - this.mySecondsPassed;
//
//		// Check if pizza is already done
//		if (secondsLeft <= 0) {
//			/*
//			 * Draw the "! PIZZA !"-String to the middle of the screen
//			 */
////			String itIsPizzaTime = getResources().getString(
////					R.string.pizza_countdown_end);
////			canvas.drawText(itIsPizzaTime, 10, (this.getHeight() / 2) + 30,
////					this.myPizzaTimeTextPaint);
//		} else {
//			// At least one second left
//			float angleAmountMinutes = ((this.mySecondsPassed * 1.0f) / this.mySecondsTotal) * 360;
//			float angleAmountSeconds = ((60 - secondsLeft % 60) * 1.0f) / 60 * 360;
//
//			/*
//			 * Calculate an Rectangle, with some spacing to the edges
//			 */
////			RectF arcRect = new RectF(ARCSTROKEWIDTH / 2, ARCSTROKEWIDTH / 2,
////					this.getWidth() - ARCSTROKEWIDTH / 2, this.getHeight()
////							- ARCSTROKEWIDTH / 2);
////
////			// Draw the Minutes-Arc into that rectangle
////			canvas.drawArc(arcRect, -90f,angleAmountMinutes, false
////					,this.myArcMinutePaint);
////
////			// Draw the Seconds-Arc into that rectangle
////			canvas.drawArc(arcRect, -90f, angleAmountSeconds, false
////					,this.myArcSecondPaint);
//
//			String timeDisplayString;
//			if (secondsLeft > 60) // Show minutes
//				timeDisplayString = "" + (secondsLeft / 60);
//			else
//				// Show seconds when less than a minute
//				timeDisplayString = "" + secondsLeft;
//
//			// Draw the remaining time.
////			canvas.drawText(timeDisplayString, this.getWidth() / 2
////					- (30 * timeDisplayString.length()),
////					this.getHeight() / 2 + 30, this.myCountDownTextPaint);
//			canvas.drawText(""+secondsLeft, this.getWidth() / 2
//					- (30 * timeDisplayString.length()),
//					this.getHeight() / 2 + 30, this.myCountDownTextPaint);
//		}
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
//		myPizzaTimeTextPaint.setARGB(200, 255, 130, 20);
//		myPizzaTimeTextPaint.setAntiAlias(true);
//		myPizzaTimeTextPaint.setStyle(Style.STROKE);
////		canvas
////				.drawText("PICTURE PUZZLE", 1, 13, 50f, 50f,
////						myPizzaTimeTextPaint);
//		int secondsLeft = this.mySecondsTotal - this.mySecondsPassed;
//		
//		float angleAmountMinutes = ((this.mySecondsPassed * 1.0f) / this.mySecondsTotal) * 360;
//		float angleAmountSeconds = ((60 - secondsLeft % 60) * 1.0f) / 60 * 360;
//		
//		String timeDisplayString;
//		if (secondsLeft > 60) // Show minutes
//			timeDisplayString = "" + (secondsLeft / 60);
//		else
//			// Show seconds when less than a minute
//			timeDisplayString = "" + secondsLeft;
////		canvas.drawText(timeDisplayString, this.getWidth() / 2
////				- (30 * timeDisplayString.length()),
////				this.getHeight() / 2 + 30, this.myCountDownTextPaint);
//		canvas.drawText(""+secondsLeft, this.getWidth() / 2
//				- (30 * timeDisplayString.length()),
//				this.getHeight() / 2 + 30, this.myCountDownTextPaint);
	}

	public void updateSecondsPassed(int someSeconds) {
//		this.mySecondsPassed = someSeconds;
	}

	public void updateSecondsTotal(int totalSeconds) {
//		this.mySecondsTotal = totalSeconds;
	}
}
