package com.mygdx.chromafall;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class DeathScreen implements Screen {
    private final int w = Gdx.graphics.getWidth();
    private final int h = Gdx.graphics.getHeight();
    private final int score;
    private final Stage stage;
    private final Table table = new Table();
    private final BitmapFont font;

    private final MyGdxGame gameApp;
    private final Screen menuScreen;

    public DeathScreen(int obtainedScore, Screen menuScreen, MyGdxGame applicationListener) {
        score = obtainedScore;
        stage = new Stage(new ExtendViewport(w, h));
        Gdx.input.setInputProcessor(stage);
        gameApp = applicationListener;
        this.menuScreen = menuScreen;

        FreeTypeFontGenerator fontGen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/myFont.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParams.size = 70;
        fontParams.color = Color.WHITE;
        font = fontGen.generateFont(fontParams);
        fontGen.dispose();
    }

    private ImageTextButton createButton(String text) {
        Texture emptyButtonTexture = new Texture(
                Gdx.files.internal("EmptyButton.png"));
        TextureRegionDrawable drawableTexture = new TextureRegionDrawable(
                new TextureRegion(emptyButtonTexture));
        ImageTextButton.ImageTextButtonStyle buttonStyle = new ImageTextButton
                .ImageTextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.up = drawableTexture;
        return new ImageTextButton(text, buttonStyle);
    }

    /**
     * Called when this screen becomes the current screen for a {@link Game}.
     */
    @Override
    public void show() {
        table.setFillParent(true);
        table.top();
        ImageTextButton playAgainButton = createButton("Play Again");
        ImageTextButton goBackButton = createButton("Go Back to Menu");
        ImageTextButton quitButton = createButton("Quit");

        playAgainButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                table.reset();
                gameApp.setScreen(new GameScreen(gameApp, menuScreen));
            }
        });

        goBackButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                table.reset();
                gameApp.setScreen(menuScreen);
            }
        });

        quitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        FreeTypeFontGenerator fontGen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/myFont.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParams.size = 110;
        fontParams.color = Color.WHITE;
        fontParams.borderColor = Color.FIREBRICK;
        fontParams.borderWidth = 8;

        table.defaults().width(0.85f* w);
        table.defaults().height(0.10f* h);
        table.defaults().pad(0.01f* h);
        table.defaults().align(Align.center);
        Label.LabelStyle titleLabelStyle = new Label.LabelStyle(fontGen.generateFont(fontParams), Color.WHITE);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        fontGen.dispose(); // Freeing fontGen to avoid memory leaks
        Label gameOverTitle = new Label("Game Over!", titleLabelStyle);
        gameOverTitle.setAlignment(Align.center);
        Label scoreLabel = new Label("Score: " + score, labelStyle);
        scoreLabel.setAlignment(Align.center);
        table.row();
        table.add(gameOverTitle).padTop(0.10f* h);
        table.row();
        table.add(scoreLabel).padBottom(0.15f* h);
        table.row();
        table.add(playAgainButton);
        table.row();
        table.add(goBackButton);
        table.row();
        table.add(quitButton);

        stage.addActor(table);

    }

    /**
     * Called when the screen should render itself.
     *
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        stage.getViewport().apply();
        Gdx.gl.glClearColor(.102f,.102f,.102f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }

    /**
     * @param width
     * @param height
     * @see ApplicationListener#resize(int, int)
     */
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    /**
     * @see ApplicationListener#pause()
     */
    @Override
    public void pause() {

    }

    /**
     * @see ApplicationListener#resume()
     */
    @Override
    public void resume() {

    }

    /**
     * Called when this screen is no longer the current screen for a {@link Game}.
     */
    @Override
    public void hide() {

    }

    /**
     * Called when this screen should release all resources.
     */
    @Override
    public void dispose() {
        font.dispose();
    }
}
