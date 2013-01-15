package team059.encampment;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;
import team059.RobotBehavior;
import team059.utils.Utils;

public class ArtilleryBehavior extends RobotBehavior {

	private final boolean[][] IN_RANGE = {
			{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false},
			{false, false, false, false, false, true, true, true, true, true, true, true, false, false, false, false, false},
			{false, false, false, true, true, true, true, true, true, true, true, true, true, true, false, false, false},
			{false, false, true, true, true, true, true, true, true, true, true, true, true, true, true, false, false},
			{false, false, true, true, true, true, true, true, true, true, true, true, true, true, true, false, false},
			{false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false},
			{false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false},
			{false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false},
			{false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false},
			{false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false},
			{false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false},
			{false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false},
			{false, false, true, true, true, true, true, true, true, true, true, true, true, true, true, false, false},
			{false, false, true, true, true, true, true, true, true, true, true, true, true, true, true, false, false},
			{false, false, false, true, true, true, true, true, true, true, true, true, true, true, false, false, false},
			{false, false, false, false, false, true, true, true, true, true, true, true, false, false, false, false, false},
			{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false}
	};
	
	
	public ArtilleryBehavior(RobotController rc) {
		super(rc);
	}

	@Override
	public void run() {

		if(!rc.isActive())
			return;
		
		MapLocation me = rc.getLocation();
		
		Robot[] robots = rc.senseNearbyGameObjects(Robot.class, 98);
		int[][] weight = new int[17][17];
		
		RobotInfo info;
		
		for(Robot robot : robots) {
			try {
				
				info = rc.senseRobotInfo(robot);
				
				int x = info.location.x;
				int y = info.location.y;
				
				x -= me.x;
				y -= me.y;
				
				weight[x + 8][y + 8] = weightedWeight(info.type,info.team);
				
			} catch (GameActionException e) {
				e.printStackTrace();
			}
			

			int attackX = 0;
			int attackY = 0;
			int attackWeight = 0;
			
			for(int i = 0; i < 17; i++) {
				for(int j = 0; j < 17; j++) {
					if(!IN_RANGE[i][j])
						continue;
					
					int val = 0;
					val += weight[i-1][j-1];
					val += weight[i-1][j+1];
					val += weight[i+1][j-1];
					val += weight[i+1][j+1];
					val += weight[i][j-1];
					val += weight[i][j+1];
					val += weight[i-1][j];
					val += weight[i+1][j];
					val += weight[i][j]*2;
					
					if(val > attackWeight) {
						attackWeight = val;
						attackX = i;
						attackY = j;
					}
				}
			}
			
			
			if(attackX != 0 || attackY != 0) {
				try {
					rc.attackSquare(new MapLocation(me.x + attackX - 8, me.y + attackY - 8));
				} catch (GameActionException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private int weightedWeight(RobotType type, Team team) {
		return (team == Utils.ALLY_TEAM ? -1 : 1) * weight(type);
	}
	
	private int weight(RobotType type) {
		switch(type) {
			case SOLDIER:
				return 2;
			case HQ:
				return 6;
			case SHIELDS:
				return 4;
			case MEDBAY:
				return 4;
			case ARTILLERY:
				return 4;
			default:
				return 1;
		}
	}
	

}