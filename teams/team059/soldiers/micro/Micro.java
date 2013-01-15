package team059.soldiers.micro;

import team059.messaging.MessagingSystem;
import team059.movement.Mover;
import team059.movement.NavType;
import team059.soldiers.SoldierBehavior;
import team059.utils.Utils;
import battlecode.common.*;

public class Micro {

//	GameObject[] foe = new GameObject[0], friends = new GameObject[0];
	RobotInfo[] badSoldiers = new RobotInfo[0], goodSoldiers = new RobotInfo[0];
	
	MapLocation retreatTarget = null;
	MapLocation encampTarget = null, c = null;
	Direction d = null;
	public final Mover mover;
	
	SoldierBehavior sb;
	RobotController rc;
	
	MapLocation enemySoldierTarget;
	
	public static int sensorRadius = 11; // The radius the rc uses to detect enemies and allies. This distance.
	

	public Micro(SoldierBehavior sb) throws GameActionException {
		mover = sb.mover;
		goodSoldiers = null; 
		badSoldiers = null;
		this.sb = sb;
		rc = sb.rc;	
		
		enemySoldierTarget = null;
	}
	
	public void run() throws GameActionException{
		mover.setNavType(NavType.BUG);
		goodSoldiers = findAlliedSoldiers(sensorRadius); 
		badSoldiers = findEnemySoldiers(sensorRadius);
		enemySoldierTarget = closestSoldierTarget(badSoldiers);

		signalEnemyNearby(); // signals if enemies are nearby
	 	
		fightOrRetreat();
	}
	
	/**
	 * Determines whether to retreat or fight during micro
	 * @throws GameActionException
	 */
	
	public void fightOrRetreat() throws GameActionException{
		setRetreatBack();
		if (!hasEnoughAllies())
		{
			mover.setTarget(retreatTarget);
		}
		
		else
		{	
			this.sb.attackTarget(enemySoldierTarget);
		}
	}
	
	/**
	 * Writes a message when enemies are nearby
	 * @throws GameActionException
	 */
	
	public void signalEnemyNearby() throws GameActionException{
		if ((Clock.getRoundNum() + rc.getRobot().getID()) % 10 == 0)
		{
			sb.messagingSystem.writeAttackMessage(closestSoldierTarget(findEnemySoldiers(Micro.sensorRadius)), 0);
		}
	}
	
	/**
	 * Sets the destinations to retreat to.
	 * @throws GameActionException
	 */
	public void setRetreatBack() throws GameActionException
	{
		c = rc.getLocation();
		if (enemySoldierNearby(Micro.sensorRadius))
		{
			
			retreatTarget = c.	add(rc.getLocation().directionTo(enemySoldierTarget).opposite(), 2);
			if (retreatTarget == null)
			{
				retreatTarget = Utils.ALLY_HQ;
			}
		}
	}
	public void setRetreatEncampment() throws GameActionException //makes the retreat target the nearest encampment
	{
		c = rc.getLocation();
		if (rc.senseAlliedEncampmentSquares() != null)
			retreatTarget = Utils.closest(rc.senseAlliedEncampmentSquares(), c);
		else
			//retreatTarget = Utils.ALLY_HQ;
			setRetreatBack();
	}

	/**
	 * Methods for detecting Allies and Enemies within a certain radius
	 */
	
	// Determines whether there are enough allies nearby to engage
	public boolean hasEnoughAllies() throws GameActionException
	{
		if(goodSoldiers.length > badSoldiers.length)
		{
			return true;
		}
		return false;
	}	
	public boolean enemyNearby(int radius)
	{
		GameObject[] enemies = rc.senseNearbyGameObjects(Robot.class, radius, sb.enemyTeam); 
		if (enemies == null || enemies.length == 0)
		{
			return false;
		}
		return true;
	}
	
	public boolean enemySoldierNearby(int radius) throws GameActionException
	{
		if (findEnemySoldiers(radius) == null || findEnemySoldiers(radius).length == 0)
		{
			return false;
		}
		return true;
	}	
	public boolean allyNearby(int radius)
	{
		GameObject[] allies = rc.senseNearbyGameObjects(Robot.class, radius, sb.myTeam); 
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
	public void findEnemyAndAlliedSoldiers(int radius) throws GameActionException
	{
		GameObject[] robots = rc.senseNearbyGameObjects(Robot.class, radius);
		RobotInfo robotInfo; 
		RobotInfo temp[] = new RobotInfo[50];
		if (robots != null && robots.length != 0)
		{
			int aLength = 0;
			int eLength = 0;
			
			for (int i = 0; i < robots.length; i++)
			{				
				robotInfo = rc.senseRobotInfo((Robot)robots[i]);
				if (robotInfo.type == RobotType.SOLDIER) 
				{
					if (robotInfo.team == sb.myTeam)
						aLength++;
					else
						eLength++;
				}
			}
			
			goodSoldiers = new RobotInfo[aLength];
			badSoldiers = new RobotInfo[eLength];
			for (int i = 0; i < eLength + aLength; i++)
			{
				robotInfo = rc.senseRobotInfo((Robot)robots[i]);
			}
		}
	}
	public RobotInfo[] findEnemySoldiers(int radius) throws GameActionException
	{
		GameObject[] enemies = rc.senseNearbyGameObjects(Robot.class, radius, sb.enemyTeam);
		RobotInfo[] enemySoldiers = new RobotInfo[0];
		if (enemyNearby(radius))
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
		return enemySoldiers;
	}
	
	public RobotInfo[] findAlliedSoldiers(int radius) throws GameActionException // might be useless
	{
		GameObject[] allies = rc.senseNearbyGameObjects(Robot.class, radius, sb.myTeam); 
		RobotInfo[] alliedSoldiers = new RobotInfo[0];
		if (allies!=null && allies.length != 0)
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
		return alliedSoldiers;
	}
	
	public int enemyNumber(int radius) throws GameActionException
	{
		return findEnemySoldiers(radius).length;
	}
	
	public int allyNumber(int radius) throws GameActionException
	{
		return findAlliedSoldiers(radius).length;
	}
	
	/**
	 * @param array of enemies
	 * @return closest enemy target
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
	
}