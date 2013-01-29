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
	int numberOfTargetsToCheck = 5;
	public boolean microModeEntered = false;
	
	/**
	 * Timidity
	 */
	public int timidity = 0;
	
	public Micro() {
		enemyTarget = null;
	}
	public void run() throws GameActionException{
		
		timidity = strategy.parameters.timidity;
		if (enemyRobots.length == 0)
		{
			updateFarawayEnemyTarget(1);
			rushToBattle();
		}
		else{
			setMicroVariables();
			farawayEnemyTarget = enemyTarget;
			micro();
		}
	}

	public void rushToBattle() throws GameActionException{
		//farawayEnemyTarget should be already set if micro mode is entered
		mover.setNavType(NavType.BUG);

		if (farawayEnemyTarget != null)
		{
			attackTarget(farawayEnemyTarget);
		}
		if(RC.isActive())
		{
			RC.setIndicatorString(2, "GOING TO BATTLE " + Clock.getRoundNum() + "Target: " + mover.getTarget());
			mover.execute();
		}
	}
	
	public void micro() throws GameActionException{
		if(enemyTarget != null) {		
			attackOrRetreat();
		}	
		if(RC.isActive())
		{
			RC.setIndicatorString(2, "MICRO " + Clock.getRoundNum() + " ALLY WEIGHT: " + allyWeight + " ENEMY WEIGHT: " + enemyWeight + " Target: " + mover.getTarget());
			mover.execute();
		}
	}

	public void setMicroVariables() throws GameActionException{
//		setEnemyTargetAndWeight();
		setEnemyTarget(numberOfTargetsToCheck);
//		MapLocation m = averageMapLocation(enemyTarget, currentLocation, 2/3);
		//cheap micro
		if (timidity < -5)
		{
			enemyWeight = -10000;
			allyWeight = 100000;
		}
		else
		{
			MapLocation m = enemyTarget;
			setEnemyWeight(m, sensorRadius);
			setAllyWeight(m, sensorRadius);
		}
	}
	private MapLocation averageMapLocation(MapLocation m1, MapLocation m2, double k)
	{
		return new MapLocation((int)((k*m1.x+(1-k)*m2.x)), (int)(k*m1.y+(1-k)*m2.y));
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
		//TODO: Account for robot types!!!
		if (enemyTarget.distanceSquaredTo(RC.getLocation())<= 2 || naiveDistance(ALLY_HQ, currentLocation) <= 6 && 
				(naiveDistance(ALLY_HQ, currentLocation) > naiveDistance(ALLY_HQ, ENEMY_HQ)/3))
		{
			mover.setTarget(currentLocation);
		}
		else if (!shouldIAttack())
		{
			if (RC.senseNearbyGameObjects(Robot.class, enemyTarget, 81, ALLY_TEAM).length < 12)
			{
				setRetreatBack();
				mover.setNavType(NavType.BUG);
				mover.setTarget(retreatTarget);
			}
			else
			{
				mover.setTarget(currentLocation);
			}
		}
		else
		{
			mover.setNavType(NavType.BUG);
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
			retreatTarget = currentLocation.add(RC.getLocation().directionTo(enemyTarget).opposite(), 2);
		}
		else
		{
			retreatTarget = ALLY_HQ;
		}
	}
	
	// Determines whether there are enough allies nearby to engage
	public boolean shouldIAttack() throws GameActionException
	{
		if(15*allyWeight > (15+timidity)*enemyWeight)
		{
			return true;
		}		
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