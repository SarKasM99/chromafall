package com.mygdx.chromafall;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Ball {
    private Circle ball;    //Will contain the position in our coordinate system
    private Texture ballImg;
    private Vector3 position = new Vector3();
    private Color color;

    public Ball(float Ox, float Oy, float radius){
        ball = new Circle(Ox,Oy,radius);
        this.ballImg = new Texture("Circ_Deg8.png");
        color = new Color(0x2467DAFF);
    }

    public Color getColor() {
        return color;
    }

    //This function will update the balls position
    public void update(Viewport camera){
        float accelerometerX = -Gdx.input.getAccelerometerX(); //Inversed compare to our coordinate system
        float accelerometerY = -Gdx.input.getAccelerometerY();

        position.set(accelerometerX,accelerometerY,0);
        camera.unproject(position);

        ball.x += accelerometerX*10;

        //Boundaries on the x axis and y axis
        ball.x = MathUtils.clamp(ball.x,ball.radius,camera.getWorldWidth()-ball.radius);
    }

    //This function will draw the ball
    public void draw(SpriteBatch batch){
        //Since our texture is "square", we need to draw it from buttom left to top right, hence the x or y - radius
        batch.draw(ballImg,ball.x-ball.radius,ball.y-ball.radius, ball.radius*2,ball.radius*2);
    }

    public Circle getHitbox(){
        return this.ball;
    }
}
