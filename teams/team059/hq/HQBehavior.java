package team059.hq;

import team059.RobotBehavior;
import static team059.utils.Utils.*;
import team059.messaging.MessagingSystem;
import team059.utils.Utils;
import battlecode.common.*;

public class HQBehavior extends RobotBehavior {

	final Upgrade[] SPARSE_UPGRADES = {Upgrade.PICKAXE, Upgrade.VISION, Upgrade.NUKE}; //upgrades in the order we should research them
	final Upgrade[] DENSE_UPGRADES = {Upgrade.PICKAXE, Upgrade.VISION, Upgrade.NUKE}; //upgrades in the order we should research them
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
	public void beginRound() {
		messaging = RC.getTeamPower() > MessagingSystem.MESSAGING_COST;
		//messaging = false;
		if(messaging) {
			try {
				messagingSystem.beginRoundHQ();
				//messagingSystem.writeAttackMessage(RC.getLocation(), 5);
			} catch (GameActionException e1) {
				e1.printStackTrace();
			}
		}
	}

	@Override
	public void run() {		
		messagingSystem.handleMessages(messageHandlers);

		if(rc.isActive()) {
			if(Clock.getRoundNum() < 100 || (rc.getTeamPower() - 40.0 > 15.0 && rc.hasUpgrade(upgradeList[upgradeList.length - 2]))) {
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
			for(int i = 0; i < 8; i++) {
				if(goodPlaceToMakeSoldier(dir)) {
					rc.spawn(dir);
					return;
				}
				dir = dir.rotateRight();
			}

			//message guys to get out of the way??
		}
	}

	private boolean goodPlaceToMakeSoldier(Direction dir) {
		return rc.canMove(dir) && !Utils.isEnemyMine(rc.getLocation().add(dir));
	}

	public void researchUpgrade(Upgrade upg) {

	}

}
