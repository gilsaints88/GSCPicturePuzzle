package com.gilsaints.picturepuzzle;

import java.io.Serializable;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.AbsoluteLayout;
import android.widget.AbsoluteLayout.LayoutParams;
import android.widget.ImageView;

public class Block extends ImageView implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3364301604944901228L;
	private int top = 0;
	private int left = 0;
	private int height = 0;

	private int width = 0;
	private Bitmap bitmap = null;
	
	private Bitmap scaledBitmap;

	public Bitmap getScaledBitmap() {
		return scaledBitmap;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	private int sequence = 0;
	private int randomCol = 0;

	public int getRandomCol() {
		return randomCol;
	}

	public void setRandomCol(int randomCol) {
		this.randomCol = randomCol;
	}

	public int getRandomRow() {
		return randomRow;
	}

	public void setRandomRow(int randomRow) {
		this.randomRow = randomRow;
	}

	private int randomRow = 0;

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	private int sequenceOrder = 0;

	public Block(Context context, int left, int top, int width, int height,
			int sequence, Bitmap bitmap, int randomCol, int randomRow,
			int sequenceOrder) {
		super(context);
		this.top = top;
		this.left = left;
		this.sequence = sequence;
		this.width = width;
		this.height = height;
		this.bitmap = bitmap;
		this.randomCol = randomCol;
		this.randomRow = randomRow;
		this.sequenceOrder = sequenceOrder;

		this.scaledBitmap = Bitmap.createBitmap(bitmap, randomCol, randomRow,
				width, height);
		this.setImageBitmap(scaledBitmap);
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setSequenceOrder(int sequenceOrder) {
		this.sequenceOrder = sequenceOrder;
	}

	public int getSequenceOrder() {
		return sequenceOrder;
	}

	public AbsoluteLayout.LayoutParams getViewLayoutParams() {
		return new LayoutParams(width, height, left, top);
	}

	public boolean isTapped(float x, float y) {
		return (x > left && x < (left + getWidth()) && y > top && y < (top + getHeight()));
	}

	/**
	 * @param top
	 *            the top to set
	 */
	public void setTop2(int top) {
		setTop(top);
		this.top = top;
	}

	/**
	 * @param left
	 *            the left to set
	 */
	public void setLeft2(int left) {
		setLeft(left);
		this.left = left;
	}

	/**
	 * @return the sequence
	 */
	public int getSequence() {
		return sequence;
	}

	public String toString() {
		// return "sequence="+sequence+", top="+top+", left="+left +
		// " sequenceOrder=> " + sequenceOrder;
		return "Block:sequenceOrder=> " + sequenceOrder;
	}

	public int getLocTop() {
		return top;
	}

	public int getLocLeft() {
		return left;
	}

	public int getImageHeight() {
		return height;
	}

	public int getImageWidth() {
		return width;
	}

	public Block clone(Context context, Bitmap wholeBitmap) {
		Block clone = new Block(context, this.getLocLeft(), this.getLocTop(),
				this.getImageWidth(), this.getImageHeight(), this.sequence,
				wholeBitmap, this.randomCol, this.randomRow,
				this.sequenceOrder);

		return clone;
	}
}