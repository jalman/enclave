package team059.soldiers.micro;

import team059.movement.Mover;
import team059.soldiers.SoldierBehavior;
import static team059.utils.Utils.*;
import battlecode.common.*;

public class AttackCode {
	/** 
	 * Nearby allies will flock to a battle.
	 */
	Micro micro;
	MapLocation target;
	SoldierBehavior sb;
	
	public AttackCode(Micro micro){
		this.micro = micro;
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
		
	}
}
