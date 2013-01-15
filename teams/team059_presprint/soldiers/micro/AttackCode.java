package team059_presprint.soldiers.micro;

import team059_presprint.movement.Mover;
import team059_presprint.soldiers.SoldierBehavior;
import team059_presprint.utils.Utils;
import battlecode.common.*;

public class AttackCode {
	/** 
	 * Nearby allies will flock to a battle.
	 */
	Micro micro;
	RobotController rc;
	public final Mover mover;
	MapLocation target;
	SoldierBehavior sb;
	
	public AttackCode(Micro micro){
		this.micro = micro;
		mover = micro.mover;
		rc = micro.rc;
	}
	
	public void run(){
		try {
			goToBattle();
		} catch (GameActionException e) {
			e.printStackTrace();
		}
	}
	
	public void goToBattle() throws GameActionException
	{ 
		micro.sb.attackTarget(mover.getTarget());
	}
}
