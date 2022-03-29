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
    private Color color;
    private final Color[] colors = new Color[]{
            Color.BLUE,
            Color.BROWN,
            Color.CHARTREUSE,
            Color.CORAL,
            Color.CYAN,
            Color.FIREBRICK,
            Color.FOREST,
            Color.GOLD,
            Color.GOLDENROD,
            Color.GREEN,
            Color.LIGHT_GRAY,
            Color.LIME,
            Color.MAGENTA,
            Color.OLIVE,
            Color.ORANGE,
            Color.PINK,
            Color.PURPLE,
            Color.RED,
            Color.ROYAL,
            Color.SALMON,
            Color.SCARLET,
            Color.SKY,
            Color.TAN,
            Color.TEAL,
            Color.VIOLET,
            Color.WHITE,
            Color.YELLOW
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
