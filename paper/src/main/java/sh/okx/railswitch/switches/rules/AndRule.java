package sh.okx.railswitch.switches.rules;

import java.util.List;

public class AndRule implements ISwitchRule {
    private ISwitchRule ruleA;
    private ISwitchRule ruleB;

    public AndRule(ISwitchRule a, ISwitchRule b){
        this.ruleA = a;
        this.ruleB = b;
    }

    @Override
    public int test(List<String> destinations) {
        if(this.ruleA == null || this.ruleB == null){
            return 0;
        }

        return this.ruleA.test(destinations) > 0 && this.ruleB.test(destinations) > 0 ? 15 : 0;

        //return (this.ruleA != null && this.ruleA.test(destinations)) && (this.ruleB != null && this.ruleB.test(destinations));
    }

    @Override
    public String toString() {
        String str = "ruleA|%s,ruleB|%s";
        return String.format(str, (ruleA == null ? "none" : ruleA.toString()), (ruleB == null ? "none" : ruleB.toString()));
    }
}
