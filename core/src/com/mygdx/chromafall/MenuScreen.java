package com.mygdx.chromafall;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
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
    public final ImageTextButton soundButton;
    public final ImageTextButton musicButton;
    public final ImageTextButton deathSoundButton;
    public final ImageTextButton deathMusicButton;

    private Table mainTable;
    private Table optionTable;
    private Table deathTable;
    private Table optionsDeathTable;

    private Table boardTable;
    private Table tutoTable;

    private Label scoreLabel;
    private Label deathScoreLabel;
    private Label tutorial;
    public Label highScores;

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
        optionsDeathTable = new Table();
        boardTable = new Table();
        tutoTable = new Table();

        // Sets table to fill stage
        mainTable.setFillParent(true);
        optionTable.setFillParent(true);
        deathTable.setFillParent(true);
        optionsDeathTable.setFillParent(true);
        boardTable.setFillParent(true);
        tutoTable.setFillParent(true);

        // Sets alignment of contents in the table. (table on the top)
        mainTable.top();
        optionTable.top();
        deathTable.top();

        optionsDeathTable.top();
        boardTable.top();
        tutoTable.top();

        // Buttons
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/myFont.ttf"));    // Font generator with model myFont.ttf
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();    // Parameters of the font
        param.size = w/14;
        param.color = Color.WHITE;
        FreeTypeFontGenerator.FreeTypeFontParameter dialogParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        dialogParam.size = w/25;
        dialogParam.color = Color.WHITE;

        // Label style
        final Label.LabelStyle labelStyle = new Label.LabelStyle(gen.generateFont(param), Color.WHITE);
        final Label.LabelStyle dialogStyle = new Label.LabelStyle(gen.generateFont(dialogParam), Color.WHITE);

        // Game over
        FreeTypeFontGenerator fontGen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/myFont.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParams.size = w/7;
        fontParams.color = Color.WHITE;
        fontParams.borderColor = Color.FIREBRICK;
        fontParams.borderWidth = w/125;

        // Button case
        final ImageTextButton.ImageTextButtonStyle buttonStyle = new ImageTextButton.ImageTextButtonStyle();    // Parameters (style) of the button
        buttonStyle.font = gen.generateFont(param);    // Generates the font with parameters param
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("EmptyButton.png"))));

        final ImageTextButton.ImageTextButtonStyle dialogBtnStyle = new ImageTextButton.ImageTextButtonStyle();    // Parameters (style) of the button
        dialogBtnStyle.font = gen.generateFont(dialogParam);    // Generates the font with parameters param
        TextureRegion btnTexture = new TextureRegion(new Texture(Gdx.files.internal("EmptyButton.png")));
        btnTexture.setRegionWidth(w*3/10);
        btnTexture.setRegionHeight(h/15);
        dialogBtnStyle.up = new TextureRegionDrawable(btnTexture);    // Image of the button

        // Creates buttons for the main table
        ImageTextButton playButton = new ImageTextButton("Play",buttonStyle);
        ImageTextButton optionsButton = new ImageTextButton("Options",buttonStyle);
        ImageTextButton boardButton = new ImageTextButton("Leaderboard",buttonStyle);
        ImageTextButton tutoButton = new ImageTextButton("Tutorial",buttonStyle);

        // Creates text and buttons for the leaderboard table
        ImageTextButton resetButton = new ImageTextButton("Reset",buttonStyle);
        ImageTextButton boardGoBackButton = new ImageTextButton("Back",buttonStyle);

        // Creates buttons for the options table
        soundButton = new ImageTextButton("Sound : ON",buttonStyle);
        musicButton = new ImageTextButton("Music : ON",buttonStyle);
        final ImageTextButton blindButton = new ImageTextButton("Colorblind : OFF",buttonStyle);
        ImageTextButton backButton  = new ImageTextButton("Back",buttonStyle);

        // Creates buttons for the death table
        ImageTextButton playAgainButton = new ImageTextButton("Play again",buttonStyle);
        ImageTextButton goBackButton = new ImageTextButton("Go back to menu",buttonStyle);
        ImageTextButton optionsDeathButton = new ImageTextButton("Options",buttonStyle);

        // Creates buttons for the options death table
        deathSoundButton = new ImageTextButton("Sound : ON",buttonStyle);
        deathMusicButton = new ImageTextButton("Music : ON",buttonStyle);
        final ImageTextButton deathBlindButton = new ImageTextButton("Colorblind : OFF",buttonStyle);
        ImageTextButton deathBackButton  = new ImageTextButton("Back",buttonStyle);

        // Creates buttons and label for the tuto table
        tutorial = new Label(
                "You play a colorful ball falling through an endless pit.\n\n" +
                "The pit contains rectangle obstacles that you must dodge by tilting your phone or tablet.\n\n" +
                "Along the way, there are round orbs that you can pick up in order to change your own color.\n\n" +
                "This is useful because you can safely pass through the obstacles that are the same color as yours.\n\n" +
                "Try to survive as long as possible.\n\n" +
                "Good luck and have fun.\n\n",
                labelStyle);
        tutorial.setFontScale(0.65f);
        tutorial.setWrap(true);
        tutorial.setAlignment(Align.top,Align.center);
        ImageTextButton okButton = new ImageTextButton("OK",buttonStyle);

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

        tutoButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(game.isSoundOn()) open.play();    // Closing sound
                mainTable.remove();
                stage.addActor(tutoTable);
            }
        });

        boardButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(game.isSoundOn()) open.play();
                mainTable.remove();
                stage.addActor(boardTable);
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

        // Adds listeners to buttons of the leaderboard table
        boardGoBackButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (game.isSoundOn()) close.play();
                boardTable.remove();
                stage.addActor(mainTable);
            }
        });

        resetButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (game.isSoundOn()) open.play();
                Skin skin = new Skin(Gdx.files.internal("skin.json"));
                Dialog dialog = new Dialog("", skin) {
                    public void result(Object object) {
                        if (game.isSoundOn()) close.play();
                        if (object.equals(true)) {
                            if (game.isSoundOn()) open.play();
                            Preferences prefs = Gdx.app.getPreferences("chromafall.leaderboard");
                            prefs.putInteger("hs1.score", 0);
                            prefs.putInteger("hs2.score", 0);
                            prefs.putInteger("hs3.score", 0);
                            prefs.putInteger("hs4.score", 0);
                            prefs.putInteger("hs5.score", 0);
                            prefs.putString("hs1.name", "User");
                            prefs.putString("hs2.name", "User");
                            prefs.putString("hs3.name", "User");
                            prefs.putString("hs4.name", "User");
                            prefs.putString("hs5.name", "User");
                            prefs.flush();
                            highScores.setText("1. " + "User" + " : " + 0 + "\n" +
                                    "2. " + "User" + " : " + 0 + "\n" +
                                    "3. " + "User" + " : " + 0 + "\n" +
                                    "4. " + "User" + " : " + 0 + "\n" +
                                    "5. " + "User" + " : " + 0);
                        }
                    }
                };
                Label dialogLabel = new Label("Are you sure you want\nto reset the scores ?", labelStyle);
                dialogLabel.setAlignment(Align.center);
                ImageTextButton yesDialogBtn = new ImageTextButton("Yes", buttonStyle);
                ImageTextButton noDialogBtn = new ImageTextButton("No", buttonStyle);

                Table buttonTable = dialog.getButtonTable();
                buttonTable.defaults().width(0.35f * w);
                buttonTable.defaults().height(0.05f * h);
                buttonTable.defaults().pad(0.01f * h);    // Space between cells
                buttonTable.defaults().align(Align.center);

                dialog.text(dialogLabel);
                dialog.button(yesDialogBtn, true);
                dialog.button(noDialogBtn, false);
                dialog.show(stage);
            }

        });

        // Adds listeners to buttons of the tuto table
        okButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(game.isSoundOn()) close.play();
                isMainMenu = true;
                tutoTable.remove();
                stage.addActor(mainTable);
            }
        });

        // Adds listeners to buttons of the option table
        soundButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(game.isSoundOn()) {
                    soundButton.setText("Sound : OFF");
                    deathSoundButton.setText("Sound : OFF");
                    gameScreen.soundButton.setText("Sound : OFF");
                    game.setSoundOn(false);
                }

                else{
                    game.setSoundOn(true);
                    open.play();
                    soundButton.setText("Sound : ON");
                    deathSoundButton.setText("Sound : ON");
                    gameScreen.soundButton.setText("Sound : ON");
                }
            }
        });

        musicButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(game.isMusicOn()) {
                    if(game.isSoundOn()) close.play();
                    musicButton.setText("Music : OFF");
                    deathMusicButton.setText("Music : OFF");
                    gameScreen.musicButton.setText("Music : OFF");
                    game.setMusicOn(false);
                    menuMusic.pause();
                }

                else{
                    if(game.isSoundOn()) open.play();
                    musicButton.setText("Music : ON");
                    deathMusicButton.setText("Music : ON");
                    gameScreen.musicButton.setText("Music : ON");
                    game.setMusicOn(true);
                    menuMusic.play();
                }
            }
        });

        blindButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(game.getBlind()) {
                    if(game.isSoundOn()) close.play();
                    blindButton.setText("Colorblind : OFF");
                    deathBlindButton.setText("Colorblind : OFF");
                    Obstacle.colors = MyGdxGame.colors;
                    Orb.colors = MyGdxGame.colors;
                    game.setBlind(false);
                }

                else{
                    if(game.isSoundOn()) open.play();
                    blindButton.setText("Colorblind : ON");
                    deathBlindButton.setText("Colorblind : ON");
                    Obstacle.colors = MyGdxGame.blindColors;
                    Orb.colors = MyGdxGame.blindColors;
                    game.setBlind(true);
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
        //gameScreen.dispose();
        // Creating a new game screen in order to have another invisible path
        //gameScreen = new GameScreen(game, this);
        playAgainButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(game.isSoundOn()) open.play();
                deathTable.remove();
                game.setScreen(gameScreen);
            }
        });

        optionsDeathButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(game.isSoundOn()) open.play();
                deathTable.remove();
                stage.addActor(optionsDeathTable);
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

        // Adds listeners to buttons of the options death table
        deathSoundButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(game.isSoundOn()) {
                    deathSoundButton.setText("Sound : OFF");
                    soundButton.setText("Sound : OFF");
                    gameScreen.soundButton.setText("Sound : OFF");
                    game.setSoundOn(false);
                }

                else{
                    game.setSoundOn(true);
                    open.play();
                    deathSoundButton.setText("Sound : ON");
                    soundButton.setText("Sound : ON");
                    gameScreen.soundButton.setText("Sound : ON");
                }
            }
        });

        deathMusicButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(game.isMusicOn()) {
                    if(game.isSoundOn()) close.play();
                    deathMusicButton.setText("Music : OFF");
                    musicButton.setText("Music : OFF");
                    gameScreen.musicButton.setText("Music : OFF");
                    game.setMusicOn(false);
                    menuMusic.pause();
                }

                else{
                    if(game.isSoundOn()) open.play();
                    deathMusicButton.setText("Music : ON");
                    musicButton.setText("Music : ON");
                    gameScreen.musicButton.setText("Music : ON");
                    game.setMusicOn(true);
                }
            }
        });

        deathBlindButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(game.getBlind()) {
                    if(game.isSoundOn()) close.play();
                    deathBlindButton.setText("Colorblind : OFF");
                    blindButton.setText("Colorblind : OFF");
                    Obstacle.colors = MyGdxGame.colors;
                    Orb.colors = MyGdxGame.colors;
                    game.setBlind(false);
                }

                else{
                    if(game.isSoundOn()) open.play();
                    deathBlindButton.setText("Colorblind : ON");
                    blindButton.setText("Colorblind : ON");
                    Obstacle.colors = MyGdxGame.blindColors;
                    Orb.colors = MyGdxGame.blindColors;
                    game.setBlind(true);
                }
            }
        });

        deathBackButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(game.isSoundOn()) close.play();
                optionsDeathTable.remove();
                stage.addActor(deathTable);
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
        mainTable.add(playButton).padTop(-0.015f * h);
        mainTable.row();
        mainTable.add(tutoButton);
        mainTable.row();
        mainTable.add(boardButton);
        mainTable.row();
        mainTable.add(optionsButton);

        // Add button and label to tutoTable
        tutoTable.defaults().width(0.65f * w);
        tutoTable.defaults().height(0.10f * h);
        tutoTable.defaults().pad(0.01f * h);
        tutoTable.defaults().align(Align.center);

        tutoTable.row();
        tutoTable.add(tutorial).padTop(0.15f * h);
        tutoTable.row();
        tutoTable.add(okButton).padTop(0.52f * h);

        // Leaderboard labels
        param.size = w/10;
        param.borderColor = Color.OLIVE;
        param.borderWidth = w/250;
        Label.LabelStyle highScoresStyle = new Label.LabelStyle(gen.generateFont(param), Color.WHITE);
        Preferences prefs = Gdx.app.getPreferences("chromafall.leaderboard");
        int hs1score = prefs.getInteger("hs1.score", 0);
        int hs2score = prefs.getInteger("hs2.score", 0);
        int hs3score = prefs.getInteger("hs3.score", 0);
        int hs4score = prefs.getInteger("hs4.score", 0);
        int hs5score = prefs.getInteger("hs5.score", 0);
        String hs1name = prefs.getString("hs1.name", "User");
        String hs2name = prefs.getString("hs2.name", "User");
        String hs3name = prefs.getString("hs3.name", "User");
        String hs4name = prefs.getString("hs4.name", "User");
        String hs5name = prefs.getString("hs5.name", "User");
        highScores = new Label("1. "+hs1name+" : "+hs1score+"\n"+
                                    "2. "+hs2name+" : "+hs2score+"\n"+
                                    "3. "+hs3name+" : "+hs3score+"\n"+
                                    "4. "+hs4name+" : "+hs4score+"\n"+
                                    "5. "+hs5name+" : "+hs5score,
                                    labelStyle);
        Label leaderboard = new Label("Leaderboard", highScoresStyle);
        leaderboard.setAlignment(Align.center);

        // Default cells options for the leaderboard table
        boardTable.defaults().width(0.65f * w);
        boardTable.defaults().height(0.10f * h);
        boardTable.defaults().pad(0.01f * h);    // Space between cells
        boardTable.defaults().align(Align.center);

        // Fills the leaderboard table with the buttons (and logo)
        boardTable.add(logoBoard).size(0.40f * h).padBottom(-0.1f * h);
        boardTable.row();    // Next cell
        boardTable.add(leaderboard).padBottom(0.05f * h);
        boardTable.row();
        boardTable.add(highScores).padBottom(0.1f*h);
        boardTable.row();
        boardTable.add(resetButton);
        boardTable.row();
        boardTable.add(boardGoBackButton);

        // Adds buttons to optionTable
        optionTable.defaults().width(0.65f * w);
        optionTable.defaults().height(0.10f * h);
        optionTable.defaults().pad(0.01f * h);
        optionTable.defaults().align(Align.center);

        optionTable.add(logoOption).size(0.40f * h).padBottom(0);
        optionTable.row();
        optionTable.add(soundButton).padTop(-0.015f * h);
        optionTable.row();
        optionTable.add(musicButton);
        optionTable.row();
        optionTable.add(blindButton);
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

        // Score
        scoreLabel = new Label("Score : " + score, labelStyle);
        scoreLabel.setAlignment(Align.center);

        deathTable.row();
        deathTable.add(gameOverTitle).padTop(0.10f * h);
        deathTable.row();
        deathTable.add(scoreLabel).padBottom(0.05f * h);
        deathTable.row();
        deathTable.add(playAgainButton);
        deathTable.row();
        deathTable.add(optionsDeathButton);
        deathTable.row();
        deathTable.add(goBackButton);

        // Adds buttons to optionsDeathTable
        optionsDeathTable.defaults().width(0.65f * w);
        optionsDeathTable.defaults().height(0.10f * h);
        optionsDeathTable.defaults().pad(0.01f * h);
        optionsDeathTable.defaults().align(Align.center);

        // Game Over
        Label deathGameOverTitle = new Label("Game Over !", titleLabelStyle);
        deathGameOverTitle.setAlignment(Align.center);
        fontGen.dispose();    // Freeing fontGen to avoid memory leaks

        // Score
        deathScoreLabel = new Label("Score : " + score, labelStyle);
        deathScoreLabel.setAlignment(Align.center);

        optionsDeathTable.row();
        optionsDeathTable.add(deathGameOverTitle).padTop(0.10f * h);
        optionsDeathTable.row();
        optionsDeathTable.add(deathScoreLabel).padBottom(0.05f * h);
        optionsDeathTable.row();
        optionsDeathTable.add(deathSoundButton);
        optionsDeathTable.row();
        optionsDeathTable.add(deathMusicButton);
        optionsDeathTable.row();
        optionsDeathTable.add(deathBlindButton);
        optionsDeathTable.row();
        optionsDeathTable.add(deathBackButton);
    }

    public void setScore(int scoreObtained) {
        score = scoreObtained;
        scoreLabel.setText("Score : " + score);
        deathScoreLabel.setText("Score : " + score);
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
