package com.andre.sparvolli.minesweeper;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;

/**
 * Funções de desenho em {@link Pixmap} compartilhadas pelos elementos de UI gerados em código
 * (botões, painel do campo).
 */
public final class PixmapUtils {

	private PixmapUtils() {
	}

	/**
	 * Preenche um retângulo com cantos arredondados.
	 *
	 * @param radius raio dos cantos em pixels; 0 ou menos desenha um retângulo comum
	 */
	public static void fillRoundedRect( Pixmap pixmap, int x, int y, int width, int height, int radius, Color color ) {
		pixmap.setColor( color );

		if ( radius <= 0 ) {
			pixmap.fillRectangle( x, y, width, height );
			return;
		}

		// Faixas horizontal e vertical em cruz
		pixmap.fillRectangle( x + radius, y, width - radius * 2, height );
		pixmap.fillRectangle( x, y + radius, width, height - radius * 2 );

		// Um círculo em cada canto
		int left = x + radius;
		int right = x + width - 1 - radius;
		int top = y + radius;
		int bottom = y + height - 1 - radius;
		pixmap.fillCircle( left, top, radius );
		pixmap.fillCircle( right, top, radius );
		pixmap.fillCircle( left, bottom, radius );
		pixmap.fillCircle( right, bottom, radius );
	}
}
