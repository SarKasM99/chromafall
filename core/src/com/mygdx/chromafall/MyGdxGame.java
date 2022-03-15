package com.mygdx.chromafall;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
//import com.badlogic.gdx.Game;

import java.awt.Menu;
import java.util.Iterator;

public class MyGdxGame extends Game {
	private SpriteBatch batch;
	private OrthographicCamera camera;

	private Ball ball;
	private Texture ballImg;
	private Array<Obstacle> obstacles;
	private long lastSpawnTime;

	private float vpHeight;
	private float vpWidth;
	private Vector3 pixelCoords = new Vector3();

	private FreeTypeFontGenerator generator;
	private BitmapFont font;					//Allows us to draw text

	private FitViewport gameViewport;
	private ScreenViewport screenViewport;
	private float acceleration = 0;
	int score = 0;
	private boolean deathFlash = false;
	private int deathFlashFrameCount = 10;

	private void spawnObstacle(float width, float height){
		float x = MathUtils.random(0,camera.viewportWidth-width);
		float y = -width;	//Let it appear below the screen

		pixelCoords.set(width,0,0);
		camera.project(pixelCoords);
		int pixWidth = Math.round(pixelCoords.x);

		pixelCoords.set(height,0,0);
		camera.project(pixelCoords);
		int pixHeight = Math.round(pixelCoords.x);

		Obstacle obstacle = new Obstacle(x, y, width, height, pixWidth,pixHeight);
		obstacles.add(obstacle);
		lastSpawnTime = TimeUtils.millis();
	}

	@Override
	public void create() {
		//Setting up the camera and the world coordinates
		camera = new OrthographicCamera();
		vpWidth = Gdx.graphics.getWidth();
		vpHeight = Gdx.graphics.getHeight();
		this.setScreen(new MenuScreen());

	}
	@Override
	public void render() {
	    super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		ballImg.dispose();
		font.dispose();
		generator.dispose();

		for(Obstacle obs : obstacles){
			obs.dispose();
		}
	}
}
