package com.mygdx.chromafall;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Shape2D;

import java.util.Random;

public class Orb {
    private final int w = Gdx.graphics.getWidth();
    private final int h = Gdx.graphics.getHeight();
    public Circle circle;
    private Color color;
    private Texture texture;

    public Orb(float x) {
        final Color[] colors = new Color[]{
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
        float radius = w*0.035f;
        int intRadius = MathUtils.ceil(radius)+1;
        float y = -radius*2;
        circle = new Circle(x, y, radius);
        Pixmap pixmap = new Pixmap(intRadius*2, intRadius*2, Pixmap.Format.RGBA8888);
        color = colors[MathUtils.random(0, colors.length-1)];
        pixmap.setColor(color);
        pixmap.fillCircle(intRadius, intRadius, intRadius);
        texture = new Texture(pixmap);
        pixmap.dispose();
    }

    public Color getColor() {
        return color;
    }

    public Circle getHitbox() {
        return circle;
    }

    public void update(float speed) {
        circle.y += 0.5f + speed;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, circle.x, circle.y, circle.radius*2, circle.radius*2);
    }
}
