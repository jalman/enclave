package team059.soldiers.scout;

import team059.RobotBehavior;
import team059.movement.Mover;
import team059.messaging.MessageHandler;
import team059.utils.Utils;
import battlecode.common.*;
import team059.soldiers.mineLay.MineLayer;

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
