package sh.okx.railswitch.switches.rules;

import java.util.List;

public interface ISwitchRule {

    /**
     *
     * @param destinations the list of strings to test
     * @return Redstone signal strength between 0-15
     */
    int test(List<String> destinations);

}
