package team059.soldiers;

import team059.Strategy;
import team059.movement.Mover;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import static team059.utils.Utils.*;

public class CenterScoutTask extends ScoutTask {
	private boolean runningAway = false;
	
	public CenterScoutTask() {
		super();
		
		waypoint = new MapLocation[3];
		waypointIndex = 0;
		
		MapLocation center = new MapLocation((ALLY_HQ.x + ENEMY_HQ.x)/2, (ALLY_HQ.y + ENEMY_HQ.y)/2);

		//MapLocation third = new MapLocation((ALLY_HQ.x * 2 + ENEMY_HQ.x ) / 3, (ALLY_HQ.y * 2 + ENEMY_HQ.y ) / 3);
		//MapLocation twothird = new MapLocation((ALLY_HQ.x + ENEMY_HQ.x * 2) / 3, (ALLY_HQ.y + ENEMY_HQ.y * 2) / 3);
		int thirdy = scoutRight ? (ALLY_HQ.y - center.y)/3 : -(ALLY_HQ.y - center.y)/3;
		int thirdx = scoutRight ? (-ALLY_HQ.x + center.x)/3 : -(-ALLY_HQ.x + center.x)/3;

		waypoint[0] = new MapLocation(
				clamp((ALLY_HQ.x + center.x * 2) / 3 + (int) 1.4*thirdx, 0, MAP_WIDTH), 
				clamp((ALLY_HQ.y + center.y * 2) / 3 + (int) 1.4*thirdy, 0, MAP_HEIGHT)
		);

		waypoint[1] = new MapLocation(
				clamp(center.x + (int) 1.4*thirdx, 0, MAP_WIDTH), 
				clamp(center.y + (int) 1.4*thirdy, 0, MAP_HEIGHT)
		);
		
		Direction dir = Direction.values()[getDirTowards(-thirdy,-thirdx)];
		
		waypoint[2] = center.add(dir).add(dir);
		MAX_SCOUT_TURNS = 4*naiveDistance(ALLY_HQ, center);
	}

	@Override
	protected void seeEnemyWarn(int n) {
		try {
			parameters.greed = (int) (n > 2 ? 0 : n == 2 ? parameters.greed*0.2 : parameters.greed*0.5);
			messagingSystem.writeParameters(parameters);
		} catch (GameActionException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String toString() {
		return "Center SCOUTING TO " + super.mover.getTarget();
	}
}
