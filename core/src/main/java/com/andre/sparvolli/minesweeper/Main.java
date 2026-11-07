package com.andre.sparvolli.minesweeper;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main implements ApplicationListener {
	private static final Color BG_COLOR = Color.valueOf("4C4C4C");
	public static final int TILE_SIZE = 32;

    private SpriteBatch spriteBatch;
    private FitViewport viewport;

    private BitmapFont buttonFont;
    private GameButtonStyle buttonStyle;
    private GameButton btnBeginner, btnIntermediate, btnAdvanced;

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(1000, 800);

        initButtons();
    }

    @Override
    public void resize(int width, int height) {
        if ( width <= 0 || height <= 0 ) {
        	return;
        }

        viewport.update( width, height, true );
    }

    @Override
    public void render() {
        draw();
    }

    private void draw() {
		ScreenUtils.clear( BG_COLOR );		
		viewport.apply();
		spriteBatch.setProjectionMatrix( viewport.getCamera().combined );

		spriteBatch.begin();

		btnBeginner.draw( spriteBatch, 1f );
		btnIntermediate.draw( spriteBatch, 1f );
		btnAdvanced.draw( spriteBatch, 1f );

		spriteBatch.end();
	}

	@Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    	buttonStyle.dispose();
    	buttonFont.dispose();
    	spriteBatch.dispose();
    }

	private void initButtons() {
		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("Font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();
        p.size = 32;
        p.minFilter = Texture.TextureFilter.Linear;
        p.magFilter = Texture.TextureFilter.Linear;
        buttonFont = gen.generateFont(p);
        gen.dispose();

        buttonStyle = new GameButtonStyle( buttonFont );
        btnBeginner = new GameButton( "9 X 9", buttonStyle, 137, 716, 150, 66, () -> { /* iniciar 9x9 */ } );
        btnIntermediate = new GameButton( "16 X 16", buttonStyle, 423, 716, 150, 66, () -> { /* iniciar 16x16 */ } );
        btnAdvanced = new GameButton( "30 X 16", buttonStyle, 709, 716, 150, 66, () -> { /* iniciar 30x16 */ } );
	}
}