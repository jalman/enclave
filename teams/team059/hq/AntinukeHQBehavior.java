package team059.hq;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Upgrade;
import team059.RobotBehavior;
import team059.Strategy;
import team059.utils.Shields;
import static team059.utils.Utils.*;

public class AntinukeHQBehavior extends HQBehavior {

//	0-9: encamp
//	10-34: DIFFUSION
//	35,45,55,65,75,85,95,105: ATTACKER group 1
//	115,125,135,145,155: Diffusers
//	after: ATTACKER unless need more diffusers
	
	Direction spawndir = ALLY_HQ.directionTo(ENEMY_HQ);
	MapLocation shield = ALLY_HQ;
	int dist = Integer.MAX_VALUE;

	public AntinukeHQBehavior() throws GameActionException {
		super(Strategy.RUSH);
		
		//decide where the shield will be
		MapLocation[] encampments = { ALLY_HQ };
		try {
			encampments = RC.senseEncampmentSquares(ALLY_HQ, ALLY_HQ.distanceSquaredTo(ENEMY_HQ)/16, null);
		} catch (GameActionException e) {
			e.printStackTrace();
		}
		int tempdist;
		for(MapLocation encampment : encampments) {
			tempdist = encampment.distanceSquaredTo(ENEMY_HQ);
			if(tempdist < dist) {
				dist = tempdist;
				shield = encampment;
			}
		}
		Shields.insertShield(shield);
	}

	
	@Override
	public void run() throws GameActionException {
		if(RC.isActive()) {
			if(Clock.getRoundNum() < 35 && Clock.getRoundNum() > 9) {
				RC.researchUpgrade(Upgrade.DEFUSION);
			} else {
				buildSoldier();
			}
		}
	}
}
