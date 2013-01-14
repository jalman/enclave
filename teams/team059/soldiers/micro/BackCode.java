package team059.soldiers.micro;

import team059.soldiers.SoldierBehavior;
import team059.utils.Utils;
import team059.movement.Mover;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameObject;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class BackCode{
	
	MapLocation c = null;// , m = null;	
	MapLocation retreatTarget = null;
	
	Direction d = null;

	RobotController rc;
	Micro micro;
	
	public final Mover mover;
	
	// to optimize: what calls to use each turn and which calls not to use each turn.
	
	public BackCode(Micro micro) {
		this.micro = micro;
		rc = micro.rc;
		c = micro.c;
		mover = micro.mover;
	}
	public void run() throws GameActionException{
		mover.defuseMoving = false;
		setRetreatEncampment();
		d = micro.c.directionTo(retreatTarget);
		if (!micro.hasEnoughAllies())
		{
			if (rc.senseMine(c.add(d)) != null)
			{
				if (rc.senseMine(c.add(d)) != rc.getTeam()) // is enemy's mine. Never bug around for now (maybe bug to artillery)
				{
					mover.setTarget(c);
				}
				else
				{
					mover.setTarget(c); 
					//bug around neutral mine (maybe)
				}
			}
			else
			{
				mover.setTarget(retreatTarget);
			}
		}
		else
		{	
			micro.attackTarget(micro.closestSoldierTarget(micro.findEnemySoldiers(Micro.sensorRadius)));
		}
		rc.setIndicatorString(2, "MICRO " + mover.getTarget());
	}

	public void setRetreatBack() throws GameActionException
	{
		c = micro.c;
		if (micro.enemySoldierNearby(Micro.sensorRadius))
		{
			retreatTarget = c.add(rc.getLocation().directionTo(micro.closestSoldierTarget(micro.findEnemySoldiers(Micro.sensorRadius))).opposite());
		}
	}
	
	public void setRetreatEncampment() throws GameActionException //makes the retreat target the nearest encampment
	{
		c = micro.c;
		if (rc.senseAlliedEncampmentSquares() != null)
			retreatTarget = Utils.closest(rc.senseAlliedEncampmentSquares(), c);
		else
			setRetreatBack();
	}
}