package team059.utils;

import battlecode.common.GameActionException;

public class ClothingManager extends Utils {

	public ClothingManager() {
		super();
	}
	
	void getDressed() {
		try {
			RC.wearHat();
		} catch (GameActionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
