package com.andre.sparvolli.minesweeper;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Queue;

public class Board {
	public enum Status { READY, PLAYING, WON, LOST }

	private static final int[][] NEIGHBOR_OFFSETS = {
		{-1, -1}, {-1, 0}, {-1, 1},
		{ 0, -1},          { 0, 1},
		{ 1, -1}, { 1, 0}, { 1, 1}
	};

	private final int rows;
	private final int cols;
	private final int mineCount;
	private final Tile[][] grid;

	private Status status = Status.READY;
	private int revealedSafe;
	private int flagsPlaced;
	private Tile explodedTile;

	private final Queue<Tile> floodQueue = new Queue<>();

	public Board( int rows, int cols, int mineCount ) {
		this.rows = rows;
		this.cols = cols;
		this.mineCount = mineCount;
		this.grid = new Tile[rows][cols];
		initTiles();
	}

	public void reveal( int row, int col ) {
		if ( isGameOver() || !inBounds( row, col ) ) {
			return;
		}

		Tile tile = this.grid[row][col];

		if ( !tile.reveal() ) {
			return;
		}

		if ( this.status == Status.READY ) {
			plantMines( row, col );
			calculateAdjacentMines();
			this.status = Status.PLAYING;
		}

		if ( tile.isMine() ) {
			this.explodedTile = tile;
			this.status = Status.LOST;
			return;
		}

		this.revealedSafe++;

		if ( tile.getAdjacentMines() == 0 ) {
			floodFill( tile );
		}

		winCheck();
	}

	public void cycleMark( int row, int col ) {
		if ( isGameOver() || !inBounds( row, col ) ) {
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

		for ( int r = 0; r < this.rows; r++ ) {
			for ( int c = 0; c < this.cols; c++ ) {
				this.grid[r][c].resetTile();
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
		return this.rows;
	}

	public int getCols() {
		return this.cols;
	}

	private void initTiles() {
		for ( int r = 0; r < this.rows; r++ ) {
			for ( int c = 0; c < this.cols; c++ ) {
				this.grid[r][c] = new Tile( r, c );
			}
		}
	}

	private void plantMines( int safeRow, int safeCol ) {
		int minesPlanted = 0;
		Tile safeTile = this.grid[safeRow][safeCol];

		while ( minesPlanted < this.mineCount ) {
			Tile tile = this.grid[MathUtils.random( this.rows - 1 )][MathUtils.random( this.cols - 1 )];

			if ( !tile.isMine() && tile != safeTile ) {
				tile.setMine();
				minesPlanted++;
			}
		}
	}

	private void calculateAdjacentMines() {
		for ( int r = 0; r < this.rows; r++ ) {
			for ( int c = 0; c < this.cols; c++ ) {
				Tile tile = this.grid[r][c];

				if ( !tile.isMine() ) {
					tile.setAdjacentMines( countAdjacentMines( r, c ) );
				}
			}
		}
	}

	private int countAdjacentMines( int row, int col ) {
		int count = 0;

		for ( int[] offset : NEIGHBOR_OFFSETS ) {
			int r = row + offset[0];
			int c = col + offset[1];

			if ( inBounds( r, c ) && this.grid[r][c].isMine() ) {
				count++;
			}
		}

		return count;
	}

	private void floodFill( Tile start ) {
		this.floodQueue.addLast( start );

		while ( this.floodQueue.notEmpty() ) {
			Tile current = this.floodQueue.removeFirst();

			for ( int[] offset : NEIGHBOR_OFFSETS ) {
				int r = current.getRow() + offset[0];
				int c = current.getCol() + offset[1];

				if ( !inBounds( r, c ) ) {
					continue;
				}

				Tile neighbor = this.grid[r][c];

				if ( neighbor.reveal() ) {
					this.revealedSafe++;

					if ( neighbor.getAdjacentMines() == 0 ) {
						this.floodQueue.addLast( neighbor );
					}
				}
			}
		}
	}

	private boolean isGameOver() {
		return this.status == Status.WON || this.status == Status.LOST;
	}

	private boolean inBounds( int row, int col ) {
		return row >= 0 && row < this.rows && col >= 0 && col < this.cols;
	}

	private void winCheck() {
		if ( this.revealedSafe == this.rows * this.cols - this.mineCount ) {
			this.status = Status.WON;
		}
	}
}
