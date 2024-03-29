package team059;

import static team059.utils.Utils.*;
import team059.hq.BuildSoldier;
import team059.hq.HQAction;
import team059.hq.UpgradeAction;
import battlecode.common.MapLocation;
import battlecode.common.Team;
import static battlecode.common.Upgrade.*;

public enum Strategy {
	NORMAL(30, -1.5, -5, 0, 70, new BuildSoldier(2), new UpgradeAction(FUSION), new BuildSoldier(12), new UpgradeAction(DEFUSION)),
	NUCLEAR(5, -2.0, 5, 0, 30, new UpgradeAction(PICKAXE), new BuildSoldier(7)), //, new UpgradeAction(NUKE)),
	RUSH(1, 1.5, -1000, 1, 90, new BuildSoldier(2), new UpgradeAction(DEFUSION));
	
	/**
	 * Default parameters for this strategy.
	 */
	public final Parameters parameters;
	
	public final int soldierLimitPercentage; // out of 100
	
	public final HQAction[] buildOrder;
	
	private Strategy(int greed, double border, int mine, int timidity, int soldierLimitPercentage, HQAction... buildOrder) {
		parameters = new Parameters(greed, border, mine, timidity);
		this.soldierLimitPercentage = soldierLimitPercentage;
		this.buildOrder = buildOrder;
	}
	
	/**
	 * Decides what strategy to use at the beginning of the game.
	 * @return The decided-upon strategy.
	 */
	public static Strategy decide() {
//		if(ALLY_TEAM.equals(Team.A))
//			return NORMAL;
//		else if(ALLY_TEAM.equals(Team.B))
//		  return NUCLEAR;
//		//ADD SOMETHING THEREABOUT (THERE=NUKE)
//
//		if(ALLY_TEAM.equals(Team.A))
//			return NUCLEAR;
//
//		 return NORMAL;

		
		int distance = naiveDistance(ALLY_HQ, ENEMY_HQ);
		
		MapLocation halfway = new MapLocation((ALLY_HQ.x + ENEMY_HQ.x)/2, (ALLY_HQ.y + ENEMY_HQ.y)/2);
		int radius2 = halfway.distanceSquaredTo(ENEMY_HQ);
		
		int mines = RC.senseMineLocations(halfway, radius2 / 2, Team.NEUTRAL).length;
		
		if (distance > 55 && mines > 400) {
			return NUCLEAR;
		} else if (distance < 32) {
			return RUSH;
		} else {
			return NORMAL;
		}
	}
}
