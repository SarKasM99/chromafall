package com.mygdx.chromafall;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.Iterator;
import java.util.Random;

public class GameScreen implements Screen {
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
	private Game game;
	private Screen screen;

	public GameScreen(Game gameObj, Screen screenObj) {
		//Setting up the camera and the world coordinates
        game = gameObj;
        screen = screenObj;

		camera = new OrthographicCamera();
		vpWidth = Gdx.graphics.getWidth();
		vpHeight = Gdx.graphics.getHeight();

		//Note: the x axis and the y axis do not have the same scale
		camera.setToOrtho(false,100, 100*(vpHeight/vpWidth)); //World defined by 100x100 grid
		//Note: we could technically just use the gameViewport to convert everything into our games coordinates
		//But doing it with the camera will bring the same results since the coordinates are defined the same way
		gameViewport = new FitViewport(100,100*(vpHeight/vpWidth),camera);
		screenViewport = new ScreenViewport(camera);

		//Creating Ball Object
		ballImg = new Texture("Circ_Deg8.png");
		float ballRadius = 8;		//The radius is defined in world coordinates
		ball = new Ball(camera.viewportWidth/2, camera.viewportHeight-15, ballRadius,ballImg);

		//Creating Obstacles object
		float obstacleWidth = 17.5f;
		float obstacleHeight = 17.5f;

		obstacles = new Array<>();
		spawnObstacle(obstacleWidth,obstacleHeight);

		batch = new SpriteBatch();

		//Setting up the score text
		float textSize = 4;
		pixelCoords.set(textSize,0,0);
		camera.project(pixelCoords);
		int textPixelSize = Math.round(pixelCoords.x);

		float borderSize = 0.5f;
		pixelCoords.set(borderSize,0,0);
		camera.project(pixelCoords);
		int borderPixelSize = Math.round(pixelCoords.x);

		//Font used: Copyright 2016 The Fredoka Project Authors (https://github.com/hafontia/Fredoka-One)
		generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/myFont.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.color = Color.WHITE;
		parameter.size = textPixelSize;
		//parameter.minFilter =  Texture.TextureFilter.Nearest;
		//parameter.magFilter = Texture.TextureFilter.Nearest;
		parameter.borderColor = Color.BLACK;
		parameter.borderWidth = borderPixelSize;

		font = generator.generateFont(parameter);


	}
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
	public void resize(int width, int height) {
		gameViewport.update(width, height);
		screenViewport.update(width, height);

	}

	/**
	 * @see ApplicationListener#pause()
	 */
	@Override
	public void pause() {

	}

	/**
	 * @see ApplicationListener#resume()
	 */
	@Override
	public void resume() {

	}

	/**
	 * Called when this screen is no longer the current screen for a {@link Game}.
	 */
	@Override
	public void hide() {

	}

	/**
	 * Called when the screen should render itself.
	 *
	 * @param delta The time in seconds since the last render.
	 */
	@Override
	public void render(float delta) {
		batch.begin();
		//Drawing everything in world coordinates
		gameViewport.apply();
		batch.setProjectionMatrix(gameViewport.getCamera().combined); //Will draw in worlds coordinate

		if(deathFlash){
			ScreenUtils.clear(Color.RED);
			if (deathFlashFrameCount <= 0) {
				deathFlash = false;
			}
			deathFlashFrameCount--;
		}

		else {
			ScreenUtils.clear(Color.GRAY);
			ball.draw(batch);
		}

		//camera.update();							//Will need to be used if we start to utilise the camera (maybe at a later stage)
		for(Obstacle obs : obstacles){
			obs.draw(batch);
		}


		//Drawing everything in pixel coordinates (for the score)
		screenViewport.apply();
		batch.setProjectionMatrix(screenViewport.getCamera().combined);
		TextureRegion fontRegion = font.getRegion();

		font.draw(batch,"Score: " + score,
				0,
				0);

		batch.end();
		gameViewport.apply();

		//Updating the Ball
		ball.update(camera);


		score++;

		//Updating the Obstacles
		if(TimeUtils.timeSinceMillis(lastSpawnTime) > 2000/acceleration){
			Random rand = new Random();
			float obstacleWidth = 17.5f + rand.nextInt(20);
			float obstacleHeight = 17.5f + rand.nextInt(20);
			spawnObstacle(obstacleWidth,obstacleHeight);
		}

		Iterator<Obstacle> iter = obstacles.iterator();

		while (iter.hasNext()) {
			Obstacle obstacle = iter.next();
			obstacle.update(acceleration);

			if (obstacle.getY() > camera.viewportHeight) {
				iter.remove();
				obstacle.dispose();
			}

			else if (Intersector.overlaps(ball.getHitbox(), obstacle.getHitbox())){
				acceleration = 0.0f;
				deathFlash = true;
				score = 0;
				deathFlashFrameCount = 10;
				obstacles = new Array<>();
				this.dispose();
				game.setScreen(screen);
			}
		}

		acceleration += 0.001;
	}

	@Override
	public void show() {
		// I don't know
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

