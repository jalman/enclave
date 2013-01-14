package team059.soldiers.micro;

import team059.soldiers.SoldierBehavior;
import team059.movement.Mover;
import team059.utils.Utils;
import battlecode.common.*;

public class Micro {

	GameObject[] enemies = new GameObject[0], allies = new GameObject[0];
	RobotInfo[] enemySoldiers = new RobotInfo[0], alliedSoldiers = new RobotInfo[0];
	MapLocation encampTarget = null, c = null;
	Direction d = null;
	public final Mover mover;
	
	/**
	 * The SoldierBehavior and rc that calls micro
	 */
	SoldierBehavior sb;
	RobotController rc;
	
	/**
	 * backcode executes the micro strategy
	 */
	BackCode backcode;
	AttackCode attackcode;
	
	// The radius the rc uses to detect enemies and allies. This distance should change.
	public static int radius = 10;
	public Micro(SoldierBehavior sb) throws GameActionException {
		mover = sb.mover;
		this.sb = sb;
		rc = sb.rc;	
		
		backcode = new BackCode(this);
		attackcode = new AttackCode(this);
	}
	
	public void run() throws GameActionException
	{	
		c=rc.getLocation();
		enemies = rc.senseNearbyGameObjects(Robot.class, radius, sb.enemyTeam);
		allies = rc.senseNearbyGameObjects(Robot.class, radius+3, sb.myTeam);
		findEnemySoldiers();
		findAlliedSoldiers();
		backcode.run();
	}

	/**
	 * Methods for detecting Allies and Enemies Nearby
	 */
	
	public boolean enemyNearby()
	{
		enemies = rc.senseNearbyGameObjects(Robot.class, radius, sb.enemyTeam); 
		if (enemies == null || enemies.length == 0)
		{
			return false;
		}
		return true;
	}
	
	//should only be used in run
	public boolean enemySoldierNearby()
	{
		if (enemySoldiers == null || enemySoldiers.length == 0)
		{
			return false;
		}
		return true;
	}
	
	public boolean allyNearby()
	{
		if (allies.length == 0) // condition
		{
			return false;
		}
		return true;
	}
	
	/**
	 * Detects enemy and allied soldiers nearby
	 * sets the arrays enemySoldiers and alliedSoldiers respectively
	 * @throws GameActionException
	 */
	
	public void findEnemySoldiers() throws GameActionException
	{
		if (enemyNearby())
		{
			int l = 0;
			RobotInfo r = null;
			for (int i = 0; i < enemies.length; i++)
			{
				if (rc.senseRobotInfo((Robot)enemies[i]).type == RobotType.SOLDIER) 
				{
					l++;
				}
			}
			enemySoldiers = new RobotInfo[l];
			l = 0;
			for (int i = 0; i < enemies.length; i++)
			{
				r = rc.senseRobotInfo((Robot)enemies[i]);
				if (r.type == RobotType.SOLDIER)
				{
					enemySoldiers[l] = r;
					l++;
				}
			}
		}
	}
	
	public void findAlliedSoldiers() throws GameActionException // might be useless
	{
		if (allyNearby())
		{
			int l = 0;
			RobotInfo r = null;
			for (int i = 0; i < allies.length; i++)
			{
				if (rc.senseRobotInfo((Robot)allies[i]).type == RobotType.SOLDIER) 
				{
					l++;
				}
			}
			alliedSoldiers = new RobotInfo[l];
			l = 0;
			for (int i = 0; i < allies.length; i++)
			{
				r = rc.senseRobotInfo((Robot)allies[i]);
				if (r.type == RobotType.SOLDIER)
				{
					alliedSoldiers[l] = r;
					l++;
				}
			}
		}
	}
	
	public int enemyNumber()
	{
		return enemySoldiers.length;
	}
	
	public int allyNumber()
	{
		return alliedSoldiers.length;
	}
	
	public void hasNearbyEnemy() //broadcasts that there are enemies nearby; incomplete
	{
		if (enemyNearby())
		{
			
		}		
	}
	
	/**
	 * This method should only be called in run.
	 * @param array of enemy soldiers
	 * @return closest enemy soldier target
	 * @throws GameActionException
	 */
	public MapLocation closestTarget(GameObject[] enemies) throws GameActionException
	{
		c=rc.getLocation();
		MapLocation m = new MapLocation(10000,10000);
		int d = 10000;
		MapLocation loc;
		if (enemies != null && enemies.length!=0)
		{
			for(int i =0; i < enemies.length; i++)
			{
				loc = rc.senseRobotInfo((Robot)enemies[i]).location;
				if (Utils.naiveDistance(loc, c) < d)
				{
					m = loc;
					d = Utils.naiveDistance(loc, c);
				}
			}
			return m;
		}
		else
			return null;
	}
	public MapLocation closestSoldierTarget(RobotInfo[] enemySoldiers) throws GameActionException // find closest enemy target nearby; use in battle
	{
		c=rc.getLocation();
		MapLocation m = new MapLocation(10000,10000);
		int d = 10000;
		MapLocation loc;
		if (enemySoldiers != null && enemySoldiers.length!=0)
		{
			for(int i =0; i < enemySoldiers.length; i++)
			{
				loc = enemySoldiers[i].location;//rc.senseRobotInfo((Robot)enemies[i]).location;
				if (Utils.naiveDistance(loc, c) < d)
				{
					m = loc;
					d = Utils.naiveDistance(loc, c);
				}
			}
			return m;
		}
		else
			return null;
	}	
	
	/**
	 * Determines whether there are enough allies nearby to engage
	 * @return
	 */
	public boolean hasEnoughAllies()
	{
		if(enemyNumber() < allyNumber())
		{
			return true;
		}
		return false;
	}
	
	public void attackTarget(MapLocation m) throws GameActionException
	{
		c = rc.getLocation();
		if (c.distanceSquaredTo(m) > 2)
		{
			mover.setTarget(m);
		}
	}
}
