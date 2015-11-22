package com.gilsaints.picturepuzzle;

public class PicLocation {
	public int getLeft() {
		return left;
	}

	public void setLeft(int left) {
		this.left = left;
	}

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}

	private int left = 0;
	private int top = 0;
	
	public PicLocation(int left, int top) {
		this.left = left;
		this.top = top;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof PicLocation) {
			PicLocation pic = (PicLocation) o;
			if(pic.getLeft() == this.getLeft() && pic.getTop() == this.getTop()) {
				return true;
			}
		}
		return false;
	}
}
