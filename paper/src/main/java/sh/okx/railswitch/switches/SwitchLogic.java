package sh.okx.railswitch.switches;

import com.google.re2j.Matcher;
import org.bukkit.entity.Player;

/**
 * Base class for switch decisions, and a function to determine which subclass to use
 */
public abstract class SwitchLogic {
	public SwitchLogic() throws Exception {

	}

	abstract boolean decide(Player player) throws Exception;

	public static SwitchLogic tryCreate(String[] lines) throws Exception {
		//Simple destinations
		if (DestinationLogic.isNormal(lines[0]) || DestinationLogic.isInverted(lines[0]))
			return new DestinationLogic(lines);

		//Regex destinations
		Matcher destex = DestExLogic.DESTEX.matcher(lines[0]);
		if (destex.matches()) return new DestExLogic(lines, destex);

		//Destination adding
		if (lines[0].equalsIgnoreCase(DestAddLogic.INVOCATION)) return new DestAddLogic(lines);

		//Destination removal
		if (lines[0].equalsIgnoreCase(DestRmLogic.INVOCATION)) return new DestRmLogic(lines);

		//Destination removal via regex
		Matcher destexrm = DestRmExLogic.DESTEX.matcher(lines[0]);
		if (destexrm.matches()) return new DestRmExLogic(lines, destexrm);

		//Invalid header
		if (lines[0].startsWith("[") && lines[0].endsWith("]")) {
			throw new RuntimeException("Unknown logic specifier");
		}
		
		return null;
	}

}
