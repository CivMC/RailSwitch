package sh.okx.railswitch.switches;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import org.apache.commons.lang3.StringUtils;

import org.bukkit.entity.Player;

import sh.okx.railswitch.settings.SettingsManager;

public class DestRmLogic extends SwitchLogic {
	public static final String INVOCATION = "[destrm]";

	public final String[] toRemove;

	public DestRmLogic(String[] lines) throws Exception {
		toRemove = Arrays.copyOfRange(lines, 1, lines.length);
	}

	@Override
	public boolean decide(Player player) {
		boolean didSomething = false;
		ArrayList<String> dests = new ArrayList();
		String destString = SettingsManager.getDestination(player);
		Collections.addAll(dests, StringUtils.split(destString, " "));

		Iterator<String> iter = dests.iterator();
		while (iter.hasNext()) {
			String dest = iter.next();
			if (dest.isBlank()) continue;

			for (String removeDest : toRemove) {
				if (dest.equals(removeDest)) {
					iter.remove();
					didSomething = true;
					break;
				}
			}
		}

		if (didSomething) {
			SettingsManager.setDestination(player, String.join(" ", dests));
			return true;
		}
		return false;
	}
}
