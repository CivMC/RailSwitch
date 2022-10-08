package sh.okx.railswitch.switches;

import java.util.regex.Matcher;

/**
 * Base class for switch decisions, and a function to determine which subclass to use
 */
public abstract class SwitchLogic {
	public SwitchLogic() throws Exception {

	}

	abstract boolean decide(String dest_string) throws Exception;

	public static SwitchLogic try_create(String[] lines) throws Exception {
		//Simple destinations
		if (lines[0].equalsIgnoreCase(SimpleDestLogic.NORMAL) || lines[0].equalsIgnoreCase(SimpleDestLogic.INVERTED))
			return new SimpleDestLogic(lines);

		//Regex destinations
		Matcher regex = DestExLogic.DESTEX.matcher(lines[0]);
		if (regex.matches()) return new DestExLogic(lines, regex);

		//Failure to specify the [] bits
		if (lines[0].length() >= 2 && lines[0].charAt(0) == '[' && lines[0].charAt(lines[0].length() - 1) == ']') {
			throw new RuntimeException("Unknown logic specifier");
		}
		
		return null;
	}

}
