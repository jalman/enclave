package team059.soldiers.micro;

import team059.messaging.MessagingSystem;
import team059.movement.Mover;
import team059.movement.NavType;
import team059.soldiers.SoldierBehavior2;
import team059.utils.Utils;
import static team059.utils.Utils.*;
import battlecode.common.*;
import team059.soldiers.SoldierUtils;


public class Micro {
	
	MapLocation retreatTarget = null;
	MapLocation encampTarget = null, c = null;
	int enemyNumber, allyNumber;
//	public static int ALLY_RADIUS = 3;
	public static int ALLY_RADIUS2 = 10;
	public static final Mover mover = new Mover();
	public MapLocation enemySoldierTarget, curLoc;
	
	int count = 0;
	
	public static int sensorRadius = 11; // The radius the RC uses to detect enemies and allies. This distance.
	
	public Micro() {		
		enemySoldierTarget = null;
	}
	
	public void run() throws GameActionException{
		int k = Clock.getBytecodeNum();
//		if((count) % 10 == Math.random()*10)
//		{
//			System.out.println(k + " Pre-micro bytecode");
//		}
		if (count % 2 == 0)
		{
			setVariables();
		}
		
//		RC.setIndicatorString(2, "MICRO MODE " + SoldierBehavior2.microSystem.mover.getTarget() + " " + Clock.getRoundNum());
		if(count % 15 == 1 && enemySoldierTarget != null)
		{
			Utils.messagingSystem.writeMicroMessage(enemySoldierTarget, Clock.getRoundNum());
		}
		if(enemySoldierTarget != null) {		
			microCode();
		}
//		if(count % 10 == 0 || (count+Clock.getRoundNum()) % 10 == Math.random()*10)
//		{
//			System.out.println(Clock.getBytecodeNum() - k + " ");
//			if(Clock.getBytecodeNum() == 10000)
//				System.out.println("Hit 10000 bytecode");
//		}
		
		if(RC.isActive())
			mover.execute();
		count++;
		
	}
	public void setVariables() throws GameActionException{
		enemyNumber = Utils.enemyRobots.length;
		allyNumber = RC.senseNearbyGameObjects(Robot.class, ALLY_RADIUS2, Utils.ALLY_TEAM).length - RC.senseEncampmentSquares(Utils.currentLocation, ALLY_RADIUS2, Utils.ALLY_TEAM).length;
		enemySoldierTarget = SoldierUtils.findClosebyEnemy();
	}
	/**
	 * Retreats during micro if there are no adjacent enemies and enough allies nearby.
	 * @throws GameActionException
	 */
	
	public void microCode() throws GameActionException{
		setRetreatBack();
		if (enemySoldierTarget.distanceSquaredTo(RC.getLocation())<= 2)
		{
			mover.setTarget(RC.getLocation());
		}
		else if (!shouldIAttack())
		{
			setRetreatBack();
			mover.setTarget(retreatTarget);
		}
		else
		{	
			attackTarget(enemySoldierTarget);
		}
	}
	
	/**
	 * Writes a message when enemies are nearby
	 * @throws GameActionException
	 */
	
	public void signalEnemyNearby() throws GameActionException{
		if ((Clock.getRoundNum() + RC.getRobot().getID()) % 10 == 0)
		{
			messagingSystem.writeAttackMessage(SoldierUtils.closestSoldierTarget(SoldierUtils.findEnemySoldiers(Micro.sensorRadius)), 0);
		}
	}
	
	/**
	 * Sets the destinations to retreat to.
	 * @throws GameActionException
	 */
	public void setRetreatBack() throws GameActionException
	{
		c = RC.getLocation();
		if (enemySoldierTarget != null)
		{
			retreatTarget = c.add(RC.getLocation().directionTo(enemySoldierTarget).opposite(), 2);
		}
		else
		{
			retreatTarget = Utils.ALLY_HQ;
		}
	}
	public void setRetreatEncampment() throws GameActionException //makes the retreat target the nearest encampment
	{
		// fill in with messaging
	}

	/**
	 * Methods for detecting Allies and Enemies within a certain radius
	 */
	
	// Determines whether there are enough allies nearby to engage
	public boolean shouldIAttack() throws GameActionException
	{
		if(allyNumber > enemyNumber)
		{
			RC.setIndicatorString(2, "I should attack! " + allyNumber + " > " + enemyNumber);
			if(enemyNumber > allyNumber/4) {
				mover.setNavType(NavType.BUG);
			} else {
				mover.setNavType(NavType.BUG_HIGH_DIG);
			}
			return true;
		}		
		mover.setNavType(NavType.BUG);
		RC.setIndicatorString(2, "I shouldn't attack! " + allyNumber + " <= " + enemyNumber);
		return false;
	}
	
	public boolean enemySoldierNearby(int radius) throws GameActionException
	{
		if (SoldierUtils.findEnemySoldiers(radius) == null || SoldierUtils.findEnemySoldiers(radius).length == 0)
		{
			return false;
		}
		return true;
	}	
	public boolean allyNearby(int radius)
	{
		GameObject[] allies = RC.senseNearbyGameObjects(Robot.class, radius, ALLY_TEAM); 
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
/*	public void findEnemyAndAlliedSoldiers(int radius) throws GameActionException
	{
		GameObject[] robots = RC.senseNearbyGameObjects(Robot.class, radius);
		RobotInfo robotInfo; 
		RobotInfo temp[] = new RobotInfo[50];
		if (robots != null && robots.length != 0)
		{
			int aLength = 0;
			int eLength = 0;
			
			for (int i = 0; i < robots.length; i++)
			{				
				robotInfo = RC.senseRobotInfo((Robot)robots[i]);
				if (robotInfo.type == RobotType.SOLDIER) 
				{
					if (robotInfo.team == ALLY_TEAM)
						aLength++;
					else
						eLength++;
				}
			}
			
			goodSoldiers = new RobotInfo[aLength];
			badSoldiers = new RobotInfo[eLength];
			for (int i = 0; i < eLength + aLength; i++)
			{
				robotInfo = RC.senseRobotInfo((Robot)robots[i]);
			}
		}
	}
	
	
	
	public int enemyNumber(int radius) throws GameActionException
	{
		return SoldierUtils.findEnemySoldiers(radius).length;
	}
	
	public int allyNumber(int radius) throws GameActionException
	{
		return SoldierUtils.findAlliedSoldiers(radius).length;
	}
	*/
	/**
	 * @param array of enemies
	 * @return closest enemy target
	 * @throws GameActionException
	 */
	public MapLocation closestTarget(GameObject[] enemies) throws GameActionException
	{
		c = RC.getLocation();
		MapLocation m = new MapLocation(10000,10000);
		int d = 10000;
		MapLocation loc;
		if (enemies != null && enemies.length!=0)
		{
			for(int i =0; i < enemies.length; i++)
			{
				loc = RC.senseRobotInfo((Robot)enemies[i]).location;
				if (naiveDistance(loc, c) < d)
				{
					m = loc;
					d = naiveDistance(loc, c);
				}
			}
			return m;
		}
		else
			return null;
	}
	public void attackTarget(MapLocation m) throws GameActionException
	{
		if (RC.getLocation().distanceSquaredTo(m) > 2)
		{
			mover.setTarget(m);
		}
		else
		{
			mover.setTarget(RC.getLocation());
		}
	}
		
}