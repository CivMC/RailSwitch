package sh.okx.railswitch.switches;

import com.google.common.base.Strings;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;

import org.bukkit.entity.Player;

import sh.okx.railswitch.settings.SettingsManager;

/**
 * Logic for the traditional, simple destination matching
 */
public class DestinationLogic extends SwitchLogic {
	public static final String NORMAL_V1 = "[destination]";
	public static final String NORMAL_V2 = "[dest]";
	public static final String INVERTED_V1 = "[!destination]";
	public static final String INVERTED_V2 = "[!dest]";
	
	public static boolean isNormal(String destLine) {
		return destLine.equalsIgnoreCase(NORMAL_V1) || destLine.equalsIgnoreCase(NORMAL_V2);
	}
	public static boolean isInverted(String destLine) {
		return destLine.equalsIgnoreCase(INVERTED_V1) || destLine.equalsIgnoreCase(INVERTED_V2);
	}

	public final String[] switchDestinations;
	public final boolean inverted;

	public DestinationLogic(String[] lines) throws Exception {
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
			for (String playerDestination : StringUtils.split(setDest, " ")) {
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
