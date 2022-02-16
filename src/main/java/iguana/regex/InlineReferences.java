package iguana.regex;

import iguana.regex.visitor.InlineReferencesVisitor;
import iguana.regex.visitor.RegularExpressionVisitor;

import java.util.HashMap;
import java.util.Map;

public class InlineReferences {

    public static Map<String, RegularExpression> inline(Map<String, RegularExpression> definitions) {
        Map<String, RegularExpression> current = definitions;

        while (true) {
            RegularExpressionVisitor<RegularExpression> visitor = new InlineReferencesVisitor(current);

            Map<String, RegularExpression> newMap = new HashMap<>();
            for (Map.Entry<String, RegularExpression> entry : current.entrySet()) {
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
