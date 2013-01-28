package team059;

import static team059.utils.Utils.*;
import team059.hq.BuildSoldier;
import team059.hq.HQAction;
import team059.hq.UpgradeAction;
import battlecode.common.Team;
import static battlecode.common.Upgrade.*;

public enum Strategy {
	NORMAL(30, 0.0, 0, 0, new BuildSoldier(2), new UpgradeAction(FUSION), new BuildSoldier(12), new UpgradeAction(DEFUSION)),
	NUCLEAR(5, -3.0, 10, 50, new UpgradeAction(PICKAXE), new BuildSoldier(4), new UpgradeAction(NUKE)),
	RUSH(1, 3.0, 20, -50, new BuildSoldier(2), new UpgradeAction(DEFUSION));
	
	/**
	 * Default parameters for this strategy.
	 */
	public final Parameters parameters;
	
	public final HQAction[] buildOrder;
	
	private Strategy(int greed, double border, int attack, int mine, HQAction... buildOrder) {
		parameters = new Parameters(greed, border, attack, mine);
		this.buildOrder = buildOrder;
	}
	
	/**
	 * Decides what strategy to use at the beginning of the game.
	 * @return The decided-upon strategy.
	 */
	public static Strategy decide() {
		if(RC.getTeam() == Team.A) {
			return NUCLEAR;
		}
		return NORMAL;
		/*
		int distance = naiveDistance(ALLY_HQ, ENEMY_HQ);
		
		
		MapLocation halfway = new MapLocation((ALLY_HQ.x + ENEMY_HQ.x)/2, (ALLY_HQ.y + ENEMY_HQ.y)/2);
		int dx = halfway.x - ALLY_HQ.x;
		int dy = halfway.y - ALLY_HQ.y;
		
		int mines = RC.senseMineLocations(halfway, dx*dx + dy*dy, Team.NEUTRAL).length;
		
		
		if (distance > 50 && mines > dx*dx + dy*dy) {
			return NUCLEAR;
		} else if (distance < 20) {
			return RUSH;
		} else {
			return NORMAL;
		}
		*/
	}
}
