package com.andre.sparvolli.minesweeper;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main implements ApplicationListener {
	private static final Color BG_COLOR = Color.valueOf( "4C4C4C" );
	public static final int TILE_SIZE = 32;

	private static final float WORLD_WIDTH = 1000;
	private static final float WORLD_HEIGHT = 800;

	// Barra de status no rodapé
	private static final float STATUS_BAR_X = 50;
	private static final float STATUS_BAR_Y = 16;
	private static final float STATUS_BAR_WIDTH = WORLD_WIDTH - STATUS_BAR_X * 2;
	private static final float STATUS_BAR_HEIGHT = 58;

	// Os botões começam em y = 716; o campo é centralizado entre a barra de status e os botões
	private static final float BUTTONS_Y = 716;
	private static final float BOARD_AREA_Y = STATUS_BAR_Y + STATUS_BAR_HEIGHT;
	private static final float BOARD_AREA_HEIGHT = BUTTONS_Y - BOARD_AREA_Y;

	private FitViewport viewport;
	private SpriteBatch spriteBatch;
	private Stage stage;

	private BitmapFont buttonFont;
	private BitmapFont numberFont;
	private BitmapFont statusFont;
	private GameButtonStyle buttonStyle;
	private GameButton btnBeginner, btnIntermediate, btnAdvanced;

	private GameAssets gameAssets;
	private Board board;
	private BoardRenderer boardRenderer;
	private BoardInput boardInput;
	private StatusBar statusBar;

	@Override
	public void create() {
		this.spriteBatch = new SpriteBatch();
		this.viewport = new FitViewport( WORLD_WIDTH, WORLD_HEIGHT );

		this.gameAssets = new GameAssets();

		initFonts();
		initButtons();

		this.boardRenderer = new BoardRenderer( this.gameAssets, this.numberFont, 0, BOARD_AREA_Y, WORLD_WIDTH, BOARD_AREA_HEIGHT );
		this.boardInput = new BoardInput( this.viewport, this.boardRenderer );
		this.statusBar = new StatusBar( this.gameAssets, this.statusFont, STATUS_BAR_X, STATUS_BAR_Y, STATUS_BAR_WIDTH, STATUS_BAR_HEIGHT );

		this.stage = new Stage( this.viewport, this.spriteBatch );
		this.stage.addActor( this.btnBeginner );
		this.stage.addActor( this.btnIntermediate );
		this.stage.addActor( this.btnAdvanced );

		Gdx.input.setInputProcessor( new InputMultiplexer( this.stage, this.boardInput ) );

		startGame( 9, 9, 10 );
	}

	@Override
	public void resize( int width, int height ) {
		if ( width <= 0 || height <= 0 ) {
			return;
		}

		this.viewport.update( width, height, true );
	}

	@Override
	public void render() {
		ScreenUtils.clear( BG_COLOR );
		this.viewport.apply();
		this.spriteBatch.setProjectionMatrix( this.viewport.getCamera().combined );
		this.stage.act( Gdx.graphics.getDeltaTime() );
		this.statusBar.update();

		this.spriteBatch.begin();
		this.boardRenderer.draw( this.spriteBatch );
		this.statusBar.draw( this.spriteBatch );
		this.spriteBatch.end();

		this.stage.draw();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		this.stage.dispose();
		this.buttonStyle.dispose();
		this.buttonFont.dispose();
		this.numberFont.dispose();
		this.statusFont.dispose();
		this.gameAssets.dispose();
		this.spriteBatch.dispose();
	}

	private void initButtons() {
		this.buttonStyle = new GameButtonStyle( this.buttonFont );
		this.btnBeginner = new GameButton( "9 X 9", this.buttonStyle, 137, BUTTONS_Y, 150, 66, () -> startGame( 9, 9, 10 ) );
		this.btnIntermediate = new GameButton( "16 X 16", this.buttonStyle, 423, BUTTONS_Y, 150, 66, () -> startGame( 16, 16, 40 ) );
		this.btnAdvanced = new GameButton( "30 X 16", this.buttonStyle, 709, BUTTONS_Y, 150, 66, () -> startGame( 16, 30, 99 ) );
	}

	private void initFonts() {
		FreeTypeFontGenerator gen = new FreeTypeFontGenerator( Gdx.files.internal( "Font.ttf" ) );
		FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();

		p.size = 32;
		p.minFilter = Texture.TextureFilter.Linear;
		p.magFilter = Texture.TextureFilter.Linear;
		this.buttonFont = gen.generateFont( p );

		p.size = 22;
		this.numberFont = gen.generateFont( p );

		p.size = 28;
		this.statusFont = gen.generateFont( p );

		gen.dispose();
	}

	private void startGame( int rows, int cols, int mines ) {
		this.board = new Board( rows, cols, mines );
		this.boardRenderer.setBoard( this.board );
		this.boardInput.setBoard( this.board );
		this.statusBar.setBoard( this.board );
	}
}
