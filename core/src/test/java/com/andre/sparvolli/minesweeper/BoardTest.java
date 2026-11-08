package com.andre.sparvolli.minesweeper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.badlogic.gdx.math.MathUtils;

class BoardTest {
	private static final int ROWS = 9;
	private static final int COLS = 9;
	private static final int MINES = 10;

	private Board board;

	@BeforeEach
	void setUp() {
		// semente fixa: o sorteio das minas sai sempre igual e os testes ficam previsíveis
		MathUtils.random.setSeed( 42 );
		this.board = new Board( ROWS, COLS, MINES );
	}

	// ---------- estado inicial ----------

	@Test
	void newBoardStartsReadyWithAllTilesHidden() {
		assertEquals( Board.Status.READY, this.board.getStatus() );
		assertEquals( MINES, this.board.getRemainingMines() );
		assertNull( this.board.getExplodedTile() );

		for ( int r = 0; r < ROWS; r++ ) {
			for ( int c = 0; c < COLS; c++ ) {
				Tile tile = this.board.getTile( r, c );
				assertNotNull( tile );
				assertEquals( Tile.State.HIDDEN, tile.getState() );
				assertFalse( tile.isMine() );
			}
		}
	}

	// ---------- primeiro clique ----------

	@ParameterizedTest
	@ValueSource( longs = { 1, 2, 3, 42, 1234, 99999 } )
	void firstRevealNeverLoses( long seed ) {
		MathUtils.random.setSeed( seed );
		Board b = new Board( ROWS, COLS, MINES );

		b.reveal( 4, 4 );

		assertNotEquals( Board.Status.LOST, b.getStatus() );
		assertFalse( b.getTile( 4, 4 ).isMine() );
	}

	@Test
	void firstRevealPlantsExactMineCount() {
		this.board.reveal( 0, 0 );

		assertEquals( MINES, countMines( this.board ) );
	}

	@Test
	void firstRevealChangesStatusToPlaying() {
		this.board.reveal( 0, 0 );

		// com 10 minas em 81 tiles, um único clique não abre o tabuleiro inteiro
		assertEquals( Board.Status.PLAYING, this.board.getStatus() );
	}

	@Test
	void revealOnFlaggedTileBeforeFirstClickDoesNotStartGame() {
		this.board.cycleMark( 0, 0 ); // FLAGGED
		this.board.reveal( 0, 0 );

		assertEquals( Board.Status.READY, this.board.getStatus() );
		assertEquals( 0, countMines( this.board ) );
	}

	// ---------- números ----------

	@Test
	void adjacentMineCountsAreCorrect() {
		this.board.reveal( 0, 0 );

		for ( int r = 0; r < ROWS; r++ ) {
			for ( int c = 0; c < COLS; c++ ) {
				Tile tile = this.board.getTile( r, c );
				if ( !tile.isMine() ) {
					assertEquals( bruteForceCount( this.board, r, c ), tile.getAdjacentMines(),
						"número errado em (" + r + ", " + c + ")" );
				}
			}
		}
	}

	// ---------- flood fill ----------

	@Test
	void revealingZeroTileOpensAllNeighbors() {
		this.board.reveal( 0, 0 );

		Tile zero = findSafeZeroTile( this.board );
		if ( zero == null ) {
			return; // improvável, mas o tabuleiro sorteado pode não ter nenhum 0 sobrando
		}

		this.board.reveal( zero.getRow(), zero.getCol() );

		for ( int dr = -1; dr <= 1; dr++ ) {
			for ( int dc = -1; dc <= 1; dc++ ) {
				int r = zero.getRow() + dr;
				int c = zero.getCol() + dc;
				if ( r >= 0 && r < ROWS && c >= 0 && c < COLS ) {
					assertEquals( Tile.State.REVEALED, this.board.getTile( r, c ).getState() );
				}
			}
		}
	}

	@Test
	void floodFillNeverRevealsMines() {
		this.board.reveal( 0, 0 );

		for ( int r = 0; r < ROWS; r++ ) {
			for ( int c = 0; c < COLS; c++ ) {
				Tile tile = this.board.getTile( r, c );
				if ( tile.isMine() ) {
					assertNotEquals( Tile.State.REVEALED, tile.getState() );
				}
			}
		}
	}

	@Test
	void floodFillDoesNotOpenFlaggedTiles() {
		// flag colocada antes do primeiro clique, vizinha ao tile clicado:
		// se a cascata passar por ela, a flag precisa continuar lá
		this.board.cycleMark( 0, 1 );
		this.board.reveal( 0, 0 );

		assertEquals( Tile.State.FLAGGED, this.board.getTile( 0, 1 ).getState() );
	}

	// ---------- vitória ----------

	@Test
	void revealingAllSafeTilesWins() {
		this.board.reveal( 0, 0 );
		revealAllSafe( this.board );

		assertEquals( Board.Status.WON, this.board.getStatus() );
	}

	@Test
	void revealingSameTileTwiceDoesNotCountTwice() {
		this.board.reveal( 0, 0 );

		// clicar várias vezes em tiles já revelados não pode antecipar a vitória
		for ( int i = 0; i < 200; i++ ) {
			this.board.reveal( 0, 0 );
		}

		assertEquals( Board.Status.PLAYING, this.board.getStatus() );
	}

	// ---------- derrota ----------

	@Test
	void revealingMineLosesAndStoresExplodedTile() {
		this.board.reveal( 0, 0 );
		Tile mine = findMine( this.board );

		this.board.reveal( mine.getRow(), mine.getCol() );

		assertEquals( Board.Status.LOST, this.board.getStatus() );
		assertSame( mine, this.board.getExplodedTile() );
	}

	@Test
	void revealingFlaggedMineDoesNotLose() {
		this.board.reveal( 0, 0 );
		Tile mine = findMine( this.board );

		this.board.cycleMark( mine.getRow(), mine.getCol() ); // FLAGGED
		this.board.reveal( mine.getRow(), mine.getCol() );

		assertEquals( Board.Status.PLAYING, this.board.getStatus() );
		assertNull( this.board.getExplodedTile() );
	}

	@Test
	void revealingQuestionMarkedMineLoses() {
		this.board.reveal( 0, 0 );
		Tile mine = findMine( this.board );

		this.board.cycleMark( mine.getRow(), mine.getCol() ); // FLAGGED
		this.board.cycleMark( mine.getRow(), mine.getCol() ); // QUESTION
		this.board.reveal( mine.getRow(), mine.getCol() );

		assertEquals( Board.Status.LOST, this.board.getStatus() );
	}

	@Test
	void actionsAreIgnoredAfterGameOver() {
		this.board.reveal( 0, 0 );
		Tile mine = findMine( this.board );
		this.board.reveal( mine.getRow(), mine.getCol() );

		Tile safeHidden = findSafeHidden( this.board );
		int remaining = this.board.getRemainingMines();

		this.board.reveal( safeHidden.getRow(), safeHidden.getCol() );
		this.board.cycleMark( safeHidden.getRow(), safeHidden.getCol() );

		assertEquals( Tile.State.HIDDEN, safeHidden.getState() );
		assertEquals( remaining, this.board.getRemainingMines() );
		assertEquals( Board.Status.LOST, this.board.getStatus() );
	}

	// ---------- flags / contador ----------

	@Test
	void cycleMarkUpdatesRemainingMines() {
		this.board.cycleMark( 0, 0 ); // HIDDEN -> FLAGGED
		assertEquals( MINES - 1, this.board.getRemainingMines() );

		this.board.cycleMark( 0, 0 ); // FLAGGED -> QUESTION
		assertEquals( MINES, this.board.getRemainingMines() );

		this.board.cycleMark( 0, 0 ); // QUESTION -> HIDDEN
		assertEquals( MINES, this.board.getRemainingMines() );
		assertEquals( Tile.State.HIDDEN, this.board.getTile( 0, 0 ).getState() );
	}

	@Test
	void remainingMinesCanGoNegative() {
		for ( int c = 0; c < COLS; c++ ) {
			this.board.cycleMark( 0, c );
			this.board.cycleMark( 1, c );
		}

		assertEquals( MINES - 2 * COLS, this.board.getRemainingMines() );
		assertTrue( this.board.getRemainingMines() < 0 );
	}

	@Test
	void cycleMarkOnRevealedTileDoesNothing() {
		this.board.reveal( 0, 0 );
		this.board.cycleMark( 0, 0 );

		assertEquals( Tile.State.REVEALED, this.board.getTile( 0, 0 ).getState() );
		assertEquals( MINES, this.board.getRemainingMines() );
	}

	// ---------- limites ----------

	@Test
	void outOfBoundsActionsAreIgnored() {
		this.board.reveal( -1, 0 );
		this.board.reveal( 0, -1 );
		this.board.reveal( ROWS, 0 );
		this.board.reveal( 0, COLS );
		this.board.cycleMark( -1, -1 );
		this.board.cycleMark( ROWS, COLS );

		assertEquals( Board.Status.READY, this.board.getStatus() );
		assertEquals( MINES, this.board.getRemainingMines() );
	}

	// ---------- reset ----------

	@Test
	void resetRestoresInitialState() {
		this.board.cycleMark( 8, 8 );
		this.board.reveal( 0, 0 );
		Tile mine = findMine( this.board );
		this.board.reveal( mine.getRow(), mine.getCol() );

		this.board.reset();

		assertEquals( Board.Status.READY, this.board.getStatus() );
		assertEquals( MINES, this.board.getRemainingMines() );
		assertNull( this.board.getExplodedTile() );
		assertEquals( 0, countMines( this.board ) );

		for ( int r = 0; r < ROWS; r++ ) {
			for ( int c = 0; c < COLS; c++ ) {
				assertEquals( Tile.State.HIDDEN, this.board.getTile( r, c ).getState() );
			}
		}
	}

	@Test
	void canWinAgainAfterReset() {
		this.board.reveal( 0, 0 );
		revealAllSafe( this.board );
		this.board.reset();

		this.board.reveal( 4, 4 );
		assertEquals( MINES, countMines( this.board ) );

		revealAllSafe( this.board );
		assertEquals( Board.Status.WON, this.board.getStatus() );
	}

	// ---------- helpers ----------

	private static int countMines( Board b ) {
		int count = 0;
		for ( int r = 0; r < b.getRows(); r++ ) {
			for ( int c = 0; c < b.getCols(); c++ ) {
				if ( b.getTile( r, c ).isMine() ) {
					count++;
				}
			}
		}
		return count;
	}

	private static int bruteForceCount( Board b, int row, int col ) {
		int count = 0;
		for ( int r = row - 1; r <= row + 1; r++ ) {
			for ( int c = col - 1; c <= col + 1; c++ ) {
				if ( ( r != row || c != col ) && r >= 0 && r < b.getRows() && c >= 0 && c < b.getCols()
					&& b.getTile( r, c ).isMine() ) {
					count++;
				}
			}
		}
		return count;
	}

	private static void revealAllSafe( Board b ) {
		for ( int r = 0; r < b.getRows(); r++ ) {
			for ( int c = 0; c < b.getCols(); c++ ) {
				if ( !b.getTile( r, c ).isMine() ) {
					b.reveal( r, c );
				}
			}
		}
	}

	private static Tile findMine( Board b ) {
		for ( int r = 0; r < b.getRows(); r++ ) {
			for ( int c = 0; c < b.getCols(); c++ ) {
				if ( b.getTile( r, c ).isMine() ) {
					return b.getTile( r, c );
				}
			}
		}
		throw new IllegalStateException( "nenhuma mina no tabuleiro" );
	}

	private static Tile findSafeHidden( Board b ) {
		for ( int r = 0; r < b.getRows(); r++ ) {
			for ( int c = 0; c < b.getCols(); c++ ) {
				Tile t = b.getTile( r, c );
				if ( !t.isMine() && t.getState() == Tile.State.HIDDEN ) {
					return t;
				}
			}
		}
		throw new IllegalStateException( "nenhum tile seguro escondido" );
	}

	private static Tile findSafeZeroTile( Board b ) {
		for ( int r = 0; r < b.getRows(); r++ ) {
			for ( int c = 0; c < b.getCols(); c++ ) {
				Tile t = b.getTile( r, c );
				if ( !t.isMine() && t.getAdjacentMines() == 0 && t.getState() == Tile.State.HIDDEN ) {
					return t;
				}
			}
		}
		return null;
	}
}
