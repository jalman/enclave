package team059.hq;

import battlecode.common.MapLocation;

/**
 * Coordinates early-game expanding.
 * @author vlad
 */
public class ExpandSystem {
	public MapLocation[][] encampments = new MapLocation[3][];
	public boolean[][] taken;
	
	int suppliers = 0;
	int generators = 0;
	
	public ExpandSystem() {
		
	}
	
}
