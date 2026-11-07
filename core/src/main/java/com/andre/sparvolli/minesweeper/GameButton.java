package com.andre.sparvolli.minesweeper;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/**
 * Botão de texto do jogo.
 * Cuida apenas do comportamento (texto, posição, clique); o visual vem de um estilo
 * compartilhado (ex: {@link GameButtonStyle}), criado uma vez e passado para todos os botões.
 */
public class GameButton extends TextButton {

	/**
	 * @param text    texto do botão, ex: "9 X 9"
	 * @param style   estilo compartilhado (o botão não o libera; quem criou o estilo é responsável por isso)
	 * @param x       posição x do canto inferior esquerdo, em unidades do viewport
	 * @param y       posição y do canto inferior esquerdo, em unidades do viewport
	 * @param width   largura do botão
	 * @param height  altura do botão
	 * @param onClick ação executada quando o botão é clicado (ao soltar o mouse, como o button_up do Godot)
	 */
	public GameButton( String text, TextButtonStyle style, float x, float y, float width, float height, Runnable onClick ) {
		super( text, style );

		setBounds( x, y, width, height );

		if ( onClick != null ) {
			addListener( new ChangeListener() {
				@Override
				public void changed( ChangeEvent event, Actor actor ) {
					onClick.run();
				}
			} );
		}
	}
}
