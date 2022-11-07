package sh.okx.railswitch.switches;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;

import com.google.re2j.Pattern;
import com.google.re2j.Matcher;
import org.bukkit.entity.Player;

import sh.okx.railswitch.settings.SettingsManager;

/**
 * Logic for removing destinations via regular expressions
 */
public class RemoveDestExLogic extends SwitchLogic {
	public static final Pattern DESTEX = Pattern.compile("\\[destrmex(?:;(\\w*))?\\]", Pattern.CASE_INSENSITIVE);

	public final Pattern pattern;
	//public final boolean multi_match;

	public RemoveDestExLogic(String[] lines, Matcher match) throws Exception {
		super();
		pattern = DestExUtils.compile_pattern(Arrays.copyOfRange(lines, 1, lines.length), match.group(1));
		//this.multi_match = pattern.flags() & Pattern.MULTILINE > 0;
	}

	@Override
	public boolean decide(Player player) {
		boolean did_something = false;
		String dest_string = SettingsManager.getDestination(player);

		DestIterator iter = new DestIterator(dest_string);
		while (iter.hasNext()) {
			String dest = iter.next();
			if (dest.isBlank()) continue;

			Matcher matcher = pattern.matcher(dest);
			if (matcher.matches()) {
				iter.remove();
				did_something = true;
			}
		}

		if (did_something) {
			SettingsManager.setDestination(player, iter.dest_string());
			return true;
		}
		return false;
	}
}
