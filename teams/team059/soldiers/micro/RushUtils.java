package team059.soldiers.micro;

import static team059.utils.Utils.*;
import team059.*;
import team059.soldiers.micro.Micro;
import static team059.soldiers.SoldierUtils.*;
import battlecode.common.*;

public class RushUtils {
	
	public static int locRetreatedFromTooFarSquared = 50;
	public static MapLocation locRetreatedFrom;
	public static int turnRetreated;
	public static Robot[] allyRobots;
	
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
		if (locRetreatedFrom == null ||
					Clock.getRoundNum() - turnRetreated >= 9
				|| currentLocation.distanceSquaredTo(locRetreatedFrom) > locRetreatedFromTooFarSquared)
		{
			return true;
		}

		return false;
	}
	public static boolean shouldIAttack() throws GameActionException
	{		
		if (naiveDistance(currentLocation, ALLY_HQ) <= 4)
			return true;
		else if ((enemyWeight < 13 || Clock.getRoundNum() - turnRetreated >= 3) && 
				((setAllyWeight(currentLocation, 14) >25 && enemyWeight > allyWeight) || Clock.getRoundNum() - turnRetreated >= 5 ) )
			return true;
		return false;
	}
}
