package artillerybot1.hq;

import artillerybot1.RobotBehavior;
import battlecode.common.*;

public class HQBehavior extends RobotBehavior {
	
	final Upgrade[] SPARSE_UPGRADES = {Upgrade.DEFUSION, Upgrade.NUKE}; //upgrades in the order we should research them
	final Upgrade[] DENSE_UPGRADES = {Upgrade.DEFUSION, Upgrade.NUKE}; //upgrades in the order we should research them
	Upgrade[] upgradeList;
	int currentUpgrade = 0;
	
	double mineDensity;
	
	public HQBehavior(RobotController therc) {
		super(therc);
		MapLocation[] mines = therc.senseNonAlliedMineLocations(therc.getLocation(), 100000);
		mineDensity = (mines.length / (double) therc.getMapHeight()) / (double) therc.getMapWidth();
		upgradeList = mineDensity > 0.4 ? DENSE_UPGRADES : SPARSE_UPGRADES;
	}

	@Override
	public void run() {
		if(rc.getTeamPower() - 40.0 > 15.0 || Clock.getRoundNum() < 40) {
			try {
				rc.setIndicatorString(0, Double.toString(rc.getTeamPower()));
				buildSoldier(rc.getLocation().directionTo(rc.senseEnemyHQLocation()));
			} catch (Exception e) {
				e.printStackTrace();
			}			
		} else if(rc.isActive() ) {
			if(!rc.hasUpgrade(upgradeList[currentUpgrade])) {
				try{
					rc.researchUpgrade(upgradeList[currentUpgrade]);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if(currentUpgrade < upgradeList.length){
				currentUpgrade++;
			}
		}

	}

	public void buildSoldier(Direction dir) throws GameActionException {
		if (rc.isActive()) {
			// Spawn a soldier
			if (rc.canMove(dir) && rc.senseMine(rc.getLocation().add(dir)) == null)
				rc.spawn(dir);
		}
	}

	public void researchUpgrade(Upgrade upg) {

	}
}
