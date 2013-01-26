package team059.soldiers.micro;

import team059.messaging.MessagingSystem;
import team059.movement.Mover;
import team059.movement.NavType;
import team059.soldiers.SoldierBehavior2;
import team059.utils.Utils;
import static team059.utils.Utils.*;
import battlecode.common.*;
import static team059.soldiers.SoldierUtils.*;

public class MicroOld {
	
	MapLocation retreatTarget = null;
	public int goIn = 0;
	private static final int maxNumberOfEnemiesToCheckToFindATarget = 5;
	public static final Mover mover = new Mover();
	public SoldierBehavior2 sb;
	int count = 0;
	
	public MapLocation battleSpot;
	public int battleSpotAge;

	public final static int sensorRadius = 25;
	public final static int closeEnoughToGoToBattle = 11;
	public final static int microMessageAgeThreshold = 3;
	public final static int microThreshold = ENEMY_RADIUS;
	
	public MicroOld(SoldierBehavior2 sb) {		
		enemyTarget = null;
		this.sb = sb;
	}
	
	public void run() throws GameActionException{
		setVariables();
		if((enemyTarget != null && battleSpotAge >= 4) || (battleSpot != null && battleSpot.distanceSquaredTo(enemyTarget) > 36))
		{
			messagingSystem.writeMicroMessage(currentLocation, goIn);
			battleSpotAge = 0;
		}
		if(enemyTarget != null) {		
			attackOrRetreat();
		}
		if(RC.isActive())
			mover.execute();
		count++;
	}
	
	public void setVariables() throws GameActionException{
		setEnemyTargetAndWeight();
		setAllyWeight(enemyTarget, sensorRadius);
	}
	
	/**
	 * Retreats during micro if there are no adjacent enemies and enough allies nearby.
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
	 * Writes a message when enemies are nearby
	 * @throws GameActionException
	 */

	
	/**
	 * Sets the destinations to retreat to.
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
	/**
	 * Methods for detecting Allies and Enemies within a certain radius
	 */
	
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
	/**
	 * 
	 */
	public void goToBattle(int mapLocX, int mapLocY){
		MapLocation tempBattleSpot = new MapLocation(mapLocX, mapLocY);
		if (battleSpot == null || naiveDistance(tempBattleSpot, currentLocation) < naiveDistance(battleSpot, currentLocation) 
				||  battleSpotAge >= 4)
		{
			battleSpot = tempBattleSpot;
			battleSpotAge = 0;
		}
		int distance = Utils.naiveDistance(battleSpot, Utils.currentLocation);
		if(distance < 11 && distance > 3)
		{
			mover.setTarget(battleSpot);
		}
	}	
}