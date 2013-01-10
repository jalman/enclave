package team059;

import team059.utils.MessageHandler;
import team059.utils.MessageType;
import team059.utils.MessagingSystem;
import team059.utils.Utils;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;

public abstract class RobotBehavior {
	public final RobotController rc;
	public final Team myTeam;
	public final Team enemyTeam;
	public final MapLocation myBase, enemyBase;
	public final int width, height;
	
	protected MessagingSystem messagingSystem;
	protected MessageHandler[] messageHandlers;
	protected Utils utils;
	
	public RobotBehavior(RobotController rc) {
		this.rc = rc;
		myTeam = rc.getTeam();
		enemyTeam = (myTeam == Team.A) ? Team.B : Team.A;
		myBase = rc.senseHQLocation();
		enemyBase = rc.senseEnemyHQLocation();
		width = rc.getMapWidth();
		height = rc.getMapHeight();
		
		messagingSystem = new MessagingSystem(rc);
		messageHandlers = new MessageHandler[MessageType.values().length];
		messageHandlers[MessageType.ATTACK_LOCATION.ordinal()] = getAttackHandler();
		
		utils = new Utils(rc);
	}
	
	public boolean isEnemyMine(Team team) {
		return !(team == myTeam || team == null);
	}

	public boolean isEnemyMine(MapLocation loc) {
		return isEnemyMine(rc.senseMine(loc));
	}

	public int[][] adj_tile_offsets = {
		{ -1, -1 },
		{ -1,  0 },
		{ -1,  1 }, 
		{  0, -1 },
		{  0,  1 },
		{  1, -1 },
		{  1,  0 },
		{  1,  1 }
	};
	
	protected int danger(MapLocation loc) {return 0;}
	
	/**
	 * Called every round.
	 */
	public abstract void run();
	
	private static class DefaultMessageHandler implements MessageHandler {
		@Override
		public void handleMessage(int[] message) {}		
	}
	
	/**
	 * Override in order to respond to this type of message.
	 * @return The default message handler (does nothing).
	 */
	protected MessageHandler getAttackHandler() {return new DefaultMessageHandler();}
}
