package preSprintBot.soldiers;

import preSprintBot.soldiers.micro.Micro;
import battlecode.common.MapLocation;

/**
 * Makes decisions weighing priorities, map distances, and other information.
 * @author vlad
 */

public class PrioritySystem {

	private static int battleDistance = 5;
	
	public static int rate(int distance) {
		if(distance < battleDistance){
			return 1;
		}
		else{
			return 0;
		}
	}
}
