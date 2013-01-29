package team059.soldiers.micro;

import static team059.utils.Utils.*;
import team059.*;
import team059.soldiers.micro.Micro;
import static team059.soldiers.SoldierUtils.*;
import battlecode.common.*;

public class RushUtils {
	
	public static int locRetreatedFromTooFarSquared = 25;
	public static MapLocation locRetreatedFrom;
	public static int turnRetreated;
	public static Robot[] allyRobots;
	public static int turnSinceRetreat; // - Clock.getRoundNum()-turnRetreated();
	public RushUtils(){

	}
	public static void updateRushUtils() throws GameActionException
	{
		allyRobots = RC.senseNearbyGameObjects(Robot.class, currentLocation, 10, ALLY_TEAM);
		if (shouldIUpdateTurnRetreated())
		{
			turnRetreated = Clock.getRoundNum();
			locRetreatedFrom = farawayEnemyTarget;
		}
	}
	
	public static boolean shouldIUpdateTurnRetreated() throws GameActionException
	{
		if (locRetreatedFrom == null 
//				|| ((enemyWeight > 40 && 1.1*enemyWeight > allyWeight) && enemyRobots.length != 0 && Clock.getRoundNum() - turnRetreated >= 6)
				|| Clock.getRoundNum() - turnRetreated >= 11
				|| currentLocation.distanceSquaredTo(locRetreatedFrom) > locRetreatedFromTooFarSquared)
		{
			return true;
		}

		return false;
	}
	public static boolean shouldIAttack() throws GameActionException
	{		
		if (naiveDistance(currentLocation, ALLY_HQ) <= 4 || currentLocation.distanceSquaredTo(enemyTarget) > 16)
			return true;
		else if (Clock.getRoundNum() - turnRetreated >= 7 || 
				(allyWeight > 52 && 2*allyWeight > enemyWeight))
			return true;
		return false;
	}
}
