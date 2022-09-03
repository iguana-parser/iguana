package org.iguana.iggy;

import org.iguana.grammar.Annotation;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.regex.Alt;
import org.iguana.regex.RegularExpression;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RegularExpressionCategories {

    public static Map<RegularExpression, String> getCategories(Grammar grammar) {
        RuntimeGrammar runtimeGrammar = grammar.toRuntimeGrammar();
        List<Annotation> annotations = runtimeGrammar.getAnnotations();
        List<Annotation> tokenTypeAnnotations = annotations.stream()
            .filter(annotation -> annotation.getName().equals("TokenType"))
            .collect(Collectors.toList());

        Map<RegularExpression, String> regularExpressionCategories = new LinkedHashMap<>();
        for (Map.Entry<String, RegularExpression> entry : runtimeGrammar.getLiterals().entrySet()) {
            regularExpressionCategories.put(entry.getValue(), entry.getKey());
        }

        for (Annotation annotation : tokenTypeAnnotations) {
            RegularExpression regularExpression = runtimeGrammar.getRegularExpressions().get(annotation.getSymbolName());
            // TODO: this is a hack, find a better solution to mark keywords
            if (annotation.getValue().equals("Keyword")) {
                Alt<?> alt = (Alt<?>) regularExpression;
                for (RegularExpression regex : alt.getSymbols()) {
                    regularExpressionCategories.put(regex, annotation.getValue());
                }
            } else {
                regularExpressionCategories.put(regularExpression, annotation.getValue());
            }
        }
        return regularExpressionCategories;
    }
}
