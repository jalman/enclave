package team059.soldiers;

import static team059.utils.Utils.*;
import team059.*;
import team059.soldiers.micro.Micro;
import battlecode.common.*;

public class SoldierUtils {
	public static Robot[] enemies;
	
	public static MapLocation findClosebyEnemy() throws GameActionException{
		MapLocation m = findSomeEnemyGuy(2);
		if (m != null)
			return m;
		m = findSomeEnemyGuy(8);
		if (m != null)
			return m;
		m = findSomeEnemyGuy(16);
		return m;
	}
	
	public static MapLocation SLfindClosebySoldier() throws GameActionException{
		MapLocation m = findSomeEnemySoldier(2);
		if (m != null)
			return m;
		m = findSomeEnemySoldier(9);
		if (m != null)
			return m;
		m = findSomeEnemySoldier(18);
		if (m != null)
			return m;
		m = findSomeEnemySoldier(36);
		return m;
	}
	/**
	 * Writes a message when enemies are nearby
	 * @throws GameActionException
	 */
	
	public static MapLocation findSomeEnemyGuy(int radiusSquared) throws GameActionException
	{
		enemies = RC.senseNearbyGameObjects(Robot.class, radiusSquared, ENEMY_TEAM);
		return enemies.length > 0 ? RC.senseRobotInfo(enemies[0]).location : null;
		
	}
	public static MapLocation findSomeEnemySoldier(int radiusSquared) throws GameActionException
	{
		Robot[] enemies = RC.senseNearbyGameObjects(Robot.class, radiusSquared, ENEMY_TEAM);
		for(Robot enemy : enemies) {
			RobotInfo someEnemy = RC.senseRobotInfo(enemy);
			if(someEnemy.type == RobotType.SOLDIER) {
				return someEnemy.location;
			}
		}
		return null;
	}
	
	/**
	 * Finds whether there are too many enemies to engage in an area
	 * @param radius
	 * @return a weight; higher = many enemies
	 * @throws GameActionException
	 */
	public static int enemyWeight(int radius) throws GameActionException
	{
		int l = 0;
		Robot[] enemies = RC.senseNearbyGameObjects(Robot.class, radius, ENEMY_TEAM);
		for(Robot enemy : enemies) {
			if(RC.senseRobotInfo(enemy).type == RobotType.SOLDIER)
				l++;
		}

		return l;
	}
	public static int allyWeight(int radius) throws GameActionException 
	{
		int l = 0;
		Robot[] enemies = RC.senseNearbyGameObjects(Robot.class, radius, ALLY_TEAM);
		for(Robot enemy : enemies) {
			if(RC.senseRobotInfo(enemy).type == RobotType.SOLDIER)
				l++;
		}

		return l;
	}
	public static RobotInfo[] findEnemySoldiers(int radius) throws GameActionException
	{
		GameObject[] enemies = RC.senseNearbyGameObjects(Robot.class, radius, ENEMY_TEAM);
		RobotInfo[] enemySoldiers = new RobotInfo[0];
		RobotInfo[] helperArray = new RobotInfo[25];
		int l = 0;
		
		if (enemies != null && enemies.length !=0)
		{
			RobotInfo r = null;
			for (int i = 0; i < enemies.length; i++)
			{
				r = RC.senseRobotInfo((Robot)enemies[i]);
				if (r.type == RobotType.SOLDIER) 
				{
					helperArray[l] = r;
					l++;
				}
			}
			enemySoldiers = new RobotInfo[l];
			
			for (int i = 0; i < l; i++)
			{
				enemySoldiers[i] = helperArray[i];
			}
		}
		return enemySoldiers;
	}
	
	public static boolean amISquadLeader(){
		return RC.getRobot().getID() % 3 == 0;
	}
	
	public static RobotInfo[] findAlliedSoldiers(int radius) throws GameActionException // might be useless
	{
		GameObject[] allies = RC.senseNearbyGameObjects(Robot.class, radius, ENEMY_TEAM);
		RobotInfo[] alliedSoldiers = new RobotInfo[0];
		RobotInfo[] helperArray = new RobotInfo[25];
		int l = 0;
		
		if (allies != null && allies.length !=0)
		{
			RobotInfo r = null;
			for (int i = 0; i < allies.length; i++)
			{
				r = RC.senseRobotInfo((Robot)allies[i]);
				if (r.type == RobotType.SOLDIER) 
				{
					helperArray[l] = r;
					l++;
				}
			}
			alliedSoldiers = new RobotInfo[l];
			
			for (int i = 0; i < l; i++)
			{
				alliedSoldiers[i] = helperArray[i];
			}
		}
		return alliedSoldiers;
	}

	public static MapLocation closestSoldierTarget(RobotInfo[] enemySoldiers) throws GameActionException // finds closest enemy target nearby; use in battle
	{
		MapLocation c=RC.getLocation();
		MapLocation m = null;
		int d = 10000;
		MapLocation loc;
		if (enemySoldiers != null && enemySoldiers.length!=0)
		{
			for(int i =0; i < enemySoldiers.length; i++)
			{
				loc = enemySoldiers[i].location;//RC.senseRobotInfo((Robot)enemies[i]).location;
				if (naiveDistance(loc, c) < d)
				{
					m = loc;
					d = naiveDistance(loc, c);
				}
			}
		}
		return m;
	}
}
