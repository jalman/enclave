package preSprintBot.soldiers.mineLay;

import preSprintBot.utils.Utils;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class MineLayer {
	/**
	 * This should probably just be moved into soldier behavior.
	 */
	RobotController rc;
	MapLocation[] a; // array of allied encampments
	MapLocation c; //  

	public double mineProb;

	public MineLayer(RobotController rc) {
		this.rc = rc;
		c = rc.getLocation();
		a = rc.senseAllEncampmentSquares();
		mineProb = 0;
	}
	
	/** 
	 * Generates a probability for mines to be laid.
	 */	
	
	public void randomize(){
		mineProb = rc.senseHQLocation().distanceSquaredTo(rc.getLocation()) + rc.senseEnemyHQLocation().distanceSquaredTo(rc.getLocation());
		mineProb /= rc.senseHQLocation().distanceSquaredTo(rc.senseEnemyHQLocation());
		mineProb *= mineProb;
		mineProb *= 40;
		mineProb /= rc.getMapWidth();
		mineProb /= rc.getMapHeight();
		if(rc.senseMine(rc.getLocation()) == Utils.ALLY_TEAM) {
			mineProb = 0.0;
		}
	}
	
	public boolean adjacentToEncampment()
	{
		c = rc.getLocation();
		a = rc.senseAlliedEncampmentSquares();
		if (a != null && a.length !=0)
		{
			if (Utils.naiveDistance(Utils.closest(a,c),c)==1)
			{
				return true;
			}
		}
		return false;
	}
	
	public void mineAroundEncampment() throws GameActionException
	{
		if (adjacentToEncampment())
		{	
			if(rc.senseMine(rc.getLocation())==null)
			{	
				rc.layMine();
			}
		}
		else
			rc.setIndicatorString(0, "ERROR");
	}
	
	/**
	 * lays mines with some probability.
	 * @throws GameActionException
	 */
	public void randomMine() throws GameActionException {
		
	}
	
	
	
}
