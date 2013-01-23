package team059.soldiers;

import battlecode.common.GameActionException;
import static team059.utils.Utils.*;

public class MineManager extends TaskGiver {
	MineTask task;
	SoldierBehavior2 sb;
	
	public MineManager(SoldierBehavior2 sb) {
		this.sb = sb;
		 task = new MineTask(sb);
	}
	
	
	@Override
	public void compute() throws GameActionException {
		 task = new MineTask(sb);
		
	}

	@Override
	public Task getTask() {
		return task;
	}

}
