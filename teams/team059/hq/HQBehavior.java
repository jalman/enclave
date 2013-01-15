package team059.hq;

import team059.RobotBehavior;
import team059.Strategy;
import static team059.utils.Utils.*;
import team059.messaging.MessagingSystem;
import team059.utils.Utils;
import battlecode.common.*;

public class HQBehavior extends RobotBehavior {
	
	HQAction[] buildOrder;
	int buildOrderProgress = 0;
	
	public HQBehavior(RobotController therc) {
		super(therc);
	}

	@Override
	public void beginRound() {
		if(Clock.getRoundNum() == 0) {
			strategy = Strategy.decide();
			buildOrder = strategy.buildOrder;
		}
		
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
		
		messagingSystem.handleMessages(messageHandlers);
	}

	@Override
	public void run() {
		if(buildOrderProgress < buildOrder.length) {
			try {
				if(buildOrder[buildOrderProgress].execute(this)) {
					buildOrderProgress++;
				}
			} catch (GameActionException e) {
				e.printStackTrace();
			}
		} else if(rc.isActive()) {
			if(Clock.getRoundNum() < 100 || (rc.getTeamPower() - 40.0 > 15.0)) {
				try {
					rc.setIndicatorString(0, Double.toString(rc.getTeamPower()) + "  asdf");
					buildSoldier(rc.getLocation().directionTo(rc.senseEnemyHQLocation()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				try {
					researchUpgrade(Upgrade.NUKE);
				} catch (GameActionException e) {
					e.printStackTrace();
				}
			}
		}

	}
	
	void rushStrategy() {
		
	}
	
	boolean buildSoldier() throws GameActionException {
		return buildSoldier(ALLY_HQ.directionTo(ENEMY_HQ));
	}
	
	boolean buildSoldier(Direction dir) throws GameActionException {
		if (rc.isActive()) {
			// Spawn a soldier
			for(int i = 0; i < 8; i++) {
				if(goodPlaceToMakeSoldier(dir)) {
					rc.spawn(dir);
					messagingSystem.writeHQMessage(strategy);
					return true;
				}
				dir = dir.rotateRight();
			}
			//message guys to get out of the way??
		}
		return false;
	}

	private boolean goodPlaceToMakeSoldier(Direction dir) {
		return rc.canMove(dir) && !Utils.isEnemyMine(rc.getLocation().add(dir));
	}

	void researchUpgrade(Upgrade upg) throws GameActionException {
		if(RC.isActive()) {
			RC.researchUpgrade(upg);
		}
	}

}
