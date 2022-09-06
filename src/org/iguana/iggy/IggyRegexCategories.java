package org.iguana.iggy;

import org.iguana.grammar.Annotation;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.iggy.gen.IggyGrammar;
import org.iguana.regex.Alt;
import org.iguana.regex.RegularExpression;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IggyRegexCategories {

    private static final Map<RegularExpression, String> regularExpressionCategories;

    static {
        RuntimeGrammar runtimeGrammar = IggyGrammar.getGrammar().toRuntimeGrammar();
        List<Annotation> annotations = runtimeGrammar.getAnnotations();
        List<Annotation> tokenTypeAnnotations = annotations.stream()
            .filter(annotation -> annotation.getName().equals("TokenType"))
            .collect(Collectors.toList());

        Map<RegularExpression, String> map = new LinkedHashMap<>();
        for (Map.Entry<String, RegularExpression> entry : runtimeGrammar.getLiterals().entrySet()) {
            map.put(entry.getValue(), entry.getKey());
        }

        for (Annotation annotation : tokenTypeAnnotations) {
            RegularExpression regularExpression = runtimeGrammar.getRegularExpressions().get(annotation.getSymbolName());
            // TODO: this is a hack, find a better solution to mark keywords
            if (annotation.getValue().equals("Keyword")) {
                Alt<?> alt = (Alt<?>) regularExpression;
                for (RegularExpression regex : alt.getSymbols()) {
                    map.put(regex, annotation.getValue());
                }
            } else {
                map.put(regularExpression, annotation.getValue());
            }
        }
        regularExpressionCategories = map;
    }

    public static Map<RegularExpression, String> getCategories() {
        return regularExpressionCategories;
    }

    public static String getCategory(RegularExpression regex) {
        return regularExpressionCategories.get(regex);
    }
}
