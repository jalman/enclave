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
	
	GameObject[] enemies = new GameObject[0], allies = new GameObject[0];
	RobotInfo[] enemySoldiers = new RobotInfo[0], alliedSoldiers = new RobotInfo[0];
	MapLocation encampTarget = null, retreatTarget = null;
	MapLocation c = null;// , m = null;	
	
	Direction d = null;

	RobotController rc;
	SoldierBehavior sb;
	BackCode backcode;
	Micro micro;
	
	public static int r;
	public final Mover mover;
	
	// to optimize: what calls to use each turn and which calls not to use each turn.
	
	public BackCode(Micro micro) {
		this.micro = micro;
		r = micro.r;
		sb = micro.sb;
		rc = micro.rc;
		c = micro.c;
		mover = micro.mover;
	}
	public void run() throws GameActionException{
		
		setRetreatBack();
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
			micro.attackTarget(micro.closestTarget(micro.enemies));
		}
	}
	public void setRetreatEncampment() //makes the retreat target the nearest encampment
	{
		retreatTarget = Utils.closest(rc.senseAlliedEncampmentSquares(), c);
	}
	public void setRetreatBack() throws GameActionException
	{
		c = micro.c;
	
		
		retreatTarget = c.add(rc.getLocation().directionTo(micro.closestTarget(micro.enemies)).opposite());
	}
}
