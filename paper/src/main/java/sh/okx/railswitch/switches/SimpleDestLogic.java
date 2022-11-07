package sh.okx.railswitch.switches;

import com.google.common.base.Strings;
import java.util.Arrays;

import org.bukkit.entity.Player;

import sh.okx.railswitch.settings.SettingsManager;

/**
 * Logic for the traditional, simple destination matching
 */
public class SimpleDestLogic extends SwitchLogic {
	public static final String NORMAL_V1 = "[destination]";
	public static final String NORMAL_V2 = "[dest]";
	public static final String INVERTED_V1 = "[!destination]";
	public static final String INVERTED_V2 = "[!dest]";
	
	public static boolean isNormal(String dest_line) {
		return dest_line.equalsIgnoreCase(NORMAL_V1) || dest_line.equalsIgnoreCase(NORMAL_V2);
	}
	public static boolean isInverted(String dest_line) {
		return dest_line.equalsIgnoreCase(INVERTED_V1) || dest_line.equalsIgnoreCase(INVERTED_V2);
	}

	public final String[] switchDestinations;
	public final boolean inverted;

	public SimpleDestLogic(String[] lines) throws Exception {
		super();
		inverted = isInverted(lines[0]);
		switchDestinations = Arrays.copyOfRange(lines, 1, lines.length);
	}

	public static final String WILDCARD = "*";

	@Override
	public boolean decide(Player player) {
		// Determine whether a player has a destination that matches one of the destinations
		// listed on the switch signs, or match if there's a wildcard.
		String setDest = SettingsManager.getDestination(player);

		boolean matched = false;
		if (!Strings.isNullOrEmpty(setDest)) {
			matcher:
			for (String playerDestination : DestIterator.iterate(setDest)) {
				if (Strings.isNullOrEmpty(playerDestination)) {
					continue;
				}
				if (playerDestination.equals(WILDCARD)) {
					matched = true;
					break;
				}
				for (String switchDestination : switchDestinations) {
					if (Strings.isNullOrEmpty(switchDestination)) {
						continue;
					}
					if (switchDestination.equals(WILDCARD)
							|| playerDestination.equalsIgnoreCase(switchDestination)) {
						matched = true;
						break matcher;
					}
				}
			}
		}

		return matched ^ inverted;
	}
}
