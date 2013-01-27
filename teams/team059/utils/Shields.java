package team059.utils;

import static battlecode.common.GameConstants.*;
import battlecode.common.MapLocation;

public class Shields {
	public static boolean[][] hasShield = new boolean[MAP_MAX_WIDTH][MAP_MAX_HEIGHT];
	public static ArraySet<MapLocation> shieldLocations = new ArraySet<MapLocation>(100);
	
	public static void insertShield(MapLocation shield) {
		if(!hasShield[shield.x][shield.y]) {
			shieldLocations.insert(shield);
			hasShield[shield.x][shield.y] = true;
		}
	}
}
