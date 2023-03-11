package com.wham.launcher;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import com.wham.gamestates.GameState;
import com.wham.gamestates.RecordState;
import com.wham.gamestates.TitleState;

public class Launcher extends StateBasedGame {

	public static final int STATE_TITLE = 0;
	public static final int STATE_GAME = 1;
	public static final int STATE_RECORD = 2;

	public Launcher(String name) {
		super(name);
	}


	/*
	 * Код практически платформонезависим.
	 * Данный лаунчер компьютерный только потому, что тестировать удобнее на пк.
	 * */
	public static void main(String[] args) throws SlickException {
		StateBasedGame game = new Launcher("Whac-a-Mole");
		AppGameContainer container = new AppGameContainer(game, 800, 600, false);
		container.setTargetFrameRate(30);
		container.setVSync(true);
		container.start();
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		this.addState(new TitleState(STATE_TITLE));
		this.addState(new GameState(STATE_GAME));
		this.addState(new RecordState(STATE_RECORD));
		enterState(STATE_TITLE);
	}

}
