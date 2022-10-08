package sh.okx.railswitch.switches;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Logic for destination matching via regular expressions
 */
public class DestExLogic extends SwitchLogic {
	public static final Pattern DESTEX = Pattern.compile("\\[(!)?destex(?:;(\\w+)?)?\\]", Pattern.CASE_INSENSITIVE);

	public final Pattern pattern;
	public final boolean inverted;
	public final boolean multi_match;

	public DestExLogic(String[] lines, Matcher match) throws Exception {
		super();

		//Lines after the first are appended together with no separators to form the pattern text
		StringBuilder pattern_appender = new StringBuilder();
		for (int i = 1; i < lines.length; i++) pattern_appender.append(lines[i]);
		String pattern_text = pattern_appender.toString();

		//Optional flags may have been specified in the first line
		int flags = 0;
		boolean multi_match = false;
		String raw_flags = match.group(2);
		if (raw_flags != null) {
			raw_flags = raw_flags.toLowerCase();
			for (int i = 0; i < raw_flags.length(); i++) {
				switch (raw_flags.charAt(0)) {
					case 'e': flags |= Pattern.CANON_EQ; break;
					case 'i': flags |= Pattern.CASE_INSENSITIVE; break;
					case 'c': flags |= Pattern.COMMENTS; break;
					case 'd': flags |= Pattern.DOTALL; break;
					case 'l': flags |= Pattern.LITERAL; break;
					case 'u': flags |= Pattern.UNICODE_CHARACTER_CLASS; break;
					case 'b': flags |= Pattern.UNICODE_CASE; break;

					case 'm':
						flags |= Pattern.MULTILINE | Pattern.UNIX_LINES;
						multi_match = true;
						break;
				}
			}
		}

		pattern = Pattern.compile(pattern_text, flags);
		this.multi_match = multi_match;

		//If there's an exclamation mark before destex, it's inverted
		inverted = match.group(1) != null && match.group(1).length() > 0;

	}

	private boolean decide_internal(String dest_string) {
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
	@Override public boolean decide(String dest_string) {
		return decide_internal(dest_string) ^ inverted;
	}
}
