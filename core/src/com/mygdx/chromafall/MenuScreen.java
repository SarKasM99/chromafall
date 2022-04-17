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
    protected Stage stage;

    private int score;

    private MyGdxGame game;
    private MenuScreen screen;

    private Music menuMusic;
    private Sound open;
    private Sound close;

    private Table mainTable;
    private Table optionTable;
    private Table deathTable;

    public MenuScreen(MyGdxGame gameArg) {
        screen = this;
        game = gameArg;    // MyGdxGame instance
        score = 0;

        stage = new Stage(new ExtendViewport(w, h));    // Stage handles the viewport and distributes input events.

        // Sounds and music
        open = Gdx.audio.newSound(Gdx.files.internal("Sounds/open.wav"));
        close = Gdx.audio.newSound(Gdx.files.internal("Sounds/close.wav"));
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("Sounds/menu_music.wav"));
    }

    public void setScore(int scoreObtained) {
        score = scoreObtained;
    }

    @Override
    public void show() {
        // Stage should control input
        Gdx.input.setInputProcessor(stage);

        // Creates Table (for the buttons locations)
        mainTable = new Table();
        optionTable = new Table();
        deathTable = new Table();

        // Sets table to fill stage
        mainTable.setFillParent(true);
        optionTable.setFillParent(true);
        deathTable.setFillParent(true);

        // Sets alignment of contents in the table. (table on the top)
        mainTable.top();
        optionTable.top();
        deathTable.top();

        // Creates fonts

        // Buttons
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/myFont.ttf"));    // Font generator with model myFont.ttf
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();    // Parameters of the font
        param.size = 70;
        param.color = Color.WHITE;
        //param.borderWidth = 50;

        // Game over
        FreeTypeFontGenerator fontGen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/myFont.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParams.size = 110;
        fontParams.color = Color.WHITE;
        fontParams.borderColor = Color.FIREBRICK;
        fontParams.borderWidth = 8;

        // Button case
        ImageTextButton.ImageTextButtonStyle buttonStyle = new ImageTextButton.ImageTextButtonStyle();    // Parameters (style) of the button
        buttonStyle.font = gen.generateFont(param);    // Generates the font with parameters param
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("EmptyButton.png"))));    // Image of the button

        // Creates buttons for the main table
        ImageTextButton playButton = new ImageTextButton("Play",buttonStyle);
        ImageTextButton optionsButton = new ImageTextButton("Options",buttonStyle);
        ImageTextButton quitButton = new ImageTextButton("Quit",buttonStyle);

        // Creates buttons for the options table
        final ImageTextButton soundButton = new ImageTextButton("Sound : ON",buttonStyle);
        final ImageTextButton musicButton = new ImageTextButton("Music : ON",buttonStyle);
        ImageTextButton backButton  = new ImageTextButton("Back",buttonStyle);

        // Creates buttons for the death table
        ImageTextButton playAgainButton = new ImageTextButton("Play again",buttonStyle);
        ImageTextButton goBackButton = new ImageTextButton("Go back to menu",buttonStyle);
        ImageTextButton quitDeathButton = new ImageTextButton("Quit",buttonStyle);

        // Logo
        Image logoMain = new Image(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("chroma-fall-logo.png")))));
        Image logoOption = new Image(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("chroma-fall-logo.png")))));

        // Adds listeners to buttons of the main table
        playButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(game.isSoundOn()) open.play();    // Opening sound
                if(game.isMusicOn()) menuMusic.pause();    // Menu music stops
                mainTable.remove();
                game.setScreen(new GameScreen(game, screen));
            }
        });

        optionsButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(game.isSoundOn()) open.play();
                mainTable.remove();
                stage.addActor(optionTable);    // Replaces the main table
            }
        });

        quitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(game.isSoundOn()) close.play();    // Closing sound
                Gdx.app.exit();
            }
        });

        // Adds listeners to buttons of the option table
        soundButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(game.isSoundOn()) {
                    soundButton.setText("Sound : OFF");
                    game.setSoundOn(false);
                }

                else{
                    game.setSoundOn(true);
                    open.play();
                    soundButton.setText("Sound : ON");
                }
            }
        });

        musicButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(game.isSoundOn()) open.play();

                if(game.isMusicOn()) {
                    musicButton.setText("Music : OFF");
                    game.setMusicOn(false);
                    menuMusic.pause();
                }

                else{
                    musicButton.setText("Music : ON");
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
                stage.addActor(mainTable);    // Replaces the option table
            }
        });

        // Adds listeners to buttons of the death table
        playAgainButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(game.isSoundOn()) open.play();
                deathTable.remove();
                game.setScreen(new GameScreen(game, screen));
            }
        });

        goBackButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(game.isSoundOn()) close.play();
                if(game.isSoundOn()) menuMusic.play();
                deathTable.remove();
                stage.addActor(mainTable);
            }
        });

        quitDeathButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(game.isSoundOn()) close.play();
                Gdx.app.exit();
            }
        });

        // Adds buttons to MainTable

        // Default cells options for the main table
        mainTable.defaults().width(0.60f * w);
        mainTable.defaults().height(0.10f * h);
        mainTable.defaults().pad(0.01f * h);    // Space between cells
        mainTable.defaults().align(Align.center);

        // Fills the table with the buttons (and logo)
        mainTable.add(logoMain).size(0.40f*h).padBottom(0);
        mainTable.row();    // Next cell
        mainTable.add(playButton);
        mainTable.row();
        mainTable.add(optionsButton);
        mainTable.row();
        mainTable.add(quitButton);

        // Adds buttons to optionTable
        optionTable.defaults().width(0.60f * w);
        optionTable.defaults().height(0.10f * h);
        optionTable.defaults().pad(0.01f * h);
        optionTable.defaults().align(Align.center);

        optionTable.add(logoOption).size(0.40f*h).padBottom(0);
        optionTable.row();
        optionTable.add(soundButton);
        optionTable.row();
        optionTable.add(musicButton);
        optionTable.row();
        optionTable.add(backButton);

        // Adds buttons to deathTable
        deathTable.defaults().width(0.60f * w);
        deathTable.defaults().height(0.10f * h);
        deathTable.defaults().pad(0.01f * h);
        deathTable.defaults().align(Align.center);

        // Game Over
        Label.LabelStyle titleLabelStyle = new Label.LabelStyle(fontGen.generateFont(fontParams), Color.WHITE);
        Label gameOverTitle = new Label("Game Over!", titleLabelStyle);
        gameOverTitle.setAlignment(Align.center);
        fontGen.dispose();    // Freeing fontGen to avoid memory leaks

        // Score
        Label.LabelStyle labelStyle = new Label.LabelStyle(gen.generateFont(param), Color.WHITE);
        Label scoreLabel = new Label("Score : " + score, labelStyle);
        scoreLabel.setAlignment(Align.center);

        deathTable.row();
        deathTable.add(gameOverTitle).padTop(0.10f* h);
        deathTable.row();
        deathTable.add(scoreLabel).padBottom(0.15f* h);
        deathTable.row();
        deathTable.add(playAgainButton);
        deathTable.row();
        deathTable.add(goBackButton);
        deathTable.row();
        deathTable.add(quitDeathButton);

        // Adds MainTable to stage first
        if (score == 0) stage.addActor(mainTable);
        // Adds deathTable if a game was played (score != 0)
        else stage.addActor(deathTable);
    }

    @Override
    public void render(float delta) {
        stage.getViewport().apply();    // Applies the viewport to the camera
        Gdx.gl.glClearColor(.102f,.102f,.102f, 1);    // Background
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();

        // Menu music looping
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
