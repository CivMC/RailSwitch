package sh.okx.railswitch.switches;

import java.util.Iterator;

import com.google.re2j.Pattern;

public class DestExUtils {
	/** Compile a pattern from an array of strings and additional flags */
	static Pattern compilePattern(String[] lines, String rawFlags) throws Exception {
		String patternText = String.join("", lines);

		//Optional flags may have been specified in the first line
		int flags = 0;
		if (rawFlags != null) {
			rawFlags = rawFlags.toLowerCase();
			for (int i = 0; i < rawFlags.length(); i++) {
				switch (rawFlags.charAt(i)) {
					case 'i': flags |= Pattern.CASE_INSENSITIVE; break;
					case 'u': flags |= Pattern.DISABLE_UNICODE_GROUPS; break;
					case 'd': flags |= Pattern.DOTALL; break;
					//case 'l': flags |= Pattern.LONGEST_MATCH; break;
					case 'm': flags |= Pattern.MULTILINE; break;
				}
			}
		}

		return Pattern.compile(patternText, flags);
	}
}
