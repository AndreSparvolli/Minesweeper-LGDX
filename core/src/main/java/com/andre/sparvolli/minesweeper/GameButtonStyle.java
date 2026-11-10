package com.andre.sparvolli.minesweeper;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Disposable;

/**
 * Estilo visual dos botões do jogo, no estilo dos botões do Minesweeper do Godot.
 * Deve ser criado uma única vez e compartilhado por todos os botões.
 *
 * O fundo (com borda e cantos arredondados) é gerado em código via Pixmap.
 * Os três estados (up, over, down) ficam lado a lado em uma única textura,
 * e cada um é usado como NinePatch, então o botão pode ter qualquer tamanho sem deformar os cantos.
 */
public class GameButtonStyle extends TextButtonStyle implements Disposable {

	// Cores de fundo e borda para cada estado (ajuste com o color picker se quiser mais fidelidade)
	// UP_FILL e UP_BORDER são visíveis no pacote porque o painel do campo (GameAssets) usa as mesmas cores
	static final Color UP_FILL     = Color.valueOf("3D3E4A");
	static final Color UP_BORDER   = Color.valueOf("555767");
	private static final Color OVER_FILL   = Color.valueOf("4A4B59");
	private static final Color OVER_BORDER = Color.valueOf("666879");
	private static final Color DOWN_FILL   = Color.valueOf("2C2D36");
	private static final Color DOWN_BORDER = Color.valueOf("454654");

	// Cores do texto para cada estado
	private static final Color FONT_COLOR      = Color.valueOf("E0E0E0");
	private static final Color OVER_FONT_COLOR = Color.valueOf("F2F2F2");
	private static final Color DOWN_FONT_COLOR = Color.valueOf("FFFFFF");

	// Raio dos cantos arredondados em pixels (o padrão do Godot é 3)
	private static final int CORNER_RADIUS = 3;
	// Tamanho de cada estado na textura: cantos + 1px de miolo esticável
	private static final int PATCH_SIZE = CORNER_RADIUS * 2 + 3;

	// Textura única com os três estados lado a lado: [ up | over | down ]
	private final Texture texture;

	/**
	 * @param font fonte usada no texto dos botões (o estilo não a libera; quem criou a fonte é responsável por isso)
	 */
	public GameButtonStyle( BitmapFont font ) {
		Pixmap pixmap = new Pixmap( PATCH_SIZE * 3, PATCH_SIZE, Pixmap.Format.RGBA8888 );
		drawPatch( pixmap, 0, UP_FILL, UP_BORDER );
		drawPatch( pixmap, 1, OVER_FILL, OVER_BORDER );
		drawPatch( pixmap, 2, DOWN_FILL, DOWN_BORDER );

		texture = new Texture( pixmap );
		pixmap.dispose();

		up   = createPatch( 0 );
		over = createPatch( 1 );
		down = createPatch( 2 );

		this.font = font;
		fontColor = FONT_COLOR;
		overFontColor = OVER_FONT_COLOR;
		downFontColor = DOWN_FONT_COLOR;
	}

	// Desenha o fundo de um estado na posição "index" da textura
	private static void drawPatch( Pixmap pixmap, int index, Color fill, Color border ) {
		int x = index * PATCH_SIZE;
		// Borda: retângulo arredondado completo; depois o preenchimento por cima, 1px para dentro
		PixmapUtils.fillRoundedRect( pixmap, x, 0, PATCH_SIZE, PATCH_SIZE, CORNER_RADIUS, border );
		PixmapUtils.fillRoundedRect( pixmap, x + 1, 1, PATCH_SIZE - 2, PATCH_SIZE - 2, CORNER_RADIUS - 1, fill );
	}

	// Cria o NinePatch de um estado a partir da sua região na textura
	private NinePatchDrawable createPatch( int index ) {
		TextureRegion region = new TextureRegion( texture, index * PATCH_SIZE, 0, PATCH_SIZE, PATCH_SIZE );
		// Os cantos (CORNER_RADIUS + 1 px) ficam fixos e apenas o miolo é esticado
		int margin = CORNER_RADIUS + 1;
		return new NinePatchDrawable( new NinePatch( region, margin, margin, margin, margin ) );
	}

	@Override
	public void dispose() {
		texture.dispose();
	}
}
