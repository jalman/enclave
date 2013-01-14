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
		r = Micro.r;
		sb = micro.sb;
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
			System.out.println(micro.closestSoldierTarget(micro.enemySoldiers));
			micro.attackTarget(micro.closestSoldierTarget(micro.enemySoldiers));
		}
		rc.setIndicatorString(2, "MICRO " + mover.getTarget());
	}

	public void setRetreatBack() throws GameActionException
	{
		c = micro.c;
		if (micro.enemySoldierNearby())
		{
			retreatTarget = c.add(rc.getLocation().directionTo(micro.closestSoldierTarget(micro.enemySoldiers)).opposite());
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
