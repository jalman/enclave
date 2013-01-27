package team059.soldiers;

import static team059.utils.Utils.*;
import team059.*;
import team059.soldiers.micro.Micro;
import team059.utils.Utils;
import battlecode.common.*;

public class SoldierUtils {
	
	public final static int MAX_SOLDIER_ENERGON = 40;
	public final static int MAX_ENCAMPMENT_ENERGON = 100;
	public final static int MAX_HQ_ENERGON = 500;
	
	public final static int sensorRadius = ENEMY_RADIUS2;
	public final static int closeEnoughToGoToBattleSquared = 100;
	private static Robot[] enemiesFarAway; // enemies within closeEnoughToGoToBattle of a soldier. Only used to find farawayEnemyTarget
	public static final int maxNumberOfEnemiesToCheckToFindATarget = 5;

	static RobotInfo tempRobotInfo;	
	
	public static MapLocation enemyTarget; 
	public static int enemyWeight;
	public static int allyWeight;
	
	public static boolean farawayTargetSet = false;
	public static MapLocation farawayEnemyTarget = null; // the maplocation of a high priority bot in enemiesFarAway
//	public static int farawayEnemyTargetAge = 1000;
	
	/**
	 * If the soldier does not have a farawayEnemyTarget, he calls getFarAwayEnemyTarget to obtain one. If he receives one, he should
	 * immediately enter micro mode from whichever task he was previously performing.
	 * @throws GameActionException
	 */
	public static void updateSoldierUtils() throws GameActionException{
		getFarAwayEnemyTarget();
	}
	public static void getFarAwayEnemyTarget() throws GameActionException{
		detectenemiesFarAway();
		farawayEnemyTarget = getHighestPriority(enemiesFarAway);
		farawayTargetSet = true;
	}
	/**
	 * Obtains target and enemyWeight with one traversal of the enemyRobots array. This is cheaper bytecode-wise.
	 * NOTE: If setEnemyTargetAndWeight, also update enemyWeight
	 * @throws GameActionException
	 */
	public static void setEnemyTargetAndWeight() throws GameActionException{
		int priority = 0;
		enemyWeight = 0;
		enemyTarget = null;
		for (Robot enemy : enemyRobots)
		{
			tempRobotInfo = RC.senseRobotInfo(enemy);
			enemyWeight+=findEnemyWeight(tempRobotInfo);
			
			//updates enemyTarget
			if(overallPriority(tempRobotInfo) > priority)
			{
				enemyTarget = tempRobotInfo.location;
				priority = overallPriority(tempRobotInfo);
			}
		}
	}
	/**
	 * @param RobotInfo of a robot
	 * @return the weight its given (total weight decides whether to charge in or not)
	 */
	private static int findEnemyWeight(RobotInfo r){
		if (r.type == RobotType.SOLDIER)
		{
			//Full health soldier returns 20.
			return Math.max(0, 6 - 3*r.roundsUntilMovementIdle + (int)(14*(r.energon)/MAX_SOLDIER_ENERGON));
		}
		else if (r.type == RobotType.ARTILLERY)
		{
			//full health medium charge artillery returns 35
			return 10 - (int)(r.roundsUntilAttackIdle/2) + (int)(30*(r.energon)/MAX_ENCAMPMENT_ENERGON);
		}
		else if (r.type == RobotType.HQ)
		{
			return -70;
		}
		return 7;
	}
	private static int findAllyWeight(RobotInfo r)
	{	
		if (r.type == RobotType.SOLDIER)
		{
			if (r.roundsUntilAttackIdle <= 2)
				//full helath soldier returns 20
				return Math.max(0, 8 - 2*r.roundsUntilAttackIdle + (int)(12*(r.energon)/MAX_SOLDIER_ENERGON));
		}
		else if (r.type == RobotType.ARTILLERY)
		{
			//full health medium charge artillery returns 35
			return 20 - (int)(r.roundsUntilAttackIdle/2) + (int)(20*(r.energon)/MAX_ENCAMPMENT_ENERGON);
		}
		else if (r.type == RobotType.HQ)
		{
			return 120;
		}
		return 7;
	}
	/**
	 * Finds enemy target within the micro radius with highest priority. 
	 * @throws GameActionException
	 */
	public static MapLocation setEnemyTarget() throws GameActionException{
		int priority = 0;
		enemyTarget = null;
		for (int i = 0; i < enemyRobots.length; i++)
		{
			tempRobotInfo = RC.senseRobotInfo(enemyRobots[i]);
			if(overallPriority(tempRobotInfo) > priority)
			{
				enemyTarget = tempRobotInfo.location;
			}
		}	
		return enemyTarget;
	}
	
	/**
	 * Checks a maximum of numberOfTargetsToCheck when searching for an enemy target; saves Bytecode.
	 * @param numberOfTargetsToCheck
	 * @return
	 * @throws GameActionException
	 */
	public static void setEnemyTarget(int numberOfTargetsToCheck) throws GameActionException{
		int priority = 0;
		enemyTarget = null;
		for (int i = 0; i < enemyRobots.length && i < numberOfTargetsToCheck; i++)
		{
			tempRobotInfo = RC.senseRobotInfo(enemyRobots[i]);
			if(overallPriority(tempRobotInfo) > priority)
			{
				enemyTarget = tempRobotInfo.location;
			}
		}	
	}
	
	/**
	 * Returns the priority that an enemy robot has, based on distance, health of enemy, and type of enemy.
	 * 
	 * TODO: The priorities right now is random.
	 * @param RobotInfo r
	 * @return
	 * @throws GameActionException
	 */
	public static int overallPriority(RobotInfo r) throws GameActionException
	{
		int distanceSquared = currentLocation.distanceSquaredTo(r.location);
		double healthPercent = robotHealthPercent(r);
		int priority = robotTypePriority(r);
		int roundsUntilAttackActive = r.roundsUntilAttackIdle;
		return (100-(int)(20*healthPercent)-(int)(Math.sqrt((double)distanceSquared)*12)+priority+roundsUntilAttackActive*2);
	}
	
	//Helper methods for overallPriority
	private static int robotTypePriority(RobotInfo r) throws GameActionException{
		if (r.type == RobotType.SOLDIER)
		{
			return 15;
		}
		else if (r.type == RobotType.ARTILLERY)
		{
			return 25;
		}
		if (r.type == RobotType.HQ)
		{
			return 40;
		}
		else
		{
			return 0;
		}
	}
	
	private static double robotHealthPercent(RobotInfo r) throws GameActionException{
		if (r.type == RobotType.SOLDIER)
		{
			return (r.energon/MAX_SOLDIER_ENERGON); 
		}
		else if (r.type == RobotType.HQ)
		{
			return (r.energon/MAX_HQ_ENERGON);
		}
		else {
			return (r.energon/MAX_ENCAMPMENT_ENERGON);
		}
	}
	
	/**
	 * Determines whether there are enough allies to engage enemies to engage in an area. 
	 * NOTE: If changes are made to setEnemyTargetAndWeight, also update enemyWeight
	 * @param radiusSquared
	 * @return weight; higher weight = more enemies
	 * @throws GameActionException
	 */
	public static int setEnemyWeight(MapLocation m, int radiusSquared) throws GameActionException
	{
		enemyWeight = 0;
		
		Robot[] enemies = RC.senseNearbyGameObjects(Robot.class, m, radiusSquared, ALLY_TEAM);
		for(Robot enemy : enemies) {
			tempRobotInfo = RC.senseRobotInfo(enemy);
			enemyWeight += findEnemyWeight(tempRobotInfo);
		}
		return enemyWeight;
	}
	public static int setAllyWeight(MapLocation m, int radiusSquared) throws GameActionException 
	{
		allyWeight = 10;
		RobotInfo r;
		Robot[] allies = RC.senseNearbyGameObjects(Robot.class, m, radiusSquared, ALLY_TEAM);
		for(Robot ally : allies) {
			tempRobotInfo = RC.senseRobotInfo(ally);
			allyWeight += findAllyWeight(tempRobotInfo);
		}
		return allyWeight;
	}
	/**
	 * Finds an enemy to go to battle with;
	 * If sees enemy within radius squared of 81, then go to battle;
	 */
	private static void detectenemiesFarAway()
	{
		enemiesFarAway = RC.senseNearbyGameObjects(Robot.class, currentLocation, closeEnoughToGoToBattleSquared, ENEMY_TEAM);
	}
	
	/**
	 * When the soldier sees a faraway Enemy, he updates his target as he gets closer. This method should be called 
	 * exclusively in micro.
	 * @param k = the number of turns in between updates to the
	 * @throws GameActionException 
	 */
	public static void updateFarawayEnemyTarget(int k) throws GameActionException
	{
		/*if ((Clock.getRoundNum()+RC.getRobot().getID()) % k == 0)
		{*/
			enemiesFarAway = RC.senseNearbyGameObjects(Robot.class, farawayEnemyTarget, 2*k*k, ENEMY_TEAM);
			if (enemiesFarAway.length > 0)
				farawayEnemyTarget = RC.senseRobotInfo(enemiesFarAway[0]).location;
			//getHighestPriority(enemiesFarAway);
		//}
	}
	/**
	 * @param array of (enemy) robots
	 * @return robot with highest priority
	 * @throws GameActionException
	 */
	private static MapLocation getHighestPriority(Robot[] arr) throws GameActionException
	{
		RobotInfo targetInfo = null;
		for (int i = 0; i < maxNumberOfEnemiesToCheckToFindATarget && i < arr.length; i++)
		{
			tempRobotInfo = RC.senseRobotInfo(arr[i]);
			if (targetInfo == null || overallPriority(targetInfo) < overallPriority (tempRobotInfo))
			{
				targetInfo = tempRobotInfo;
			}
		}
		if (targetInfo == null)
			return null;
		return targetInfo.location;
	}
}
