package team059.movement;

import team059.RobotBehavior;
import team059.utils.*;
import battlecode.common.*;
import static team059.utils.Utils.*;

public class AStar1 extends NavAlg {
	
	private MapLocation[] path; // reversed path!!!
	private int pathlength; //, pathpos;
	
	public final int MINE_MOVE_COST = 13;
	public final int NORMAL_MOVE_COST = 1;
	
	private int bc, total_bc, loop_bc;
	
	public AStar1() {
		this.path = new MapLocation[70*70];
		this.pathlength = 0;
	}
	
	public void recompute() {
		this.curLoc = RC.getLocation();
		this.pathlength = 0;
		//this.pathpos = 0;
		this.compute_shortest(curLoc, finish);
	}
	
	public void recompute(MapLocation finish) {
		this.finish = finish;
		this.recompute();
	}
	

	@SuppressWarnings("unchecked")
	private boolean compute_shortest(MapLocation start, MapLocation finish) {		
		total_bc = 0;
		if(finish == null) return false;
		
		FastIterableLocSet checked = new FastIterableLocSet(); // nodes already checked
		FastIterableLocSet unchecked = new FastIterableLocSet(); // nodes not yet checked
		unchecked.add(start);
		
		MapLocation[][] previous = new MapLocation[MAP_WIDTH][MAP_HEIGHT];
		previous[start.x][start.y] = null;

		int tentative_pre_score;
		int[][] pre_score = new int[MAP_WIDTH][MAP_HEIGHT];
		FibonacciHeap.FibonacciNode<MapLocation>[][] nodesInHeap = new FibonacciHeap.FibonacciNode[MAP_WIDTH][MAP_HEIGHT];
		FibonacciHeap<MapLocation> score = new FibonacciHeap<MapLocation>();
		
		MapLocation current, neighbor;
		
		pre_score[start.x][start.y] = 0;// Cost from start along best known path.
		// Estimated total cost from start to goal through y.
		nodesInHeap[start.x][start.y] = score.insert(heuristic_cost(start, finish), start); 
			// key is really 0 + heuristic_cost(start,goal)

		while(!unchecked.isEmpty()) {
			loop_bc = bc = Clock.getBytecodesLeft();
			current = score.deleteMin();
			System.out.println(current);
			if(current.equals(finish)) {
				System.out.println("Bytecodes used by A* pre-reconstruction = " + Integer.toString(total_bc));
				reconstruct_path(previous, finish); 
				return true;
			}
			unchecked.remove(current);
			checked.add(current);
			//System.out.println("Bytecodes used by unchecked/checked node manip = " + Integer.toString(bc-Clock.getBytecodesLeft()));
			
			for(int idx = 0; idx < 8; ++idx) {
				//bc = Clock.getBytecodesLeft();
				neighbor = new MapLocation(current.x+Utils.DX[idx], current.y+Utils.DY[idx]);
				if(checked.contains(neighbor) || 
						neighbor.x >= MAP_WIDTH || neighbor.x < 0 || 
						neighbor.y >= MAP_HEIGHT || neighbor.y < 0) {
					continue;
				}
				tentative_pre_score = pre_score[current.x][current.y] + neighbor_move_cost(neighbor);
				//System.out.println("Bytecodes used by initial neighbor stuff = " + Integer.toString(bc-Clock.getBytecodesLeft()));
				
				
				if(!unchecked.contains(neighbor)) {
					previous[neighbor.x][neighbor.y] = current;
					bc = Clock.getBytecodesLeft();
					nodesInHeap[neighbor.x][neighbor.y] = 
						score.insert(tentative_pre_score + heuristic_cost(neighbor, finish), neighbor);
					System.out.println("Bytecodes used by fib heap insert [new-unchecked neighbor] = " + Integer.toString(bc-Clock.getBytecodesLeft()));
					pre_score[neighbor.x][neighbor.y] = tentative_pre_score;
					unchecked.add(neighbor);
				} /* else if(tentative_pre_score <= pre_score[neighbor.x][neighbor.y]) {
					previous[neighbor.x][neighbor.y] = current;
					bc = Clock.getBytecodesLeft();
					score.decreaseKey(nodesInHeap[neighbor.x][neighbor.y], tentative_pre_score + heuristic_cost(neighbor, finish));
					System.out.println("Bytecodes used by fib heap decreaseKey [new-unchecked neighbor] = " + Integer.toString(bc-Clock.getBytecodesLeft()));
					pre_score[neighbor.x][neighbor.y] = tentative_pre_score;
				} */ 
			}
			int c = (loop_bc - Clock.getBytecodesLeft());
			total_bc += ( (c > 0) ? c : 10000+c ) ; 
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
		
	
	private int heuristic_cost(MapLocation loc, MapLocation finish) {
		return naive_heuristic(loc, finish);
	}
	
	private int naive_heuristic(MapLocation loc, MapLocation finish) {
		return Math.max(Math.abs(loc.x-finish.x), Math.abs(loc.y-finish.y));
	}
	
	private int neighbor_move_cost(MapLocation dest) { 
		try{
			if(Utils.isEnemyMine(dest)) {
				return MINE_MOVE_COST;
			} else if (RC.canSenseSquare(dest) && RC.senseObjectAtLocation(dest) != null) {
				int distance = naiveDistance(RC.getLocation(), dest);
				if(distance == 1) return 100000;
				return (MAP_WIDTH + MAP_HEIGHT)/( 2 * (distance - 1) ) ;
			} else {
				return NORMAL_MOVE_COST;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 100000;
	}
	
	
	private void reconstruct_path(MapLocation[][] previous, MapLocation finish_node) {
		MapLocation current = finish_node;
		while(previous[current.x][current.y] != null) { // builds path in reverse!
			path[pathlength++] = current;
			current = previous[current.x][current.y];
		}
		//pathpos = pathlength;
		System.out.println("Path length = " + Integer.toString(pathlength));
	}
	/*
	    if(came_from[current_node] in set)
	        p := reconstruct_path(came_from, came_from[current_node])
	        return (p + current_node)
	    else
	        return current_node
	}*/
	
	public Direction getNextDir() {
		this.curLoc = RC.getLocation();
		pathlength--; // path is reversed!
		if(pathlength < 0) {
			pathlength = 0;
			return Direction.NONE;
		}
		Direction d = curLoc.directionTo(path[pathlength]);
		return d;
	}

}
