package iguana.regex.matcher;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
        String tuplePattern = "\\(([^(),]+),([^(),]+)\\)";
        String pattern = tuplePattern + "(\\s*" + tuplePattern + ")*";

        Matcher matcher = Pattern.compile(pattern).matcher("(1.0, 2.3)   (12.3  , 7.3) (8, 9) (10, 11)");

        List<String> groups = new ArrayList<>();
        if (matcher.matches()) {
            for (int i = 0; i <= matcher.groupCount(); i++) {
                System.out.println(matcher.group(i));
                String group = matcher.group(i).trim();
                if (!group.startsWith("("))
                    groups.add(group);
            }
        }

        System.out.println(groups);
    }
}
