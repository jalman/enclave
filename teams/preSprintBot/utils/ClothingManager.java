package preSprintBot.utils;

import battlecode.common.GameActionException;

public class ClothingManager extends Utils {

	public ClothingManager() {
		super();
	}
	
	void getDressed() throws GameActionException {
		RC.wearHat();
	}

}
