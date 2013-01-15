package team059.soldiers.micro;

import team059.movement.Mover;
import team059.soldiers.SoldierBehavior;
import team059.utils.Utils;
import battlecode.common.*;

public class AttackCode {
	/** 
	 * Nearby allies will flock to a battle.
	 */
	Micro micro;
	RobotController rc;
	public final Mover mover;
	MapLocation c; //current location
	MapLocation target;
	
	public AttackCode(Micro micro){
		this.micro = micro;
		mover = micro.mover;
		rc = micro.rc;
		c = rc.getLocation();
	}
	
	public void run(){
		c = rc.getLocation();
		/*try {
			goToBattle();
		} catch (GameActionException e) {
			e.printStackTrace();
		}*/
	}
	
	public void goToBattle() throws GameActionException
	{
		if (micro.enemyNearby(Micro.battleDistance))
		{
			target = micro.closestSoldierTarget((micro.findEnemySoldiers(Micro.battleDistance))); 
			micro.attackTarget(target);
		}
	}
}
