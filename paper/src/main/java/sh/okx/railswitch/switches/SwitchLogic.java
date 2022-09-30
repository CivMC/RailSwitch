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
		
		return null;
	}
}
