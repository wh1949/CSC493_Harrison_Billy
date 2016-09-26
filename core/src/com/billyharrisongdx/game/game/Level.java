package com.billyharrisongdx.game.game;

import com.badlogic.gdx.Gdx ;
import com.badlogic.gdx.graphics.Pixmap ;
import com.badlogic.gdx.graphics.g2d.SpriteBatch ;
import com.badlogic.gdx.utils.Array ;
import com.billyharrisongdx.game.game.objects.AbstractGameObject ;
import com.billyharrisongdx.game.game.objects.Ground ;
import com.billyharrisongdx.game.game.objects.Mountains ;
import com.billyharrisongdx.game.game.objects.LavaOverlay ;
import com.billyharrisongdx.game.game.objects.Trees ;
import com.billyharrisongdx.game.game.objects.Volcano ;

public class Level
{
	public static final String TAG = Level.class.getName() ;

	/**
	 * Color data used to compare from the
	 * level map to determine what block
	 * is to be drawn
	 */
	public enum BLOCK_TYPE
	{
		EMPTY(0, 0, 0), // Black
		GROUND(0, 255, 0), // Green
		PLAYER_SPAWNPOINT(255, 255, 255), // White
		FIRE(255, 0, 255), // Purple
		ICE(255, 255, 0) ; // Yellow

		private int color ;

		/**
		 * Stores color values into one int for easy comparison
		 * @param r
		 * @param g
		 * @param b
		 */
		private BLOCK_TYPE(int r, int g, int b)
		{
			color = r << 24 | g << 16 | b << 8 | 0xff ;
		}

		/**
		 * Compares to colors
		 * @param Color
		 * @return true if colors are the same
		 */
		public boolean sameColor(int Color)
		{
			return this.color == Color ;
		}

		/**
		 * @return the color of a block
		 */
		public int getColor()
		{
			return color ;
		}
	}
		// Objects
		public Array<Ground> grounds ;

		// Decoration
		public Trees trees ;
		public Mountains mountains ;
		public LavaOverlay lavaOverlay ;
		public Volcano volcano ;

		/**
		 * Initiates a level using the input filename
		 * @param filename
		 */
		public Level(String filename)
		{
			init(filename) ;
		}

		private void init(String filename)
		{
			// Objects
			grounds = new Array<Ground>() ;

			// Load image file that represents the level data
			Pixmap pixmap = new Pixmap(Gdx.files.internal(filename)) ;
			// Scan pixels from top-left to bottom-right
			int lastPixel = -1 ;
			for(int pixelY = 0; pixelY < pixmap.getHeight(); pixelY++)
			{
				for(int pixelX = 0; pixelX < pixmap.getWidth(); pixelX++)
				{
					AbstractGameObject obj = null ;
					float offsetHeight = 0 ;
					// Height grows from bottom to top
					float baseHeight = pixmap.getHeight() - pixelY ;
					// Get color of current pixel as 32-bit RGBA value
					int currentPixel = pixmap.getPixel(pixelX, pixelY) ;
					// Find matching color value to identify block type at (x, y)
					// point and create the corresponding game object if there is
					// a match

					// Empty space
					if(BLOCK_TYPE.EMPTY.sameColor(currentPixel))
					{
						// Do nothing
					}
					// Ground
					else if(BLOCK_TYPE.GROUND.sameColor(currentPixel))
					{
						if(lastPixel != currentPixel)
						{
							obj = new Ground() ;
							float heightIncreaseFactor = 0.25f ;
							offsetHeight = -2.5f ;
							obj.position.set(pixelX, baseHeight * obj.dimension.y * heightIncreaseFactor + offsetHeight) ;
							grounds.add((Ground) obj) ;
						}
						else
						{
							grounds.get(grounds.size - 1).increaseLength(1) ;
						}
					}
					// Player spawn point
					else if(BLOCK_TYPE.PLAYER_SPAWNPOINT.sameColor(currentPixel))
					{
					}
					// Fire
					else if(BLOCK_TYPE.FIRE.sameColor(currentPixel))
					{
					}
					// Ice
					else if(BLOCK_TYPE.ICE.sameColor(currentPixel))
					{
					}
					// Unknown object/pixel color
					else
					{
						int r = 0xff & (currentPixel >>> 24) ; // Red color channel
						int g = 0xff & (currentPixel >>> 16) ; // Green color channel
						int b = 0xff & (currentPixel >>> 8) ; // Blue collor channel
						int a = 0xff & currentPixel ; // Alpha channel
						Gdx.app.error(TAG, "Unknown object at x<" + pixelX + "> y<" + pixelY + ">: r<" + r + "> g<" + g + "> b<" + b + "> a<" + a + ">") ;
					}
					lastPixel = currentPixel ;
				}
			}

			// Decoration
			trees = new Trees(pixmap.getWidth()) ;
			trees.position.set(0, 2) ;
			mountains = new Mountains(pixmap.getWidth()) ;
			mountains.position.set(-1, -1) ;
			lavaOverlay = new LavaOverlay(pixmap.getWidth()) ;
			lavaOverlay.position.set(0, -3.75f) ;
			volcano = new Volcano(pixmap.getWidth()) ;

			// Free memory
			pixmap.dispose() ;
			Gdx.app.debug(TAG, "level'" + filename + "' loaded") ;
		}
		public void render(SpriteBatch batch)
		{
			// Draw Volcano background
			volcano.render(batch);

			// Draw Mountains
			mountains.render(batch) ;

			// Draw Trees
//			trees.render(batch) ;

			// Draw Ground
			for (Ground ground : grounds)
			{
				ground.render(batch) ;
			}

			// Draw Lava Overlay
			lavaOverlay.render(batch) ;
		}
}