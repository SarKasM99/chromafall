package com.mygdx.chromafall;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MenuScreen implements Screen {

    private SpriteBatch batch;
    protected Stage stage;
    private Game game;
    private Screen screen;

    public MenuScreen(Game gameArg) {
        screen = this;
        game = gameArg;

        batch = new SpriteBatch();

        stage = new Stage(new FitViewport(400,800), batch);
    }


    @Override
    public void show() {

        //Stage should control input
        Gdx.input.setInputProcessor(stage);

        //Create Table
        final Table mainTable = new Table();
        //Set table to fill stage
        mainTable.setFillParent(true);
        //Set alignment of contents in the table.
        mainTable.top();

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



        //Create buttons
        Button playButton = new ImageTextButton("Play",buttonstyle);
        Button optionsButton = new ImageTextButton("Options",buttonstyle);
        Button exitButton = new ImageTextButton("Exit",buttonstyle);

        //logo
        Image logo = new Image(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("chroma-fall-logo.png")))));

        //Add listeners to buttons
        playButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mainTable.reset();
                game.setScreen(new GameScreen(game, screen));
            }
        });

        exitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });


        //Add buttons to table
        mainTable.add(logo).size(300);
        mainTable.row();
        mainTable.add(playButton).size(400,100);
        mainTable.row();
        mainTable.add(optionsButton).size(400,100).padTop(50);
        mainTable.row();
        mainTable.add(exitButton).size(400,100).padTop(50);

        //Add table to stage
        stage.addActor(mainTable);
    }

    @Override
    public void render(float delta) {
        stage.getViewport().apply();
        Gdx.gl.glClearColor(.102f,.102f,.102f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width,height,true);
    }

    //TODO: Pause resume for spawns of obstacles
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
    }
}
