package team059.hq;

import static team059.utils.Utils.RC;
import battlecode.common.GameActionException;
import battlecode.common.Upgrade;

/**
 * Specifies opening build order.
 * @author vlad
 */
public class UpgradeAction implements HQAction {
	
	public final Upgrade upgrade;
	private int todo;
	
	public UpgradeAction(Upgrade upgrade) {
		this.upgrade = upgrade;
		todo = upgrade.numRounds;
	}
	
	@Override
	public boolean execute(HQBehavior hq) throws GameActionException {
		RC.researchUpgrade(upgrade);
		return --todo == 0;
	}
	
	public String toString() {
		return "Upgrading " + upgrade;
	}
}
