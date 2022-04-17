package com.mygdx.chromafall;


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
	private float speed = 5;
	private int incremencer = 10;
	private boolean needtoPop = false;
	private int score = 0;
	private final InvisiblePath invisPath;

	//Sound and music
	private Music gameMusic;
	private Sound itemSound;
	private Sound collisionSound;
	private Sound open;
	private Sound close;

	//Defining the state of the game
	private enum State{
		PAUSE,
		RUN,
		RESUME,
		DEATH
	}

	private State state = State.RUN;

	public GameScreen(final MyGdxGame game, MenuScreen menusScreen) {
		this.game = game;
		this.menusScreen = menusScreen;

		//Initialising the viewport
		this.gameView = new ExtendViewport(w,h);

		//Defining the font in order to write text
		generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/myFont.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.color = Color.WHITE;
		parameter.size = w/20;
		font = generator.generateFont(parameter);

		//Initialising game objects
		stockedObstacle = new LinkedList<Obstacle>();
		usedObstacles = new LinkedList<Obstacle>();

		this.ball = new Ball(w/2f,h-h/12f,w/16f);
		this.batch = new SpriteBatch();

		invisPath = new InvisiblePath(1);
		orb = new Orb(invisPath.evaluate(0));

		for (int i = 0; i < 30; i++) {
			stockedObstacle.add(new Obstacle());
		}

		Obstacle temp = stockedObstacle.remove();
		temp.prepare(w, this.ball, invisPath.evaluate(0));
		usedObstacles.add(temp);

		//Pause button
		TextureRegionDrawable pauseb = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("PauseButton.png"))));
		TextureRegionDrawable playb = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("PlayButton.png"))));

		pauseButton = new ImageButton(pauseb,pauseb,playb);
		float pauseButtonSize = w/12f;
		pauseButton.setSize(pauseButtonSize,pauseButtonSize);
		pauseButton.setPosition(w-pauseButtonSize,h-pauseButtonSize);

		pauseButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(pauseButton.isChecked()){
					if(game.isSoundOn()) open.play();
					state = State.PAUSE;
				}
				else{
					if(game.isSoundOn()) close.play();
					state = State.RUN;
				}
			}
		});

		//Stage
		stage = new Stage(gameView);
		stage.addActor(pauseButton);
		Gdx.input.setInputProcessor(stage);

		//Sounds and music
		open = Gdx.audio.newSound(Gdx.files.internal("Sounds/open.wav"));
		close = Gdx.audio.newSound(Gdx.files.internal("Sounds/close.wav"));
		collisionSound = Gdx.audio.newSound(Gdx.files.internal("Sounds/collision.wav"));
		itemSound = Gdx.audio.newSound(Gdx.files.internal("Sounds/item.wav"));
		gameMusic = Gdx.audio.newMusic(Gdx.files.internal("Sounds/game_music.mp3"));
	}

	@Override
	public void show() {
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
				//Drawing the text and the button
				GlyphLayout layout = new GlyphLayout();
				String pauseText = "Game is paused.\n\nTap on the button in the upper right corner to resume the game.";
				layout.setText(font, pauseText, Color.WHITE, w, Align.center, true);
				font.draw(batch,layout, 0,h/2f + layout.height/2);
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
					orb.update(speed);
					if (Intersector.overlaps(ball.getHitbox(), orb.getHitbox())) {
						ball.setColor(orb.getColor());
						if(game.isSoundOn()) itemSound.play();
						//TODO: isOrbShown = false;
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
