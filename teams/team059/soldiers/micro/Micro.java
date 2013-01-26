package team059.soldiers.micro;

import team059.messaging.MessagingSystem;
import team059.movement.Mover;
import team059.movement.NavType;
import team059.soldiers.SoldierBehavior2;
import team059.utils.Utils;
import static team059.utils.Utils.*;
import battlecode.common.*;
import static team059.soldiers.SoldierUtils.*;

public class Micro {
	
	MapLocation retreatTarget = null;
	public int goIn = 0;
	public static final Mover mover = new Mover();
	int count = 0;
	public boolean microModeEntered = false;
	
	public Micro() {
		enemyTarget = null;
	}
	
	public void rushToBattle() throws GameActionException{
		//farawayEnemyTarget should be set if micro mode is entered
		if (enemyRobots.length == 0)
		{
			attackTarget(farawayEnemyTarget);
		}
	}
	public void micro() throws GameActionException{
		setMicroVariables();
		if(enemyTarget != null) {		
			attackOrRetreat();
		}	
		if(RC.isActive())
			mover.execute();
	}
	
	public void run() throws GameActionException{
		if (enemyRobots.length == 0)
		{
			rushToBattle();
		}
		else{
			microModeEntered = true;
			micro();
		}
	}
	public boolean shouldIBeRunningMicroSystem(){
		if (microModeEntered = true && enemyRobots.length == 0)
		{
			microModeEntered = false;
			return false;
		}
		return true;
	}
	
	public void setBattleVariables() throws GameActionException{
		
	}
	public void setMicroVariables() throws GameActionException{
		setEnemyTargetAndWeight();
		setAllyWeight(enemyTarget, sensorRadius);
	}
	
	/**
	 * Rushes into battle if it can see an enemy nearby. 
	 * @throws GameActionException 
	 */
	
	/**
	 * Determines whether a soldier should attack or retreat
	 * If: adjacent enemies: stay put. 
	 * Else if: there aren't enough allies nearby to engage: retreat
	 * Else: attack
	 * @throws GameActionException
	 */
	
	public void attackOrRetreat() throws GameActionException{
		setRetreatBack();
		if (enemyTarget.distanceSquaredTo(RC.getLocation())<= 2)
		{
			mover.setTarget(RC.getLocation());
		}
		else if (!shouldIAttack())
		{
			setRetreatBack();
			mover.setTarget(retreatTarget);
		}
		else
		{	
			attackTarget(enemyTarget);
		}
	}

	/**
	 * Sets the retreat target.
	 * @throws GameActionException
	 */
	public void setRetreatBack() throws GameActionException
	{
		if (enemyTarget != null)
		{
			retreatTarget = currentLocation.add(RC.getLocation().directionTo(enemyTarget).opposite());
		}
		else
		{
			retreatTarget = ALLY_HQ;
		}
	}
	
	// Determines whether there are enough allies nearby to engage
	public boolean shouldIAttack() throws GameActionException
	{
		if(allyWeight > enemyWeight && allyWeight > 1)
		{
			mover.setNavType(NavType.BUG_DIG_2);
			return true;
		}		
		mover.setNavType(NavType.BUG);
		return false;
	}
		
	public void attackTarget(MapLocation m) throws GameActionException
	{
		if (RC.getLocation().distanceSquaredTo(m) > 2)
		{
			mover.setTarget(m);
		}
		else
		{
			mover.setTarget(RC.getLocation());
		}
	}
//	public void goToBattle(int mapLocX, int mapLocY){
//		MapLocation tempBattleSpot = new MapLocation(mapLocX, mapLocY);
//		if (battleSpot == null || naiveDistance(tempBattleSpot, currentLocation) < naiveDistance(battleSpot, currentLocation) 
//				||  battleSpotAge >= 4)
//		{
//			battleSpot = tempBattleSpot;
//			battleSpotAge = 0;
//		}
//		int distance = Utils.naiveDistance(battleSpot, Utils.currentLocation);
//		if(distance < 11 && distance > 3)
//		{
//			mover.setTarget(battleSpot);
//		}
//	}	
}