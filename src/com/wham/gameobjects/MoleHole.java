package com.wham.gameobjects;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.util.InputAdapter;

public class MoleHole extends InputAdapter {

	private long activationTime;
	private int x, y, width, height;
	private long animationDuration = 500; 
	private Rectangle bounds;
	private boolean active;
	private boolean whacked = false;
	//нужно заблокировать лунку до следующего крота, чтобы нельзя было на одном набить несколько очков
	private boolean locked = false;	
	private long whackedTime = 0;
	private long whackedEffectDuration = 250;
	
	private Animation moleAnimation;

	public MoleHole(int x, int y, int width, int height) throws SlickException {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		bounds = new Rectangle(x, y, width, height);
		
		SpriteSheet moleAnimationSpritesheet = new SpriteSheet("res/sprites/animation/mole-show-animation.png", 700, 650);
		int spritesCount = moleAnimationSpritesheet.getHorizontalCount() * moleAnimationSpritesheet.getVerticalCount();
		moleAnimation = new Animation(moleAnimationSpritesheet, (int) (animationDuration / spritesCount));
		moleAnimation.setLooping(false);
		moleAnimation.setPingPong(true);
		moleAnimation.stop();
	}

	public void render(Graphics g) throws SlickException {
		if(System.currentTimeMillis()-whackedTime<whackedEffectDuration) {
			moleAnimation.drawFlash(x, y, width, height);
		} else {
			moleAnimation.draw(x, y, width, height);
		}
	}

	public void update(int delta) throws SlickException {
		moleAnimation.update(delta);
		if(System.currentTimeMillis() - activationTime > animationDuration) {
			active = false;
			locked = false;
		}	
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean isActive) {
		active = isActive;
		if(active) {
			moleAnimation.start();
			activationTime = System.currentTimeMillis();
		}
	}
	
	/** время анимации и кликабельности в миллисекундах */
	public void setAnimationDuration(long timeMillis) {
		animationDuration = timeMillis;
		for(int i = 0; i < moleAnimation.getFrameCount(); i++)
			moleAnimation.setDuration(i, (int) (animationDuration/moleAnimation.getFrameCount()));
	}
	
	/** время анимации и кликабельности в миллисекундах */
	public long getAnimationDuration() {
		return animationDuration;
	}
	
	@Override
	public void mousePressed(int button, int x, int y) {
		super.mousePressed(button, x, y);
		if (bounds.contains(x, y) && active && !locked) {
			whacked = true;
			locked = true;
			whackedTime = System.currentTimeMillis();
		}
	}

	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public boolean isWhacked() {
		return whacked;
	}
	
	public void setWhacked(boolean whacked) {
		this.whacked = whacked;
	}
}
