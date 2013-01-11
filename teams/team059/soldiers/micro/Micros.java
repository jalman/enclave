package team059.soldiers.micro;

import team059.RobotBehavior;
import team059.utils.Utils;
import battlecode.common.*;

public class Micros {

	GameObject[] enemies = new GameObject[0], allies = new GameObject[0];
	RobotInfo[] enemySoldiers = new RobotInfo[0], alliedSoldiers = new RobotInfo[0];
	MapLocation encampTarget = null, retreatTarget = null;
	MapLocation c = null;// , m = null;	
	Direction d = null;

	RobotController rc;
	RobotBehavior rb;
	BackCode backcode;

	
	public Micros(RobotBehavior rb) throws GameActionException {
		this.rb = rb;
		rc = rb.rc;
		c=rc.getLocation();
//		d = c.directionTo(m);
		enemies = rc.senseNearbyGameObjects(Robot.class, 14, rb.enemyTeam); 
		allies = rc.senseNearbyGameObjects(Robot.class, 14, rb.myTeam);
		
		
		backcode = new BackCode(this);
		
		if (hasEnoughAllies())
		{
			backcode.run();
		}
		else
		{
			
		}
	}
	
	/*
	 * Methods for detecting Allies and Enemies Nearby
	 */
	
	public boolean enemyNearby()
	{
		if (enemies.length == 0)
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
	
	public MapLocation closestTarget() throws GameActionException // find closest enemy target nearby; use in battle
	{
		MapLocation m = new MapLocation(1000,1000);
		int d = 10000;
		MapLocation loc;
		if (enemyNearby())
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
	
	public MapLocation setRetreatTarget()
	{
		return null;
	}
	//todo: Determine how to weight encampments of various types.
	public boolean hasEnoughAllies()
	{
		if(enemySoldiers.length < alliedSoldiers.length + 1)
		{
			return true;
		}
		return false;
	}

	//Movement Code: 
	public void moveToTarget(MapLocation m) throws GameActionException //retreats to m until it hits a mine and then engages.
	{
		if(rc.canMove(d))
		{
			rc.move(d);
		}		
	}
	
	public void retreatCode() // run retreatCode before attackCode in run
	{
		
	}


	public void attackTarget(MapLocation m) throws GameActionException
	{
		if (c.distanceSquaredTo(m) > 2)
		{
			moveToTarget(m);
		}
	}
}
