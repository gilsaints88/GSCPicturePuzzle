package com.gilsaints.picturepuzzle;
/**
 * Created by Evgeni Shafran, http://evgeni-shafran.blogspot.com/, 12/01/2011
 * Created for common use, and learning.
 *
 * This demo demonstrated how to get the exact size on current screen
 * 
 * We use this extended layout to determine our screen size.
 * we load it in the main screen so we have the size for the whole program
 * 
 * we store the size in public static variable so we can always access it.
 * 
 */
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.LinearLayout;





public class LayoutMessure extends LinearLayout{

	int i,z;
	public int hsize=0;
	
	public LayoutMessure(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
	}

	public LayoutMessure(Context context) {
		super(context);
		System.out.println("ON CREATE");
		
	}
	
	static public int windowW=0;
	static public int windowH=0	;
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) 
	{
		// we overriding onMeasure because this is where the application gets its right size.
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		System.out.println("Screen Mesure: "+getMeasuredWidth()+" "+getMeasuredHeight());
		windowW = getMeasuredWidth();
		windowH = getMeasuredHeight();
	     
	    
	}
	@Override
	protected void onDraw(Canvas canvas) {
		
		super.onDraw(canvas);
	}
	
}
