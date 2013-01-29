package team059;

import static battlecode.common.GameConstants.*;
import battlecode.common.Clock;

public class Encampments {
	public static int[][] value = new int[MAP_MAX_WIDTH][MAP_MAX_HEIGHT];
	
	public static void claim(int x, int y, int appeal) {
		value[x][y] = Math.max(value[x][y], appeal + Clock.getRoundNum());
	}
}
