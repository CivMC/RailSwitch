package sh.okx.railswitch.switches.rules;

import java.util.List;

public class NegateRule implements ISwitchRule {

    private ISwitchRule rule;

    public NegateRule(ISwitchRule rule){
        this.rule = rule;
    }

    @Override
    public int test(List<String> destinations) {
        if(this.rule == null){
            return 0;
        }

        return 15-this.rule.test(destinations);
    }

    @Override
    public String toString() {
        return "rule|"+(rule == null ? "none" : rule.toString());
    }
}
