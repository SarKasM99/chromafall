package com.mygdx.chromafall;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

public class MyGdxGame extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture ballImg;
	private OrthographicCamera camera;
	private Circle ball;
	private Rectangle obstacle;
	private Texture texture;
	private float vpHeight;
	private float vpWidth;
	private int frame;

	@Override
	public void create () {

		frame = 0;
		ballImg = new Texture("Circ_Deg8.png");

		camera = new OrthographicCamera();
		vpWidth = 700;
		vpHeight = 1200;
		camera.setToOrtho(false,vpWidth, vpHeight);
		batch = new SpriteBatch();

		ball = new Circle();
		ball.radius = 64;
		ball.x = vpWidth/2 - ball.radius;
		ball.y = 1050;

		obstacle = new Rectangle();
		obstacle.x = vpWidth/2;
		obstacle.y = 3;
		obstacle.width = 128;
		obstacle.height = 128;
		Pixmap pixmap = new Pixmap(
				(int)obstacle.getWidth(),
				(int)obstacle.getHeight(),
				Pixmap.Format.RGBA8888);
		pixmap.setColor(Color.RED);
		pixmap.fill();
		texture = new Texture(pixmap);
	}

	@Override
	public void render () {
		frame++;
		ScreenUtils.clear(Color.GRAY);
		//camera.update();
		batch.setProjectionMatrix(camera.combined);

		batch.begin();
		batch.draw(ballImg, ball.x, ball.y,128,128);
		batch.draw(texture, obstacle.x, obstacle.y,128,128);
		batch.end();

		float gyroscopeY = Gdx.input.getGyroscopeY();
		ball.x += gyroscopeY*30;

		if(ball.x < 0){
			ball.x = 0;
		}
		if(ball.x > 700-128){
			ball.x = 700-128;
		}

		float oldObstacleY = obstacle.y;
		obstacle.y = (obstacle.y + 1+0.98f*frame/120) % vpHeight;
		// Si l'obstacle a dépassé le bord supérieur de l'écran et a bouclé
		if (obstacle.y < oldObstacleY) {
			obstacle.x = (float)Math.random() * vpWidth;
		}

	}
	
	@Override
	public void dispose () {
		batch.dispose();
		ballImg.dispose();
		texture.dispose();
	}
}
