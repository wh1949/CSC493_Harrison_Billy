/**
 * Author: Billy Harrison
 *
 * Date: 10/1/16
 *
 * Class: Game Design
 */
package com.billyharrisongdx.game.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch ;
import com.badlogic.gdx.graphics.g2d.TextureRegion ;
import com.billyharrisongdx.game.game.Assets ;
import com.badlogic.gdx.math.MathUtils ;

public class Fire extends AbstractGameObject
{
	/**
	 * Holds Fire texture region and collected status
	 */
	private TextureRegion regFire ;

	public boolean collected ;

	public Fire()
	{
		init() ;
	}

	/**
	 * Sets fire size, region, bounds, and collected status
	 */
	private void init()
	{

		dimension.set(0.5f, 0.5f) ;

		setAnimation(Assets.instance.fire.animFire) ;
		stateTime = MathUtils.random(0.0f, 1.0f) ;

		// Set bounding box for collision detection
		bounds.set(0, 0, dimension.x, dimension.y) ;

		collected = false ;
	}

	/**
	 * Draws feather in specified location
	 */
	public void render(SpriteBatch batch)
	{
		if(collected) return ;

		TextureRegion reg = null ;
		reg = animation.getKeyFrame(stateTime, true) ;
		batch.draw(reg.getTexture(), position.x, position.y, origin.x, origin.y,
				dimension.x, dimension.y,scale.x, scale.y,rotation,reg.getRegionX(),
				reg.getRegionY(), reg.getRegionWidth(),reg.getRegionHeight(), false, false) ;
	}

	/**
	 * Returns Feather point value
	 */
	public static int getScore()
	{
		return 250 ;
	}
}
