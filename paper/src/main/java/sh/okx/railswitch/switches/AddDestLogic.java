package sh.okx.railswitch.switches;

import java.util.Arrays;
import java.util.ArrayList;

import org.bukkit.entity.Player;

import sh.okx.railswitch.settings.SettingsManager;

public class AddDestLogic extends SwitchLogic {
	public static final String INVOCATION = "[destadd]";

	public final String[] to_add;

	public AddDestLogic(String[] lines) throws Exception {
		to_add = Arrays.copyOfRange(lines, 1, lines.length);
	}

	@Override
	public boolean decide(Player player) {
		boolean did_something = false;
		ArrayList<String> dests = new ArrayList(Arrays.asList(SettingsManager.getDestination(player).split(" ")));

		for (String additional_dest : to_add) {
			if (additional_dest.trim().length() == 0) continue;

			boolean already_exists = false;
			for (String dest : dests) {
				if (dest.equals(additional_dest)) {
					already_exists = true;
					break;
				}
			}

			if (!already_exists) {
				dests.add(additional_dest);
				did_something = true;
			}
		}

		if (did_something) {
			SettingsManager.setDestination(player, String.join(" ", dests));
			return true;
		}
		return false;
	}
}
