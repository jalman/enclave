package team059;

import static team059.hq.UpgradeAction.*;
import static team059.utils.Utils.*;
import team059.hq.BuildSoldier;
import team059.hq.HQAction;
import battlecode.common.MapLocation;
import battlecode.common.Team;

public enum Strategy {
	NORMAL(30, 0.0),
	NUCLEAR(5, -1.0, new BuildSoldier(4), UPGRADE_PICKAXE, new BuildSoldier(4)),
	RUSH(5, 100.0, new BuildSoldier(2), UPGRADE_DEFUSION);
	
	/**
	 * How quickly to expand at the beginning.
	 */
	public final int greed;
	
	/**
	 * Roughly defines the border between us and the enemy.
	 */
	public final double border;
	
	/**
	 * Half the "width" of the border.
	 */
	public static final double margin = 0.3;
	
	public final HQAction[] buildOrder;
	
	private Strategy(int greed, double border, HQAction... buildOrder) {
		this.greed = greed;
		this.border = border;
		this.buildOrder = buildOrder;
	}
	
	/**
	 * Decides what strategy to use at the beginning of the game.
	 * @return The decided-upon strategy.
	 */
	public static Strategy decide() {
		return RUSH;
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
