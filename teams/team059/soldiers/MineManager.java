package team059.soldiers;

import java.util.Arrays;

import team059.utils.FastIterableLocSet;
//import team059.utils.FastLocSet;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import static team059.utils.Utils.*;

public class MineManager extends TaskGiver {
	MineTask task;
	SoldierBehavior2 sb;
	private FastIterableLocSet currentlyMining = new FastIterableLocSet();
	
	public MineManager(SoldierBehavior2 sb) {
		this.sb = sb;
	}
	
	
	@Override
	public void compute() throws GameActionException {
		if(!RC.isActive()) return;
		
		
		
		
//		MapLocation[] mineLocs = RC.senseMineLocations(currentLocation, CHECK_MINE_RADIUS_SQUARED, null);
//		Direction dir = currentLocation.directionTo(ALLY_HQ);
//		int dx = dir.dx, dy = dir.dy;
//		//int [][] mineMap = searchForMineLoc(mineLocs, currentLocation.directionTo(ENEMY_HQ), CHECK_MINE_RADIUS);
//		int MINE_CHECK_SIZE = 2*CHECK_MINE_RADIUS+3;
//		int[][] mineMap = new int[MINE_CHECK_SIZE][MINE_CHECK_SIZE];
//		for(MapLocation loc : mineLocs) {
//			int ax = loc.x-curX+CHECK_MINE_RADIUS+1;
//			int ay = loc.y-curY+CHECK_MINE_RADIUS+1;
//			mineMap[ax][ay]+=CHECK_MINE_RADIUS;
//			mineMap[ax+1][ay]+=CHECK_MINE_RADIUS;
//			mineMap[ax-1][ay]+=CHECK_MINE_RADIUS;
//			mineMap[ax][ay+1]+=CHECK_MINE_RADIUS;
//			mineMap[ax][ay-1]+=CHECK_MINE_RADIUS;
//		}
//		
//		for(MapLocation loc : currentlyMining.getKeys()) {
//			int ax = loc.x-curX+CHECK_MINE_RADIUS+1;
//			int ay = loc.y-curY+CHECK_MINE_RADIUS+1;
//			if(ax >= 1 && ax < MINE_CHECK_SIZE-1 && ay >= 1 && ay < MINE_CHECK_SIZE-1) {
//				mineMap[ax][ay]+=CHECK_MINE_RADIUS;
//				mineMap[ax+1][ay]+=CHECK_MINE_RADIUS;
//				mineMap[ax-1][ay]+=CHECK_MINE_RADIUS;
//				mineMap[ax][ay+1]+=CHECK_MINE_RADIUS;
//				mineMap[ax][ay-1]+=CHECK_MINE_RADIUS;
//			}
//		}
//		
//		int min_score = 100000;
//		int bestx = -1, besty = -1;
//		
//		for(int i=1; i<MINE_CHECK_SIZE-1; i++) {
//			for(int j=1; j<MINE_CHECK_SIZE-1; j++) {
//				int val = mineMap[i][j]; // - (dx*(i - CHECK_MINE_RADIUS -1) + dy*(j-CHECK_MINE_RADIUS -1)); // subtract dot product, so lower if in correct direction
//				if(val < min_score) {
//					int tryx = i+curX-CHECK_MINE_RADIUS-1;
//					int tryy = j+curY-CHECK_MINE_RADIUS-1;
//					if(tryx >= 0 && tryx < MAP_WIDTH && tryy >= 0 && tryy < MAP_HEIGHT) {
//						bestx = tryx;
//						besty = tryy;
//						min_score = val;
//					}
//				}
//			}
//		}
//		if(bestx == -1 || min_score >= 2*CHECK_MINE_RADIUS) {
//			currentlyMining = new FastIterableLocSet();
//			task = null;
////			for(int[] row : mineMap) {
////				System.out.print(Arrays.toString(row) + "; ");
////			}
////			System.out.println();
//		} else {
//			MapLocation loc = new MapLocation(bestx, besty);
//			task = new MineTask(loc, 10-min_score);
//		}
//		
//		 //task = new MineTask();
	}
	
	public void receiveMineMessage(MapLocation loc) {
		currentlyMining.add(loc);
	}
	

	@Override
	public Task getTask() {
		return task;
	}

}
