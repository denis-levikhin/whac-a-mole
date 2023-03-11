package com.wham.gamestates;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.wham.launcher.Launcher;

public class RecordState extends BasicGameState {
	private int stateID;

	private int overallRecord = 0, lastRecord = 0;
	private String overallRecordString, lastRecordString, newRecordString;
	
	private float overallX, overallY, overallWidth;
	private float lastX, lastY, lastWidth;
	private float newRecordX, newRecordY, newRecordWidth;
	
	private float buttonX = 0, buttonY = 0, buttonWidth = 200, buttonHeight = 40;
	
	private List<String> scoresStrings;
	private List<Integer> scores;
	
	private Rectangle playAgainButton, menuButton;
	
	private StateBasedGame game;
	
	public RecordState(int stateID) {
		this.stateID = stateID;
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		super.enter(container, game);
		try { 
			scores = new LinkedList<>();
			scoresStrings = Files.readAllLines(Paths.get("records/whacamole.record"));
			for(String score: scoresStrings)
				if(!score.isEmpty())scores.add(Integer.parseInt(score));
			overallRecord = scores.get(0);
			lastRecord = scores.get(1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		overallRecordString = "Best score: "+overallRecord*100;
		lastRecordString = "Your score: "+lastRecord*100;
		newRecordString = "NEW BEST RECORD!";

		// можно создать класс-структуру и сделать эти переменные его полями, но для всего трёх строк оно того не стоит
		// проще просто потерпеть немножко дублирования кода и отрефакторить по мере надобности
		overallWidth = container.getDefaultFont().getWidth(overallRecordString);
		overallX = (container.getWidth() - overallWidth)/2;
		overallY = container.getHeight()/2 - 30; 
		
		lastWidth = container.getDefaultFont().getWidth(lastRecordString);
		lastX = (container.getWidth() - lastWidth)/2;
		lastY = container.getHeight()/2;
		
		newRecordWidth = container.getDefaultFont().getWidth(newRecordString);
		newRecordX = (container.getWidth() - newRecordWidth)/2;
		newRecordY = container.getHeight()/2 + 30; 
		
		buttonX = (container.getWidth() - buttonWidth)/2;
		buttonY = newRecordY + 50;
		
		playAgainButton = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
		menuButton = new Rectangle(buttonX, buttonY+50, buttonWidth, buttonHeight);
		
		this.game = game;
	}
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {

	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		g.setColor(Color.white);
		if(g.getFont() != container.getDefaultFont())
			g.setFont(container.getDefaultFont());
		
		g.drawString(overallRecordString, overallX, overallY);
		g.drawString(lastRecordString, lastX, lastY);
		if(lastRecord > overallRecord) {
			g.drawString(newRecordString, newRecordX, newRecordY);
		}

		g.draw(menuButton);
		g.draw(playAgainButton);
		
		float menuStringX = buttonX+(buttonWidth-g.getFont().getWidth("MENU"))/2;
		float playStringX = buttonX+(buttonWidth-g.getFont().getWidth("PLAY AGAIN"))/2;
		float stringY = buttonY+(buttonHeight-g.getFont().getLineHeight())/2;
		g.drawString("PLAY AGAIN", playStringX, stringY);
		g.drawString("MENU", menuStringX, stringY+50);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		super.leave(container, game);
		try {
			scoresStrings.set(0, (overallRecord > lastRecord ? overallRecord : lastRecord) + ""); 
	        Files.write(Paths.get("records/whacamole.record"), scoresStrings, StandardCharsets.UTF_8, StandardOpenOption.WRITE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void mouseClicked(int button, int x, int y, int clickCount) {
		super.mouseClicked(button, x, y, clickCount);
		if(playAgainButton.contains(x, y)) 
			game.enterState(Launcher.STATE_GAME);
		else if(menuButton.contains(x, y)) 
			game.enterState(Launcher.STATE_TITLE);
	}
	
	@Override
	public int getID() {
		return stateID;
	}

}
