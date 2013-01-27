package team059.soldiers;

import java.util.Arrays;

import team059.utils.FastIterableLocSet;
import team059.utils.Utils;
//import team059.utils.FastLocSet;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Upgrade;
import static team059.utils.Utils.*;

public class MineManager extends TaskGiver {
	MineTask task;
	SoldierBehavior2 sb;
	private FastIterableLocSet currentlyMining = new FastIterableLocSet();
	
	public int miningPriority = 0;
	
	public int[][] mineLocs;
	int Ax, Ay; // ally hq (x,y)
	int Ox, Oy; // "origin" (x,y) for lattice
	int v1x, v1y, v2x, v2y; 
	int coordSum = 0; // is c1 + c2 for (c1*(v1x, v1y) + c2*(v2x, v2y))
	public int maxCoordSum = 0;
	public int minCoordSum = 0;
	
	boolean efficientMining = true;
//	boolean biased = false;
	
	
	MapLocation center;
	
	
	public MineManager() {
		setMineCenter(ALLY_HQ);
		minCoordSum = -2;
		task = null;
	}

	public void setMineCenter(MapLocation center) {
		if(!center.equals(this.center)) {
			this.center = center;
			coordSum = minCoordSum;
			findPackingLattice();
		}
	}

	public MapLocation getMineCenter() {
		return center;
	}
	
//	public void setBias(boolean flag) {
//		biased = flag;
//	}
//	
//	public boolean getBias() {
//		return biased;
//	}
//	
	@Override
	public void compute() throws GameActionException {
		switch(strategy) {
		case NUCLEAR:
			miningPriority = 10000;
			maxCoordSum = 3;
			maxCoordSum = -2;
			efficientMining = true;
			setMineCenter(ALLY_HQ);
			break;
		case NORMAL:
			miningPriority = 0;
			maxCoordSum = 1;
			minCoordSum = -1;
			efficientMining = false;
			break;
		case RUSH:
			miningPriority = -1000;
			maxCoordSum = 0;
			minCoordSum = 0;
			efficientMining = false;
			break;
		}
		
		if(task == null || task.done()) {
			if(efficientMining) {
				task = new MineTask(findNextInLattice(), miningPriority);
			} else {
				
			}
		}
	}
	
	private MapLocation findNextInLattice() {
		while(coordSum <= maxCoordSum) {
			for(int i = minCoordSum; i <= maxCoordSum; i++) {
				int j = coordSum-i;
				if(j < minCoordSum) break;
				int mx = Ax + i*v1x + j*v2x;
				int my = Ay + i*v1y + j*v2y;
				if(mineLocs[mx][my] != 2) {
					return new MapLocation(mx, my);
				}
			}
			coordSum++;
		}
		mineLocs[center.x][center.y] = 10;
		return null;
	}
		
	private void findPackingLattice() {
		coordSum = 0;
		if(!RC.isActive()) return;
		mineLocs = new int[MAP_WIDTH][MAP_HEIGHT];
		Ax = center.x;
		Ay = center.y;
		mineLocs[Ax][Ay] = -1; 
		
		int dx = ENEMY_HQ.x - Ax;
		int dy = ENEMY_HQ.y - Ay;
		

		if(UPGRADES_RESEARCHED[Upgrade.PICKAXE.ordinal()]) {
			if(dx > 0) {
				if(dy > dx) {
					// Face toward N-NE
					Ox = Ax;
					Oy = Ay-1;
					v1x = 2; v1y = 1; v2x = -1;	v2y = 2;
				} else if(dy > 0) {
					// Face toward E-NE
					Ox = Ax-1;
					Oy = Ay;
					v1x = 1; v1y = 2; v2x = 2; v2y = -1;
				} else if(dy+dx > 0) {
					// Face toward E-SE
					Ox = Ax-1;
					Oy = Ay;
					v1x = 1; v1y = -2; v2x = 2; v2y = 1;
				} else {
					// Face toward S-SE
					Ox = Ax;
					Oy = Ay+1;
					v1x = -1; v1y = -2; v2x = 2; v2y = -1;
				}
			} else {
				if(dy > dx) {
					// Face toward N-NW
					Ox = Ax;
					Oy = Ay-1;
					v1x = 1; v1y = 2; v2x = -2;	v2y = 1;
				} else if(dy > 0) {
					// Face toward W-NW
					Ox = Ax+1;
					Oy = Ay;
					v1x = -1; v1y = 2; v2x = -2; v2y = -1;
				} else if(dy+dx > 0) {
					// Face toward W-SW
					Ox = Ax+1;
					Oy = Ay;
					v1x = -1; v1y = -2; v2x = -2; v2y = 1;
				} else {
					// Face toward S-SW
					Ox = Ax;
					Oy = Ay+1;
					v1x = 1; v1y = -2; v2x = -2; v2y = -1;
				}
			}
		} else {
			Ox = Ax;
			Oy = Ay;
			v1x = 1; v1y = 0; v2x = 0; v2y = 1;
		}
	}
		
/*
	public void findMinePlace() throws GameActionException {
		for(int i=-1; i<=coordSum+1; i++) {
			int j = coordSum-i;
			int mx = Ox+i*v1x+j*v2x, my = Oy+i*v1y+j*v2y;
			if(mineLocs[mx][my] == 0) {
				messagingSystem.writeLayingMineMessage(new MapLocation(mx, my), ID);
				
			}
		}
		
		MapLocation[] mineLocs = RC.senseMineLocations(currentLocation, CHECK_MINE_RADIUS_SQUARED, null);
		Direction dir = currentLocation.directionTo(ALLY_HQ);
		int dx = dir.dx, dy = dir.dy;
		//int [][] mineMap = searchForMineLoc(mineLocs, currentLocation.directionTo(ENEMY_HQ), CHECK_MINE_RADIUS);
		int MINE_CHECK_SIZE = 2*CHECK_MINE_RADIUS+3;
		int[][] mineMap = new int[MINE_CHECK_SIZE][MINE_CHECK_SIZE];
		for(MapLocation loc : mineLocs) {
			int ax = loc.x-curX+CHECK_MINE_RADIUS+1;
			int ay = loc.y-curY+CHECK_MINE_RADIUS+1;
			mineMap[ax][ay]+=CHECK_MINE_RADIUS;
			mineMap[ax+1][ay]+=CHECK_MINE_RADIUS;
			mineMap[ax-1][ay]+=CHECK_MINE_RADIUS;
			mineMap[ax][ay+1]+=CHECK_MINE_RADIUS;
			mineMap[ax][ay-1]+=CHECK_MINE_RADIUS;
		}
		
		for(MapLocation loc : currentlyMining.getKeys()) {
			int ax = loc.x-curX+CHECK_MINE_RADIUS+1;
			int ay = loc.y-curY+CHECK_MINE_RADIUS+1;
			if(ax >= 1 && ax < MINE_CHECK_SIZE-1 && ay >= 1 && ay < MINE_CHECK_SIZE-1) {
				mineMap[ax][ay]+=CHECK_MINE_RADIUS;
				mineMap[ax+1][ay]+=CHECK_MINE_RADIUS;
				mineMap[ax-1][ay]+=CHECK_MINE_RADIUS;
				mineMap[ax][ay+1]+=CHECK_MINE_RADIUS;
				mineMap[ax][ay-1]+=CHECK_MINE_RADIUS;
			}
		}
		
		int min_score = 100000;
		int bestx = -1, besty = -1;
		
		for(int i=1; i<MINE_CHECK_SIZE-1; i++) {
			for(int j=1; j<MINE_CHECK_SIZE-1; j++) {
				int val = mineMap[i][j]; // - (dx*(i - CHECK_MINE_RADIUS -1) + dy*(j-CHECK_MINE_RADIUS -1)); // subtract dot product, so lower if in correct direction
				if(val < min_score) {
					int tryx = i+curX-CHECK_MINE_RADIUS-1;
					int tryy = j+curY-CHECK_MINE_RADIUS-1;
					if(tryx >= 0 && tryx < MAP_WIDTH && tryy >= 0 && tryy < MAP_HEIGHT) {
						bestx = tryx;
						besty = tryy;
						min_score = val;
					}
				}
			}
		}
		if(bestx == -1 || min_score >= 2*CHECK_MINE_RADIUS) {
			currentlyMining = new FastIterableLocSet();
			task = null;
//			for(int[] row : mineMap) {
//				System.out.print(Arrays.toString(row) + "; ");
//			}
//			System.out.println();
		} else {
			MapLocation loc = new MapLocation(bestx, besty);
			task = new MineTask(loc, 10-min_score);
		}
		
		 //task = new MineTask();
	}
*/
	
	public void receiveMineMessage(int x, int y) {
		mineLocs[x][y] = 2;
		if(UPGRADES_RESEARCHED[Upgrade.PICKAXE.ordinal()]) {
			mineLocInsert(x-1,y,1);
			mineLocInsert(x+1,y,1);
			mineLocInsert(x,y-1,1);
			mineLocInsert(x,y+1,1);
		}
	}
	
	public void mineLocInsert(int x, int y, int c) {
		if(x >= 0 && x < MAP_WIDTH && y >= 0 && y < MAP_WIDTH) {
			mineLocs[x][y] = c;
		}
	}

	@Override
	public Task getTask() {
		return task;
	}

}
