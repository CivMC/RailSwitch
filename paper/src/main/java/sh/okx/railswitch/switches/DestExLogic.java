package sh.okx.railswitch.switches;

import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;

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
	public final boolean multiMatch;

	public DestExLogic(String[] lines, Matcher match) throws Exception {
		super();
		pattern = DestExUtils.compilePattern(Arrays.copyOfRange(lines, 1, lines.length), match.group(2));
		this.multiMatch = (pattern.flags() & Pattern.MULTILINE) > 0;

		//If there's an exclamation mark before destex, it's inverted
		inverted = match.group(1) != null && match.group(1).length() > 0;

	}

	private boolean decideInternal(Player player) {
		String destString = SettingsManager.getDestination(player);
		if (multiMatch) {
			//If the multi-match flag is specified, match against all dests at once
			Matcher matcher = pattern.matcher(destString);
			return matcher.matches();
		} else {
			//Otherwise, run the regex against each dest individually
			for (String dest : StringUtils.split(destString, " ")) {
				Matcher matcher = pattern.matcher(dest);
				if (matcher.matches()) return true;
			}
			return false;
		}
	}
	@Override public boolean decide(Player player) {
		return decideInternal(player) ^ inverted;
	}
}
