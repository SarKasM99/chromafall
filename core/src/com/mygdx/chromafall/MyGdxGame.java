package com.mygdx.chromafall;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;

public class MyGdxGame extends Game {
    private boolean musicOn = true;
    private boolean soundOn = true;
    private boolean blind = false;

    public static final Color[] colors = new Color[]{
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

    public static final Color[] blindColors = new Color[]{
            Color.WHITE,
            new Color(51f/255,34f/255,136f/255,1),
            new Color(17f/255,119f/255,51f/255,1),
            new Color(68f/255,170f/255,153f/255,1),
            new Color(136f/255,204f/255,238f/255,1),
            new Color(221f/255,204f/255,119f/255,1),
            new Color(204f/255,102f/255,119f/255,1),
            new Color(170f/255,68f/255,153f/255,1),
            new Color(136f/255,34f/255,85f/255,1),
            new Color(95f/255,0f/255,0f/255,1)
    };

    /**
     * Called when the {@link Application} is first created.
     */
    @Override
    public void create() {
        setScreen(new MenuScreen(this));    // Starting with the menu screen
    }

    @Override
    public void render() {
        super.render();    // Game loop
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    //Allows to set if the music/sound should be on or not
    public void setMusicOn(boolean val){ musicOn = val;}
    public void setSoundOn(boolean val){ soundOn = val;}

    //Returns whether the music/sound is on or not
    public boolean isMusicOn(){return musicOn;}
    public boolean isSoundOn(){return soundOn;}

    public boolean getBlind() {
        return blind;
    }

    public void setBlind(boolean b) {
        blind = b;
    }
}
