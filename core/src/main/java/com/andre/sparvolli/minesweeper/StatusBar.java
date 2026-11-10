package com.andre.sparvolli.minesweeper;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

/**
 * Barra de status no rodapé: contador de flags restantes, total de minas e a mensagem da partida.
 * Como o BoardRenderer, só lê o Board; quem decide as regras continua sendo o Board.
 */
public class StatusBar {

	private static final String MESSAGE_WON = "You won! start a new game on the buttons above.";
	private static final String MESSAGE_LOST = "You lost, start a new game on the buttons above.";

	private static final float PADDING = 14f;
	private static final float ICON_SIZE = 24f;
	private static final float ICON_TEXT_GAP = 8f;
	// Espaço fixo para o número de cada contador, para o ícone da mina não "pular" quando o valor muda (ex.: 10 -> 9 -> -1)
	private static final float COUNTER_TEXT_WIDTH = 34f;
	// Onde começa a área da mensagem, a partir da esquerda da barra (depois dos dois contadores)
	private static final float MESSAGE_AREA_LEFT = 150f;

	private final GameAssets assets;
	private final BitmapFont font;
	private final float x;
	private final float y;
	private final float width;
	private final float height;

	// Layouts reaproveitados: o texto só é recalculado quando o valor muda, não a cada frame
	private final GlyphLayout flagsLayout = new GlyphLayout();
	private final GlyphLayout minesLayout = new GlyphLayout();
	private final GlyphLayout messageLayout = new GlyphLayout();

	private Board board;
	private Board.Status lastStatus;
	private int lastRemainingMines;

	public StatusBar( GameAssets assets, BitmapFont font, float x, float y, float width, float height ) {
		this.assets = assets;
		this.font = font;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public void setBoard( Board board ) {
		this.board = board;
		this.lastStatus = board.getStatus();
		this.lastRemainingMines = board.getRemainingMines();

		this.flagsLayout.setText( this.font, String.valueOf( this.lastRemainingMines ) );
		this.minesLayout.setText( this.font, String.valueOf( board.getMineCount() ) );
		setMessage( "Started new " + board.getCols() + "x" + board.getRows() + " game with " + board.getMineCount() + " mines" );
	}

	/** Chamado a cada frame: atualiza os textos só quando o estado do Board mudou. */
	public void update() {
		if ( this.board == null ) {
			return;
		}

		int remainingMines = this.board.getRemainingMines();

		if ( remainingMines != this.lastRemainingMines ) {
			this.lastRemainingMines = remainingMines;
			this.flagsLayout.setText( this.font, String.valueOf( remainingMines ) );
		}

		Board.Status status = this.board.getStatus();

		if ( status != this.lastStatus ) {
			this.lastStatus = status;

			if ( status == Board.Status.WON ) {
				setMessage( MESSAGE_WON );
			} else if ( status == Board.Status.LOST ) {
				setMessage( MESSAGE_LOST );
			}
		}
	}

	public void draw( SpriteBatch batch ) {
		this.assets.panel.draw( batch, this.x, this.y, this.width, this.height );

		if ( this.board == null ) {
			return;
		}

		float centerY = this.y + this.height / 2f;
		float cursorX = this.x + PADDING;

		cursorX = drawCounter( batch, this.assets.flag, this.flagsLayout, cursorX, centerY );
		drawCounter( batch, this.assets.mine, this.minesLayout, cursorX, centerY );

		// Mensagem centralizada na área à direita dos contadores
		float messageX = this.x + MESSAGE_AREA_LEFT;
		this.font.draw( batch, this.messageLayout, messageX, centerY + this.messageLayout.height / 2f );
	}

	// Desenha ícone + número e devolve o x onde o próximo elemento começa
	private float drawCounter( SpriteBatch batch, Texture icon,GlyphLayout textLayout, float startX, float centerY ) {
		batch.draw( icon, startX, centerY - ICON_SIZE / 2f, ICON_SIZE, ICON_SIZE );

		float textX = startX + ICON_SIZE + ICON_TEXT_GAP;
		this.font.draw( batch, textLayout, textX, centerY + textLayout.height / 2f );

		return textX + COUNTER_TEXT_WIDTH + PADDING;
	}

	private void setMessage( String message ) {
		float messageWidth = this.width - MESSAGE_AREA_LEFT - PADDING;
		this.messageLayout.setText( this.font, message, Color.WHITE, messageWidth, Align.center, false );
	}
}
