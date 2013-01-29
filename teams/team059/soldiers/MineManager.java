package team059.soldiers;

import java.util.Arrays;
import java.util.LinkedList;

import team059.Strategy;
import team059.utils.ArraySet;
import team059.utils.FastIterableLocSet;
//import team059.utils.FastLocSet;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;
import battlecode.common.Upgrade;
import static team059.utils.Utils.*;

public class MineManager extends TaskGiver {
	MineTask task;
	//SoldierBehavior2 sb;
	//private FastIterableLocSet currentlyMining = new FastIterableLocSet();
	
	public int miningPriority = 0;
	
	public int[][] mineLocs;
	int Ax, Ay; // ally hq (x,y)
	int Ox, Oy; // "origin" (x,y) for lattice
	int v1x, v1y, v2x, v2y; // basis vectors of lattice
	int det; // determinant
	int nextX = -1, nextY = -1;
	//int coordSum = 0; // is c1 + c2, corresponding to the square (c1*(v1x, v1y) + c2*(v2x, v2y))
	
//	public int minCoordSum = 0; // always <= 0
//	public int maxCoordSum = 0; // always >= 0
	
	/**
	 * Minefield sizes for HQ, artillery, other encampments.
	 */	
	public int hqMFLeft = 0, hqMFRight = 0, hqMFForward = 0, hqMFBackward = 0, 
			artMFLeft = 0, artMFRight = 0, artMFForward = 0, artMFBackward = 0,
			encMFLeft = 0, encMFRight = 0, encMFForward = 0, encMFBackward = 0;
	
	
//	boolean efficientMining = true;
	
	/**
	 * Entries in mineLocs
	 */
	public static final int CENTER_MINE = 10; // center of a pickaxe-shaped set of mines	
	public static final int MINE_TARGET = -1; // target to mine around
	public static final int NO_MINE = 0;
	public static final int HAS_NEUTRAL_MINE = 1; 	
	public static final int HAS_ALLIED_MINE = 2;	
	// note: if pickaxe center mine is laid next to a neutral mine, assumes that square has an allied mine (for mine-laying purposes)
	public static final int HAS_ENEMY_MINE = 3;
	public static final int DONE_MINE_TARGET = -100; // if a target is "finished" mining around
//	boolean biased = false;
	
//	public static final int NUCLEAR_MINING_PRIORITY = 5000;
	
	MapLocation center, colStart; // colEnd;
	RobotType centerType;
	private int soldierID = -1, colOffset = 0, colHeight = 0, curHeight = 0;
	
	//Strategy lastStrategy;
	
	
	public MineManager() {
		mineLocs = new int[MAP_WIDTH][MAP_HEIGHT];
//		minCoordSum = -2;
		task = null;
		decideStrategy();
		setMineCenter(ALLY_HQ, RobotType.HQ);
	}

	public void setMineCenter(MapLocation center, RobotType robotType) {
		if(!center.equals(this.center)) {
			this.center = center;
			this.centerType = robotType;
			findPackingLattice();
		}
	}
	
	public void resetMineCenter() {
		findPackingLattice();
	}

	public MapLocation getMineCenter() {
		return center;
	}
	
	public void setSoldierID(int id) {
		this.soldierID = id;
		findNewCol();
	}

	public void decideStrategy() {
		miningPriority = strategy.parameters.mine;
//		System.out.println("Strategy is " + strategy);
			switch(strategy) {
			case NUCLEAR:
				hqMFLeft = 3; hqMFRight = 3; hqMFForward = 3; hqMFBackward = 1;
				artMFLeft = 1; artMFRight = 1; artMFForward = 2; artMFBackward = 1;
				encMFLeft = 1; encMFRight = 1; encMFForward = 1; encMFBackward = 1;
	//			maxCoordSum = 4;
	//			minCoordSum = -1;
	//			efficientMining = true;
				break;
			case NORMAL:
				hqMFLeft = 2; hqMFRight = 2; hqMFForward = 2; hqMFBackward = 2;
				artMFLeft = 1; artMFRight = 1; artMFForward = 2; artMFBackward = 1;
				encMFLeft = 1; encMFRight = 1; encMFForward = 1; encMFBackward = 1;
	//			maxCoordSum = 1;
	//			minCoordSum = -1;
	//			efficientMining = false;
				break;
			case RUSH:
				hqMFLeft = 0; hqMFRight = 0; hqMFForward = 0; hqMFBackward = 0;
				artMFLeft = 0; artMFRight = 0; artMFForward = 0; artMFBackward = 0;
				encMFLeft = 0; encMFRight = 0; encMFForward = 0; encMFBackward = 0;
	//			maxCoordSum = 0;
	//			minCoordSum = 0;
	//			efficientMining = false;
				break;
			default:
				return;
			}
	}
	
	@Override
	public void compute() throws GameActionException {
//		System.out.println(strategy + " ; " + task);
		
		decideStrategy();
		
		if(task == null) {
//			if(efficientMining) {
				MapLocation l = findNextInLine();
				if(l != null) {
					task = new MineTask(l, miningPriority);
				} else {
					task = null;
				}
//			} else {
//				task = null;
//			}
		} else 	if(task.done()) {
			receiveMineMessage(task.destination.x, task.destination.y);
//			if(efficientMining) {
				MapLocation l = findNextInLine();
				if(mineLocs[center.x][center.y] == DONE_MINE_TARGET) {
					task = null;
				} else if(l != null) {
					task = new MineTask(l, miningPriority);
				}
//			} else {
//				task = null;
//			}
		}
	}
	
	private MapLocation findNextInLine() {
//		System.out.print("column starting with " + colStart);
		while(curHeight <= colHeight) {
			curHeight++;
//			System.out.print("... currently at height " + curHeight + " ... ");
			nextX += (v1x + v2x);
			nextY += (v1y + v2y);
			//if(nextX == colEnd.x && nextY == colEnd.y) break;
			if(nextX < 0 || nextX >= MAP_WIDTH || nextY < 0 || nextY >= MAP_HEIGHT) break;
			if(mineLocs[nextX][nextY] == 0) {
				MapLocation loc = new MapLocation(nextX, nextY);
				if(RC.senseMine(loc) == ALLY_TEAM) {
					mineLocs[nextX][nextY] = HAS_ALLIED_MINE;
				} else {
//					System.out.println("trying at " + loc);
					return loc;
				}
			}
		}
//		System.out.println();
		colOffset++;
		findNewCol();
		return null;
	}
	
	/*
	private MapLocation findClosestInLattice() {
		int dx = curX - Ox, dy = curY - Oy;
		int i = (v1x*dx - v1y*dy) / det, j = (-v2x*dx + v2y*dy) / det;
		
		int mx = Ox + i*v1x + j*v2x, my = Oy + i*v1y + j*v2y;

		RC.setIndicatorString(2, "curX, curY = " + curX + ", " + curY + ". mx, my = " + mx + ", " + my);

		//these MapLocations are just int pairs that I'm too lazy to deal with, not actual MapLocations!
		
		//LinkedList<MapLocation> checkSquares = new LinkedList<MapLocation>();
		ArraySet<MapLocation> checkSquares = new ArraySet<MapLocation>(400);

		//checkSquares.add(new MapLocation(0,0)); 
		checkSquares.insert(new MapLocation(0,0)); 
		
		int index = 0;
		int offset = GameConstants.MAP_MAX_WIDTH;
		boolean[][] alreadyChecked = new boolean[2*offset][2*offset];

		int bc = Clock.getBytecodeNum();
		int loop_bc = bc;
		//while(!checkSquares.isEmpty()) {
		while(checkSquares.size > 0) {
			System.out.println("last loop used about " + (Clock.getBytecodeNum() - loop_bc - 30) + " bytecodes.");
			loop_bc = Clock.getBytecodeNum();
			if(index > 200) {
				System.out.println("index > 200; used about " + (Clock.getBytecodeNum() - bc - 30) + " bytecodes.");
				return null;
			}
			try {
			//MapLocation checking = checkSquares.pop();
			MapLocation checking = checkSquares.get(index++);
			//System.out.print(" | check " + checking+ " ");
//			if(checking == null) return null;
			int tempx = checking.x + offset, tempy = checking.y + offset;
			alreadyChecked[tempx][tempy] = true;
			//System.out.print(" | temp = " + tempx + ", " + tempy + " ");
			int thisx = Ox + v1x*checking.x + v2x*checking.y, thisy = Oy + v1y*checking.x + v2y*checking.y;
			 // NOT ANYMORE checking.x corresponds to v1, .y = v2
			
			
			if(thisx >= 0 && thisx < MAP_WIDTH && thisy >= 0 && thisy < MAP_HEIGHT) {
				if(mineLocs[thisx][thisy] == 0) {
					System.out.print(" | no mine at " + thisx + ", " + thisy + "? temp = " + tempx + ", " + tempy + " ");
//					System.out.println("("+thisx+","+thisy+") has no mine.");
					MapLocation thisLoc = new MapLocation(thisx, thisy);
					if(RC.senseMine(thisLoc) == ALLY_TEAM) {
//						System.out.println("("+thisx+","+thisy+") has a mine.");
						mineLocs[thisx][thisy] = HAS_ALLIED_MINE;
					} else {
						System.out.println("returning " + thisLoc + "; used about " + (Clock.getBytecodeNum() - bc - 30) + " bytecodes.");
//						StringBuilder s = new StringBuilder();
//						for(int zi=0; zi<alreadyChecked.length; zi++) {
//							for(int zj=0; zj<alreadyChecked[zi].length; zj++) {
//								s.append(alreadyChecked[zi][zj] ? 1 : 0);
//							}
//							s.append('\n');
//						}
//						System.out.println("BLAH\n" + s);
						return thisLoc;
					}
				}
				if(!alreadyChecked[tempx+1][tempy]) {
					//checkSquares.add(new MapLocation(checking.x + 1, checking.y));
					checkSquares.insert(new MapLocation(checking.x + 1, checking.y));
				}

				if(!alreadyChecked[tempx][tempy+1]) {
//					checkSquares.add(new MapLocation(checking.x, checking.y + 1));
					checkSquares.insert(new MapLocation(checking.x, checking.y + 1));
				}
				
				if(!alreadyChecked[tempx-1][tempy]) {
//					checkSquares.add(new MapLocation(checking.x - 1, checking.y));
					checkSquares.insert(new MapLocation(checking.x - 1, checking.y));
				}

				if(!alreadyChecked[tempx][tempy-1]) {
//					checkSquares.add(new MapLocation(checking.x, checking.y - 1));
					checkSquares.insert(new MapLocation(checking.x, checking.y - 1));
				}
			}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("index = " + index);
				for(int zi=0; zi<alreadyChecked.length; zi++) {
					for(int zj=0; zj<alreadyChecked[zi].length; zj++) {
						System.out.print(" " + (alreadyChecked[zi][zj] ? 1 : 0));
					}
					System.out.println();
				}
			}
		}
		mineLocs[center.x][center.y] = DONE_MINE_TARGET;
		return null;
	}
	*/
	
	/*
	 private MapLocation findNextInLattice() {
		while(coordSum <= 2*maxCoordSum) {
			//System.out.println("" + coordSum);
			for(int i = minCoordSum; i <= maxCoordSum; i++) {
				int j = coordSum-i;
				if(j < minCoordSum || j > maxCoordSum) continue;
				int mx = Ox + i*v1x + j*v2x;
				int my = Oy + i*v1y + j*v2y;
				if(mx < 0 || mx >= MAP_WIDTH || my < 0 || my >= MAP_HEIGHT) continue;
				//System.out.print("| " + "(" + i + "," + j + "); " + mx + ", " + my + " ");
				if(mineLocs[mx][my] == 0) {
					return new MapLocation(mx, my);
				}
			}
			coordSum++;
		}
		mineLocs[center.x][center.y] = DONE_MINE_TARGET;
		return null;
	}
	*/
		
	private void findPackingLattice() {
//		coordSum = 2*minCoordSum;
		//if(!RC.isActive()) return;
		Ax = center.x;
		Ay = center.y;
		mineLocs[Ax][Ay] = MINE_TARGET; 
		
		int dx = ENEMY_HQ.x - Ax;
		int dy = ENEMY_HQ.y - Ay;
		

		if(UPGRADES_RESEARCHED[Upgrade.PICKAXE.ordinal()]) { // right-handed
			if(dx > 0) {
				if(dy > dx) {
					// Face toward N-NE
					Ox = Ax;
					Oy = Ay-1;
					v2x = -1; v2y = 2; v1x = 2; v1y = 1;
				} else if(dy > 0) {
					// Face toward E-NE
					Ox = Ax-1;
					Oy = Ay;
					v2x = 1; v2y = 2; v1x = 2; v1y = -1;
				} else if(dy+dx > 0) {
					// Face toward E-SE
					Ox = Ax-1;
					Oy = Ay;
					v2x = 2; v2y = 1; v1x = 1; v1y = -2;
				} else {
					// Face toward S-SE
					Ox = Ax;
					Oy = Ay+1;
					v2x = 2; v2y = -1; v1x = -1; v1y = -2;
				}
			} else {
				if(dy > -dx) {
					// Face toward N-NW
					Ox = Ax;
					Oy = Ay-1;
					v2x = -2; v2y = 1; v1x = 1; v1y = 2;
				} else if(dy > 0) {
					// Face toward W-NW
					Ox = Ax+1;
					Oy = Ay;
					v2x = -2; v2y = -1; v1x = -1; v1y = 2;
				} else if(dy > dx) {
					// Face toward W-SW
					Ox = Ax+1;
					Oy = Ay;
					v2x = -1; v2y = -2; v1x = -2; v1y = 1;
				} else {
					// Face toward S-SW
					Ox = Ax;
					Oy = Ay+1;
					v2x = 1; v2y = -2; v1x = -2; v1y = -1;
				}
			}
		} else {
			Ox = Ax;
			Oy = Ay;
			//System.out.println("ALLY_HQ.directionTo(ENEMY_HQ) = " + ALLY_HQ.directionTo(ENEMY_HQ));
			switch(ALLY_HQ.directionTo(ENEMY_HQ)) {
			case SOUTH:
				v2x = -1; v2y = 1; v1x = 1; v1y = 1;
				break;
			case SOUTH_EAST:
				v2x = 0; v2y = 1; v1x = 1; v1y = 0;
				break;
			case EAST:
				v2x = 1; v2y = -1; v1x = 1; v1y = 1;
				break;
			case NORTH_EAST:
				v2x = 1; v2y = 0; v1x = 0; v1y = -1;
				break;
			case NORTH:
				v2x = 1; v2y = -1; v1x = -1; v1y = -1;
				break;
			case NORTH_WEST:
				v2x = 0; v2y = -1; v1x = -1; v1y = 0;
				break;
			case WEST:
				v2x = -1; v2y = 1; v1x = -1; v1y = -1;
				break;
			case SOUTH_WEST:
				v2x = -1; v2y = 0; v1x = 0; v1y = 1;
				break;
			default:
				v2x = 0; v2y = 0; v1x = 0; v1y = 0;
				break;
			
			}
		}
		//System.out.println("v1 = " + v1x + ", " + v1y + "; v2 = " + v2x + ", " +v2y);
		//det = v1x*v2y - v1y-v2x;
		
		colOffset = 0;
		findNewCol();
	}
	

	/*
	 *  find segment to lay mines on...
	 */
	public void findNewCol() {
		if(soldierID == -1) return;
		int col = 0, a = 0, b = 0, height = 0, backward = 0, width = 0;
//		System.out.println("centerType = " + centerType);
		try {
		switch(centerType) { // find parameters for given centerType
		case HQ:
			width = (hqMFLeft + hqMFRight + 1);
			col = (soldierID+colOffset) % width;
			height = hqMFForward + hqMFBackward + 1; backward = hqMFBackward;
			break;
		case ARTILLERY:
			width = (artMFLeft + artMFRight + 1);
			col = (soldierID+colOffset) % width;
			height = artMFForward + artMFBackward + 1; backward = artMFBackward;
			break;
		case SOLDIER:
			colStart = currentLocation;
			colHeight = 0;
			curHeight = 0;
			//colEnd = currentLocation;
			return;
		default:
			width = (encMFLeft + encMFRight + 1);
			col = (soldierID+colOffset) % width;
			height = encMFForward + encMFBackward + 1; backward = encMFBackward;
		}
		} catch (Exception e) {
			//System.out.println("" + centerType);
			e.printStackTrace();
		}
		
		if(colOffset >= width) {
			mineLocs[center.x][center.y] = DONE_MINE_TARGET;
		}

		// given parameters, find place in lattice
		//System.out.print(col + " -> " );
		col = (col % 2 == 0) ? col/2 : -(col+1)/2; // 0 1 2 3 4 5 6 ==> 0 -1 1 -2 2 -3 3 etc hopefully
//		System.out.print("soldierID = " + soldierID + ", width = " + width + ", col = " + col + ", height = " + height + " | ");
		if(col%2 == 0) {
			a = col/2;
			b = -a-backward+1;
			height++;
		} else {
			a = (col-1)/2;
			b = -a-backward;
		}

//		System.out.print("a = " + a + ", b = " + b + " | ");
		
		colStart = new MapLocation(Ox+a*v1x+b*v2x, Oy+a*v1y+b*v2y);
		colHeight = height;
		curHeight = 0;
		//colEnd = new MapLocation(colStart.x + height*(v1x+v2x), colStart.y + height*(v1y+v2y));
//		if(strategy == strategy.NUCLEAR){ 
//		//System.out.println("col = " + col + ". NEW colStart = " + colStart + ", height = " + colHeight);
//			System.out.println("col = " + col + ": a = " + a + ", b = " + b + " | ");
//		}
		nextX = colStart.x; 
		nextY = colStart.y;
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
		try {
//			System.out.println("received mine message");
		if(mineLocs[x][y] != CENTER_MINE) {
			mineLocs[x][y] = CENTER_MINE;
			if(UPGRADES_RESEARCHED[Upgrade.PICKAXE.ordinal()]) {
				mineLocInsert(x-1,y,HAS_ALLIED_MINE);
				mineLocInsert(x+1,y,HAS_ALLIED_MINE);
				mineLocInsert(x,y-1,HAS_ALLIED_MINE);
				mineLocInsert(x,y+1,HAS_ALLIED_MINE);
			}
		}
		} catch (Exception e) {
			System.out.println("x = " + x + ", y = " + y);
			e.printStackTrace();
		}
	}
	
	public void mineLocInsert(int x, int y, int c) {
		if(x >= 0 && x < MAP_WIDTH && y >= 0 && y < MAP_HEIGHT && mineLocs[x][y] == 0) {
			mineLocs[x][y] = c;
		}
	}

	@Override
	public Task getTask() {
		return task;
	}

}
