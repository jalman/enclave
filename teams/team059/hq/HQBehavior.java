package team059.hq;

import team059.RobotBehavior;
import team059.utils.Utils;
import battlecode.common.*;

public class HQBehavior extends RobotBehavior {
	
	final Upgrade[] SPARSE_UPGRADES = {Upgrade.PICKAXE, Upgrade.DEFUSION, Upgrade.NUKE}; //upgrades in the order we should research them
	final Upgrade[] DENSE_UPGRADES = {Upgrade.DEFUSION, Upgrade.PICKAXE, Upgrade.NUKE}; //upgrades in the order we should research them
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
		try {
			super.messagingSystem.initHeaderMessage();
		} catch (GameActionException e1) {
			e1.printStackTrace();
		}
		
		super.messagingSystem.handleMessages(messageHandlers);
		
		if(rc.isActive()) {
			if(rc.getTeamPower() - 40.0 > 15.0 || Clock.getRoundNum() < 40) {
				try {
					rc.setIndicatorString(0, Double.toString(rc.getTeamPower()) + "  asdf");
					buildSoldier(rc.getLocation().directionTo(rc.senseEnemyHQLocation()));
				} catch (Exception e) {
					e.printStackTrace();
				}			
			} else {
				rc.setIndicatorString(0, Double.toString(rc.getTeamPower()) + "  fdsa");
				if(!rc.hasUpgrade(upgradeList[currentUpgrade])) {
					try{
						rc.researchUpgrade(upgradeList[currentUpgrade]);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					currentUpgrade++;
				}
			}
		}

	}

	public void buildSoldier(Direction dir) throws GameActionException {
		if (rc.isActive()) {
			// Spawn a soldier
			if (rc.canMove(dir) && !Utils.isEnemyMine(rc.getLocation().add(dir)))
				rc.spawn(dir);
		}
	}

	public void researchUpgrade(Upgrade upg) {

	}
}
