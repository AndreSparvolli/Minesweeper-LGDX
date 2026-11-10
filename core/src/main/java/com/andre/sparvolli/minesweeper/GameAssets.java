package com.andre.sparvolli.minesweeper;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.utils.Disposable;

public class GameAssets implements Disposable {

	// Geometria do painel: mesmos cantos dos botões (raio 3, 1 px de miolo esticável)
	private static final int PANEL_CORNER_RADIUS = 3;
	private static final int PANEL_PATCH_SIZE = PANEL_CORNER_RADIUS * 2 + 3;
	private static final int PANEL_MARGIN = PANEL_CORNER_RADIUS + 1;

	public final Texture tile;
	public final Texture pressedTile;
	public final Texture flag;
	public final Texture question;
	public final Texture wrong;
	public final Texture mine;

	/** Fundo do campo; desenhado com qualquer tamanho sem deformar os cantos. */
	public final NinePatch panel;
	private final Texture panelTexture;

	public GameAssets() {
		this.tile = new Texture( "tile.png" );
		this.pressedTile = new Texture( "ptile.png" );
		this.flag = new Texture( "flag.png" );
		this.question = new Texture( "qmark.png" );
		this.wrong = new Texture( "wrong.png" );
		this.mine = new Texture( "mine.png" );

		this.panelTexture = createPanelTexture();
		this.panel = new NinePatch( this.panelTexture, PANEL_MARGIN, PANEL_MARGIN, PANEL_MARGIN, PANEL_MARGIN );
	}

	// Mesmas cores do estado normal (up) dos botões: borda por fora, preenchimento 1 px para dentro
	private static Texture createPanelTexture() {
		Pixmap pixmap = new Pixmap( PANEL_PATCH_SIZE, PANEL_PATCH_SIZE, Pixmap.Format.RGBA8888 );
		PixmapUtils.fillRoundedRect( pixmap, 0, 0, PANEL_PATCH_SIZE, PANEL_PATCH_SIZE, PANEL_CORNER_RADIUS, GameButtonStyle.UP_BORDER );
		PixmapUtils.fillRoundedRect( pixmap, 1, 1, PANEL_PATCH_SIZE - 2, PANEL_PATCH_SIZE - 2, PANEL_CORNER_RADIUS - 1, GameButtonStyle.UP_FILL );

		Texture texture = new Texture( pixmap );
		pixmap.dispose();
		return texture;
	}

	@Override
	public void dispose() {
		this.tile.dispose();
		this.pressedTile.dispose();
		this.flag.dispose();
		this.question.dispose();
		this.wrong.dispose();
		this.mine.dispose();
		this.panelTexture.dispose();
	}
}
