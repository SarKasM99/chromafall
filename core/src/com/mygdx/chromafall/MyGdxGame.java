package com.mygdx.chromafall;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

public class MyGdxGame extends ApplicationAdapter {
	private SpriteBatch batch;
	private OrthographicCamera camera;


	private Ball ball;
	private Texture ballImg;
	private Array<Obstacle> obstacles;
	private long lastSpawnTime;

	private float vpHeight;
	private float vpWidth;
	private Vector3 pixelCoords = new Vector3();
	private float acceleration = 0;

	private void spawnObstacle(Vector3 pixelCoords){
		float width = pixelCoords.x;
		//float height = pixelCoords.y;
		float x = MathUtils.random(0,camera.viewportWidth-width);
		float y = -width;	//Let it appear below the screen

		Obstacle obstacle = new Obstacle(x, y, width, width, 128,128); //TODO: Find a better solution
		obstacles.add(obstacle);
		lastSpawnTime = TimeUtils.millis();
	}

	@Override
	public void create () {
		//Setting up the camera and the world coordinates
		camera = new OrthographicCamera();
		vpWidth = Gdx.graphics.getWidth();
		vpHeight = Gdx.graphics.getHeight();

		//Note: the x axis and the y axis do not have the same scale
		camera.setToOrtho(false,100, 100*(vpHeight/vpWidth)); //World defined by 100x100 grid

		//Creating Ball Object
		ballImg = new Texture("Circ_Deg8.png");
		pixelCoords.set(64,0,0);					//Translating the radius to coordinate worlds
		camera.unproject(pixelCoords);
		ball = new Ball(camera.viewportWidth/2,camera.viewportHeight/2,pixelCoords.x,ballImg);

		//Creating Obstacles object
		pixelCoords.set(128,0,0);
		camera.unproject(pixelCoords);					   //Translating width and height to world coordinates
		obstacles = new Array<Obstacle>();
		spawnObstacle(pixelCoords);

		batch = new SpriteBatch();
	}

	@Override
	public void render () {
		ScreenUtils.clear(Color.GRAY);
		//camera.update();							//Will need to be used if we start to utilise the camera (maybe at a later stage)

		batch.setProjectionMatrix(camera.combined); //Will draw in worlds coordinate

		batch.begin();
		ball.draw(batch);

		for(Obstacle obs : obstacles){
			obs.draw(batch);
		}

		batch.end();
		//Updating the Ball
		ball.update(camera);

		//Updating the Obstacles
		if(TimeUtils.timeSinceMillis(lastSpawnTime) > 2000) spawnObstacle(pixelCoords);

		Iterator<Obstacle> iter = obstacles.iterator();
		while(iter.hasNext()){
			Obstacle obs = iter.next();
			obs.update(acceleration);

			if(obs.getY() > camera.viewportHeight) {
				iter.remove();
				obs.dispose();
			}
			if(Intersector.overlaps(ball.getHitbox(),obs.getHitbox())){
				throw new ValueException("perdu");
			}
		}

		acceleration += 0.001;
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		ballImg.dispose();

		for(Obstacle obs : obstacles){
			obs.dispose();
		}
	}
}
