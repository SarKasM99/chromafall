package com.mygdx.chromafall;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.utils.ScreenUtils;

public class MyGdxGame extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture BallImg;
	private OrthographicCamera camera;
	private Circle Ball;
	
	@Override
	public void create () {

		BallImg = new Texture("Circ_Deg8.png");

		camera = new OrthographicCamera();
		camera.setToOrtho(false,700,1200);
		batch = new SpriteBatch();

		Ball = new Circle();
		Ball.x = 286; // <- 700/2 (taille de l'écran /2) - 128 /2 (taille de la balle /2)
		Ball.y = 1050;
		Ball.radius = 64;

	}

	@Override
	public void render () {
		ScreenUtils.clear(Color.GRAY);
		//camera.update();
		batch.setProjectionMatrix(camera.combined);

		batch.begin();
		batch.draw(BallImg, Ball.x, Ball.y,128,128);
		batch.end();

		float gyro = Gdx.input.getGyroscopeY();
		if(gyro > 1){
			Ball.x += 30;
		}
		if(gyro < -1){
			Ball.x -= 30;
		}


		if(Ball.x < 0){
			Ball.x = 0;
		}
		if(Ball.x > 700-128){
			Ball.x = 700-128;
		}

	}
	
	@Override
	public void dispose () {
		batch.dispose();
		BallImg.dispose();
	}
}
