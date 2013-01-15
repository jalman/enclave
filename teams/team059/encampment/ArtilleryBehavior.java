package team059.encampment;

import battlecode.common.Clock;
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

		if(rc.isActive()){


			MapLocation me = rc.getLocation();

			Robot[] robots = rc.senseNearbyGameObjects(Robot.class, 98);
			int[][] weight = new int[17][17];
			
			int[] enemiesX = new int[robots.length];
			int[] enemiesY = new int[robots.length];
			int numEnemies = 0;

			RobotInfo info;

			for(Robot robot : robots) {
				try {

					info = rc.senseRobotInfo(robot);

					int x = info.location.x;
					int y = info.location.y;

					x -= me.x;
					y -= me.y;
					
					x += 8;
					y += 8;

					if(0 <= x && x <= 16 && 0 <= y && y <= 16) {
						if (info.team == Utils.ALLY_TEAM) {
							weight[x][y] = -weight(info.type);

						} else {
							weight[x][y] = weight(info.type);
							enemiesX[numEnemies] = x;
							enemiesY[numEnemies] = y;
							numEnemies++;
						}
					}

				} catch (GameActionException e) {
					e.printStackTrace();
				}

			}
			
			int attackX = 0;
			int attackY = 0;
			int attackWeight = -1000;

			for(int n = 0; n < numEnemies; n++) {
				for(int i = enemiesX[n] - 1; i <= enemiesX[n] + 1; i++){
					for(int j = enemiesY[n] - 1; j <= enemiesY[n] + 1; j++){
						if(i <= 0 || i >= 16 || j <= 0 || j >= 16) {
							continue;
						}
						

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
			}


			if(attackX != 0 || attackY != 0) {
				try {
					rc.attackSquare(new MapLocation(me.x + attackX - 8, me.y + attackY - 8));
					rc.setIndicatorString(0, weight[attackX - 1][attackY - 1] + " " + weight[attackX][attackY - 1] + " " + weight[attackX + 1][attackY - 1]);
					rc.setIndicatorString(1, weight[attackX - 1][attackY] + " " + weight[attackX][attackY] + " " + weight[attackX + 1][attackY]);
					rc.setIndicatorString(2, weight[attackX - 1][attackY + 1] + " " + weight[attackX][attackY + 1] + " " + weight[attackX + 1][attackY + 1]);
				} catch (GameActionException e) {
					e.printStackTrace();
				}
			}
			
		}
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
	
	@Override
	public void beginRound() {
	}
	
	@Override
	public void endRound() {
	}
	


}