package com.andre.sparvolli.minesweeper;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

public class BoardInput extends InputAdapter {
	private final Viewport viewport;
	private final BoardRenderer renderer;
	private Board board;

	private final Vector2 touch = new Vector2();
	private final GridPoint2 cell = new GridPoint2();

	public BoardInput( Viewport viewport, BoardRenderer renderer ) {
		this.viewport = viewport;
		this.renderer = renderer;
	}

	@Override
	public boolean touchUp( int screenX, int screenY, int pointer, int button ) {
		if ( this.board == null ) {
			return false;
		}

		this.viewport.unproject( this.touch.set( screenX, screenY ) );

		if ( !this.renderer.toCell( this.touch.x, this.touch.y, this.cell ) ) {
			return false;
		}

		if ( button == Input.Buttons.LEFT ) {
			this.board.reveal( this.cell.y, this.cell.x );
		}

		if ( button == Input.Buttons.RIGHT ) {
			this.board.cycleMark( this.cell.y, this.cell.x );
		}

		return true;
	}

	public void setBoard( Board board ) {
		this.board = board;
	}
}
