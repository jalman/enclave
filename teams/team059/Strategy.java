package team059;

import static preSprintBot.hq.UpgradeAction.*;
import static preSprintBot.utils.Utils.*;
import preSprintBot.hq.BuildSoldier;
import preSprintBot.hq.HQAction;
import battlecode.common.MapLocation;
import battlecode.common.Team;

public enum Strategy {
	NORMAL(),
	NUCLEAR(new BuildSoldier(2), UPGRADE_PICKAXE, new BuildSoldier(4)),
	RUSH(new BuildSoldier(2), UPGRADE_DEFUSION);
	
	
	public final HQAction[] buildOrder;
	
	
	private Strategy(HQAction... buildOrder) {
		this.buildOrder = buildOrder;
	}
	
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
