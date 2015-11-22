package com.gilsaints.picturepuzzle;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.AbsoluteLayout;

public class GSCAbsoluteLayout extends AbsoluteLayout {

	public GSCAbsoluteLayout(Context context) {
		super(context);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		canvas.save(Canvas.MATRIX_SAVE_FLAG);       
	    canvas.scale(LayoutMessure.windowH, LayoutMessure.windowH);
	    
		super.dispatchDraw(canvas);
	}
}
