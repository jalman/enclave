package preSprintBot.soldiers.scout;

import preSprintBot.RobotBehavior;
import preSprintBot.messaging.MessageHandler;
import preSprintBot.movement.Mover;
import preSprintBot.soldiers.mineLay.MineLayer;
import preSprintBot.utils.Utils;
import battlecode.common.*;

public class ScoutMine {
	/**
	 * Soldiers will keep track of how many mines they run into
	 */
	MapLocation c;
	RobotController rc;
	
	public ScoutMine(RobotController rc){
		c = rc.getLocation();
		this.rc = rc;
	}
	public boolean onMine()
	{
		c = rc.getLocation();
		if (Utils.isEnemyMine(c)){
			return true;
		}
		return false;
	}
	
	/**
	 * Increments the number of stepped on mines.
	 */
	public void steppedOnMine(){
		
	}
	
	/**
	 * 
	 */
	
	public void dodgeMine(){
		
	}
}
