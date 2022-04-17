package com.mygdx.chromafall;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.awt.Font;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;


public class GameScreen implements Screen {

	private final int w = Gdx.graphics.getWidth();
	private final int h = Gdx.graphics.getHeight();
	private final InvisiblePath invisPath;

	private Viewport gameView;
	private SpriteBatch batch;
	private Ball ball;

	private Queue<Obstacle> usedObstacles;
	private MyGdxGame game;
	private Screen menusScreen;
	private FreeTypeFontGenerator generator;
	private BitmapFont font;
	private Queue<Obstacle> stockedObstacle;
	private float time;
	private float speed = 5;
	private int incremencer = 10;
	private boolean needtoPop = false;
	private int score = 0;
	private Button pauseButton;
	private Stage stage;
	private Orb orb;
	private boolean isOrbShown = false;

	private Music gameMusic;
	private Sound itemSound;
	private Sound collisionSound;
	private Sound open;
	private Sound close;

	private enum State{
		PAUSE,
		RUN,
		RESUME,
		DEATH
	}

	private State state = State.RUN;

	public GameScreen(final MyGdxGame game, Screen menusScreen) {
		//save old
		this.game = game;
		this.menusScreen = menusScreen;

		//init
		stockedObstacle = new LinkedList<Obstacle>();
		usedObstacles = new LinkedList<Obstacle>();

		//creat all the needed
		this.gameView = new ExtendViewport(w,h);
		this.ball = new Ball(w/2f,h-h/12f,w/16f);
		this.batch = new SpriteBatch();

		generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/myFont.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.color = Color.WHITE;
		parameter.size = w/20;
		font = generator.generateFont(parameter);

		invisPath = new InvisiblePath(1);
		orb = new Orb(invisPath.evaluate(0));

		for (int i = 0; i < 30; i++) {
			stockedObstacle.add(new Obstacle());
		}

		Obstacle temp = stockedObstacle.remove();
		temp.prepare(w, this.ball, invisPath.evaluate(0));
		usedObstacles.add(temp);

		//bouton pause
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

		stage = new Stage(gameView);
		stage.addActor(pauseButton);
		Gdx.input.setInputProcessor(stage);

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
			    GlyphLayout layout = new GlyphLayout();
			    String pauseText = "Game is paused.\n\nTap on the button in the upper right corner to resume the game.";
			    layout.setText(font, pauseText, Color.WHITE, w, Align.center, true);
				font.draw(batch,layout, 0,h/2f + layout.height/2);
				break;

			case RUN:
				time += delta;
				score += delta*100;

				if (!isOrbShown) {
					float randomOrbNumber = MathUtils.random(0, 1234);
					if (1 <= randomOrbNumber && randomOrbNumber <= 1221) {
						isOrbShown = true;
					}
				}

				if (isOrbShown) {
					orb.draw(batch);
					orb.update(speed);
					if (Intersector.overlaps(ball.getHitbox(), orb.getHitbox())) {
						ball.setColor(orb.getColor());
						if(game.isSoundOn()) itemSound.play();
					}

					if (orb.circle.y > h) {
						isOrbShown = false;
						orb = new Orb(invisPath.evaluate(score));
					}
				}

				//obstacle
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
						game.setScreen(new DeathScreen(score, menusScreen, game));
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

				//score
				font.draw(batch,"Score : " + score,w/100f,h-font.getScaleY()-h/100f);

				//increment
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
