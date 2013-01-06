package team007.movement;

import team007.Action;
import team007.RobotPlayer;
import battlecode.common.*;
import team007.talking.Broadcast;
//
public class BugMove implements Action {
	
	RobotController rc;
	Broadcast broad;
	Direction go; //for moving
	boolean bugging; //for moving
	boolean hugLeft; //for moving
	boolean waiting; // for waiting
	MapLocation init; //for moving
	MapLocation dest; 
	MapLocation here;
	boolean backwardFlag = false;
	
	Direction want; //comment
	
	Direction last;
	
	int counter = 0;
	
    public BugMove(RobotController rc, Broadcast broad){
        this.rc = rc;
        this.broad = broad;
        this.bugging = false;
        this.init = rc.getLocation();
        this.go = Direction.NONE;
        this.dest = this.init;
        this.want = Direction.NONE;
        this.waiting = false;
        this.last = rc.getLocation().directionTo(rc.sensePowerCore().getLocation());
    }
    
    public MapLocation getTarget(){
    	return dest;
    }

    public boolean canMove(Direction dir)
    {
    	if(dir == Direction.NONE || dir == Direction.OMNI)
    		return false;
    	
    	if(rc.canMove(dir))
    		return true;
    	
    	if(rc.getType() == RobotType.ARCHON)
    	{
    		return tryYelling(dir);
    	}
    	
    	return false;
    }
    
    public boolean tryYelling(Direction dir)
    {
    	if(counter > 0)
    		return true;
    	
		try {
			GameObject infront = rc.senseObjectAtLocation(here.add(dir), RobotLevel.ON_GROUND);
			if(infront != null) {
			RobotInfo rInfo = rc.senseRobotInfo((Robot)infront);
				if (rInfo.team == rc.getTeam() && (rInfo.type == RobotType.SOLDIER || rInfo.type == RobotType.DISRUPTER) && rInfo.flux < 5.0 && rc.getFlux() > 5.0) {
					//rc.setIndicatorString(0, "kicking " + infront + " so he'll move");
					rc.transferFlux(here.add(dir), RobotLevel.ON_GROUND, 5.0);
					broad.move(here.add(go));
					counter = 5;
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
    }
    
	public void execute(){
		
		if(!canExecute()) return;
		
		if(waiting) {
			waiting = false;
			return;
		}
		
		if(counter > 0)
			counter--;
		
		//////System.out.println(go);
		if (!canMove(go))
			{
				
				go = findDir();
				//////System.out.println(go);
			}
		else if(rc.canMove(go))
			{
				//////System.out.println("YAY");
			try {
				if(!backwardFlag){
					if (rc.getDirection() == go) {
						rc.moveForward();
						last = go;
						go = Direction.NONE;
					} else
						rc.setDirection(go);
				} else {
					if (rc.getDirection().opposite() == go) {
						rc.moveBackward();
						last = go;
						go = Direction.NONE;
					} else
						rc.setDirection(go.opposite());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setBackward(boolean flag){
		backwardFlag = flag;
	}
	
	public void waitTurn(){
		waiting = true;
	}
	
	public boolean canExecute(){
		return !(rc.isMovementActive());
	}

	public void setTarget(MapLocation loc){
		if(loc.equals(dest))
			return;
		//////System.out.println(loc + " " + dest);
		////rc.setIndicatorString(2, "BUG TO " + loc);
		init = rc.getLocation();
		this.dest = loc;
		go = Direction.NONE;
		//bugging = false;
	}
	
	public Direction findDir()
	{
		here = rc.getLocation();
		if(!bugging || want == Direction.NONE)
			want = here.directionTo(dest);
		
		if (want == Direction.NONE || want == Direction.OMNI)
	        return want;
		
		if(bugging && here.distanceSquaredTo(dest) <= init.distanceSquaredTo(dest) /*&& canMove(want)*/)
			bugging = false;
		
		if(!bugging)
			return flock();
		else		
			return bugwall();
	}
	
	public Direction flock()
	{
		////rc.setIndicatorString(0, "NOTBUG "+dest.toString() + " " + Clock.getRoundNum());
		if(canMove(want))
			return want;
		
		if(here.add(want.rotateLeft()).distanceSquaredTo(dest) < here.add(want.rotateRight()).distanceSquaredTo(dest))
		{
			if(canMove(want.rotateLeft()))
				return want.rotateLeft();

			if(canMove(want.rotateRight()))
				return want.rotateRight();

		}
		else
		{
			if(canMove(want.rotateRight()))
				return want.rotateRight();

			if(canMove(want.rotateLeft()))
				return want.rotateLeft();
		}
		////rc.setIndicatorString(0, "NOTNOTBUG ");
		
		beginbug();

		return bugwall();
	}
	
	public void beginbug()
	{
		bugging = true;
		
		init = here;
		
		Direction l,r;
		l = want.rotateLeft().rotateLeft();
		r = want.rotateRight().rotateRight();
		
		if(here.add(l).distanceSquaredTo(dest)*(1 + 0.4*(canMove(l) ? 0 : 1)) < here.add(r).distanceSquaredTo(dest)*(1 + 0.4*(canMove(r) ? 0 : 1)))
			{
				hugLeft = true;
				last = last.rotateLeft().rotateLeft();
			}
		else
		{
			hugLeft = false;
			last = last.rotateRight().rotateRight();
		}
	}
	
	public Direction bugwall()
	{
		//bugging
		
//		if(want != last && canMove(want))
//			return want;
		
		Direction tmp = hugLeft ? last.rotateRight().rotateRight() : last.rotateLeft().rotateLeft();
		
		
		
		for(int i = 0; i < 8; i++)
		{
			////rc.setIndicatorString(0, tmp.toString() + " " + i + " " + hugLeft);
			if(tmp != last.opposite() && canMove(tmp))
				return tmp;
			
			tmp = hugLeft ? tmp.rotateLeft() : tmp.rotateRight();
		}

		
		return Direction.NONE;
	
	}
}
