package team059.soldiers.micro;


import battlecode.common.*;
import team059.messaging.MessagingSystem;
import team059.movement.Mover;
import team059.movement.NavType;
import static team059.utils.Utils.*;
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
	public void run() throws GameActionException{
		
		if (enemyRobots.length == 0)
		{
			RC.setIndicatorString(2, "GOING TO BATTLE " + Clock.getRoundNum());
			mover.setNavType(NavType.BUG_DIG_2);
			updateFarawayEnemyTarget(2);
			rushToBattle();
		}
		else{
			RC.setIndicatorString(2, "MICRO " + Clock.getRoundNum());
			setMicroVariables();
			farawayEnemyTarget = enemyTarget;
			microModeEntered = true;
			micro();
		}
	}

	public void rushToBattle() throws GameActionException{
		//farawayEnemyTarget should be already set if micro mode is entered
		if (enemyRobots.length == 0 && farawayEnemyTarget != null)
		{
			attackTarget(farawayEnemyTarget);
		}
		if(RC.isActive())
			mover.execute();
	}
	public void micro() throws GameActionException{
		if(enemyTarget != null) {		
			attackOrRetreat();
		}	
		if(RC.isActive())
			mover.execute();
	}
//	public boolean shouldIBeRunningMicroSystem(){
//		if (farawayEnemyTarget == null)
//		{
//			microModeEntered = false;
//			return false;
//		}
//		return true;
//	}
	public void setBattleVariables() throws GameActionException{
		
	}
	public void setMicroVariables() throws GameActionException{
		setEnemyTargetAndWeight();
		setAllyWeight(enemyTarget, sensorRadius);
	}
	
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
//				||  battleSpotAge >= 2)
//		{
//			battleSpot = tempBattleSpot;
//			battleSpotAge = 0;
//		}
//		int distanceSquared = battleSpot.distanceSquaredTo(currentLocation);
//		if(distanceSquared < closeEnoughToGoToBattleSquared && distance > ENEMY_RADIUS2)
//		{
//			mover.setTarget(battleSpot);
//		}
//	}	
}