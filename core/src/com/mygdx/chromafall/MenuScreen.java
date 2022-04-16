package com.mygdx.chromafall;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MenuScreen implements Screen {

    private final int w = Gdx.graphics.getWidth();
    private final int h = Gdx.graphics.getHeight();
    private SpriteBatch batch;
    protected Stage stage;

    private MyGdxGame game;
    private Screen screen;

    private Music menuMusic;
    private Sound open;
    private Sound close;

    private Table mainTable;
    private Table optionTable;

    public MenuScreen(MyGdxGame gameArg) {
        screen = this;
        game = gameArg;

        batch = new SpriteBatch();

        stage = new Stage(new ExtendViewport(w, h));
    }


    @Override
    public void show() {
        //Stage should control input
        Gdx.input.setInputProcessor(stage);

        //Create Table
        mainTable = new Table();
        optionTable = new Table();

        //Set table to fill stage*
        mainTable.setFillParent(true);
        optionTable.setFillParent(true);

        //Set alignment of contents in the table.
        mainTable.top();
        optionTable.top();

        //Creat font
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/myFont.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 70;
        param.color = Color.WHITE;
        //param.borderWidth = 50;

        //button case
        TextureRegionDrawable button = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("EmptyButton.png"))));
        ImageTextButton.ImageTextButtonStyle buttonstyle = new ImageTextButton.ImageTextButtonStyle();
        buttonstyle.font = gen.generateFont(param);
        buttonstyle.up = button;

        //Create buttons for the main table
        ImageTextButton playButton = new ImageTextButton("Play",buttonstyle);
        ImageTextButton optionsButton = new ImageTextButton("Options",buttonstyle);
        ImageTextButton quitButton = new ImageTextButton("Quit",buttonstyle);

        //Create buttons for the options table
        final ImageTextButton soundButton = new ImageTextButton("Sound : On",buttonstyle);
        final ImageTextButton musicButton = new ImageTextButton("Music : On",buttonstyle);
        ImageTextButton backButton  = new ImageTextButton("Back",buttonstyle);

        //logo
        Image logoMain = new Image(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("chroma-fall-logo.png")))));
        Image logoOption = new Image(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("chroma-fall-logo.png")))));

        //Add listeners to buttons of the main table
        playButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mainTable.reset();
                if(game.isSoundOn()) open.play();
                if(game.isMusicOn()) menuMusic.pause();
                game.setScreen(new GameScreen(game, screen));
            }
        });

        optionsButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(game.isSoundOn()) open.play();
                mainTable.remove();
                stage.addActor(optionTable);
            }
        });

        quitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(game.isSoundOn()) close.play();
                Gdx.app.exit();
            }
        });

        //Add listeners to buttons of the option table
        soundButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(game.isSoundOn()) {
                    soundButton.setText("Sound : off");
                    game.setSoundOn(false);
                }

                else{
                    game.setSoundOn(true);
                    open.play();
                    soundButton.setText("Sound : on");
                }
            }
        });

        musicButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(game.isSoundOn()) open.play();

                if(game.isMusicOn()) {
                    musicButton.setText("Music : off");
                    game.setMusicOn(false);
                    menuMusic.pause();
                }

                else{
                    musicButton.setText("Music : on");
                    game.setMusicOn(true);
                    menuMusic.play();
                }
            }
        });

        backButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(game.isSoundOn()) close.play();
                optionTable.remove();
                stage.addActor(mainTable);
            }
        });

        //Add buttons to main table
        mainTable.defaults().width(0.85f* w);
        mainTable.defaults().height(0.10f* h);
        mainTable.defaults().pad(0.01f* h);
        mainTable.defaults().align(Align.center);
        mainTable.add(logoMain).size(0.45f*h).padBottom(0);
        mainTable.row();
        mainTable.add(playButton);
        mainTable.row();
        mainTable.add(optionsButton);
        mainTable.row();
        mainTable.add(quitButton);

        //Add buttons to optionTable
        optionTable.defaults().width(0.85f* w);
        optionTable.defaults().height(0.10f* h);
        optionTable.defaults().pad(0.01f* h);
        optionTable.defaults().align(Align.center);
        optionTable.add(logoOption).size(0.45f*h).padBottom(0);
        optionTable.row();
        optionTable.add(soundButton);
        optionTable.row();
        optionTable.add(musicButton);
        optionTable.row();
        optionTable.add(backButton);

        //Add table to stage
        stage.addActor(mainTable);

        open = Gdx.audio.newSound(Gdx.files.internal("Sounds/open.wav"));
        close = Gdx.audio.newSound(Gdx.files.internal("Sounds/close.wav"));
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("Sounds/menu_music.wav"));
    }

    @Override
    public void render(float delta) {
        stage.getViewport().apply();
        Gdx.gl.glClearColor(.102f,.102f,.102f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();

        if(game.isMusicOn()) {
            menuMusic.setLooping(true);
            menuMusic.play();
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width,height,true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        //TODO: We need to dispose everything in every game screen
        menuMusic.dispose();
        open.dispose();
        close.dispose();
    }
}
