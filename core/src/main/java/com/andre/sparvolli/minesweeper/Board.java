package com.andre.sparvolli.minesweeper;

public class Board {
    public enum Status { READY, PLAYING, WON, LOST }

    private final int rows;
    private final int cols;
    private final int mineCount;
    private final Tile[][] grid;

    private Status status = Status.READY;
    private int revealedSafe;
    private int flagsPlaced;
    private Tile explodedTile;

    public Board( int rows, int cols, int mineCount ) {
    	this.rows = rows;
    	this.cols = cols;
    	this.mineCount = mineCount;
    	this.grid = new Tile[rows][cols];
    	initTiles();
    }

	public void reveal( int row, int col ) {
        if ( isGameOver() || !inBounds(row, col) ) {
        	return;
        }

    	Tile tile = this.grid[row][col];
    	
    	if ( !tile.reveal() ) {
    		return;
    	}
    	
    	if ( tile.isMine() ) {
    		this.explodedTile = tile;
    		this.status = Status.LOST;
    	} else {
    		this.revealedSafe++;
    	}
    }

    public void cycleMark( int row, int col ) {
        if ( isGameOver() || !inBounds(row, col) ) {
        	return;
        }

    	Tile tile = this.grid[row][col];

    	Tile.State before = tile.getState();    		
    	tile.cycleMark();
    	Tile.State after = tile.getState();

    	if ( before != Tile.State.FLAGGED && after == Tile.State.FLAGGED ) {
    		this.flagsPlaced++;
    	}

    	if ( before == Tile.State.FLAGGED && after != Tile.State.FLAGGED ) {
    		this.flagsPlaced--;
    	}
    }

    public void reset() {
    	this.status = Status.READY;

		for (int i = 0; i < this.rows; i++) {
			for (int j = 0; j < this.cols; j++) {
				this.grid[i][j].resetTile();
			}
		}

    	this.flagsPlaced = 0;
    	this.revealedSafe = 0;
    	this.explodedTile = null;
    }

    public Tile getTile( int row, int col ) {
		return this.grid[row][col];
    }

    public Status getStatus() {
    	return this.status;
    }

    public int getRemainingMines() {
    	 return this.mineCount - this.flagsPlaced;
    }

    public Tile getExplodedTile() {
   		return this.explodedTile;
    }

	public int getRows() {
		return rows;
	}

	public int getCols() {
		return cols;
	}

    private void initTiles() {
		for (int i = 0; i < this.rows; i++) {
			for (int j = 0; j < this.cols; j++) {
				this.grid[i][j] = new Tile(i, j);
			}
		}
	}

	private boolean isGameOver() {
    	return this.status == Status.WON || this.status == Status.LOST;
    }

	private boolean inBounds( int row, int col ) {
	    return row >= 0 && row < this.rows && col >= 0 && col < this.cols;
	}
}