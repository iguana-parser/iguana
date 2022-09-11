package org.iguana.iggy;

import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.iggy.gen.IggyGrammar;
import org.iguana.regex.Alt;
import org.iguana.regex.RegularExpression;

import java.util.LinkedHashMap;
import java.util.Map;

public class IggyRegexCategories {

    private static final Map<RegularExpression, String> regularExpressionCategories = new LinkedHashMap<>();

    static {
        RuntimeGrammar runtimeGrammar = IggyGrammar.getGrammar().toRuntimeGrammar();

        // TODO: this is a hack, find a better solution to mark keywords
        RegularExpression keywords = runtimeGrammar.getRegularExpressionDefinitions().get("Keywords");
        if (keywords != null) {
            Alt<?> alt = (Alt<?>) keywords;
            for (RegularExpression regex : alt.getSymbols()) {
                regularExpressionCategories.put(regex, "Keyword");
            }
        }

        for (Map.Entry<String, RegularExpression> entry : runtimeGrammar.getLiterals().entrySet()) {
            if (!regularExpressionCategories.containsKey(entry.getValue())) {
                regularExpressionCategories.put(entry.getValue(), entry.getKey());
            }
        }

        for (Map.Entry<String, RegularExpression> entry : runtimeGrammar.getRegularExpressionDefinitions().entrySet()) {
            RegularExpression regularExpression = entry.getValue();
            String category = entry.getKey();

            if (!category.equals("Keywords")) {
                regularExpressionCategories.put(regularExpression, category);
            }
        }
    }

    public static Map<RegularExpression, String> getCategories() {
        return regularExpressionCategories;
    }

    public static String getCategory(RegularExpression regex) {
        return regularExpressionCategories.get(regex);
    }
}
