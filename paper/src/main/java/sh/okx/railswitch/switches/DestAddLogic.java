package sh.okx.railswitch.switches;

import java.util.Arrays;
import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;

import org.bukkit.entity.Player;

import sh.okx.railswitch.settings.SettingsManager;

public class DestAddLogic extends SwitchLogic {
	public static final String INVOCATION = "[destadd]";

	public final String[] toAdd;

	public DestAddLogic(String[] lines) throws Exception {
		toAdd = Arrays.copyOfRange(lines, 1, lines.length);
	}

	@Override
	public boolean decide(Player player) {
		boolean didSomething = false;
		String destString = SettingsManager.getDestination(player);

		for (String additionalDest : toAdd) {
			if (additionalDest.isBlank()) continue;

			boolean alreadyExists = false;
			for (String dest : StringUtils.split(destString, " ")) {
				if (dest.equals(additionalDest)) {
					alreadyExists = true;
					break;
				}
			}

			if (!alreadyExists) {
				destString += " " + additionalDest;
				didSomething = true;
			}
		}

		if (didSomething) {
			SettingsManager.setDestination(player, destString);
			return true;
		}
		return false;
	}
}
