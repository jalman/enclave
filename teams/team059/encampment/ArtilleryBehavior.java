package team059.encampment;

import team059.RobotBehavior;
import static team059.utils.Utils.*;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

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
	
	private final boolean canAttackEnemyHQ;
	
	public ArtilleryBehavior() {
		canAttackEnemyHQ = (currentLocation.distanceSquaredTo(ENEMY_HQ) < 64);
	}

	@Override
	public void run() {

		if(RC.isActive()){
			
			if(canAttackEnemyHQ) {
				try {
					RC.attackSquare(ENEMY_HQ);
					return;
				} catch (GameActionException e) {
					e.printStackTrace();
				}
			}


			MapLocation me = RC.getLocation();

			Robot[] robots = RC.senseNearbyGameObjects(Robot.class, 98);

			int[][] weight = new int[17][17];

			int[] enemiesX = new int[robots.length];
			int[] enemiesY = new int[robots.length];
			int numEnemies = 0;

			RobotInfo info;

			for(Robot robot : robots) {
				try {
					info = RC.senseRobotInfo(robot);

					int x = info.location.x;
					int y = info.location.y;

					x -= me.x;
					y -= me.y;

					x += 8;
					y += 8;

					if(0 <= x && x <= 16 && 0 <= y && y <= 16) {
						if (info.team == ALLY_TEAM) {
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



			if(Clock.getBytecodesLeft() > numEnemies*500) {


				for(int n = 0; n < numEnemies; n++) {
					if(Clock.getBytecodesLeft() < 500) {
						break;
					}

					int[] iplaces = {enemiesX[n] - 1, enemiesX[n], enemiesX[n] + 1 };
					int[] jplaces = {enemiesY[n] - 1, enemiesY[n], enemiesY[n] + 1 };
					for(int i : iplaces){
						for(int j : jplaces){
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
							val += weight[i][j]*4;


							if(val > attackWeight) {
								attackWeight = val;
								attackX = i;
								attackY = j;
							}
						}
					}

				}


			} else {


				for(int n = 0; n < numEnemies; n++) {
					if(Clock.getBytecodesLeft() < 250) {
						break;
					}

					int i = enemiesX[n];
					int j = enemiesY[n];
					if(i <= 0 || i >= 16 || j <= 0 || j >= 16) {
						continue;
					}


					if(!IN_RANGE[i][j])
						continue;

					MapLocation ml = new MapLocation(me.x + i - 8, me.y + j - 8);
					int val = RC.senseNearbyGameObjects(Robot.class, ml, 2, ENEMY_TEAM).length + 1;
					val -= RC.senseNearbyGameObjects(Robot.class, ml, 2, ALLY_TEAM).length;

					if(val > attackWeight) {
						attackWeight = val;
						attackX = i;
						attackY = j;
					}
				}
			}





			if(attackX != 0 || attackY != 0) {
				try {
					RC.attackSquare(new MapLocation(me.x + attackX - 8, me.y + attackY - 8));
					RC.setIndicatorString(0, weight[attackX - 1][attackY - 1] + " " + weight[attackX][attackY - 1] + " " + weight[attackX + 1][attackY - 1]);
					RC.setIndicatorString(1, weight[attackX - 1][attackY] + " " + weight[attackX][attackY] + " " + weight[attackX + 1][attackY]);
					RC.setIndicatorString(2, weight[attackX - 1][attackY + 1] + " " + weight[attackX][attackY + 1] + " " + weight[attackX + 1][attackY + 1]);
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
			return 10;
		case SHIELDS:
			return 5;
		case MEDBAY:
			return 5;
		case ARTILLERY:
			return 5;
		default:
			return 2;
		}
	}

	@Override
	public void beginRound() {
	}

	@Override
	public void endRound() {
	}



}