package team059.soldiers.micro;

import team059.messaging.MessagingSystem;
import team059.movement.Mover;
import team059.movement.NavType;
import team059.soldiers.SoldierBehavior2;
import static team059.utils.Utils.*;
import battlecode.common.*;
import static team059.soldiers.SoldierUtils.*;

public class Micro2 {
	
	MapLocation retreatTarget = null;
	MapLocation encampTarget = null;
	public int goIn = 0;
	private static final int maxNumberOfEnemiesToCheckToFindATarget = 5;
	public static final Mover mover = new Mover();
	public SoldierBehavior2 sb;
	int count = 0;
	
	public static int sensorRadius = 11; // The radius the RC uses to detect enemies and allies. This distance.
	
	public Micro2(SoldierBehavior2 sb) {		
		enemyTarget = null;
		this.sb = sb;
	}
	
	public void run() throws GameActionException{
		if (count % 2 == 0)
		{
			setVariables();
		}
		if(enemyTarget != null && sb.battleSpotAge >= 4)
		{
			messagingSystem.writeMicroMessage(enemyTarget, goIn);
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
			retreatTarget = currentLocation.add(RC.getLocation().directionTo(enemyTarget).opposite(), 2);
		}
		else
		{
			retreatTarget = ALLY_HQ;
		}
	}
	public void setRetreatEncampment() throws GameActionException //makes the retreat target the nearest encampment
	{
		// fill in with messaging
	}

	/**
	 * Methods for detecting Allies and Enemies within a certain radius
	 */
	
	// Determines whether there are enough allies nearby to engage
	public boolean shouldIAttack() throws GameActionException
	{
		if(allyWeight > enemyWeight)
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
		
}