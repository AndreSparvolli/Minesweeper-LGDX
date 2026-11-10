package com.andre.sparvolli.minesweeper;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;

public class GameAssets implements Disposable {

	public final Texture tile;
	public final Texture pressedTile;
	public final Texture flag;
	public final Texture question;
	public final Texture wrong;
	public final Texture mine;

    public GameAssets() {
        tile = new Texture( "tile.png" );
        pressedTile = new Texture( "ptile.png" );
        flag = new Texture( "flag.png" );
        question = new Texture( "qmark.png" );
        wrong = new Texture( "wrong.png" );
        mine = new Texture( "mine.png" );
    }

    @Override
    public void dispose() {
    	tile.dispose();
    	pressedTile.dispose();
    	flag.dispose();
    	question.dispose();
    	wrong.dispose();
    	mine.dispose();
    }
}
