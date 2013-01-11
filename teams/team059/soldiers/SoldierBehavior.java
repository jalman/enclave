package team059.soldiers;

import team059.RobotBehavior;
import team059.movement.Mover;
import team059.messaging.MessageHandler;
import team059.utils.Utils;
import battlecode.common.*;
import static team059.soldiers.SoldierMode.*;

public class SoldierBehavior extends RobotBehavior {

	private SoldierMode mode = IDLE;
	private MapLocation target = null;
	private int priority;
	private MapLocation gather;
	private boolean charging = false;
	public final Mover mover;

	public SoldierBehavior(RobotController therc) {
		super(therc);
		mover = new Mover((RobotBehavior) this);
		gather = new MapLocation((myBase.x * 3 + enemyBase.x * 2) / 5, (myBase.y * 3 + enemyBase.y * 2) / 5);
		mode = SoldierMode.EXPLORE; // for now
	}

	@Override
	public void run() {
		if(!rc.isActive()) return;

		try {
			messagingSystem.readMessages();
		} catch (GameActionException e1) {
			e1.printStackTrace();
		}

		messagingSystem.handleMessages(messageHandlers);

		try {
			switch(mode) {
			case IDLE:
				idleBehavior();
				break;
			case ATTACK:
				attackBehavior();
				break;
			case DEFEND:
				break;
			case MICRO:
				microBehavior();
				break;
			case EXPLORE:
				exploreBehavior();
				break;
			case TAKE_ENCAMPMENT:
				takeEncampmentBehavior();
				break;
			default:
				break;
			}

		} catch (GameActionException e) {
			e.printStackTrace();
		}

		/*
		try {
			double mineProb = rc.senseHQLocation().distanceSquaredTo(rc.getLocation()) + rc.senseEnemyHQLocation().distanceSquaredTo(rc.getLocation());
			mineProb /= rc.senseHQLocation().distanceSquaredTo(rc.senseEnemyHQLocation());
			mineProb *= mineProb;
			mineProb /= 20;
			if(rc.senseMine(rc.getLocation()) == utils.myTeam()) {
				mineProb = 0.0;
			}
			if(Math.random() < mineProb) {
				rc.layMine();
			} else {
				Direction dir = encampTarget == null ? rc.getLocation().directionTo(rc.senseEnemyHQLocation()) : rc.getLocation().directionTo(encampTarget);

				if(!moveMine(dir)) {
					if(!moveMine(dir.rotateLeft()))
						moveMine(dir.rotateRight());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		 */
	}

	protected MessageHandler getAttackHandler() {
		return new MessageHandler() {
			@Override
			public void handleMessage(int[] message) {
				MapLocation new_target = new MapLocation(message[1], message[2]);
				int new_priority = message[3];

				if(target == null || PrioritySystem.rate(new_priority, Utils.naiveDistance(rc.getLocation(), new_target)) >
					PrioritySystem.rate(priority, Utils.naiveDistance(rc.getLocation(), target))) {
					target = new_target;
					priority = new_priority;
					mode = ATTACK;
				}
			}
		};
	}

	/**
	 * Searches for empty encampments to build on.
	 * @return The best encampment found, or null otherwise.
	 * @throws GameActionException 
	 */
	private MapLocation findEmptyEncampment() throws GameActionException {
		
		MapLocation best = null;
		int distance = Integer.MAX_VALUE;
		
		for(int i = 3; i <= 6; i++) {
			MapLocation[] encampments = rc.senseEncampmentSquares(rc.getLocation(), 1 << (2*i), Team.NEUTRAL);
			for(int j = 0; j < encampments.length; j++) {
				MapLocation loc = encampments[j];
				
				if(i > 3 && rc.getLocation().distanceSquaredTo(loc) < 1 << (2*(i-1)))
					continue;
				
				if(rc.canSenseSquare(loc) && rc.senseObjectAtLocation(loc) != null)
					continue;
				
				int d = Utils.naiveDistance(rc.getLocation(), loc);
				if(d < distance) {
					best = loc;
					distance = d;
				}
			}
		}
		
		return best;
	}
	
//	protected static volatile transient int essential;
//	private static final synchronized strictfp void important() throws Exception {};
	
	private void idleBehavior() throws GameActionException {
		charging = false;
		
		//see if there is an encampment nearby to take
		if(target == null && rc.getTeamPower() > (1 + rc.senseAlliedEncampmentSquares().length)* 20.0) {
			MapLocation[] encampments = rc.senseEncampmentSquares(rc.getLocation(), 16, Team.NEUTRAL);
			if(encampments.length > 0) {
				int maxdist = 20;
				for(MapLocation encampment : encampments) {
					int dist = encampment.distanceSquaredTo(rc.getLocation());
					if(dist < maxdist) {
						maxdist = dist;
						target = encampment;
					}
				}
			}
		} else if (target != null) { //see if the encampment is taken
			int dist = rc.getLocation().distanceSquaredTo(target);
			if(dist > 0 && dist <= 14) {//change this to deal with upgrades!
				GameObject there = rc.senseObjectAtLocation(target);
				if(there != null) {
					target = null;
				}
			}
		}

		//rc.setIndicatorString(0, encampTarget.toString());

		if(rc.senseEncampmentSquare(rc.getLocation())) {
			try {
				rc.captureEncampment(RobotType.ARTILLERY);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void attackBehavior() {
		if (!charging) {
			if(rc.senseNearbyGameObjects(Robot.class, gather, 20, myTeam).length > 8) {
				charging = true;
			}
		}
		
		if (!charging) {
			target = gather;
		} else {
			target = enemyBase;
		}
		
		//mover.aboutMoveMine(rc.getLocation().directionTo(target));
		mover.setTarget(target);
		mover.execute();
	}

	private void microBehavior() {

	}
	
	private void exploreBehavior() {
		// very dumb
		mover.setTarget(enemyBase);
		mover.execute();
	}
	
	private void takeEncampmentBehavior() {
		
	}
	
	public void buildEncampment(Direction dir) {

	}

	public void researchUpgrade(Upgrade upg) {

	}
}