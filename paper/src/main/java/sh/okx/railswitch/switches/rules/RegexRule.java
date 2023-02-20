package sh.okx.railswitch.switches.rules;

import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//import java.util.regex.PatternSyntaxException;
import com.google.re2j.Pattern;
import com.google.re2j.PatternSyntaxException;
//import com.google.re2j.Matcher;

public class RegexRule implements ISwitchRule {

    private Pattern pattern = null;

    public RegexRule(String regex){
        try {
            this.pattern = Pattern.compile(regex);
        }catch(PatternSyntaxException e){
            // throw away this exception, we don't care that the regex failed
        }
    }

    @Override
    public int test(List<String> destinations) {
        if(pattern == null){
            return 0;
        }

        for(String dest : destinations){
            if(pattern.matcher(dest).matches()){
                return 15;
            }
        }

        return 0;
    }

    @Override
    public String toString() {
        return "regex|"+(pattern == null ? "invalid pattern" : pattern.toString());
    }
}
