package team059.movement;

import team059.*;
import battlecode.common.*;
import static team059.utils.Utils.*;

public class DiggingBugMoveFunLong extends NavAlg {
        final static int[][] d = new int[8][2];
        public static final Direction[] directions = Direction.values();
        static {
                for(int i=0; i<8; i++) { 
                        d[i][0] = directions[i].dx;
                        d[i][1] = directions[i].dy;
                }
        }
        
        /** -1 means not tracing, 0 means tracing wall on left, 1 means on right. */
        int tracing = -1;
        /** direction of the wall we're currently hugging. */
        int wallDir = -1;
        /** number of turns that the bug has been tracing for. */
        int turnsTraced = 0;
        /** distance to destination we started tracing at. 
         * Leave trace mode when current distance to destination is below this value. */
        double traceDistance = -1;
        /** the default direction to trace. Changes every time we trace too far. */
        int defaultTraceDirection = 0;
        /** trace threshold to reset to every time we get a new destination. */
        static final int INITIAL_TRACE_THRESHOLD = 10;
        /** number of turns to trace before resetting. */
        int traceThreshold = -1;
        /** if we've hit the edge of the map by tracing in the other direction, 
         * then there's no use switching directions. */
        boolean hitEdgeInOtherTraceDirection = false;
        boolean digging = false;
        
        int bugTurnsBlocked = 0;
        
        int tx = -1;
        int ty = -1;
        int expectedsx = -1;
        int expectedsy = -1;
        
        // Map edge variables - these need to be updated from the outside
        public int edgeXMin, edgeXMax, edgeYMin, edgeYMax;
        
        
        public DiggingBugMoveFunLong() {
            edgeXMin = 0;
            edgeXMax = MAP_HEIGHT;
            edgeYMin = 0;
            edgeYMax = MAP_WIDTH;
            
            curLoc = RC.getLocation();
            
            reset();
        }
        
        public void recompute(MapLocation loc) {
        	if(loc.x != tx || loc.y != ty) {
        		setTarget(loc.x, loc.y);
        	}
        }
        
        public void recompute() {
        	reset();
        }
        
        public void setTarget(int tx, int ty) {
                this.tx = tx;
                this.ty = ty;
                reset();
        }
        
        public void reset() {
                tracing = -1;
                defaultTraceDirection = Clock.getRoundNum()/200%2; //(int)(Util.randDouble()+0.5);
                traceThreshold = INITIAL_TRACE_THRESHOLD;
                hitEdgeInOtherTraceDirection = false;
        }
        
        public boolean isTracing() {
                return tracing!=-1;
        }
        
        public Direction getNextDir() {
        	curLoc = RC.getLocation();
        	int hx = curLoc.x, hy = curLoc.y;

        	if(digging && turnsTraced > INITIAL_TRACE_THRESHOLD/2) {
	            digging = false;
        	}
            boolean[] movable = findMovable();
            
            int[] toMove = computeMove(curLoc.x, curLoc.y, movable);
            if(toMove==null) return Direction.NONE;
            Direction ret = directions[getDirTowards(toMove[0], toMove[1])];
            if(!canMoveNoMine(ret)) bugTurnsBlocked++;
            else bugTurnsBlocked=0;
            return ret;
        }
        
        private boolean[] findMovable() {
        	Direction dir;
        	boolean movable[] = new boolean[8];
        	if(digging) {
	            for(int i=0; i<8; i++) {
	            	dir = directions[i];
	                if(bugTurnsBlocked < 2) {
	                        movable[i] = (curLoc.add(dir).x >= MAP_HEIGHT || curLoc.add(dir).x < 0 || 
	                        		curLoc.add(dir).y >= MAP_WIDTH || curLoc.add(dir).y < 0) ? false : canMoveYesMine(dir);
	                } else {
	                        movable[i] = canMoveYesMine(dir);
	                }
	            }
        	} else {
	            for(int i=0; i<8; i++) {
	            	dir = directions[i];
	                if(bugTurnsBlocked < 2) {
	                        movable[i] = (curLoc.add(dir).x >= MAP_HEIGHT || curLoc.add(dir).x < 0 || 
	                        		curLoc.add(dir).y >= MAP_WIDTH || curLoc.add(dir).y < 0) ? false : canMoveNoMine(dir);
	                } else {
	                        movable[i] = canMoveNoMine(dir);
	                }
	            }
        	}
            return movable;
        }

    	private boolean canMoveNoMine(Direction d) {
    		if(d==null) return true;
    		return ( RC.canMove(d) && !isEnemyMine(curLoc.add(d)) );
    	}
    	
    	private boolean canMoveYesMine(Direction d) {
    		if(d==null) return true;
    		return RC.canMove(d);
    	}
    	
        /** Returns a (dx, dy) indicating which way to move. 
         * <br/>
         * <br/>May return null for various reasons:
         * <br/> -already at destination
         * <br/> -no directions to move
         */
        public int[] computeMove(int sx, int sy, boolean[] movableTerrain) {
                if(sx==tx && sy==ty) 
                        return null;
                if(Math.abs(sx-tx)<=1 && Math.abs(sy-ty)<=1) {
                        return new int[] {tx-sx, ty-sy};
                }
                
                double dist = (sx-tx)*(sx-tx)+(sy-ty)*(sy-ty);
                if(tracing!=-1) {
                        turnsTraced++;
                        if(dist<traceDistance) {
                                tracing = -1;
                                hitEdgeInOtherTraceDirection = false;
                        } else if(turnsTraced>=traceThreshold) {
                        	if(traceThreshold >= 2*INITIAL_TRACE_THRESHOLD) {
                        		//System.out.println("Digging now! - " + traceThreshold);
                        		digging = true;
                        		tracing = -1;
                        		traceThreshold = INITIAL_TRACE_THRESHOLD;
                        		return computeMove(sx, sy, findMovable());
                        	}
                        	else {
	                            tracing = -1;
	                            traceThreshold *= 2;
                        	}
                        	//System.out.println("turnsTraced >= traceThreshold! New threshold: " + traceThreshold);
                            defaultTraceDirection = 1-defaultTraceDirection;
                            hitEdgeInOtherTraceDirection = false;
                        } else if(!(sx==expectedsx && sy==expectedsy)) {
                                int i = getDirTowards(expectedsx-sx, expectedsy-sy);
                                if(movableTerrain[i])
                                        return d[i];
                                else
                                        wallDir = i;
                        } else if(movableTerrain[wallDir]) {
                                // Tracing around phantom wall 
                                //   (could happen if a wall was actually a moving unit)
                                tracing = -1;
                                hitEdgeInOtherTraceDirection = false;
                        } else if(!hitEdgeInOtherTraceDirection) {
                                int x = sx + d[wallDir][0];
                                int y = sy + d[wallDir][1];
                                if(x<=edgeXMin || x>=edgeXMax || y<=edgeYMin || y>=edgeYMax) {
                                        tracing = 1 - tracing;
                                        defaultTraceDirection = 1 - defaultTraceDirection;
                                        hitEdgeInOtherTraceDirection = true;
                                }
                        }
                }
                if(tracing==-1) {
                        int dir = getDirTowards(tx-sx, ty-sy);
                        if(movableTerrain[dir]) return d[dir];
                        tracing = defaultTraceDirection;
                        traceDistance = dist;
                        turnsTraced = 0;
                        wallDir = dir;
                } 
                if(tracing!=-1) {
                        for(int ti=1; ti<8; ti++) {
                                int dir = ((1-tracing*2)*ti + wallDir + 8) % 8;
                                if(movableTerrain[dir]) {
                                        wallDir = (dir+6+5*tracing)/2%4*2; //magic formula
                                        expectedsx = sx + d[dir][0];
                                        expectedsy = sy + d[dir][1];
                                        return d[dir];
                                }
                        }
                }
                return null;
        }
        
        /** Returns the direction that is equivalent to the given dx, dy value, 
         * or as close to it as possible.
         */
        private static int getDirTowards(int dx, int dy) {
                if(dx==0) {
                        if(dy>0) return 4;
                        else return 0;
                }
                double slope = ((double)dy)/dx;
                if(dx>0) {
                        if(slope>2.414) return 4;
                        else if(slope>0.414) return 3;
                        else if(slope>-0.414) return 2;
                        else if(slope>-2.414) return 1;
                        else return 0;
                } else {
                        if(slope>2.414) return 0;
                        else if(slope>0.414) return 7;
                        else if(slope>-0.414) return 6;
                        else if(slope>-2.414) return 5;
                        else return 4;
                }
        }
}
