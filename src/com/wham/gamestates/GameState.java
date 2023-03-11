package com.wham.gamestates;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.wham.gameobjects.MoleHole;
import com.wham.launcher.Launcher;

public class GameState extends BasicGameState {
	private int stateID;

	// === Game data === //
	private final int HOLES_COUNT = 9;
	private final int HOLES_SIZE = 100;
	
	int points = 0;
	String pointsString;
	long timerStarted; // в миллисекундах
	long maxTime = 30; // в секундах
	Image background;
	MoleHole[] holes = new MoleHole[HOLES_COUNT];
	
	// === Service objects === //
	Image hammerCoursor;

	public GameState(int stateID) {
		this.stateID = stateID;
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		super.enter(container, game);
		timerStarted = System.currentTimeMillis();
		points = 0;
		pointsString = "Points: "+points; //0*100=0 why bother
		container.setMouseCursor(hammerCoursor, 0, 0);
	}

	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		background = new Image("res/backgrounds/game_background.jpg");
		for(int i = 0, j = 0; i < HOLES_COUNT; i++, j = i / 3) {
			holes[i] = new MoleHole(200+(HOLES_SIZE+10)*(i%3), 250+(HOLES_SIZE+10)*j, HOLES_SIZE, HOLES_SIZE);
			container.getInput().addListener(holes[i]);
			holes[i].setAnimationDuration(500);
		}
		
		hammerCoursor = new Image("res/sprites/hammer.png").getFlippedCopy(true, false);
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		if(g.getFont() != container.getDefaultFont())
			g.setFont(container.getDefaultFont());
		
		background.draw(0, 0, container.getWidth(), container.getHeight());
		for(MoleHole hole: holes) hole.render(g);

		int timeElapsed = (int)((System.currentTimeMillis() - timerStarted)/1000);
		int lineX = container.getWidth()-150; //оставим побольше места, потому не Font().getWidth
		int lineY = container.getHeight();
		g.drawString(pointsString, lineX, lineY - g.getFont().getLineHeight() * 2 - 10);
		g.drawString("Time left: "+(maxTime - timeElapsed), lineX, lineY - g.getFont().getLineHeight() - 10);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		//Случайно генерируем кротов
		if((int)(Math.random()*30) == 0)
			holes[(int)(Math.random() * 9)].setActive(true);
		
		for(MoleHole hole: holes) {
			hole.update(delta);
			if(hole.isWhacked()) {
				points++;
				pointsString = "Points: "+points*100; //*100 для солидности; 2-3 очка выглядят жалко, а вот 200-300 уже нормально
				hole.setWhacked(false);
			}
		}

		
		if(System.currentTimeMillis() - timerStarted > maxTime*1000) {
			game.enterState(Launcher.STATE_RECORD);
		}
	}
	
	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		super.leave(container, game);
		
		try {
			//можно было бы использовать XML или JSON но зачем - нас интересует ровно одно число..
			//в том числе из за этого можно не бояться проблем с кодировками
			List<String> scoresStrings;
			scoresStrings = Files.readAllLines(Paths.get("records/whacamole.record"));
			//list.get(0) - это рекорд за всё время, а list.get(1) - результат последней игры
			scoresStrings.set(1, points+""); 
	        Files.write(Paths.get("records/whacamole.record"), scoresStrings, StandardCharsets.UTF_8, StandardOpenOption.WRITE);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		container.setDefaultMouseCursor();
	}

	@Override
	public int getID() {
		return stateID;
	}

}
