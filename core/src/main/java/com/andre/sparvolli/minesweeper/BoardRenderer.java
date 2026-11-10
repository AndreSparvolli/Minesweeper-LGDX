package com.andre.sparvolli.minesweeper;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Align;

public class BoardRenderer {

	private static final Color[] NUMBER_COLORS = {
		null, Color.BLUE, new Color( 0, 0.5f, 0, 1 ), Color.RED, Color.NAVY,
		Color.MAROON, Color.TEAL, Color.BLACK, Color.GRAY
	};

	private final GameAssets assets;
	private final BitmapFont font;
	private final float areaWidth;
	private final float areaHeight;
	private final GlyphLayout[] numberLayouts = new GlyphLayout[9];

	private Board board;
	private float originX;
	private float originY;

	public BoardRenderer( GameAssets assets, BitmapFont numberFont, float areaWidth, float areaHeight ) {
		this.assets = assets;
		this.font = numberFont;
		this.areaWidth = areaWidth;
		this.areaHeight = areaHeight;

		for ( int n = 1; n <= 8; n++ ) {
			this.numberLayouts[n] = new GlyphLayout( numberFont, String.valueOf( n ), NUMBER_COLORS[n], 0, Align.left, false );
		}
	}

	public void setBoard( Board board ) {
		this.board = board;
		layout();
	}

	public void draw( SpriteBatch batch ) {
		if ( this.board == null ) {
			return;
		}

		boolean lost = this.board.getStatus() == Board.Status.LOST;
		int rows = this.board.getRows();

		for ( int r = 0; r < rows; r++ ) {
			for ( int c = 0; c < this.board.getCols(); c++ ) {
				float x = this.originX + c * Main.TILE_SIZE;
				float y = this.originY + ( rows - 1 - r ) * Main.TILE_SIZE;

				drawTile( batch, this.board.getTile( r, c ), x, y, lost );
			}
		}
	}

	public boolean toCell( float worldX, float worldY, GridPoint2 out ) {
		if ( this.board == null ) {
			return false;
		}

		int col = ( int ) Math.floor( ( worldX - this.originX ) / Main.TILE_SIZE );
		int rowFromBottom = ( int ) Math.floor( ( worldY - this.originY ) / Main.TILE_SIZE );
		int row = this.board.getRows() - 1 - rowFromBottom;

		if ( row < 0 || row >= this.board.getRows() || col < 0 || col >= this.board.getCols() ) {
			return false;
		}

		out.set( col, row );
		return true;
	}

	private void drawTile( SpriteBatch batch, Tile tile, float x, float y, boolean lost ) {
		switch ( tile.getState() ) {
			case HIDDEN, QUESTION -> {
				if ( lost && tile.isMine() ) {
					drawTexture( batch, this.assets.pressedTile, x, y );
					drawTexture( batch, this.assets.mine, x, y );
				} else {
					drawTexture( batch, this.assets.tile, x, y );

					if ( tile.getState() == Tile.State.QUESTION ) {
						drawTexture( batch, this.assets.question, x, y );
					}
				}
			}
			case FLAGGED -> {
				if ( lost && !tile.isMine() ) {
					drawTexture( batch, this.assets.pressedTile, x, y );
					drawNumber( batch, tile.getAdjacentMines(), x, y );
					drawTexture( batch, this.assets.wrong, x, y );
				} else {
					drawTexture( batch, this.assets.tile, x, y );
					drawTexture( batch, this.assets.flag, x, y );
				}
			}
			case REVEALED -> {
				if ( tile.isMine() ) {
					batch.setColor( Color.RED );
					drawTexture( batch, this.assets.pressedTile, x, y );
					batch.setColor( Color.WHITE );
					drawTexture( batch, this.assets.mine, x, y );
				} else {
					drawTexture( batch, this.assets.pressedTile, x, y );
					drawNumber( batch, tile.getAdjacentMines(), x, y );
				}
			}
		}
	}

	private void drawTexture( SpriteBatch batch, Texture texture, float x, float y ) {
		batch.draw( texture, x, y, Main.TILE_SIZE, Main.TILE_SIZE );
	}

	private void drawNumber( SpriteBatch batch, int n, float x, float y ) {
		if ( n <= 0 ) {
			return;
		}

		GlyphLayout numberLayout = this.numberLayouts[n];
		this.font.draw( batch, numberLayout, x + ( Main.TILE_SIZE - numberLayout.width ) / 2f, y + ( Main.TILE_SIZE + numberLayout.height ) / 2f );
	}

	private void layout() {
		float boardW = this.board.getCols() * Main.TILE_SIZE;
		float boardH = this.board.getRows() * Main.TILE_SIZE;
		this.originX = ( this.areaWidth - boardW ) / 2f;
		this.originY = ( this.areaHeight - boardH ) / 2f;
	}
}
