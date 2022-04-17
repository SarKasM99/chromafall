package com.mygdx.chromafall;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;

public class MyGdxGame extends Game {
    private boolean musicOn = true;
    private boolean soundOn = true;

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
}
