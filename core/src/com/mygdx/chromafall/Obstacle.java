package com.mygdx.chromafall;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class Obstacle {
    private final float w = Gdx.graphics.getWidth();
    private Rectangle obstacle;
    private final Texture obsImg;
    private float initSpeed = 0.5f;
    private Color color;
    private final Color[] colors = new Color[]{
        Color.WHITE,
        Color.GRAY,
        Color.ROYAL,
        Color.LIME,
        Color.YELLOW,
        Color.ORANGE,
        Color.RED,
        Color.BROWN,
        Color.PINK,
        Color.PURPLE,
    };

    public Obstacle(){
        Color i = Color.YELLOW;
        obstacle = new Rectangle(-100,-100,0,0);

        //Drawing the rectangle
        //In the future we should create a set of textures in order to optimize the game
        Pixmap pixmap = new Pixmap(1,15, Pixmap.Format.RGBA8888);
        Color bgColor = new Color(.102f,.102f,.102f, 1);
        color = colors[MathUtils.random(0, colors.length-1)];
        pixmap.setColor(color);
        pixmap.fill();

        obsImg = new Texture(pixmap);
        pixmap.dispose();
    }

    public Color getColor() {
        return color;
    }

    public void update(float acceleration){
        obstacle.y += initSpeed + acceleration;
    }

    public void prepare(float worldw, Ball ball, float pathX) {
        this.obstacle.width = worldw/MathUtils.random(2.5f,7.5f);
        this.obstacle.height = worldw/MathUtils.random(2.5f,10f);
        this.obstacle.setY(-obstacle.height);
        Rectangle pathRect = new Rectangle(pathX-0.02f*w, this.obstacle.getY(), 0.04f*w, ball.ball.radius*2.2f);
        do {
            this.obstacle.setX(MathUtils.random(10, w));
            System.out.println("Resetting the X...");
        } while (Intersector.overlaps(pathRect, this.obstacle));
    }

    public float getY(){
        return obstacle.y;
    }

    public void draw(SpriteBatch batch){
        batch.draw(obsImg,obstacle.x,obstacle.y,obstacle.width,obstacle.height);
    }

    public Rectangle getHitbox(){
        return this.obstacle;
    }

}
