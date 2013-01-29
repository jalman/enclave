package team059.soldiers.micro;


import team059.utils.Utils;
import battlecode.common.*;
import team059.messaging.MessagingSystem;
import team059.movement.Mover;
import team059.movement.NavType;
import team059.soldiers.Mines;
import static team059.utils.Utils.*;
import static team059.soldiers.SoldierUtils.*;

public class MicroOld {
	
	MapLocation retreatTarget = null;
	public int goIn = 0;
	public static final Mover mover = new Mover();
	int numberOfTargetsToCheck = 5;
	int lastTurnMicroRanOn = 0;
	
	/**
	 * Indicates the turn number that three soldiers were seen on.
	 */
	int sawTwoSoldiersOnTurnNumberEtc = Clock.getRoundNum();
	
	/**
	 * Timidity. Rush  = timidity 1, else = timidity 0.
	 */
	
	public int timidity = 0;
	
	public MicroOld() {
		enemyTarget = null;
	}
	public void run(int timidness) throws GameActionException{
		timidity = strategy.parameters.timidity;
		//this.timidity = timidity;
		if (timidity == 1)
		{
			if (RC.senseNearbyGameObjects(Robot.class, currentLocation, 25, ENEMY_TEAM).length > 0)
				lastTurnMicroRanOn=Clock.getRoundNum();
			setRushVariables();
		}
		
		if (enemyRobots.length == 0)
		{
			updateFarawayEnemyTarget(1);
			rushToBattle();
			RC.setIndicatorString(2, "CHARGING " +  Clock.getRoundNum() + " Saw enemies on " + sawTwoSoldiersOnTurnNumberEtc +  " Target: " + mover.getTarget() + farawayEnemyTarget + shouldIAttack() + currentLocation); 

		}
		else{
			setMicroVariables();
			farawayEnemyTarget = enemyTarget;
			micro();
			RC.setIndicatorString(2, "MICRO " +  Clock.getRoundNum() + " Saw enemies on " + sawTwoSoldiersOnTurnNumberEtc +  " Target: " + mover.getTarget() + farawayEnemyTarget + shouldIAttack() + currentLocation); 
		}
	}
	
	public void setRushVariables() throws GameActionException{
		if (Clock.getRoundNum() - lastTurnMicroRanOn > 3)
		{
			sawTwoSoldiersOnTurnNumberEtc = Clock.getRoundNum();
		}
	}
	public void rushToBattle() throws GameActionException{
		//farawayEnemyTarget should be already set if micro mode is entered
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
			mover.execute();
		}
		//RC.setIndicatorString(2, "GOING TO BATTLE " + Clock.getRoundNum() + "Target: " + mover.getTarget() + " Bytecode used " + (Clock.getBytecodeNum()-k));
	}
	
	public void micro() throws GameActionException{
		if(enemyTarget != null) {		
			attackOrRetreat();
		}	
		if(RC.isActive())
		{
			//RC.setIndicatorString(2, "MICRO " + Clock.getRoundNum() + " ALLY WEIGHT: " + allyWeight + " ENEMY WEIGHT: " + enemyWeight + " Target: " + mover.getTarget());
			mover.execute();
		}
	}

	public void setMicroVariables() throws GameActionException{
//		setEnemyTargetAndWeight();
		setEnemyTarget(numberOfTargetsToCheck);
//		MapLocation m = averageMapLocation(enemyTarget, currentLocation, 2/3);
		//cheap micro
		if (timidity < -5 || RC.senseNearbyGameObjects(Robot.class, enemyTarget, 50, ALLY_TEAM).length > 7)
		{
			enemyWeight = 2;
			allyWeight = 100000;
		}
		else
		{
			MapLocation m = enemyTarget;
			setEnemyWeight(m, sensorRadius);
			setAllyWeight(m, sensorRadius);
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
		if (enemyTarget.distanceSquaredTo(RC.getLocation())<= 2 && timidity != 1)
		{
			mover.setTarget(currentLocation);
		}
		else if (!shouldIAttack())
		{
			setRetreatBack();
			mover.setNavType(NavType.BUG);
			mover.setTarget(retreatTarget);
		}
		else
		{
			if (enemyWeight < 0 || currentLocation.distanceSquaredTo(ENEMY_HQ) <= 8 || (RC.getRobot().getID() % 9 == 0))
				mover.setNavType(NavType.BUG_DIG_2);
			else
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
			if (farawayEnemyTarget.distanceSquaredTo(currentLocation) <= 13 && 
				(Clock.getRoundNum() - sawTwoSoldiersOnTurnNumberEtc <= 1 || allyWeight <= enemyWeight))
					if (ENEMY_HQ.distanceSquaredTo(currentLocation) >= 20)
						return false;
			return true;
		}
		else
		{
			if(15*allyWeight > (15+timidity)*enemyWeight)
			{
				return true;
			}		
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
			mover.setTarget(RC.getLocation());
		}
	}
}
