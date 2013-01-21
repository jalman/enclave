package team059.movement;

//import java.util.Arrays;

import battlecode.common.*;
import static team059.utils.Utils.*;

public class PartialAStar3 extends NavAlg {

	private final Direction[] DIRS;

	public final int MINE_MOVE_COST = 5;
	public final int NORMAL_MOVE_COST = 1;

	//private int MIN_X, MIN_Y, MAX_X, MAX_Y;

	private int bc, total_bc, loop_bc;
	private MapLocation /*temp_dest,*/ next;

	public PartialAStar3() {
		this.DIRS = Direction.values();
	}

	public void recompute() {
		this.curLoc = RC.getLocation();
		if(finish == null) return;
		compute_shortest(curLoc, finish);
	}

	public void recompute(MapLocation finish) {
		this.finish = finish;
		//this.recompute();
	}

	public boolean compute_shortest(MapLocation start, MapLocation finish) {
		System.out.print(Clock.getBytecodeNum() + " - Compute_shortest to - " + finish + " ");
		int cost_to_neighbor;
		// Compute A* on the rectangle with min_x = min(sx, tx) - 2, max_x = max(sx, tx) + 2, etc.
		try {
			int heuristic;
			int sx = start.x, sy = start.y;
			//int tx = finish.x, ty = finish.y;
			int nx = 0, ny = 0;

			boolean[][] checked = new boolean[MAP_WIDTH][MAP_HEIGHT];
			checked[sx][sy] = true;

			MapLocation[][] previous = new MapLocation[MAP_WIDTH][MAP_HEIGHT];
			previous[sx][sy] = null;

			//the cost to get to a location
			int[][] cost_to = new int[MAP_WIDTH][MAP_HEIGHT];
			OnePassQueue<MapLocation> queue = new OnePassQueue<MapLocation>(1000, 400);
			queue.insert(naiveDistance(start, finish), start);

			MapLocation current, neighbor;
			//MapLocation current, neighbor;

			cost_to[sx][sy] = 0;// Cost from start along best known path.		
			bc = Clock.getBytecodeNum();	

			//avoid nearby allies			
			for(int i=0; i<8; i++) {
				Direction dir = DIRS[i];
				neighbor = start.add(dir); // actually saves 2 bytecodes/loop here to not use nx = neighbor.x etc
				if(neighbor.x < 0 || neighbor.x >= MAP_WIDTH || neighbor.y < 0 || neighbor.y >= MAP_HEIGHT ) { 
					continue;
				} else if(RC.canMove(dir)) {
					/*
					cost_to_neighbor = cost_to[start.x][start.y] + neighbor_move_cost(neighbor);

					queue.insert(cost_to_neighbor + naiveDistance(neighbor, finish), neighbor);
					cost_to[neighbor.x][neighbor.y] = cost_to_neighbor;

					System.out.print(neighbor + ": " + cost_to_neighbor + ", ");

					previous[neighbor.x][neighbor.y] = start;
					 */
				} else {
					checked[neighbor.x][neighbor.y] = true;
				}
			}

			MapLocation closest = null;
			int min_distance = Integer.MAX_VALUE;

			while(min_distance > 0 && queue.size > 0) {
				total_bc = Clock.getBytecodeNum();
				if(total_bc > 8000) {
					break;
				}
				
				current = queue.deleteMin();
				//System.out.print(current + " ");
				
				//System.out.println("Bytecodes used by unchecked/checked node manip = " + (bc-Clock.getBytecodesLeft()));

				for(int i = 0; i < 8; i++) { // ~170 bytecodes -> ~159 bytecodes!!

					bc = Clock.getBytecodeNum();
					//neighbor = new MapLocation(current.x + DX[i], current.y + DY[i]); // 19 bytecodes
					neighbor = current.add(DIRS[i]); // 15 bytecodes
					nx = neighbor.x; // saves ~11 bytecodes/loop iteration overall!
					ny = neighbor.y;
					//neighbor = current.add(DX[i], DY[i]);
//					if(nx < 0 || nx >= MAP_WIDTH || ny < 0 || ny >= MAP_HEIGHT 
//							|| checked[nx][ny] ) { // 19 bytecodes.
//						// 25 bytecodes with neighbor.x etc. Replacing with senseTerrainTile() adds 1
//						continue;
//					}
					
					if(nx >= 0 && nx < MAP_WIDTH && ny >= 0 && ny < MAP_WIDTH && !checked[nx][ny]) {
					
					cost_to_neighbor = cost_to[current.x][current.y] + neighbor_move_cost(neighbor); // 42 or 43
					heuristic = naiveDistance(neighbor, finish); // 31-33 bytecodes
					queue.insert(cost_to_neighbor + heuristic, neighbor); // 31 bytecodes
//					cost_to[neighbor.x][neighbor.y] = cost_to_neighbor;
//					checked[neighbor.x][neighbor.y] = true;
//					previous[neighbor.x][neighbor.y] = current; // three assignments: 27 bytecodes					
					cost_to[nx][ny] = cost_to_neighbor;
					checked[nx][ny] = true;
					previous[nx][ny] = current; // three assignments: 21 bytecodes

					if(heuristic < min_distance) {
						closest = neighbor;
						min_distance = heuristic;
					}
					
					}

					loop_bc = Clock.getBytecodeNum();

					System.out.println("@ bc used for naiveDistance for neighbor " + neighbor + " of " + current + " = " + (loop_bc-bc) );
					//System.out.println(Clock.getBytecodeNum());

					//System.out.print(neighbor + ": " + cost_to_neighbor + ", ");
				}

			}

			//System.out.println();

			if(closest != null) {
				//RC.setIndicatorString(2, closest.toString());
				reconstruct_path(previous, closest);
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		next = null;
		return false; 
	}

	/**
	 * The heuristic cost of going through a map location.
	 * Currently only takes into account whether there is a mine.
	 * @param loc The map location.
	 * @return {@link #MINE_MOVE_COST} if there is a mine, and {@link #NORMAL_MOVE_COST} otherwise.
	 */
	private int neighbor_move_cost(MapLocation loc) { 
		//try{
			//return Utils.isEnemyMine(loc) ? 13 : NORMAL_MOVE_COST; // does 1 bytecode worse sometimes????
			
			if(Utils.isEnemyMine(loc)) {
				return 13;
			} else {
				return NORMAL_MOVE_COST;
			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		//return 100000;
	}

	private void reconstruct_path(MapLocation[][] previous, MapLocation finish_node) {
		try {
			System.out.print(Clock.getBytecodeNum() + " - reconstruct_path - ");
			//temp_dest = finish_node;
			MapLocation current = finish_node, temp = null;
			while(current != null) {
				next = temp;
				temp = current;
				current = previous[current.x][current.y];
			}
			//pathpos = pathlength;
		} catch (Exception e) {
			//System.out.println("Path length = " + Integer.toString(pathlength) + ", path = " + Arrays.toString(Arrays.copyOfRange(path, 0, pathlength)));
			e.printStackTrace();
		}
	}

	public Direction getNextDir() {
		System.out.println(Clock.getBytecodeNum() +  " - getNextDir");
		if(next != null) {
			return curLoc.directionTo(next);
		} else {
			return Direction.NONE;
		}
	}

}
