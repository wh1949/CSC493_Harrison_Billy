/**
 * Author: Billy Harrison
 *
 * Date: 9/15/16
 *
 * Class: Game Design
 */

package com.billyharrisongdx.game.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera ;
import com.badlogic.gdx.graphics.g2d.SpriteBatch ;
import com.billyharrisongdx.game.game.objects.Character;
import com.billyharrisongdx.game.util.Constants ;
import com.badlogic.gdx.graphics.g2d.BitmapFont ;
import com.badlogic.gdx.utils.Align ;
import com.billyharrisongdx.game.util.GamePreferences ;
import com.badlogic.gdx.math.MathUtils ;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer ;

public class WorldRenderer {

	/**
	 * Creates instances of both cameras, the world controller
	 * and the sprite batch used to draw images
	 */
	private OrthographicCamera camera ;
	private OrthographicCamera cameraGUI ; // Initialize camera to display the GUI
	private SpriteBatch batch ;
	private WorldController worldController ;

	private static final boolean DEBUG_DRAW_BOX2D_WORLD = false ;
	private Box2DDebugRenderer b2debugRenderer ;

	/**
	 * sets this renderer's worldController and initiates the renderer
	 */
	public WorldRenderer(WorldController worldController)
	{
		this.worldController = worldController ;
		init() ;
	}

	/**
	 * Creates a sprite batch and orients the cameras
	 */
	private void init()
	{
		batch = new SpriteBatch() ;
		camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT) ; // Creates camera using the view port restraints
		camera.position.set(0, 0, 0) ; // Position camera at origin
		camera.update() ;

		cameraGUI = new OrthographicCamera(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT) ; // Creates new camera for GUI
		cameraGUI.position.set(0, 0, 0) ;
		cameraGUI.setToOrtho(true) ;

		// flip y-axis
		cameraGUI.update() ;

		b2debugRenderer = new Box2DDebugRenderer() ;
	}

	/**
	 * Render everything related to the two cameras
	 */
	public void render()
	{
		renderWorld(batch) ; // Renders the game world
		renderGui(batch) ; // Renders game's GUI
	}

	/**
	 * Draws the level including background, character,
	 * items, land, and water
	 */
	private void renderWorld(SpriteBatch batch)
	{
		worldController.cameraHelper.applyTo(camera) ;
		batch.setProjectionMatrix(camera.combined) ;
		batch.begin() ;
		worldController.level.render(batch) ;
		batch.end() ;

		if(DEBUG_DRAW_BOX2D_WORLD)
		{
			b2debugRenderer.render(worldController.world, camera.combined) ;
		}
	}

	/**
	 * Alters size of the view port
	 */
	public void resize(int width, int height)
	{
		camera.viewportWidth = (Constants.VIEWPORT_HEIGHT / height) * width ;
		camera.update() ;

		cameraGUI.viewportHeight = Constants.VIEWPORT_GUI_HEIGHT ;
		cameraGUI.viewportWidth = (Constants.VIEWPORT_GUI_HEIGHT / (float) height) * (float)width ;
		cameraGUI.position.set(cameraGUI.viewportWidth / 2, cameraGUI.viewportHeight / 2, 0) ;
		cameraGUI.update() ;
	}

	/**
	 * Sets the location for, and draws, the score icon
	 */
	private void renderGuiScore(SpriteBatch batch)
	{
		float x = -15 ;
		float y = -15 ;
		float offsetX = 50 ;
		float offsetY = 50 ;
		if(worldController.scoreVisual < worldController.score)
		{
			long shakeAlpha = System.currentTimeMillis() % 360 ;
			float shakeDist = 1.5f ;
			offsetX += MathUtils.sinDeg(shakeAlpha * 2.2f) * shakeDist ;
			offsetY += MathUtils.sinDeg(shakeAlpha * 2.9f) * shakeDist ;
		}
		batch.draw(Assets.instance.ice.ice, x, y, offsetX, offsetY, 100, 100, 0.35f, -0.35f, 0) ;
		Assets.instance.fonts.defaultBig.draw(batch, "" + (int)worldController.scoreVisual, x + 75, y + 37) ;
	}

	/**
	 * Sets the location for, and draws, the lives icons
	 * displayed as gray if life is lost
	 */
	private void renderGuiExtraLive(SpriteBatch batch)
	{
		float x = cameraGUI.viewportWidth - 50 - Constants.LIVES_START * 50 ;
		float y = -15 ;
		for(int i = 0; i < Constants.LIVES_START; i++)
		{
			if(worldController.lives <= i)
			{
				batch.setColor(0.5f, 0.5f, 0.5f, 0.5f) ;
			}
			batch.draw(Assets.instance.character.characterHead, x + i * 50, y, 50, 50, 120, 100, 0.35f, -0.35f, 0) ;
			batch.setColor(1, 1, 1, 1) ;
		}
		if(worldController.lives >= 0 && worldController.livesVisual > worldController.lives)
		{
			int i = worldController.lives ;
			float alphaColor = Math.max(0, worldController.livesVisual - worldController.lives - 0.5f) ;
			float alphaScale = 0.35f * (2 + worldController.lives - worldController.livesVisual) * 2 ;
			float alphaRotate = -45 * alphaColor ;
			batch.setColor(0.7f, 0.7f, 1.0f, alphaColor) ;
			batch.draw(Assets.instance.character.characterHead, x + i * 50, y, 50, 50, 120, 100, alphaScale, -alphaScale, alphaRotate) ;
			batch.setColor(1, 1, 1, 1) ;
		}
	}

	/**
	 * sets the location for, and draws, the FPS Counter
	 */
	private void renderGuiFpsCounter(SpriteBatch batch)
	{
		float x = cameraGUI.viewportWidth - 55 ;
		float y = cameraGUI.viewportHeight - 15 ;
		int fps = Gdx.graphics.getFramesPerSecond() ;
		BitmapFont fpsFont = Assets.instance.fonts.defaultNormal ;
		if(fps >= 45)
		{
			// 45 or more FPS show up in green
			fpsFont.setColor(0, 1, 0, 1) ;
		}
		else if(fps >= 30)
		{
			// 30 or more FPS show up in yellow
			fpsFont.setColor(1, 1, 0, 1) ;
		}
		else
		{
			// Less than 30 FPS show up in red
			fpsFont.setColor(1, 0, 0, 1) ;
		}
		fpsFont.draw(batch, "FPS: " + fps, x, y) ;
		fpsFont.setColor(1, 1, 1, 1) ; // White
	}

	/**
	 * Calls all GUI render methods to draw entire GUI
	 */
	private void renderGui(SpriteBatch batch)
	{
		batch.setProjectionMatrix(cameraGUI.combined) ;
		batch.begin() ;

		// Draw collected ice icon + text
		// (anchored to top left edge)
		renderGuiScore(batch) ;
		// Draw collected fire icon (anchored to top left edge)
		renderGuiFirePowerup(batch) ;
		// Draw extra lives icon + text (anchored to top right edge)
		renderGuiExtraLive(batch) ;
		// Draw FPS text if box is checked(anchored to bottom right edge)
		if(GamePreferences.instance.showFpsCounter)
		{
			renderGuiFpsCounter(batch) ;
		}
		// Draw game over text
		renderGuiGameOverMessage(batch) ;
		renderGuiNextLevelMessage(batch) ;

		batch.end() ;
	}

	/**
	 * Display game over message in the center of the GUI
	 */
	private void renderGuiGameOverMessage(SpriteBatch batch)
	{
		float x = cameraGUI.viewportWidth / 2 ;
		float y = cameraGUI.viewportHeight / 2 ;
		if(worldController.isGameOver())
		{
			BitmapFont fontGameOver = Assets.instance.fonts.defaultBig ;
			fontGameOver.setColor(1, 0.75f, 0.25f, 1) ;
			fontGameOver.draw(batch, "GAME OVER", x, y, 0, Align.center, false) ;
			fontGameOver.setColor(1, 1, 1, 1) ;
		}
	}

	private void renderGuiNextLevelMessage(SpriteBatch batch)
	{
		float x = cameraGUI.viewportWidth / 2 ;
		float y = cameraGUI.viewportHeight / 2 ;
		if(worldController.level.goalReached && worldController.levelNum == Constants.LEVEL_01)
		{
			BitmapFont fontGameOver = Assets.instance.fonts.defaultBig ;
			fontGameOver.setColor(1, 0.75f, 0.25f, 1) ;
			fontGameOver.draw(batch, "Level Complete", x, y, 0, Align.center, false) ;
			fontGameOver.setColor(1, 1, 1, 1) ;
		}
		else if(worldController.level.goalReached && worldController.levelNum == Constants.LEVEL_02)
		{
			BitmapFont fontGameOver = Assets.instance.fonts.defaultBig ;
			fontGameOver.setColor(1, 0.75f, 0.25f, 1) ;
			fontGameOver.draw(batch, "Congratulations!", x, y, 0, Align.center, false) ;
			fontGameOver.setColor(1, 1, 1, 1) ;
		}
	}

	/**
	 * Display the fire icon and remaining time when the
	 * fire power-up  is acquired
	 */
	private void renderGuiFirePowerup(SpriteBatch batch)
	{
		float x = -15 ;
		float y = 30 ;
		float timeLeftFirePowerup = Character.timeLeftFirePowerup ;

		if(timeLeftFirePowerup > 0)
		{
			// Start icon fade in/out if the left power-up time
			// is less than 4 seconds. The fade interval is set
			// to 5 changes per second
			if(timeLeftFirePowerup < 4)
			{
				if(((int)(timeLeftFirePowerup * 5) % 2) != 0)
				{
					batch.setColor(1, 1, 1, 0.5f) ;
				}
			}
			batch.draw(Assets.instance.fire.fire, x, y, 50, 50, 100, 100, 0.35f, -0.35f, 0) ;
			batch.setColor(1, 1, 1, 1) ;
			Assets.instance.fonts.defaultSmall.draw(batch, "" + (int)timeLeftFirePowerup, x + 60, y + 57) ;
		}
	}

	/**
	 * Acts as a clean up, releasing all resources
	 */
	public void dispose()
	{
		batch.dispose() ;
	}
}