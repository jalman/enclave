package team059.hq;

import static team059.utils.Utils.*;

import team059.RobotBehavior;
import team059.Strategy;
import team059.utils.ArraySet;
import team059.utils.Shields;
import team059.messaging.MessageHandler;
import team059.utils.Utils;
import battlecode.common.*;

public class HQBehavior extends RobotBehavior {

	HQAction[] buildOrder;
	int buildOrderProgress = 0;

	int numBots, numEncampments, numSoldiers;
	int soldierID = 0;

	ArraySet<Robot> generators = new ArraySet<Robot>(500);
	ArraySet<Robot> suppliers =  new ArraySet<Robot>(500);
	int genIndex = 0, supIndex = 0;
	
	boolean emergencySoldier = false;
	int turnsSinceEmergencySoldier = 0;

	double lastFlux = 0, thisFlux = 0, fluxDiff = 0, actualFlux = 0;

	ExpandSystem expandSystem;
	WarSystem warSystem;

	public HQBehavior(Strategy strategy) {
		Utils.strategy = strategy;
		Utils.parameters = strategy.parameters.clone();
		buildOrder = strategy.buildOrder;	
		expandSystem = new ExpandSystem();
		warSystem = new WarSystem(this);
	}
	@Override
	public void beginRound() throws GameActionException {
		//RC.setIndicatorString(0, generators.size + " generators. " + Double.toString(actualFlux) + " is pow");
		numBots = RC.senseNearbyGameObjects(Robot.class, currentLocation, 10000, ALLY_TEAM).length;
		numEncampments = RC.senseAlliedEncampmentSquares().length;
		numSoldiers = numBots - numEncampments;
		actualFlux = RC.getTeamPower() - (40 + 10*generators.size);
		//thisFlux = RC.getTeamPower();
		fluxDiff = actualFlux - lastFlux;
		lastFlux = actualFlux;
		
		 if (parameters.timidity == 1)
         {
         	if (Clock.getRoundNum() % 125 == 0 && Clock.getRoundNum() > 1)
         		parameters.greed++;
         }
		 
		if(!emergencySoldier) {
			emergencySoldier = (RC.senseNearbyGameObjects(Robot.class, currentLocation, 10000, ENEMY_TEAM).length > 11*numSoldiers/10);
		} else {
			if(turnsSinceEmergencySoldier++ > 10) {
				emergencySoldier = (RC.senseNearbyGameObjects(Robot.class, currentLocation, 10000, ENEMY_TEAM).length > 11*numSoldiers/10);
			}
		}

		
		if(strategy == strategy.NORMAL && Clock.getRoundNum() % 50 == 0) {
			parameters.greed++;
			if(numSoldiers > 15) {
				expandSystem.expand(1);
			}
		}
		messagingSystem.beginRoundHQ(messageHandlers);
	}
        @Override
        public void run() throws GameActionException {
            macro();
            expand();
            warSystem.run();
            
//            RC.setIndicatorString(0, parameters.toString());
            if (parameters.timidity == 1 && Clock.getRoundNum() > 20)
            {
            	messagingSystem.writeAttackMessage(ENEMY_HQ, 200);
            }
        }
	

	/**
	 * Handle upgrades and robots.
	 */
	protected void macro() {
		if(!RC.isActive()) return;

		boolean built = false;
		if(Clock.getRoundNum() % 3 == 0) {
			updateEncampmentCounts();
		}
		//RC.setIndicatorString(1,""+RC.getTeamPower());

		try {
			if(RC.getEnergon() > 50*(Upgrade.NUKE.numRounds - RC.checkResearchProgress(Upgrade.NUKE))) {
				researchUpgrade(Upgrade.NUKE);
			} else if(emergencySoldier) {
				//System.out.println("emergency soldier");
				built = buildSoldier();				
			} else if(buildOrderProgress < buildOrder.length) {
					HQAction action = buildOrder[buildOrderProgress];
//					RC.setIndicatorString(1, action.toString());
					if(action.execute(this)) {
						if(action instanceof UpgradeAction) {
							messagingSystem.writeAnnounceUpgradeMessage( ( (UpgradeAction) action).upgrade.ordinal() );
						}
						buildOrderProgress++;
					}
			} else if(RC.isActive()) {
				//if(Clock.getRoundNum() < 300 || actualFlux > 400.0 || (actualFlux > 20.0 && fluxDiff > 0)) {
					if(numAboveSoldierCap() < 0 && (actualFlux > 400.0 || (actualFlux > 20.0 && fluxDiff > -1))) {
						built = buildSoldier();
					} 
					if(!built) {
						if(fluxDiff*60 + actualFlux < 0 && !strategy.equals(Strategy.NUCLEAR)) {
							messagingSystem.writeAttackMessage(ENEMY_HQ, 500);
						}
						researchUpgrade(Upgrade.NUKE);
					}
			}
		} catch (GameActionException e) {
			e.printStackTrace();
		}
		RC.setIndicatorString(1, "Num above soldier cap " + numAboveSoldierCap() + " actualFlux = " + actualFlux + " fluxDiff = " + fluxDiff);
	}

	int numAboveSoldierCap() {
		return (numSoldiers*130 - strategy.soldierLimitPercentage*(40+10*generators.size))/100;
	}
	

	protected void expand() {
		//		checkForVictoryExpansion();


		if(RC.senseCaptureCost() + 10 < RC.getTeamPower()) {
			try {
				expandSystem.considerExpanding();
			} catch (GameActionException e) {
				e.printStackTrace();
			}
		}
	}

	final int VICTORY_LOOKBACK = 10;
	int[] enemies = new int[VICTORY_LOOKBACK];
	int victoryTurn = -100;

	//	public void checkForVictoryExpansion() {
	//		enemies[Clock.getRoundNum() % VICTORY_LOOKBACK] = RC.senseNearbyGameObjects(Robot.class, Integer.MAX_VALUE, ENEMY_TEAM).length;
	//		if(Clock.getRoundNum() > 20) {
	//			if(enemies[Clock.getRoundNum() % VICTORY_LOOKBACK] + 15 < enemies[(Clock.getRoundNum() + 1) % VICTORY_LOOKBACK] &&
	//					RC.senseNearbyGameObjects(Robot.class, Integer.MAX_VALUE, ALLY_TEAM).length > 20 &&
	//					victoryTurn + 15 < Clock.getRoundNum()) {
	//				expandSystem.expand(2);
	//				victoryTurn = Clock.getRoundNum();
	//				System.out.println("VICTORY");
	//			}
	//		}
	//	}

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
				//System.out.println("generator lost!");
				expandSystem.lost();
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
				//				System.out.println("supplier lost!");
				expandSystem.lost();
			} else {
				supIndex++;
			}
		}
//		RC.setIndicatorString(2, "generators: " + generators.size + ", suppliers: " + suppliers.size + ", encampments: " + numEncampments + ", soldiers: " + numSoldiers);
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
					sendMessagesOnBuild();
					RC.spawn(dir);
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

	private void sendMessagesOnBuild() throws GameActionException {
		messagingSystem.writeStrategy(strategy);
		messagingSystem.writeSoldierID(soldierID++);
		for(int i = 0; i < Shields.shieldLocations.size; i++) {
			messagingSystem.writeShieldLocationMessage(Shields.shieldLocations.get(i));
		}
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
					switch(ROBOT_TYPE[message[3]]) {
					case SUPPLIER:
						suppliers.insert(newBot);
						break;
					case GENERATOR:
						generators.insert(newBot);
						break;
					case SHIELDS:
						Shields.insertShield(loc);
						break;
					}
				} catch (GameActionException e) {
					e.printStackTrace();
				}
			}
		};
	}
}
