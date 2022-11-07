package sh.okx.railswitch.switches;

import java.util.Iterator;

import com.google.re2j.Pattern;

public class DestExUtils {
	/** Compile a pattern from an array of strings and additional flags */
	static Pattern compile_pattern(String[] lines, String raw_flags) throws Exception {
		String pattern_text = String.join("", lines);

		//Optional flags may have been specified in the first line
		int flags = 0;
		if (raw_flags != null) {
			raw_flags = raw_flags.toLowerCase();
			for (int i = 0; i < raw_flags.length(); i++) {
				switch (raw_flags.charAt(i)) {
					case 'i': flags |= Pattern.CASE_INSENSITIVE; break;
					case 'u': flags |= Pattern.DISABLE_UNICODE_GROUPS; break;
					case 'd': flags |= Pattern.DOTALL; break;
					//case 'l': flags |= Pattern.LONGEST_MATCH; break;
					case 'm': flags |= Pattern.MULTILINE; break;
				}
			}
		}

		return Pattern.compile(pattern_text, flags);
	}
}
