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
	
	static RobotInfo tempRobotInfo;	
	
	public static MapLocation enemyTarget; 
	public static int enemyWeight;
	public static int allyWeight;
	
	
	public static void updateSoldierUtils(){
		
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
			//update the priority
			if (tempRobotInfo.type == RobotType.SOLDIER)
			{
				enemyWeight++;
			}
			else if (tempRobotInfo.type == RobotType.ARTILLERY)
			{
				enemyWeight += 4;
			}
			//updates enemyTarget
			if(overallPriority(tempRobotInfo) > priority)
			{
				enemyTarget = tempRobotInfo.location;
			}
		}
	}

	/**
	 * Finds enemy target with highest priority. 
	 * @throws GameActionException
	 */
	public static MapLocation findEnemyTarget() throws GameActionException{
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
	public static void findEnemyTarget(int numberOfTargetsToCheck) throws GameActionException{
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
		return ((int)(20*healthPercent)+distanceSquared*2+priority*2);
	}
	
	//Helper methods for overallPriority
	private static int robotTypePriority(RobotInfo r) throws GameActionException{
		if (r.type == RobotType.SOLDIER)
		{
			return 15;
		}
		else if (r.type == RobotType.ARTILLERY)
		{
			return 15;
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
	 * Both weights are taken around the enemySoldierTarget. Calculated by multiple message senders who determine goIn or not.
	 * NOTE: If setEnemyTargetAndWeight, also update enemyWeight
	 * @param radiusSquared
	 * @return weight; higher weight = more enemies
	 * @throws GameActionException
	 */
	
	public static int setEnemyWeight(MapLocation m, int radiusSquared) throws GameActionException
	{
		enemyWeight = 0;
		Robot[] enemies = RC.senseNearbyGameObjects(Robot.class, m, radiusSquared, ALLY_TEAM);
		for(Robot enemy : enemies) {
			if(RC.senseRobotInfo(enemy).type == RobotType.SOLDIER)
				enemyWeight++;
			else if (RC.senseRobotInfo(enemy).type == RobotType.ARTILLERY)
				enemyWeight+=4;
		}
		return enemyWeight;
	}
	public static int setallyWeight(MapLocation m, int radiusSquared) throws GameActionException 
	{
		allyWeight = 0;
		Robot[] allies = RC.senseNearbyGameObjects(Robot.class, m, radiusSquared, ALLY_TEAM);
		for(Robot ally : allies) {
			if(RC.senseRobotInfo(ally).type == RobotType.SOLDIER)
				allyWeight++;
			else if (RC.senseRobotInfo(ally).type == RobotType.ARTILLERY)
				allyWeight+=4;
			else if (RC.senseRobotInfo(ally).type == RobotType.HQ)
				allyWeight+=5;
		}
		return allyWeight;
	}
}
