package com.andre.sparvolli.minesweeper;

public class Tile {
    public enum State { HIDDEN, REVEALED, FLAGGED, QUESTION }

    private final int row;
    private final int col;
    private boolean mine;
    private int adjacentMines;
    private State state = State.HIDDEN;

    public Tile(int row, int col) {
    	this.row = row;
    	this.col = col;
    }

    public boolean reveal() {
        if (state == State.REVEALED || state == State.FLAGGED) {
        	return false;
        }

        state = State.REVEALED;
        return true;
    }
 
    public void cycleMark() {
        switch (state) {
            case HIDDEN:   state = State.FLAGGED;  break;
            case FLAGGED:  state = State.QUESTION; break;
            case QUESTION: state = State.HIDDEN;   break;
            case REVEALED: break;
        }
    }

    public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public boolean isMine() {
		return mine;
	}

    public void setMine() {
    	this.mine = true;
    }

	public int getAdjacentMines() {
		return adjacentMines;
	}

	public void setAdjacentMines( int adjacentMines ) {
		this.adjacentMines = adjacentMines;
	}

	public State getState() {
		return state;
	}

	public void resetTile() {
		this.state = State.HIDDEN;
		this.adjacentMines = 0;
		this.mine = false;
	}
}
