package com.mygdx.chromafall;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class Ball {
    private Circle ball;    //Will contain the position in our coordinate system
    private Texture ballImg;
    private Vector3 position = new Vector3();


    public Ball(float Ox, float Oy, float r, Texture ballImg){
        ball = new Circle(Ox,Oy,r);
        this.ballImg = ballImg;
    }

    //This function will update the balls position
    public void update(OrthographicCamera camera){
        float accelerometerX = -Gdx.input.getAccelerometerX(); //Inversed compare to our coordinate system
        float accelerometerY = -Gdx.input.getAccelerometerY();

        position.set(accelerometerX,accelerometerY,0);
        camera.unproject(position);

        ball.x += accelerometerX;
        ball.y += accelerometerY;

        //Boundaries on the x axis and y axis
        ball.x = MathUtils.clamp(ball.x,ball.radius,camera.viewportWidth-ball.radius);
        ball.y = MathUtils.clamp(ball.y,ball.radius,camera.viewportHeight-ball.radius);
    }

    //This function will draw the ball
    public void draw(SpriteBatch batch){
        batch.draw(ballImg,ball.x-ball.radius,ball.y-ball.radius, ball.radius*2,ball.radius*2);
    }
}
