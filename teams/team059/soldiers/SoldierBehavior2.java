package team059.soldiers;

import team059.RobotBehavior;
import team059.utils.Utils;

public class SoldierBehavior2 extends RobotBehavior {

	@Override
	public void initTasks() {
		pendingTasks.insert(new MineTask(this, Utils.ALLY_HQ));
		pendingTasks.insert(new ExpandTask(this));
	}
	
	@Override
	public void chooseTasks() {
		
	}
}
