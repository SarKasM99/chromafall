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
	private Texture ballImg;
	private OrthographicCamera camera;
	private Circle ball;
	
	@Override
	public void create () {

		ballImg = new Texture("Circ_Deg8.png");

		camera = new OrthographicCamera();
		camera.setToOrtho(false,700,1200);
		batch = new SpriteBatch();

		ball = new Circle();
		ball.x = 286; // <- 700/2 (taille de l'écran /2) - 128 /2 (taille de la balle /2)
		ball.y = 1050;
		ball.radius = 64;

	}

	@Override
	public void render () {
		ScreenUtils.clear(Color.GRAY);
		//camera.update();
		batch.setProjectionMatrix(camera.combined);

		batch.begin();
		batch.draw(ballImg, ball.x, ball.y,128,128);
		batch.end();

		float gyroscopeY = Gdx.input.getGyroscopeY();
		if(gyroscopeY > 1){
			ball.x += 30;
		}
		if(gyroscopeY < -1){
			ball.x -= 30;
		}

		float gyroscopeX = Gdx.input.getGyroscopeX();
		if(gyroscopeX > 1){
			ball.y += 30;
		}
		if(gyroscopeX < -1){
			ball.y -= 30;
		}



		if(ball.x < 0){
			ball.x = 0;
		}
		if(ball.x > 700-128){
			ball.x = 700-128;
		}

	}
	
	@Override
	public void dispose () {
		batch.dispose();
		ballImg.dispose();
	}
}
