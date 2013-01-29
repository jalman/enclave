package team059.soldiers.micro;


import battlecode.common.*;
import team059.Strategy;
import team059.messaging.MessagingSystem;
import team059.movement.Mover;
import team059.movement.NavType;
import team059.soldiers.Mines;
import static team059.utils.Utils.*;
import static team059.soldiers.SoldierUtils.*;
import team059.soldiers.micro.RushUtils;

public class Micro{
	MapLocation retreatTarget = null;
	public int goIn = 0;
	public static final Mover mover = new Mover();
	int numberOfTargetsToCheck = 5;
	public boolean shouldIAttack;
	/**
	 * Timidity is 1 during a rush.
	 */
	public int timidity = 0;
	
	/**
	 * Rush variables
	 */
	MapLocation locationRetreatedFrom;
	int turnRetreated; // the turn that locationRetreatedFrom was retreated from. 
	
	public Micro() {
		enemyTarget = null;
	}
	public void run(int timidness) throws GameActionException{
		timidity = timidness;
		//Rush code
		if (timidity == 1)
		{
			RushUtils.updateRushUtils();
			RC.setIndicatorString(2, "Round " + Clock.getRoundNum() + " " + " Last time retreated " + RushUtils.turnRetreated + " " + RushUtils.locRetreatedFrom);
		}
		
		
		if (enemyRobots.length == 0)
		{
			updateFarawayEnemyTarget(1);
			rushToBattle();
		}
		else{
			setMicroVariables();
			farawayEnemyTarget = enemyTarget;
			micro();
		}
	}

	public void rushToBattle() throws GameActionException{
		//farawayEnemyTarget should be already set if micro mode is entered
		if (RC.getRobot().getID() % 7 == 0)
			mover.setNavType(NavType.BUG_HIGH_DIG);
		else
			mover.setNavType(NavType.BUG);
		int k = Clock.getBytecodeNum();
		if (farawayEnemyTarget != null)
		{
			attackTarget(farawayEnemyTarget);
		}
		if ((Clock.getRoundNum() + RC.getRobot().getID()) % 4 ==0 && RC.senseNearbyGameObjects(Robot.class, currentLocation, 30, ENEMY_TEAM).length == 0)
		{		
			Mines.tryDefuse(farawayEnemyTarget, true);
		}
		
		if(RC.isActive())
		{
			//RC.setIndicatorString(2, "GOING TO BATTLE " + Clock.getRoundNum() + "Target: " + mover.getTarget());
			mover.execute();
			//RC.setIndicatorString(2, "GOING TO BATTLE " + Clock.getRoundNum() + "Target: " + mover.getTarget() + " Bytecode used " + (Clock.getBytecodeNum()-k));

		}
	}
	
	public void micro() throws GameActionException{
		if(enemyTarget != null) {		
			attackOrRetreat();
		}	
		if(RC.isActive())
		{
			mover.execute();
//			RC.setIndicatorString(2, "MICRO " + Clock.getRoundNum() + " ALLY WEIGHT: " + allyWeight + " ENEMY WEIGHT: " + enemyWeight + " Target: " + mover.getTarget() + enemyTarget);
		}
	}

	public void setMicroVariables() throws GameActionException{
//		setEnemyTargetAndWeight();
		if (naiveDistance(currentLocation, ENEMY_HQ) <= 3)
		{
			enemyTarget = ENEMY_HQ;
			attackTarget(enemyTarget);
		}
		else
			setEnemyTarget();
//		MapLocation m = averageMapLocation(enemyTarget, currentLocation, 2/3);
		//cheap micro
		if (timidity < -5 || (RC.senseNearbyGameObjects(Robot.class, enemyTarget, 45, ALLY_TEAM).length >= 6))
		{
			enemyWeight = 2;
			allyWeight = 100000;
		}
		else
		{
			if (timidity != 1)
			{
				MapLocation m = averageMapLocation(currentLocation, enemyTarget, 0.25);
				setEnemyWeight(m, sensorRadius);
				setAllyWeight(m, sensorRadius);
			}
			else
			{
				setEnemyWeight(enemyTarget, sensorRadius);
				setAllyWeight(enemyTarget, sensorRadius);
			}
		}
	}
	private MapLocation averageMapLocation(MapLocation m1, MapLocation m2, double k)
	{
		return new MapLocation((int)((k*m1.x+(1-k)*m2.x)), (int)(k*m1.y+(1-k)*m2.y));
	}
	
	/**
	 * Determines whether a soldier should attack or retreat
	 * If: adjacent enemies: stay put. 
	 * Else if: there aren't enough allies nearby to engage: retreat
	 * Else: attack
	 * @throws GameActionException
	 */
	
	public void attackOrRetreat() throws GameActionException{
		setRetreatBack();
		//TODO: Account for robot types!!!
		if (timidity == 1)
		{
			shouldIAttack = RushUtils.shouldIAttack();
		}
		else
			shouldIAttack = shouldIAttack();
		
		
		if (enemyTarget.distanceSquaredTo(RC.getLocation())<= 2)
		{
			mover.setTarget(currentLocation);
		}
		else if (!shouldIAttack)
		{
			setRetreatBack();
			mover.setNavType(NavType.BUG);
			mover.setTarget(retreatTarget);
		}
		else
		{
			if (enemyWeight < 0 || (currentLocation.distanceSquaredTo(ENEMY_HQ) <= 13 && RC.getRobot().getID() % 6 == 0))
				mover.setNavType(NavType.BUG_HIGH_DIG);
			else
				mover.setNavType(NavType.BUG);
			attackTarget(enemyTarget);
		}
	}

	/**
	 * Sets the retreat target.
	 * @throws GameActionException
	 */
	public void setRetreatBack() throws GameActionException
	{
		if (enemyTarget != null)
		{
			retreatTarget = currentLocation.add(RC.getLocation().directionTo(enemyTarget).opposite(), 2);
		}
		else
		{
			retreatTarget = ALLY_HQ;
		}
	}
	
	// Determines whether there are enough allies nearby to engage
	public boolean shouldIAttack() throws GameActionException
	{
		if (timidity == 1)
		{
			
			return true;
		}
		if (15*allyWeight > (15+timidity)*enemyWeight)
		{
			return true;
		}		
		return false;
	}
		
	public void attackTarget(MapLocation m) throws GameActionException
	{
		if (RC.getLocation().distanceSquaredTo(m) > 2)
		{
			mover.setTarget(m);
		}
		else
		{
			if (naiveDistance(currentLocation, ENEMY_HQ) < 3 && m.x == ENEMY_HQ.x && m.y == ENEMY_HQ.y)
			{
				System.out.println(currentLocation + " " + m);
			}
			mover.setTarget(RC.getLocation());
		}
	}
	
}
