package team059.hq;

import battlecode.common.GameActionException;

public class BuildSoldier implements HQAction {

	int number;
	
	public BuildSoldier(int num) {
		number = num;
	}
	
	@Override
	public boolean execute(HQBehavior hq) throws GameActionException {
		if(hq.buildSoldier()) {
			number--;
		}
		return number == 0;
	}
	
	@Override
	public String toString() {
		return "Building " + number + " soldiers";
	}
}
