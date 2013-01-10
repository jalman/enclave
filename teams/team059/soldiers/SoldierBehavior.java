package team059.soldiers;

import team059.RobotBehavior;
import battlecode.common.*;

public class SoldierBehavior extends RobotBehavior {

	MapLocation encampTarget = null, retreatTarget = null;
	MapLocation c = null; //currentLocation, updated every turn;
	byte battlemode = 0; //0 = neutral, -1 = signaled, 1 = retreatmode, 2 = attackmode
	Team enemy;
	
	public SoldierBehavior(RobotController therc) {
		super(therc);
	}

	@Override
	public void run() {
		try {
			c = rc.getLocation();
			if (rc.isActive()) {
				//see if there is an encampment nearby to take
				if(encampTarget == null) {
					MapLocation[] encampments = rc.senseEncampmentSquares(rc.getLocation(), 16, Team.NEUTRAL);
					if(encampments.length > 0) {
						int maxdist = 20;
						for(MapLocation encampment : encampments) {
							int dist = encampment.distanceSquaredTo(rc.getLocation());
							if(dist < maxdist) {
								maxdist = dist;
								encampTarget = encampment;
							}
						}
					}
				} else { //see if the encampment is taken
					GameObject there = rc.senseObjectAtLocation(encampTarget);
					if(there != null) {
						encampTarget = null;
					}
				}
				
				rc.setIndicatorString(0, encampTarget + "");
				
				
				if(encampTarget != null && encampTarget.distanceSquaredTo(rc.getLocation()) == 0) {
					try{
						rc.captureEncampment(RobotType.GENERATOR);
					} catch(Exception e) {
						e.printStackTrace();
					}
				} else {
					
					Direction dir = encampTarget == null ? rc.getLocation().directionTo(rc.senseEnemyHQLocation()) : rc.getLocation().directionTo(encampTarget);
					
					if(!moveMine(dir)) {
						if(!moveMine(dir.rotateLeft()))
							moveMine(dir.rotateRight());
					}		
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean moveMine(Direction dir) {
		Team mine = rc.senseMine(rc.getLocation().add(dir));
		if(mine != null && mine != rc.getTeam()) {
			try {
				rc.defuseMine(rc.getLocation().add(dir));
				return true;
			} catch (GameActionException e) {
				e.printStackTrace();
			}
		}
		
		if(rc.canMove(dir)) {
			try {
				rc.move(dir);
			} catch (GameActionException e) {
				e.printStackTrace();
			}
			return true;
		} else {
			return false;
		}
	}

	public void buildEncampment(Direction dir) {

	}

	public void researchUpgrade(Upgrade upg) {

	}
	
	//Micromanagement Code
	
	GameObject[] T = null;
	
	public boolean enemyNearby() // tests whether an enemy soldier is nearby.
	{
		T= rc.senseNearbyGameObjects(RobotType.SOLDIER, 14, enemy); //(place this in run)
		if (T.length == 0) // condition
		{
			return false;
		}
		return true;
	}
	public MapLocation enemyBattle() // returns the enemy target with highest priority (closest, and then lowest health)
	{
		/*MapLocation m = new MapLocation(0,0);
		int d = 10000;
		if (enemyNearby())
		{
			for(int i =0; i < T.length; i++)
			{
				if (c.distanceSquaredTo(T[i]) < d)
				{
					m=T[i];
					d=c.distanceSquaredTo(m);
				}
			}
			return m;
		}
		else*/
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
			battlemode = 1;
		}
		else if (enemyNearby())
		{
			battlemode = 2;
		}
		else if (signaled())
		{
			battlemode = -1;
		}
		else 
		{
			battlemode = 0;
		}
	}

	//Movement Code: 
	
	public void moveToTarget(MapLocation m) //retreats to m until it hits a mine and then engages.
	{
		Direction d = c.directionTo(m); //place this in run method
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
