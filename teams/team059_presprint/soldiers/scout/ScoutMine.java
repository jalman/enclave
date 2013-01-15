package team059_presprint.soldiers.scout;

import battlecode.common.*;
import team059_presprint.RobotBehavior;
import team059_presprint.messaging.MessageHandler;
import team059_presprint.movement.Mover;
import team059_presprint.soldiers.mineLay.MineLayer;
import team059_presprint.utils.Utils;

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
