package sh.okx.railswitch.switches;

import java.util.Arrays;

import com.google.re2j.Pattern;
import com.google.re2j.Matcher;
import org.bukkit.entity.Player;

import sh.okx.railswitch.settings.SettingsManager;

/**
 * Logic for destination matching via regular expressions
 */
public class DestExLogic extends SwitchLogic {
	public static final Pattern DESTEX = Pattern.compile("\\[(!)?destex(?:;(\\w*))?\\]", Pattern.CASE_INSENSITIVE);

	public final Pattern pattern;
	public final boolean inverted;
	public final boolean multi_match;

	public DestExLogic(String[] lines, Matcher match) throws Exception {
		super();
		pattern = DestExUtils.compile_pattern(Arrays.copyOfRange(lines, 1, lines.length), match.group(2));
		this.multi_match = (pattern.flags() & Pattern.MULTILINE) > 0;

		//If there's an exclamation mark before destex, it's inverted
		inverted = match.group(1) != null && match.group(1).length() > 0;

	}

	private boolean decide_internal(Player player) {
		String dest_string = SettingsManager.getDestination(player);
		if (multi_match) {
			//If the multi-match flag is specified, match against all dests at once
			Matcher matcher = pattern.matcher(dest_string);
			return matcher.matches();
		} else {
			//Otherwise, run the regex against each dest individually
			for (String dest : dest_string.split(" ")) {
				Matcher matcher = pattern.matcher(dest);
				if (matcher.matches()) return true;
			}
			return false;
		}
	}
	@Override public boolean decide(Player player) {
		return decide_internal(player) ^ inverted;
	}
}
