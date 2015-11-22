package com.gilsaints.picturepuzzle;

public class Sequence {
	private int col;
	private int row;
	private int sequence;
	
	public Sequence(int col, int row) {
		this.col = col;
		this.row = row;
	}
	
	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Sequence) {
			Sequence seq = (Sequence) o;
			if( (seq.getCol() == this.getCol())
					&&
					(seq.getRow() == this.getRow()) ) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "Sequence: col=> " + this.col + " row=> " + this.row;
	}
	
}
