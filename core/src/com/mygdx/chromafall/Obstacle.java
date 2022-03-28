package com.mygdx.chromafall;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Obstacle {
    private Rectangle obstacle;
    private final Texture obsImg;
    private float initSpeed = 0.1f;

    public Obstacle(float x, float y, float width, float height, int pixWidth, int pixHeight) {
        obstacle = new Rectangle(x,y,width,height);

        //Drawing the rectangle
        //In the future we should create a set of textures in order to optimize the game
        Pixmap pixmap = new Pixmap(
                pixWidth,
                pixHeight,
                Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.RED);
        pixmap.fill();

        obsImg = new Texture(pixmap);
        pixmap.dispose();
    }

    public void update(float acceleration){
        obstacle.y += initSpeed + acceleration;
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

    public void dispose(){
        obsImg.dispose();
    }
}
