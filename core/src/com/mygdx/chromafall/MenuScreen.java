package com.mygdx.chromafall;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class MenuScreen implements Screen {

    private final int w = Gdx.graphics.getWidth();
    private final int h = Gdx.graphics.getHeight();
    protected Stage stage;

    private int score;

    private MyGdxGame game;
    private MenuScreen screen;
    private GameScreen gameScreen;

    private Music menuMusic;
    private Sound open;
    private Sound close;

    private Table mainTable;
    private Table optionTable;
    private Table deathTable;
    private Table boardTable;

    private Label scoreLabel;

    private Label highScores;

    private boolean isMainMenu = true;
    public MenuScreen(MyGdxGame gameArg) {
        screen = this;
        game = gameArg;    // MyGdxGame instance


        gameScreen = new GameScreen(game,this);

        score = 0;

        stage = new Stage(new ExtendViewport(w, h));    // Stage handles the viewport and distributes input events.

        // Sounds and music
        open = Gdx.audio.newSound(Gdx.files.internal("Sounds/open.wav"));
        close = Gdx.audio.newSound(Gdx.files.internal("Sounds/close.wav"));
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("Sounds/menu_music.wav"));


        // Creates Table (for the buttons locations)
        mainTable = new Table();
        optionTable = new Table();
        deathTable = new Table();
        boardTable = new Table();

        // Sets table to fill stage
        mainTable.setFillParent(true);
        optionTable.setFillParent(true);
        deathTable.setFillParent(true);
        boardTable.setFillParent(true);

        // Sets alignment of contents in the table. (table on the top)
        mainTable.top();
        optionTable.top();
        deathTable.top();
        boardTable.top();

        // Creates fonts

        // Buttons
        final FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/myFont.ttf"));    // Font generator with model myFont.ttf
        final FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();    // Parameters of the font
        param.size = w/14;
        param.color = Color.WHITE;

        // Game over
        FreeTypeFontGenerator fontGen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/myFont.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParams.size = w/7;
        fontParams.color = Color.WHITE;
        fontParams.borderColor = Color.FIREBRICK;
        fontParams.borderWidth = w/125;

        // Button case
        ImageTextButton.ImageTextButtonStyle buttonStyle = new ImageTextButton.ImageTextButtonStyle();    // Parameters (style) of the button
        buttonStyle.font = gen.generateFont(param);    // Generates the font with parameters param
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("EmptyButton.png"))));    // Image of the button

        // Creates buttons for the main table
        ImageTextButton playButton = new ImageTextButton("Play",buttonStyle);
        ImageTextButton optionsButton = new ImageTextButton("Options",buttonStyle);
        ImageTextButton quitButton = new ImageTextButton("Quit",buttonStyle);
        ImageTextButton boardButton = new ImageTextButton("Leaderboard",buttonStyle);

        // Creates buttons for the options table
        final ImageTextButton soundButton = new ImageTextButton("Sound : ON",buttonStyle);
        final ImageTextButton musicButton = new ImageTextButton("Music : ON",buttonStyle);
        ImageTextButton backButton  = new ImageTextButton("Back",buttonStyle);

        // Creates buttons for the death table
        ImageTextButton playAgainButton = new ImageTextButton("Play again",buttonStyle);
        ImageTextButton goBackButton = new ImageTextButton("Go back to menu",buttonStyle);
        ImageTextButton quitDeathButton = new ImageTextButton("Quit",buttonStyle);

        // Creates text and buttons for the leaderboard table
        ImageTextButton boardGoBackButton = new ImageTextButton("Go back to menu",buttonStyle);


        // Logo
        Image logoMain = new Image(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("chroma-fall-logo.png")))));
        Image logoOption = new Image(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("chroma-fall-logo.png")))));
        Image logoBoard = new Image(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("chroma-fall-logo.png")))));

        // Adds listeners to buttons of the main table
        playButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(game.isSoundOn()) open.play();    // Opening sound
                if(game.isMusicOn()) menuMusic.stop();    // Menu music stops
                mainTable.remove();
                game.setScreen(gameScreen);
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

        boardButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(game.isSoundOn()) open.play();
                Preferences prefs = Gdx.app.getPreferences("chromafall.leaderboard");
                int hs1score = prefs.getInteger("hs1.score", 0);
                int hs2score = prefs.getInteger("hs2.score", 0);
                int hs3score = prefs.getInteger("hs3.score", 0);
                int hs4score = prefs.getInteger("hs4.score", 0);
                int hs5score = prefs.getInteger("hs5.score", 0);

                highScores.setText("1:   "+hs1score+"\n" + "2:   "+hs2score+"\n" + "3:   "+hs3score+"\n" + "4:   "+hs4score+"\n" + "5:   "+hs5score);
                mainTable.remove();
                stage.addActor(boardTable);
            }
        });

        boardGoBackButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(game.isSoundOn()) close.play();
                boardTable.remove();
                stage.addActor(mainTable);
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
                game.setScreen(gameScreen);
            }
        });

        goBackButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(game.isSoundOn()) close.play();
                isMainMenu = true;
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
        mainTable.defaults().width(0.65f * w);
        mainTable.defaults().height(0.10f * h);
        mainTable.defaults().pad(0.01f * h);    // Space between cells
        mainTable.defaults().align(Align.center);

        // Fills the table with the buttons (and logo)
        mainTable.add(logoMain).size(0.40f * h).padBottom(0);
        mainTable.row();    // Next cell
        mainTable.add(playButton);
        mainTable.row();
        mainTable.add(optionsButton);
        mainTable.row();
        mainTable.add(boardButton);
        mainTable.row();
        mainTable.add(quitButton);

        // Adds buttons to optionTable
        optionTable.defaults().width(0.65f * w);
        optionTable.defaults().height(0.10f * h);
        optionTable.defaults().pad(0.01f * h);
        optionTable.defaults().align(Align.center);

        optionTable.add(logoOption).size(0.40f * h).padBottom(0);
        optionTable.row();
        optionTable.add(soundButton);
        optionTable.row();
        optionTable.add(musicButton);
        optionTable.row();
        optionTable.add(backButton);

        // Adds buttons to deathTable
        deathTable.defaults().width(0.65f * w);
        deathTable.defaults().height(0.10f * h);
        deathTable.defaults().pad(0.01f * h);
        deathTable.defaults().align(Align.center);

        // Game Over
        Label.LabelStyle titleLabelStyle = new Label.LabelStyle(fontGen.generateFont(fontParams), Color.WHITE);
        Label gameOverTitle = new Label("Game Over !", titleLabelStyle);
        gameOverTitle.setAlignment(Align.center);
        fontGen.dispose();    // Freeing fontGen to avoid memory leaks

        // Score
        Label.LabelStyle labelStyle = new Label.LabelStyle(gen.generateFont(param), Color.WHITE);
        scoreLabel = new Label("Score : " + score, labelStyle);
        scoreLabel.setAlignment(Align.center);

        deathTable.row();
        deathTable.add(gameOverTitle).padTop(0.10f * h);
        deathTable.row();
        deathTable.add(scoreLabel).padBottom(0.05f * h);
        deathTable.row();
        deathTable.add(playAgainButton);
        deathTable.row();
        deathTable.add(goBackButton);
        deathTable.row();
        deathTable.add(quitDeathButton);

        // Leaderboard labels
        Label.LabelStyle highScoresStyle = new Label.LabelStyle(gen.generateFont(param), Color.WHITE);
        highScores = new Label("1:\n2:\n3:\n4:\n5:", highScoresStyle);

        // Default cells options for the leaderboard table
        boardTable.defaults().width(0.65f * w);
        boardTable.defaults().height(0.10f * h);
        boardTable.defaults().pad(0.01f * h);    // Space between cells
        boardTable.defaults().align(Align.center);

        // Fills the leaderboard table with the buttons (and logo)
        boardTable.add(logoBoard).size(0.40f * h);
        boardTable.row();    // Next cell
        boardTable.add(highScores).padBottom(0.1f*h);
        boardTable.row();
        boardTable.add(boardGoBackButton);
    }

    public void setScore(int scoreObtained) {
        score = scoreObtained;
        scoreLabel.setText("Score : " + score);
    }

    @Override
    public void show() {
        // Stage should control input
        Gdx.input.setInputProcessor(stage);

        // Adds MainTable to stage first

        if (score == 0) stage.addActor(mainTable);
        // Adds deathTable if a game was played (score != 0)
        else {stage.addActor(deathTable); isMainMenu = false;}

    }

    @Override
    public void render(float delta) {
        stage.getViewport().apply();    // Applies the viewport to the camera
        Gdx.gl.glClearColor(.102f,.102f,.102f, 1);    // Background
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();    // Updates the actors based on time
        stage.draw();    // Draws everything in the stage

        // Menu music looping
        if(game.isMusicOn() && isMainMenu) {
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
