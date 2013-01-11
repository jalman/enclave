package team059.soldiers.micro;

import team059.RobotBehavior;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameObject;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class BackCode{
	
	GameObject[] enemies = new GameObject[0], allies = new GameObject[0];
	RobotInfo[] enemySoldiers = new RobotInfo[0], alliedSoldiers = new RobotInfo[0];
	MapLocation encampTarget = null, retreatTarget = null;
	MapLocation c = null;// , m = null;	
	
	Direction d = null;

	RobotController rc;
	RobotBehavior rb;
	BackCode backcode;
	Micros micro;
	
	public BackCode(Micros micro) {
		this.micro = micro;
		rb = micro.rb;
		rc = micro.rc;
		enemySoldiers = micro.enemySoldiers;
		alliedSoldiers = micro.alliedSoldiers;
		encampTarget = micro.encampTarget;
		retreatTarget = micro.retreatTarget;
		c = micro.c;
		d = micro.d;
	}
	public void run() throws GameActionException{
		
		d = c.directionTo(retreatTarget);
		if (micro.hasEnoughAllies())
		{
			if (rc.senseMine(c.add(d)) != null)
			{
				if (rc.senseMine(c.add(d)) != rc.getTeam()) // is enemy's mine. Never bug around for now (maybe bug to artillery)
				{
					//stand and fight.
				}
				else
				{
					//bug around neutral mine (maybe)
				}
			}
			else
			{
				micro.moveToTarget(retreatTarget);
			}
		}
		else
		{
			micro.attackTarget(micro.closestTarget());
		}
	}

}
