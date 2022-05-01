package com.mygdx.chromafall;



import com.badlogic.gdx.Input;
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
	public Preferences prefs;
	private long deathTime;
	public String highscoreName;

	//Sound and music
	private Music gameMusic;
	private Sound itemSound;
	private Sound collisionSound;
	private Sound open;
	private Sound close;
	private boolean isGameMenu;
	public final ImageTextButton soundButton;
	public final ImageTextButton musicButton;

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
		isGameMenu = true;
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
		soundButton = new ImageTextButton("Sound : ON",buttonStyle);
		musicButton = new ImageTextButton("Music : ON",buttonStyle);
		ImageTextButton backButton  = new ImageTextButton("Back",buttonStyle);
		ImageTextButton giveUpButton  = new ImageTextButton("Give up",buttonStyle);

		// Listeners
		pauseButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				isGameMenu = false;
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
				isGameMenu = true;
				if(game.isSoundOn()) close.play();
				if(game.isMusicOn()) gameMusic.play();
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
					menusScreen.soundButton.setText("Sound : OFF");
					menusScreen.deathSoundButton.setText(("Sound : OFF"));
					game.setSoundOn(false);
				} else {
					game.setSoundOn(true);
					open.play();
					soundButton.setText("Sound : ON");
					menusScreen.soundButton.setText("Sound : ON");
					menusScreen.deathSoundButton.setText(("Sound : ON"));
				}
			}
		});

		musicButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(game.isMusicOn()) {
					if(game.isSoundOn()) close.play();
					musicButton.setText("Music : OFF");
					menusScreen.musicButton.setText("Music : OFF");
					menusScreen.deathMusicButton.setText("Music : OFF");
					game.setMusicOn(false);
				} else {
					if(game.isSoundOn()) open.play();
					musicButton.setText("Music : ON");
					menusScreen.musicButton.setText("Music : ON");
					menusScreen.deathMusicButton.setText("Music : ON");
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

		giveUpButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(game.isSoundOn()) close.play();
				if(game.isMusicOn()) gameMusic.stop();
				pauseButton.remove();
				pauseTable.remove();
				menusScreen.setScore(score);
				updateHighScores();
				game.setScreen(menusScreen);
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
		pauseTable.row();
		pauseTable.add(optionsButton);
		pauseTable.row();
		pauseTable.add(giveUpButton);

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
		isGameMenu = true;
		state = State.RUN;

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

	private void updateLeaderboardTable() {
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

		if (score > hs1score) {
			prefs.putInteger("hs1.score", score);
			prefs.putInteger("hs2.score", hs1score);
			prefs.putInteger("hs3.score", hs2score);
			prefs.putInteger("hs4.score", hs3score);
			prefs.putInteger("hs5.score", hs4score);
			prefs.putString("hs1.name", highscoreName);
			prefs.putString("hs2.name", hs1name);
			prefs.putString("hs3.name", hs2name);
			prefs.putString("hs4.name", hs3name);
			prefs.putString("hs5.name", hs4name);
			prefs.flush();
		}
		else if (score > hs2score) {
			prefs.putInteger("hs2.score", score);
			prefs.putInteger("hs3.score", hs2score);
			prefs.putInteger("hs4.score", hs3score);
			prefs.putInteger("hs5.score", hs4score);
			prefs.putString("hs2.name", highscoreName);
			prefs.putString("hs3.name", hs2name);
			prefs.putString("hs4.name", hs3name);
			prefs.putString("hs5.name", hs4name);
			prefs.flush();
		}
		else if (score > hs3score) {
			prefs.putInteger("hs3.score", score);
			prefs.putInteger("hs4.score", hs3score);
			prefs.putInteger("hs5.score", hs4score);
			prefs.putString("hs3.name", highscoreName);
			prefs.putString("hs4.name", hs3name);
			prefs.putString("hs5.name", hs4name);
			prefs.flush();
		}
		else if (score > hs4score) {
			prefs.putInteger("hs4.score", score);
			prefs.putInteger("hs5.score", hs4score);
			prefs.putString("hs4.name", highscoreName);
			prefs.putString("hs5.name", hs4name);
			prefs.flush();
		}
		else if (score > hs5score) {
			prefs.putInteger("hs5.score", score);
			prefs.putString("hs5.name", highscoreName);
			prefs.flush();
		}

		hs1score = prefs.getInteger("hs1.score", 0);
		hs2score = prefs.getInteger("hs2.score", 0);
		hs3score = prefs.getInteger("hs3.score", 0);
		hs4score = prefs.getInteger("hs4.score", 0);
		hs5score = prefs.getInteger("hs5.score", 0);

		hs1name = prefs.getString("hs1.name", "User");
		hs2name = prefs.getString("hs2.name", "User");
		hs3name = prefs.getString("hs3.name", "User");
		hs4name = prefs.getString("hs4.name", "User");
		hs5name = prefs.getString("hs5.name", "User");

		menusScreen.highScores.setText("1. "+hs1name+" : "+hs1score+"\n"+
									   "2. "+hs2name+" : "+hs2score+"\n"+
									   "3. "+hs3name+" : "+hs3score+"\n"+
									   "4. "+hs4name+" : "+hs4score+"\n"+
									   "5. "+hs5name+" : "+hs5score);
	}

	private void updateHighScores() {
		prefs = Gdx.app.getPreferences("chromafall.leaderboard");

		if (score > prefs.getInteger("hs5.score", 0)) {
			Gdx.input.getTextInput(new Input.TextInputListener() {
				@Override
				public void input(String text) {
					if (text.length() > 7) {
						highscoreName = text.substring(0,7);
					} else {
						highscoreName = text;
					}
					updateLeaderboardTable();
				}

				@Override
				public void canceled() {
					highscoreName = "User";
					updateLeaderboardTable();
				}
			}, "New high score ! Enter a name\n(max. 7 characters)", "User", "Hint Value");
		}
	}

	@Override
	public void render(float delta) {
		if(game.isMusicOn() && isGameMenu){
			gameMusic.setLooping(true);
			gameMusic.play();
		}

		// Hex color code: #1a1a1a
		ScreenUtils.clear(.102f,.102f,.102f, 1);

		batch.begin();
		switch (state){
			case PAUSE:
				break;

			case DEATH:
				if(game.isMusicOn()) gameMusic.stop();
				font.draw(batch,"Score : " + score,w/100f,h-font.getScaleY()-h/100f);
				ball.draw(batch);
				if (isOrbShown) {
					orb.draw(batch);
				}
				for (Obstacle obs: usedObstacles) {
					obs.draw(batch);
				}
				if (System.currentTimeMillis() - deathTime > 1500) {
					updateHighScores();
					batch.end();
					game.setScreen(menusScreen);
					return;
				}
				break;

			case RUN:
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
					if (state == State.RUN) {
						orb.update(speed);
					}
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

					if(obs.getY() > h){
						needtoPop = true;
					}

					if (ball.getColor() != obs.getColor() &&
							Intersector.overlaps(ball.getHitbox(),obs.getHitbox())){
						if(game.isSoundOn()) collisionSound.play();
						if(game.isMusicOn()) gameMusic.stop();

						pauseButton.remove();

						menusScreen.setScore(score);

						deathTime = System.currentTimeMillis();

						state = State.DEATH;
					}

					if (state == State.RUN) {
						obs.update(speed);
					}
				}

				if(needtoPop){
					needtoPop = false;
					stockedObstacle.add(usedObstacles.remove());
				}

				// ball must be drawn after the obstacles so that it can pass
				// *over* them.
				ball.draw(batch);
				if (state == State.RUN) {
					ball.update();
				}

				//Drawing the score
				font.draw(batch,"Score : " + score,w/100f,h-font.getScaleY()-h/100f);

				//Increment
				if(time > 5/speed) {
					incremencer++;
					speed = MathUtils.log(2, incremencer)*2;
					time = 0;
				}
				break;
		}

		batch.end();
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
