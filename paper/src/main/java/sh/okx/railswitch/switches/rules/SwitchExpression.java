package sh.okx.railswitch.switches.rules;

import com.google.common.base.Strings;
import org.bukkit.block.Block;
import sh.okx.railswitch.RailSwitchPlugin;
import vg.civcraft.mc.civmodcore.utilities.MoreMath;

/*
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 */

import com.google.re2j.Pattern;
import com.google.re2j.Matcher;

import java.util.*;

/**
 * TODO:
 * - Continue-line operator (>>)
 * - Namelayer group routing
 * - Routing based on cart type
 */

public class SwitchExpression implements ISwitchRule {

    //private static final Pattern AND_PATTERN = Pattern.compile("(?:(.*)&(.*))");
    //private static final Pattern RS_PATTERN = Pattern.compile("(?:.*->\\s*(\\d+))$");

    private static final String WILDCARD = "*";
    private static final Pattern ASSIGN_PATTERN = Pattern.compile("(?:([\\w\\d]+)\\s*=\\s*(.+))");
    private static final Pattern NEGATE_PATTERN = Pattern.compile("(?:!(.*))");
    private static final Pattern AND_RS_PATTERN = Pattern.compile("(?:([^\\s]+)\\s*&&\\s*([^\\s]+))(?:\\s*->\\s*(\\d+))?");
    private static final Pattern REGEX_PATTERN = Pattern.compile("(?:\\\\(.*)\\\\)");
    private static final Pattern RS_PATTERN = Pattern.compile("(?:([.\\S]*)\\s*->\\s*(\\d+))$");


    private Map<String, ISwitchRule> env = new HashMap<>();
    private List<ISwitchRule> rules = new ArrayList<>();

    // needed for backwards compatability with SwitchType
    private boolean negateEverything;

    private SwitchExpression(boolean negateEverything){
        this.negateEverything = negateEverything;
    }

    @Override
    public int test(List<String> destinations) {

        if(destinations.contains(WILDCARD)){
            return 15;
        }

        for(ISwitchRule rule : this.rules){
            if(rule == null){
                continue;
            }

            int output = rule.test(destinations);
            if(output > 0){
                return output;
            }
        }

        return 0;
    }

    private void parseLine(String line){
        Matcher expMatcher = ASSIGN_PATTERN.matcher(line);
        if(expMatcher.matches()){
            String varName = expMatcher.group(1);
            String varValue = expMatcher.group(2);

            ISwitchRule rule = this.parseRule(varValue);
            this.env.put(varName, rule);
        }else{
            ISwitchRule rule = this.parseRule(line);
            if(rule != null) {
                if (negateEverything) {
                    rule = new NegateRule(rule);
                }
                this.rules.add(rule);
            }
        }
    }

    private ISwitchRule parseRule(String line){
        if(Strings.isNullOrEmpty(line)){
            return null;
        }

        Matcher expMatcher = AND_RS_PATTERN.matcher(line);
        if(expMatcher.matches()){
            String destAStr = expMatcher.group(1);
            String destBStr = expMatcher.group(2);
            String outputStr = expMatcher.group(3);


            if(Strings.isNullOrEmpty(outputStr)){
                outputStr = "15";
            }

            RailSwitchPlugin.getInstance(RailSwitchPlugin.class).info("parsing and expression, redstone output: " + outputStr);
            //String outputStr = Strings.isNullOrEmpty(expMatcher.group(3)) ? expMatcher.group(3) : "15";
            ISwitchRule ruleA = null;
            ISwitchRule ruleB = null;

            int output = parseInt(outputStr);
            if(output <= -1){
                output = 15;
            }
            output = MoreMath.clamp(output, 0, 15);

            boolean negateA = false;
            boolean negateB = false;

            Matcher negateMatcher = NEGATE_PATTERN.matcher(destAStr);
            if(negateMatcher.matches()){
                destAStr = negateMatcher.group(1);
                negateA = true;
            }

            negateMatcher = NEGATE_PATTERN.matcher(destBStr);
            if(negateMatcher.matches()){
                destBStr = negateMatcher.group(1);
                negateB = true;
            }

            if(this.env.containsKey(destAStr)){
                ruleA = this.env.get(destAStr);
            }

            if(this.env.containsKey(destBStr)){
                ruleB = this.env.get(destBStr);
            }

            if(ruleA == null){
                Matcher regexMatcher = REGEX_PATTERN.matcher(destAStr);
                if(regexMatcher.matches()){
                    String regex = regexMatcher.group(1);
                    ruleA = new RegexRule(regex);
                }else{
                    ruleA = new NormalRule(destAStr);
                }
            }

            if(ruleB == null){
                Matcher regexMatcher = REGEX_PATTERN.matcher(destBStr);
                if(regexMatcher.matches()){
                    String regex = regexMatcher.group(1);
                    ruleB = new RegexRule(regex);
                }else{
                    ruleB = new NormalRule(destBStr);
                }
            }

            if(negateA){
                ruleA = new NegateRule(ruleA);
            }
            if(negateB){
                ruleB = new NegateRule(ruleB);
            }

            return new RedstoneRule(new AndRule(ruleA, ruleB), output);
        }

        int redstoneOut = -1;
        expMatcher = RS_PATTERN.matcher(line);
        if(expMatcher.matches()){
            line = expMatcher.group(1);
            int output = parseInt(expMatcher.group(2));
            if(output <= -1){
                output = 15;
            }
            redstoneOut = MoreMath.clamp(output, 0, 15);
        }

        boolean shouldNegate = false;
        ISwitchRule rule = null;
        expMatcher = NEGATE_PATTERN.matcher(line);
        if(expMatcher.matches()){
            line = expMatcher.group(1);
            shouldNegate = true;
        }

        expMatcher = REGEX_PATTERN.matcher(line);
        if(expMatcher.matches()){
            rule = new RegexRule(expMatcher.group(1));
        }else{
            if(this.env.containsKey(line)){
                rule = this.env.get(line);
            }else {
                rule = new NormalRule(line);
            }
        }

        if(redstoneOut > -1){
            rule = new RedstoneRule(rule, redstoneOut);
        }

        if(shouldNegate){
            return new NegateRule(rule);
        }

        return rule;
    }

    public static SwitchExpression compile(List<String> lines, boolean negateEverything){
        SwitchExpression exp = new SwitchExpression(negateEverything);
        for(String line : lines){
            exp.parseLine(line);
        }
        return exp;
    }

    private static int parseInt(String intStr){
        try{
            return Integer.parseInt(intStr);
        }catch(NumberFormatException e){
            return -1;
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SwitchExpression {\n");
        builder.append("\tenv: {\n");
        for(Map.Entry<String, ISwitchRule> entry : this.env.entrySet()){
            builder.append("\t\t");
            builder.append(entry.getKey());
            builder.append(": ");
            builder.append(entry.getValue().toString());
            builder.append("\n");
        }
        builder.append("\t}\n");

        builder.append("\trules: [\n");
        for(ISwitchRule rule : this.rules){
            builder.append("\t\t");
            builder.append(rule.getClass().toString());
            builder.append(": ");
            builder.append(rule.toString());
            builder.append("\n");
        }
        builder.append("\t]\n");
        return builder.toString();
    }
}
