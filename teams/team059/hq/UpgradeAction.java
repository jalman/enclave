package team059.hq;

import static preSprintBot.utils.Utils.RC;
import static battlecode.common.Upgrade.*;
import battlecode.common.GameActionException;
import battlecode.common.Upgrade;

/**
 * Specifies opening build order.
 * @author vlad
 */
public enum UpgradeAction implements HQAction {	
	UPGRADE_FUSION(FUSION), UPGRADE_DEFUSION(DEFUSION), UPGRADE_PICKAXE(PICKAXE), UPGRADE_VISION(VISION), UPGRADE_NUKE(NUKE);
	
	public final Upgrade upgrade;
	
	private UpgradeAction(Upgrade upgrade) {
		this.upgrade = upgrade;
	}
	
	@Override
	public boolean execute(HQBehavior hq) throws GameActionException {
		if(RC.hasUpgrade(upgrade)) {
			return true;
		} else {
			hq.researchUpgrade(upgrade);
			return false;
		}
	}
}
