package team059.hq;

import static team059.utils.Utils.*;

import java.util.Arrays;

import team059.RobotBehavior;
import team059.Strategy;
import team059.utils.ArraySet;
import team059.messaging.MessageHandler;
import team059.messaging.MessagingSystem;
import team059.utils.Utils;
import battlecode.common.*;

public class HQBehavior extends RobotBehavior {
	
	HQAction[] buildOrder;
	int buildOrderProgress = 0;
	boolean enemyNukeHalfDone = false;
	int enemyNukeHalfRound;
	int numBots;
	
	ArraySet<Robot> generators = new ArraySet<Robot>(500);
	ArraySet<Robot> suppliers =  new ArraySet<Robot>(500);
	int genIndex = 0, supIndex = 0;
	
	double lastFlux = 0, thisFlux = 0, fluxDiff = 0, actualFlux = 0;
	
	boolean panicking = false;
	
	ExpandSystem expandSystem;
	
	public HQBehavior() {
		strategy = Strategy.decide();
		buildOrder = strategy.buildOrder;	
		expandSystem = new ExpandSystem();
	}
	
	@Override
	public void beginRound() throws GameActionException {
		RC.setIndicatorString(0, generators.size + " generators. " + Double.toString(actualFlux) + " is pow");
		numBots = RC.senseNearbyGameObjects(Robot.class, currentLocation, MAP_WIDTH+MAP_HEIGHT, ALLY_TEAM).length;
		actualFlux = RC.getTeamPower() - (40 + 10*generators.size);
		//thisFlux = RC.getTeamPower();
		fluxDiff = actualFlux - lastFlux;
		lastFlux = actualFlux;
		messaging = actualFlux > MessagingSystem.MESSAGING_COST;
		//messaging = false;
		if(messaging) {
			try {
				messagingSystem.beginRoundHQ(messageHandlers);
			} catch (GameActionException e1) {
				e1.printStackTrace();
			}
		}
		
		if(!enemyNukeHalfDone && Clock.getRoundNum() > Upgrade.NUKE.numRounds / 2) {
			enemyNukeHalfDone = RC.senseEnemyNukeHalfDone();
			enemyNukeHalfRound = Clock.getRoundNum();
		}
	}

	/**
	 * Handle upgrades and robots.
	 */
	private void macro() {
		boolean built = false;
		if(Clock.getRoundNum() % 3 == 0) {
			updateEncampmentCounts();
		}
		//RC.setIndicatorString(1,""+RC.getTeamPower());
		if(buildOrderProgress < buildOrder.length) {
			try {
				HQAction action = buildOrder[buildOrderProgress];
				if(action.execute(this)) {
					if(action instanceof UpgradeAction) {
						messagingSystem.writeAnnounceUpgradeMessage( ( (UpgradeAction) action).upgrade.ordinal() );
					}
					buildOrderProgress++;
				}
			} catch (GameActionException e) {
				e.printStackTrace();
			}
		} else if(RC.isActive()) {
			if(actualFlux > 400.0 || (actualFlux > 10.0 && fluxDiff > 0)) {
				try {
					built = buildSoldier();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if(!built){
				try {
					researchUpgrade(Upgrade.NUKE);
				} catch (GameActionException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void expand() {
		if(RC.senseCaptureCost() + 10 < RC.getTeamPower()) {
			try {
				expandSystem.considerExpanding(0); //fix this
			} catch (GameActionException e) {
				e.printStackTrace();
			} 
		}
	}
	
	private int ENCAMPMENTS_TO_CHECK = 6;
	private void updateEncampmentCounts() { // throws GameActionException {
//		int[] toDelete = new int[500];
//		int numToDelete = 0;
		
		for(int i=0; i<ENCAMPMENTS_TO_CHECK; i++) {
			if(genIndex >= generators.size){
				genIndex = 0;
				break;
			}
			if(!RC.canSenseObject(generators.get(genIndex))) {
				generators.delete(genIndex);
				System.out.println("generator lost!");
			} else {
				genIndex++;
			}
		}

		for(int i=0; i<ENCAMPMENTS_TO_CHECK; i++) {
			if(supIndex >= suppliers.size){
				supIndex = 0;
				break;
			}
			if(!RC.canSenseObject(suppliers.get(supIndex))) {
				suppliers.delete(supIndex);
				System.out.println("supplier lost!");
			} else {
				supIndex++;
			}
		}
	}
	
	@Override
	public void run() throws GameActionException {
		macro();
		expand();
		
		if(panic()) {
			messagingSystem.writeAttackMessage(ENEMY_HQ, 500000);
		}
	}
	
	public boolean panic() {
		try {
			if(!panicking) {
				panicking = enemyNukeHalfDone && Clock.getRoundNum() - enemyNukeHalfRound + Upgrade.NUKE.numRounds / 2 > RC.checkResearchProgress(Upgrade.NUKE);
			}
		} catch (GameActionException e) {
			e.printStackTrace();
		}
		return panicking;
	}
	
	/**
	 * Tries to build a soldier.
	 * @return Whether successful.
	 * @throws GameActionException
	 */
	boolean buildSoldier() throws GameActionException {
		return buildSoldier(ALLY_HQ.directionTo(ENEMY_HQ));
	}
	
	/**
	 * Tries to build a soldier.
	 * @param dir The direction in which to build.
	 * @return Whether successful.
	 * @throws GameActionException
	 */
	boolean buildSoldier(Direction dir) throws GameActionException {
		if (RC.isActive()) {
			// Spawn a soldier
			for(int i = 0; i < 8; i++) {
				if(goodPlaceToMakeSoldier(dir)) {
					RC.spawn(dir);
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
		return RC.canMove(dir) && !Utils.isEnemyMine(RC.getLocation().add(dir));
	}

	void researchUpgrade(Upgrade upg) throws GameActionException {
		if(RC.isActive()) {
			RC.researchUpgrade(upg);
		}
	}

	@Override
	protected MessageHandler getBirthInfoHandler() {
		return new MessageHandler() {
			@Override
			public void handleMessage(int[] message) {
				try {
					MapLocation loc = new MapLocation(message[0], message[1]);
					//System.out.println(loc);
					Robot newBot = (Robot) RC.senseObjectAtLocation(loc);
					//System.out.print("seen " + Arrays.toString(message) + " born!");
					if(newBot == null) {
						//System.out.println("wat");
						return;
					}
					if(message[3] == MessagingSystem.SUPPLIER) {
						suppliers.insert(newBot);
						//System.out.println(" supplier born. suppliers.size = " + suppliers.size);
					} else if(message[3] == MessagingSystem.GENERATOR) {
						generators.insert(newBot);
						//System.out.println(" generator born. generators.size = " + generators.size);
					}
				} catch (GameActionException e) {
					e.printStackTrace();
				}
			}
		};
	}
}
