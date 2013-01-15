package team059.hq;

import static team059.utils.Utils.RC;
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
		return number > 0;
	}
}
