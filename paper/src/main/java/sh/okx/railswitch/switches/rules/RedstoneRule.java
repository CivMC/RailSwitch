package sh.okx.railswitch.switches.rules;

import vg.civcraft.mc.civmodcore.utilities.MoreMath;

import java.util.List;

public class RedstoneRule implements ISwitchRule {

    private ISwitchRule rule;
    private int strength;

    public RedstoneRule(ISwitchRule rule, int strength){
        this.rule = rule;
        this.strength = MoreMath.clamp(strength, 0, 15);
    }

    @Override
    public int test(List<String> destinations) {
        if(this.rule == null){
            return 0;
        }

        return this.rule.test(destinations) > 0 ? this.strength : 0;
    }

    @Override
    public String toString() {
        return "strength|" + this.strength + ",rule{"+(rule == null ? "none" : rule.toString())+"}";
    }
}
