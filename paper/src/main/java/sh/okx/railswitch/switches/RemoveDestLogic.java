package sh.okx.railswitch.switches;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.entity.Player;

import sh.okx.railswitch.settings.SettingsManager;

public class RemoveDestLogic extends SwitchLogic {
	public static final String INVOCATION = "[destrm]";

	public final String[] to_remove;

	public RemoveDestLogic(String[] lines) throws Exception {
		to_remove = Arrays.copyOfRange(lines, 1, lines.length);
	}

	@Override
	public boolean decide(Player player) {
		boolean did_something = false;
		String dest_string = SettingsManager.getDestination(player);

		DestIterator iter = new DestIterator(dest_string);
		while (iter.hasNext()) {
			String dest = iter.next();
			if (dest.isBlank()) continue;

			for (String remove_dest : to_remove) {
				if (dest.equals(remove_dest)) {
					iter.remove();
					did_something = true;
					break;
				}
			}
		}

		if (did_something) {
			SettingsManager.setDestination(player, iter.dest_string());
			return true;
		}
		return false;
	}
}
