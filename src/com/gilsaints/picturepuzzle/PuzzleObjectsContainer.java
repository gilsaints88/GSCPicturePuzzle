package com.gilsaints.picturepuzzle;

import java.util.List;

public class PuzzleObjectsContainer {
	private Level level = null;
	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public List<Block> getBlocks() {
		return blocks;
	}

	public void setBlocks(List<Block> blocks) {
		this.blocks = blocks;
	}

	List<Block> blocks = null;
	
	public PuzzleObjectsContainer(Level level, List<Block> blocks) {
		this.blocks = blocks;
		this.level = level;
	}
}
