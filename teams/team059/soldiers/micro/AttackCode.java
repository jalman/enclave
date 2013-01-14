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
	
	public static int r;
	public final Mover mover;
	MapLocation c; //current location
	MapLocation target;
	
	public AttackCode(Micro micro){
		mover = micro.mover;
		c = micro.c;
	}
	
	public void run(){
		c = micro.c;
	}
	
	public void goToBattle() throws GameActionException
	{
		target = micro.closestSoldierTarget(micro.enemySoldiers); 
		mover.setTarget(target);
	}
}
