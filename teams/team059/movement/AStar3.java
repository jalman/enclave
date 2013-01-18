package team059.movement;

import java.util.Arrays;

import team059.RobotBehavior;
import team059.utils.*;
import battlecode.common.*;
import static team059.utils.Utils.*;

public class AStar3 extends NavAlg {
	
	private MapLocation[] path; // reversed path!!!
	private int pathlength; //, pathpos;
	
	public final int MINE_MOVE_COST = 13;
	public final int NORMAL_MOVE_COST = 1;
	
	private int MIN_X, MIN_Y, MAX_X, MAX_Y;
	
	private int bc, total_bc, loop_bc;
	
	public AStar3() {
		this.path = new MapLocation[MAP_WIDTH * MAP_HEIGHT];
		this.pathlength = 0;
	}
	
	public void recompute() {
		this.curLoc = RC.getLocation();
		this.pathlength = 0;
		if(finish == null) return;
		this.compute_shortest(curLoc, finish);
	}
	
	public void recompute(MapLocation finish) {
		this.finish = finish;
		this.recompute();
	}

	private boolean compute_shortest(MapLocation start, MapLocation finish) {
		// Compute A* on the rectangle with min_x = min(sx, tx) - 2, max_x = max(sx, tx) + 2, etc.
		try {
			int sx = start.x, sy = start.y;
			int tx = finish.x, ty = finish.y;
			MIN_X = sx < tx ? sx-2 : tx-2;
			MIN_X = MIN_X < 0 ? 0 : MIN_X;
			MAX_X = sx > tx ? sx+2 : tx+2;
			MAX_X = MAX_X > MAP_WIDTH ? MAP_WIDTH : MAX_X;
			

			MIN_Y = sy < ty ? sy-2 : ty-2;
			MIN_Y = MIN_Y < 0 ? 0 : MIN_Y;
			MAX_Y = sy > ty ? sy+2 : ty+2;
			MAX_Y = MAX_Y > MAP_HEIGHT ? MAP_HEIGHT : MAX_Y;
			
			boolean[][] checked = new boolean[MAP_WIDTH][MAP_HEIGHT];
			checked[sx][sy] = true;
			
			MapLocation[][] previous = new MapLocation[MAP_WIDTH][MAP_HEIGHT];
			previous[sx][sy] = null;
	
			//the cost to get to a location
			int[][] cost_to = new int[MAP_WIDTH][MAP_HEIGHT];
			OnePassQueue<MapLocation> queue = new OnePassQueue<MapLocation>(1000, 400);
			
			MapLocation current, neighbor;
			//MapLocation current, neighbor;
			
			cost_to[sx][sy] = 0;// Cost from start along best known path.
			// Estimated total cost from start to goal through y.
			queue.insert(naiveDistance(start, finish), start);
			//nodesInHeap[start.x][start.y] = score.insert(heuristic_cost(start, finish), start); 
				// key is really 0 + heuristic_cost(start,goal)

			current = queue.deleteMin();

			for(int i = 0; i < 8; i++) {
				neighbor = new MapLocation(current.x + DX[i], current.y + DY[i]);
				try {
					if(neighbor.x < MIN_X || neighbor.y < MIN_Y || 
							neighbor.x >= MAX_X || neighbor.y >= MAX_Y || checked[neighbor.x][neighbor.y] || 
							(RC.canSenseSquare(neighbor) && RC.senseObjectAtLocation(neighbor) != null)) {
						continue;
					}
				} catch(GameActionException e) {
					System.out.println(RC.getLocation());
					System.out.println(neighbor);
					e.printStackTrace();
				}
				int cost_to_neighbor = cost_to[current.x][current.y] + neighbor_move_cost(neighbor);
				
				queue.insert(cost_to_neighbor + naiveDistance(neighbor, finish), neighbor);
				cost_to[neighbor.x][neighbor.y] = cost_to_neighbor;
				checked[neighbor.x][neighbor.y] = true;
				
				previous[neighbor.x][neighbor.y] = current;
			}
			
			while(true) {
				/* 
				if(queue.size < 5)
				System.out.println(queue);
				
				if(queue.size == 0) {
					System.out.println(finish);
					for(int y = 0; y < MAP_HEIGHT; y++) {
						for(int x = 0; x < MAP_WIDTH; x++) {
							System.out.print(checked[x][y] ? 'X' : ' ');
						}
						System.out.println();
					}
					
					throw new Exception("A* queue empty! No path to target.");
				}
				*/
				if(queue.size == 0) {
					return false;
				}
				current = queue.deleteMin();
				if(current.equals(finish)) {
					//System.out.println("Bytecodes used by A* pre-reconstruction = " + total_bc);
					reconstruct_path(previous, finish);
					return true;
				}
				
				//checked[current.x][current.y] = true;
				//System.out.println("Bytecodes used by unchecked/checked node manip = " + (bc-Clock.getBytecodesLeft()));
				
				for(int i = 0; i < 8; i++) {
					neighbor = new MapLocation(current.x + DX[i], current.y + DY[i]);
					if(neighbor.x < MIN_X || neighbor.y < MIN_Y || 
							neighbor.x >= MAX_X || neighbor.y >= MAX_Y || checked[neighbor.x][neighbor.y] ) {
						continue;
					}
					int cost_to_neighbor = cost_to[current.x][current.y] + neighbor_move_cost(neighbor);
					
					queue.insert(cost_to_neighbor + naiveDistance(neighbor, finish), neighbor);
					cost_to[neighbor.x][neighbor.y] = cost_to_neighbor;
					checked[neighbor.x][neighbor.y] = true;
					
					previous[neighbor.x][neighbor.y] = current;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		path = null;
		return false; 
	}

	/*
	 function A*(start,goal)
	     checked := the empty set    // The set of nodes already evaluated.
	     unchecked := {start}    // The set of tentative nodes to be evaluated, initially containing the start node
	     previous := the empty map    // The map of navigated nodes.
	 
	     pre_score[start] := 0    // Cost from start along best known path.
	     // Estimated total cost from start to goal through y.
	     score[start] := pre_score[start] + heuristic_cost_estimate(start, goal)
	 
	     while unchecked is not empty
	         current := the node in unchecked having the lowest score[] value
	         if current = goal
	             return reconstruct_path(previous, goal)
	 
	         remove current from unchecked
	         add current to checked
	         for each neighbor in neighbor_nodes(current)
	             if neighbor in checked
	                 continue
	             tentative_pre_score := pre_score[current] + dist_between(current,neighbor)
	 
	             if neighbor not in unchecked or tentative_pre_score <= pre_score[neighbor] 
	                 previous[neighbor] := current
	                 pre_score[neighbor] := tentative_pre_score
	                 score[neighbor] := pre_score[neighbor] + heuristic_cost_estimate(neighbor, goal)
	                 if neighbor not in unchecked
	                     add neighbor to unchecked
	 
	     return failure
	 */
		
	
	/*private int estimated_distance(MapLocation loc, MapLocation finish) {
		return naiveDistance(loc, finish);
	}*/
	
	/**
	 * The heuristic cost of going through a map location.
	 * Currently only takes into account whether there is a mine.
	 * @param loc The map location.
	 * @return {@link #MINE_MOVE_COST} if there is a mine, and {@link #NORMAL_MOVE_COST} otherwise.
	 */
	private int neighbor_move_cost(MapLocation loc) { 
		try{
			if(Utils.isEnemyMine(loc)) {
				return MINE_MOVE_COST;
			} /*else if (rc.canSenseSquare(dest)) { // || rc.senseObjectAtLocation(dest) != null) {
				int distance = ut.naiveDistance(rc.getLocation(), dest);
				if(distance == 1) return 100000;
				return (MAP_WIDTH + MAP_HEIGHT)/( 2 * (distance - 1) ) ;
			} */ else {
				return NORMAL_MOVE_COST;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 100000;
	}
	
	
	private void reconstruct_path(MapLocation[][] previous, MapLocation finish_node) {
		try{ 
		MapLocation current = finish_node;
		while(previous[current.x][current.y] != null) { // builds path in reverse!
			path[pathlength++] = current;
			current = previous[current.x][current.y];
		}
		//pathpos = pathlength;
		} catch (Exception e) {
			System.out.println("Path length = " + Integer.toString(pathlength) + ", path = " + Arrays.toString(Arrays.copyOfRange(path, 0, pathlength)));
			e.printStackTrace();
		}
	}
	/*
	    if(came_from[current_node] in set)
	        p := reconstruct_path(came_from, came_from[current_node])
	        return (p + current_node)
	    else
	        return current_node
	}*/
	
	public Direction getNextDir() {
		curLoc = RC.getLocation();
		//pathlength--; // path is reversed!
		if(--pathlength < 0) {
			pathlength = 0;
			return Direction.NONE;
		}
		Direction d = curLoc.directionTo(path[pathlength]);
		if(!RC.canMove(d)) {
			if(curLoc.distanceSquaredTo(finish) <= 2) {
				return Direction.NONE;
			}
			recompute();
			return getNextDir();
		}
		return d;
	}

}
