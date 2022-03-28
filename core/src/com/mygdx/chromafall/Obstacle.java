package com.mygdx.chromafall;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class Obstacle {
    private Rectangle obstacle;
    private final Texture obsImg;
    private float initSpeed = 0.5f;

    public Obstacle(){
        obstacle = new Rectangle(-100,-100,0,0);

        //Drawing the rectangle
        //In the future we should create a set of textures in order to optimize the game
        Pixmap pixmap = new Pixmap(1,15, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.RED);
        pixmap.fill();

        obsImg = new Texture(pixmap);
        pixmap.dispose();
    }

    public void update(float acceleration){
        obstacle.y += initSpeed + acceleration;
    }

    public void prepare(float worldw){
        this.obstacle.width = worldw/MathUtils.random(2.5f,7.5f);
        this.obstacle.height = worldw/MathUtils.random(2.5f,10f);
        this.obstacle.setX(MathUtils.random(0,worldw-obstacle.width));
        this.obstacle.setY(-obstacle.height);
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
