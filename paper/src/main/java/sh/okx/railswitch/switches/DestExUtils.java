package sh.okx.railswitch.switches;


import com.google.re2j.Pattern;

public class DestExUtils {
	static Pattern compile_pattern(String[] lines, String raw_flags) throws Exception {
		StringBuilder pattern_appender = new StringBuilder();
		for (String line : lines) pattern_appender.append(line);
		String pattern_text = pattern_appender.toString();

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
