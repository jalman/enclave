package team059.movement;

import team059.*;
import battlecode.common.*;
import static team059.utils.Utils.*;

public class DigBugMoveFunIterative extends NavAlg {
	
	private final DiggingBugMoveFun1 dbmf;
	
	private MapLocation tempDest;
	
	public DigBugMoveFunIterative() {
		this.dbmf = new DiggingBugMoveFun1();
		this.tempDest = null;
	}
	
    public void recompute(MapLocation loc) {
    	this.finish = loc;
    }
    
    public void recompute() {
    }
        
    public Direction getNextDir() {
    	curLoc = RC.getLocation();
    	
    	if(tempDest == null || curLoc.distanceSquaredTo(tempDest) <= 2) {
    		tempDest = curLoc.add(curLoc.directionTo(finish), 3);
    		dbmf.recompute(tempDest);
    		//RC.setIndicatorString(2, "Finish: " + finish + ", tempDest: " + tempDest);
    	}
        
    	return dbmf.getNextDir();
    }
}
