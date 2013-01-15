package team059.movement;

public enum NavType {
	
	ASTAR2(new AStar2()),
	BUG(new BugMoveFun()), STRAIGHT_DIG(new DigMove()), TANGENT_BUG, RANDOM, BEELINE, BUG_STRAIGHT_DIG(new BuggingDigMove());
	
	public final NavAlg navAlg;

	private NavType() {
		this.navAlg = null;
	}
	
	private NavType(NavAlg alg) {
		this.navAlg = alg;
	}
}
