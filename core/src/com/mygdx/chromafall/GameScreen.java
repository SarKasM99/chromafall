package com.mygdx.chromafall;


import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.LinkedList;
import java.util.Queue;


public class GameScreen implements Screen {
	private MyGdxGame game;
	private final int w = Gdx.graphics.getWidth();
	private final int h = Gdx.graphics.getHeight();
	private Viewport gameView;
	private SpriteBatch batch;
	private MenuScreen menusScreen;
	private Stage stage;

	//Game objects
	private Ball ball;

	private Queue<Obstacle> usedObstacles;
	private Queue<Obstacle> stockedObstacle;

	private Orb orb;
	private boolean isOrbShown = false;

	//Pause button
	private Button pauseButton;
	private FreeTypeFontGenerator generator;
	private BitmapFont font;

	//Game parameters
	private float time;
	private float speed;
	private int incremencer;
	private boolean needtoPop;
	private int score;
	private final InvisiblePath invisPath;
	private Preferences prefs;

	//Sound and music
	private Music gameMusic;
	private Sound itemSound;
	private Sound collisionSound;
	private Sound open;
	private Sound close;

	// Pause menu table
	private Table pauseTable;
	private Table optionTable;
	private Label scoreLabelPause;
	private Label scoreLabelOptions;

	//Defining the state of the game
	private enum State{
		PAUSE,
		RUN,
		RESUME,
		DEATH
	}

	private State state = State.RUN;

	public GameScreen(final MyGdxGame game, final MenuScreen menusScreen) {
		this.game = game;
		this.menusScreen = menusScreen;

		// Initialising the viewport
		this.gameView = new ExtendViewport(w,h);

		// Batch
		this.batch = new SpriteBatch();

		stage = new Stage(gameView);

		invisPath = new InvisiblePath(1);

		//Sounds and music
		open = Gdx.audio.newSound(Gdx.files.internal("Sounds/open.wav"));
		close = Gdx.audio.newSound(Gdx.files.internal("Sounds/close.wav"));
		collisionSound = Gdx.audio.newSound(Gdx.files.internal("Sounds/collision.wav"));
		itemSound = Gdx.audio.newSound(Gdx.files.internal("Sounds/item.wav"));
		gameMusic = Gdx.audio.newMusic(Gdx.files.internal("Sounds/game_music.mp3"));

		// Game paused font
		FreeTypeFontGenerator fontGen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/myFont.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter fontParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
		fontParams.size = w/7;
		fontParams.color = Color.WHITE;
		fontParams.borderColor = Color.ROYAL;
		fontParams.borderWidth = w/125;

		// Score font
		generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/myFont.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.color = Color.WHITE;
		parameter.size = w/20;
		font = generator.generateFont(parameter);

		// Creates Tables (for the pause menu buttons)
		pauseTable = new Table();
		pauseTable.setFillParent(true);
		pauseTable.top();

		optionTable = new Table();
		optionTable.setFillParent(true);
		optionTable.top();

		// Buttons font
		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/myFont.ttf"));    // Font generator with model myFont.ttf
		FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();    // Parameters of the font
		param.size = w/14;
		param.color = Color.WHITE;

		// Buttons
		ImageTextButton.ImageTextButtonStyle buttonStyle = new ImageTextButton.ImageTextButtonStyle();    // Parameters (style) of the button
		buttonStyle.font = gen.generateFont(param);    // Generates the font with parameters param
		buttonStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("EmptyButton.png"))));    // Image of the button

		TextureRegionDrawable pauseb = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("PauseButton.png"))));
		TextureRegionDrawable playb = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("PlayButton.png"))));
		pauseButton = new ImageButton(pauseb);
		float pauseButtonSize = w/12f;
		pauseButton.setSize(pauseButtonSize,pauseButtonSize);
		pauseButton.setPosition(w-pauseButtonSize,h-pauseButtonSize);

		ImageTextButton resumeButton = new ImageTextButton("Resume",buttonStyle);
		ImageTextButton optionsButton = new ImageTextButton("Options",buttonStyle);
		final ImageTextButton soundButton = new ImageTextButton("Sound : ON",buttonStyle);
		final ImageTextButton musicButton = new ImageTextButton("Music : ON",buttonStyle);
		ImageTextButton backButton  = new ImageTextButton("Back",buttonStyle);
		ImageTextButton goBackButton  = new ImageTextButton("Give up",buttonStyle);
		ImageTextButton quitButton = new ImageTextButton("Quit",buttonStyle);

		// Listeners
		pauseButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(game.isSoundOn()) open.play();
				if(game.isSoundOn()) gameMusic.pause();
				state = State.PAUSE;
				pauseButton.remove();
				scoreLabelPause.setText("Score : " + score);
				scoreLabelOptions.setText("Score : " + score);
				stage.addActor(pauseTable);
			}
		});

		resumeButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(game.isSoundOn()) close.play();
				if(game.isSoundOn()) gameMusic.play();
				pauseTable.remove();
				stage.addActor(pauseButton);
				state = State.RUN;
			}
		});

		optionsButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(game.isSoundOn()) open.play();
				pauseTable.remove();
				stage.addActor(optionTable);    // Replaces the main table
			}
		});

		soundButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(game.isSoundOn()) {
					soundButton.setText("Sound : OFF");
					game.setSoundOn(false);
				} else {
					game.setSoundOn(true);
					open.play();
					soundButton.setText("Sound : ON");
				}
			}
		});

		musicButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(game.isMusicOn()) {
					if(game.isSoundOn()) close.play();
					musicButton.setText("Music : OFF");
					game.setMusicOn(false);
				} else {
					if(game.isSoundOn()) open.play();
					musicButton.setText("Music : ON");
					game.setMusicOn(true);
				}
			}
		});

		backButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(game.isSoundOn()) close.play();
				optionTable.remove();
				stage.addActor(pauseTable);    // Replaces the option table
			}
		});

		goBackButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(game.isSoundOn()) close.play();
				if(game.isSoundOn()) gameMusic.stop();
				pauseButton.remove();
				pauseTable.remove();
				menusScreen.setScore(score);
				game.setScreen(menusScreen);
				state = State.RUN;
			}
		});

		quitButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(game.isSoundOn()) close.play();
				Gdx.app.exit();
			}
		});

		// Adds buttons to tables

		// Game Paused
		Label.LabelStyle titleLabelStyle = new Label.LabelStyle(fontGen.generateFont(fontParams), Color.WHITE);
		Label gamePausedTitle = new Label("Game paused", titleLabelStyle);
		gamePausedTitle.setAlignment(Align.center);
		Label gamePausedOptions = new Label("Game paused", titleLabelStyle);
		gamePausedOptions.setAlignment(Align.center);
		fontGen.dispose();

		// Score
		Label.LabelStyle labelStyle = new Label.LabelStyle(gen.generateFont(param), Color.WHITE);
		scoreLabelPause = new Label("Score : " + score, labelStyle);
		scoreLabelPause.setAlignment(Align.center);
		scoreLabelOptions = new Label("Score : " + score, labelStyle);
		scoreLabelOptions.setAlignment(Align.center);

		pauseTable.defaults().width(0.65f * w);
		pauseTable.defaults().height(0.10f * h);
		pauseTable.defaults().pad(0.01f * h);
		pauseTable.defaults().align(Align.center);

		pauseTable.add(gamePausedTitle).padTop(0.10f * h);
		pauseTable.row();
		pauseTable.add(scoreLabelPause).padBottom(0.05f * h);
		pauseTable.row();
		pauseTable.add(resumeButton);
//		pauseTable.row();
//		pauseTable.add(optionsButton);
		pauseTable.row();
		pauseTable.add(goBackButton);
		pauseTable.row();
		pauseTable.add(quitButton);

		optionTable.defaults().width(0.65f * w);
		optionTable.defaults().height(0.10f * h);
		optionTable.defaults().pad(0.01f * h);
		optionTable.defaults().align(Align.center);

		optionTable.add(gamePausedOptions).padTop(0.10f * h);
		optionTable.row();
		optionTable.add(scoreLabelOptions).padBottom(0.05f * h);
		optionTable.row();
		optionTable.add(soundButton);
		optionTable.row();
		optionTable.add(musicButton);
		optionTable.row();
		optionTable.add(backButton);
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);

		//Initialising the game parameters
		score = 0;
		speed = 5;
		incremencer = 10;
		needtoPop = false;

		//Initialising game objects
		stockedObstacle = new LinkedList<Obstacle>();
		usedObstacles = new LinkedList<Obstacle>();

		this.ball = new Ball(w/2f,h-h/12f,w/16f);

		orb = new Orb(invisPath.evaluate(0));

		for (int i = 0; i < 30; i++) {
			stockedObstacle.add(new Obstacle());
		}

		Obstacle temp = stockedObstacle.remove();
		temp.prepare(w, this.ball, invisPath.evaluate(0));
		usedObstacles.add(temp);

		stage.addActor(pauseButton);
	}

	@Override
	public void render(float delta) {
		if(game.isMusicOn()){
			gameMusic.setLooping(true);
			gameMusic.play();
		}

		gameView.apply();
		// Hex color code: #1a1a1a
		ScreenUtils.clear(.102f,.102f,.102f, 1);

		batch.begin();
		switch (state){
			case PAUSE:
				if(game.isSoundOn()) gameMusic.pause();
				break;

			case RUN:
				if(game.isSoundOn()) gameMusic.play();

				time += delta;
				score += delta*100;

				//Spawning the orb
				if (!isOrbShown) {
					float randomOrbNumber = MathUtils.random(0, 1234);
					if (1 <= randomOrbNumber && randomOrbNumber <= 1221) {
						isOrbShown = true;
					}
				}

				//Checking the player picked up the orb
				if (isOrbShown) {
					orb.draw(batch);
					orb.update(speed);
					if (Intersector.overlaps(ball.getHitbox(), orb.getHitbox())) {
						ball.setColor(orb.getColor());
						if(game.isSoundOn()) itemSound.play();
						isOrbShown = false;
						orb = new Orb(invisPath.evaluate(score));
					}

					if (orb.circle.y > h) {
						isOrbShown = false;
						orb = new Orb(invisPath.evaluate(score));
					}
				}

				//Spawning and managing obstacles
				if(time > 5/speed){
					Obstacle temp = stockedObstacle.remove();
					temp.prepare(w, ball, invisPath.evaluate(score));
					usedObstacles.add(temp);
				}

				for (Obstacle obs: usedObstacles) {
					obs.draw(batch);
					obs.update(speed);

					if(obs.getY() > h){
						needtoPop = true;
					}

					if (ball.getColor() != obs.getColor() &&
							Intersector.overlaps(ball.getHitbox(),obs.getHitbox())){
						ball.draw(batch);
						batch.end();
						if(game.isSoundOn()) collisionSound.play();
						if(game.isMusicOn()) gameMusic.stop();

						prefs = Gdx.app.getPreferences("chromafall.leaderboard");
						int hs1score = prefs.getInteger("hs1.score", 0);
						int hs2score = prefs.getInteger("hs2.score", 0);
						int hs3score = prefs.getInteger("hs3.score", 0);
						int hs4score = prefs.getInteger("hs4.score", 0);
						int hs5score = prefs.getInteger("hs5.score", 0);
						if (score > hs1score) {
							prefs.putInteger("hs1.score", score);
							prefs.putInteger("hs2.score", hs1score);
							prefs.putInteger("hs3.score", hs2score);
							prefs.putInteger("hs4.score", hs3score);
							prefs.putInteger("hs5.score", hs4score);
							prefs.flush();
						}
						else if (score > hs2score) {
							prefs.putInteger("hs2.score", score);
							prefs.putInteger("hs3.score", hs2score);
							prefs.putInteger("hs4.score", hs3score);
							prefs.putInteger("hs5.score", hs4score);
							prefs.flush();
						}
						else if (score > hs3score) {
							prefs.putInteger("hs3.score", score);
							prefs.putInteger("hs4.score", hs3score);
							prefs.putInteger("hs5.score", hs4score);
							prefs.flush();
						}
						else if (score > hs4score) {
							prefs.putInteger("hs4.score", score);
							prefs.putInteger("hs5.score", hs4score);
							prefs.flush();
						}
						else if (score > hs5score) {
							prefs.putInteger("hs5.score", score);
							prefs.flush();
						}

						menusScreen.setScore(score);
						game.setScreen(menusScreen);
						return;
					}
				}

				if(needtoPop){
					needtoPop = false;
					stockedObstacle.add(usedObstacles.remove());
				}

				// ball must be drawn after the obstacles so that it can pass
				// *over* them.
				ball.draw(batch);
				ball.update();

				//Drawing the score
				font.draw(batch,"Score : " + score,w/100f,h-font.getScaleY()-h/100f);

				//Increment
				if(time > 5/speed) {
					incremencer++;
					speed = MathUtils.log(2, incremencer)*2;
					time = 0;
				}
		}

		batch.end();
		stage.getViewport().apply();
		stage.act();
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		gameView.update(width,height,true);
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
		batch.dispose();
	}
}
