package com.wham.gamestates;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.wham.launcher.Launcher;

public class TitleState extends BasicGameState {
	private int stateID;

	// === UI objects === //
	private Rectangle playButtonBounds;
	private Image backgroundImage;
	private Image moleImage1;
	private Image moleImage2;
	private Image moleImage3;
	private Image moleImage4;

	// === Service objects === //
	private TrueTypeFont buttonFont;
	private TrueTypeFont rulesFont;
	private StateBasedGame game;

	// === UI data === //
	private int highScore = 0;
	private String highScoreString;
	private String[] rules = {
		"Moles appear on the screen randomly for a short period of time.",
		"Smash them as fast as you can, before they dissapear!",
		"The more you hit - the better!",
		"Have fun ;3"
	};

	// === UI parameters === //
	private float buttonX, buttonY, buttonWidth, buttonHeight;
	private boolean buttonHover = false;
	private float underlineWidth;
	private Color buttonBackgroundColor = new Color(1, 1, 1, 0.35f);

	public TitleState(int stateID) {
		this.stateID = stateID;
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		super.enter(container, game);
		try {
			List<String> scoresStrings = Files.readAllLines(Paths.get("records/whacamole.record"));
			highScore = Integer.parseInt(scoresStrings.get(0));
		} catch (IOException e) {
			e.printStackTrace();
		}
		highScoreString = "High Score: "+highScore*100;
	}

	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		buttonFont = loadFont("RampartOne-Regular", "rampart_one", "ttf", 32);
		rulesFont  = loadFont("AmaticSC-Bold", "amatic_sc", "ttf", 24);

		buttonHeight = buttonFont.getLineHeight();
		buttonWidth = buttonFont.getWidth("PLAY");
		buttonX = (container.getWidth() - buttonWidth) / 2;
		buttonY = (container.getHeight() - buttonHeight) / 2;

		playButtonBounds = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
		underlineWidth = 0;

		//нет смысла использовать/создавать к-л загрузчик ресурсов так как игра очень мала и расширяться не будет
		backgroundImage = new Image("res/backgrounds/title_background.jpg");
		moleImage1 = new Image("res/sprites/mole.png");
		moleImage1 = moleImage1.getScaledCopy(0.5f);
		moleImage1.rotate(-30);

		moleImage2 = moleImage1.getScaledCopy(0.7f);
		moleImage2.rotate(180);

		moleImage3 = moleImage1.getScaledCopy(0.45f);
		moleImage3 = moleImage3.getFlippedCopy(true, false);
		moleImage3.rotate(16);

		moleImage4 = moleImage1.getScaledCopy(0.25f);

		container.setDefaultFont(rulesFont);
		this.game = game;
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		g.setAntiAlias(true);
		g.setColor(Color.black);
		backgroundImage.draw(0, 0, container.getWidth(), container.getHeight());
		if(buttonHover) {
			underlineWidth = underlineWidth < buttonWidth ? underlineWidth+=7: buttonWidth;
			g.setColor(buttonBackgroundColor);
			g.fillRoundRect(buttonX, buttonY, underlineWidth, buttonHeight, 5);
			g.setColor(Color.black);
			g.drawLine(buttonX, buttonY+buttonHeight, buttonX+underlineWidth, buttonY+buttonHeight);
		} else underlineWidth = 0;

		if(buttonFont != null) g.setFont(buttonFont);
		g.drawString("PLAY", buttonX, buttonY);

		if(rulesFont != null) g.setFont(rulesFont);
		else g.resetFont();
		for(int i = 0; i < rules.length; i++) {
			String line = rules[i];
			g.drawString(line, (container.getWidth()-rulesFont.getWidth(line))/2, buttonY+buttonHeight + 50 + i*rulesFont.getLineHeight());
		}

		float highScoreLabelCenter = (container.getWidth()-rulesFont.getWidth(highScoreString))/2;
		g.drawString(highScoreString, highScoreLabelCenter, 150);

		moleImage1.draw(container.getWidth()-moleImage1.getWidth()/4*3, container.getHeight()-moleImage1.getHeight()/4*3);
		moleImage2.draw(-moleImage2.getWidth()/3, -moleImage2.getHeight()/4);
		moleImage3.draw(-moleImage3.getWidth()/3, container.getHeight()-moleImage3.getHeight()/3*2);
		moleImage4.draw(container.getWidth()/4*3, container.getHeight()/3);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		//nothing to update all content is static
	}

	@Override
	public int getID() {
		return stateID;
	}

	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		super.mouseMoved(oldx, oldy, newx, newy);
		if(playButtonBounds.contains(newx, newy)) {
			buttonHover = true;
		} else buttonHover = false;
	}

	@Override
	public void mouseClicked(int button, int x, int y, int clickCount) {
		super.mouseClicked(button, x, y, clickCount);
		if(button == Input.MOUSE_LEFT_BUTTON && playButtonBounds.contains(x, y)) {
			game.enterState(Launcher.STATE_GAME);// TODO: написать входной и выходной Transition
		}
	}

	private TrueTypeFont loadFont(String name, String folderName, String ext, float size) {
		TrueTypeFont trueTypeFont = null;
		try {
			Font awtFont = Font.createFont(Font.TRUETYPE_FONT, new File("res/fonts/"+folderName+"/"+name+"."+ext)).deriveFont(size);
			trueTypeFont = new TrueTypeFont(awtFont, true);
		} catch (FontFormatException | IOException e) {
			System.err.println("Cannot load font '"+name+"' sorry :(");
		}
		return trueTypeFont;
	}
}
