package com.mygdx.chromafall;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Ball {
    public Circle ball;    // Will contain the position in our coordinate system
    private Circle hitbox;
    private Texture ballImg;
    private Vector3 position = new Vector3();
    private Color color;

    public Ball(float Ox, float Oy, float radius){
        ball = new Circle(Ox,Oy,radius);
        hitbox = new Circle(Ox, Oy, radius);    // Smaller hitbox to avoid "lag" when death
        setColor(Color.ROYAL);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color newColor) {
        color = newColor;
        int intRadius = MathUtils.round(ball.radius);    // Rounds radius (float) to an int for pixmap
        Pixmap pixmap = new Pixmap(intRadius*2, intRadius*2, Pixmap.Format.RGBA8888);    // Image (square) IN MEMORY with dimensions intRadius*2 x intRadius*2 and RGBA8888 format (colors) to fill with a shape
        pixmap.setColor(color);
        pixmap.fillCircle(intRadius, intRadius, intRadius);    // begins to top left corner, goes intRadius to the right, then intRadius to the bottom, and then draws (in memory) a filled circle with radius intRadius from that position
        ballImg = new Texture(pixmap);    // Texture is used for the draw method of a SpriteBatch
        pixmap.dispose();
    }

    // This function will update the ball's position
    public void update(){
        float accelerometerX = -Gdx.input.getAccelerometerX();    // Inverted compared to our coordinates system

        ball.x += accelerometerX*10;

        // Boundaries on the x axis (for the position of the ball's center)
        ball.x = MathUtils.clamp(ball.x,ball.radius,Gdx.graphics.getWidth()-ball.radius);
        hitbox.x = ball.x;    // Hitbox center = ball center (previously, it was not the case if the ball hit the border)
    }

    // This function will draw the ball
    public void draw(SpriteBatch batch){
        // Since our texture is "square", we need to draw it from bottom left to top right, hence the x (or y) - radius (from the center)
        batch.draw(ballImg,ball.x-ball.radius,ball.y-ball.radius, ball.radius*2,ball.radius*2);
    }

    public Circle getHitbox(){
        return this.hitbox;
    }
}
