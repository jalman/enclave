/*package team059.soldiers;

import team059.RobotBehavior;
import battlecode.common.*;

public class micro {


	GameObject[] T = null;
	RobotInfo[] I = null;
	MapLocation encampTarget = null, retreatTarget = null;
	MapLocation c = null; //currentLocation, updated every turn;
	byte battlemode = 0; //0 = neutral, -1 = signaled, 1 = retreatmode, 2 = attackmode
	Team enemy;
	Direction d = null;
	public micro(therc) {
		this.rc = therc; // is 'this' necessary?
		c=rc.getLoc();
		d = c.directionTo(m); //place this in run method
	}
	
	public boolean enemyNearby() // tests whether an enemy soldier is nearby. //hello
	{
		T= rc.senseNearbyGameObjects(RobotType.SOLDIER, 14, enemy); 
		if (T.length == 0) // condition
		{
			return false;
		}
		return true;
	}
	public MapLocation closestTarget() // find closest enemy target nearby; use in battle
	{
		MapLocation m = new MapLocation(1000,1000);
		int d = 10000;
		MapLocation loc;
		if (enemyNearby())
		{
			for(int i =0; i < T.length; i++)
			{
				loc = rc.senseRobotInfo((Robot)T[i]).location;
				if (c.distanceSquaredTo(loc) < d)
				{
					m = loc;
					d = c.distanceSquaredTo(m);
				}
			}
			return m;
		}
		else
			return null;
	}
	
	public boolean hasEnoughAllies() // Incomplete: tests if robot has enough allies nearby to engage.
	{
		if(true)// wants to have allies who can sense enemies. Use the messaging system to do this.
		{
			return true;
		}
		return false;
	}

	public boolean signaled() //Incomplete: returns true if there's a battle nearby and the robot wants to get to it.
	{
		if(true)
			return true;
		return false;
	}
	
	public void battleState() // determines battle state each turn. Run every few turns.
	{
		if(enemyNearby() && !hasEnoughAllies())
		{
			battlemode = 1;  //retreat
		}
		else if (enemyNearby())
		{
			battlemode = 2; //attack
		}
		else if (signaled())
		{
			battlemode = -1; // signaled
		}
		else 
		{
			battlemode = 0; //neutral
		}
	}

	//Movement Code: 
	
	public void moveToTarget(MapLocation m) //retreats to m until it hits a mine and then engages.
	{
		if(rc.canMove(d))
		{
			rc.move(d);
		}
		
	}
	
	public void retreatCode() // run retreatCode before attackCode in run
	{
		Direction d = c.directionTo(retreatTarget);
		if (rc.senseMine(c.add(d)) != null)
		{
			if (rc.senseMine(c.add(d)) != rc.getTeam()) // is enemy's mine. Never bug around.
			{
				battlemode = 2;
			}
			else
			{
				//bug around neutral mine (maybe)
			}
		}
		else
		{
			moveToTarget(retreatTarget);
		}
	}


	public void attackTarget(MapLocation m)
	{
		if (c.distanceSquaredTo(m) > 2)
		{
			moveToTarget(m);
		}
	}
	
	public void attackCode()
	{
		attackTarget(enemyBattle());
	}

	public void movement() //temporary method.
	{
		switch(battlemode)
		{
			case -1:
				break;
			case 0:
				break;
			case 1: retreatCode();
				break;
			case 2: attackCode();
				break;
			default: System.out.println("error");
		}
	}
}
*/