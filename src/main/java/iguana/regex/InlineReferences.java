package iguana.regex;

import iguana.regex.visitor.InlineReferencesVisitor;
import iguana.regex.visitor.RegularExpressionVisitor;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class InlineReferences {

    public static Map<String, RegularExpression> inline(Map<String, RegularExpression> definitions) {
        Map<String, RegularExpression> current = definitions;

        while (true) {
            LinkedHashSet<String> set = new LinkedHashSet<>();
            RegularExpressionVisitor<RegularExpression> visitor = new InlineReferencesVisitor(current, set);

            Map<String, RegularExpression> newMap = new HashMap<>();
            for (Map.Entry<String, RegularExpression> entry : current.entrySet()) {
                set.add(entry.getKey());
                newMap.put(entry.getKey(), entry.getValue().accept(visitor));
            }
            if (newMap.equals(current)) {
                break;
            }
            current = newMap;
        }

        return current;
    }
}
