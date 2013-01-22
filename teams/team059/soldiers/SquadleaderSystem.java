package team059.soldiers;

import team059.utils.Utils;
import battlecode.common.*;
import team059.*;
import team059.movement.NavType;
import static team059.utils.Utils.ALLY_HQ;
import static team059.utils.Utils.RC;

/**
 * Squad leader 
 * @author Tim
 * Squad leader scans surroundings to see if engagement should occur
 * 
 * Need to: 
 * broadcast current map location; to determine whether the ball is getting too separate
 * broadcast enemy attack locations within a certain radius; to find the enemies to move to.
 * 
 */

public class SquadleaderSystem{
	
	public RobotInfo[] badSoldiers;
	RobotInfo[] goodSoldiers;
	MapLocation retreatTarget = null;
	MapLocation encampTarget = null, c = null;
	Direction d = null;
	
	MapLocation nearestEnemy;
	GameObject[] enemyRobots;
	MapLocation enemySoldierTarget;
	
	SoldierBehavior sb;
	
	public boolean retreat = true;
	public int detectionRadius = 35;
	
	public SquadleaderSystem(SoldierBehavior sb) throws GameActionException{	
		goodSoldiers = new RobotInfo[0]; 
		badSoldiers = new RobotInfo[0];
		enemySoldierTarget = null; //SoldierUtils.closestenemySoldierTarget(badSoldiers);
		
		this.sb = sb;
	}
	public void run() throws GameActionException{
		enemySoldierTarget = SoldierUtils.SLfindClosebySoldier(); //SoldierUtils.closestenemySoldierTarget(badSoldiers);
		if (enemySoldierTarget != null)
		{
			goodSoldiers = SoldierUtils.findAlliedSoldiers(detectionRadius); 
			badSoldiers = SoldierUtils.findEnemySoldiers(detectionRadius);
		}
//		sb.mover.setNavType(NavType.BUG);
		microSignalCode();
		//fightOrRetreat();

	}

	public void fightOrRetreat() throws GameActionException{
		
		if (Utils.naiveDistance(sb.curLoc, sb.mover.getTarget()) < 4)
		{
			if (enemySoldierTarget.distanceSquaredTo(sb.curLoc)<= 2)
			{
				sb.mover.setTarget(sb.curLoc);
			}
			else if (!hasEnoughAllies())
			{
				setRetreatBack();
				sb.mover.setTarget(retreatTarget);
			}
			else
			{	
				sb.attackTarget(enemySoldierTarget);
			}
		}
	}
	public void setRetreatBack() throws GameActionException
	{
		c = RC.getLocation();
		if (enemySoldierTarget != null)
		{
			retreatTarget = c.add(RC.getLocation().directionTo(enemySoldierTarget).opposite(), 2);
			if (RC.canSenseSquare(retreatTarget))
			{
				retreatTarget = ALLY_HQ;
			}
		}
	}
	/**
	 * Micromanagement signaling code. // I could probably outsource this into Utils.
	 * @param shouldIretreat is 0 if retreating, 1 if attacking
	 * @throws GameActionException
	 */
	
	private void sendMicroTargetSignal(int shouldIretreat) throws GameActionException{
		sb.messagingSystem.writeMicroMessage(enemySoldierTarget, shouldIretreat);
	}
	public void sendMicroSignal() throws GameActionException{
		if(!hasEnoughAllies()){
			sendMicroTargetSignal(0);
		}
		else
		{
			sendMicroTargetSignal(1);
			return;
		}
	}
	public void microSignalCode() throws GameActionException{
		if (enemySoldierTarget != null && (Clock.getRoundNum() + RC.getRobot().getID()) % 2 == 0)
		{
			sendMicroSignal();
		}
	}
	
	/**
	 * Determines if there are enough nearby enemies to attack.
	 * @return
	 * @throws GameActionException
	 */
	public boolean hasEnoughAllies() throws GameActionException
	{
		if(goodSoldiers.length > badSoldiers.length)
		{
			return true;
		}
		return false;
	}	
}
