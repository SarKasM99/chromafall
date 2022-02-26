package com.mygdx.chromafall;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.utils.ScreenUtils;

import jdk.internal.org.jline.utils.Log;

public class MyGdxGame extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture BalleImage;
	private OrthographicCamera camera;
	private Circle Balle;
	
	@Override
	public void create () {

		BalleImage = new Texture("Circ_Deg8.png");

		camera = new OrthographicCamera();
		camera.setToOrtho(false,700,1200);
		batch = new SpriteBatch();

		Balle = new Circle();
		Balle.x = 286; // <- 700/2 (taille de l'écran /2) - 128 /2 (taille de la balle /2)
		Balle.y = 1050;
		Balle.radius = 64;

	}

	@Override
	public void render () {
		ScreenUtils.clear(Color.GRAY);
		//camera.update();
		batch.setProjectionMatrix(camera.combined);

		batch.begin();
		batch.draw(BalleImage,Balle.x,Balle.y,128,128);
		batch.end();

		float gyro = Gdx.input.getGyroscopeY();
		if(gyro > 1){
			Balle.x += 30;
		}
		if(gyro < -1){
			Balle.x -= 30;
		}


		if(Balle.x < 0){
			Balle.x = 0;
		}
		if(Balle.x > 700-128){
			Balle.x = 700-128;
		}

	}
	
	@Override
	public void dispose () {
		batch.dispose();
		BalleImage.dispose();
	}
}
