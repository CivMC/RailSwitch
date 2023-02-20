package sh.okx.railswitch.switches.rules;

import java.util.List;

public class NormalRule implements ISwitchRule {

    private String destination;

    public NormalRule(String destination){
        this.destination = destination;
    }

    @Override
    public int test(List<String> destinations) {
        if(this.destination == null){
            return 0;
        }
        return destinations.contains(this.destination) ? 15 : 0;
        //return this.destination != null && destinations.contains(this.destination);
    }

    @Override
    public String toString() {
        return "destination|"+destination;
    }
}
