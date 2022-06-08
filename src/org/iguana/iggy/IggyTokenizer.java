package org.iguana.iggy;

import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.regex.IguanaTokenizer;
import org.iguana.regex.RegularExpression;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class IggyTokenizer {

    private static IguanaTokenizer iggyTokenizer;

    public static IguanaTokenizer getIggyTokenizer() {
        if (iggyTokenizer == null) {
            iggyTokenizer = createIggyTokenizer();
        }
        return iggyTokenizer;
    }

    private static IguanaTokenizer createIggyTokenizer() {
        RuntimeGrammar grammar = IggyParser.iggyGrammar().toRuntimeGrammar();
        Map<RegularExpression, String> regularExpressionCategories = new LinkedHashMap<>();

        regularExpressionCategories.put(grammar.getLiterals().get("var"), "Keyword");
        regularExpressionCategories.put(grammar.getLiterals().get("else"), "Keyword");
        regularExpressionCategories.put(grammar.getLiterals().get("regex"), "Keyword");
        regularExpressionCategories.put(grammar.getLiterals().get("align"), "Keyword");
        regularExpressionCategories.put(grammar.getLiterals().get("ignore"), "Keyword");
        regularExpressionCategories.put(grammar.getLiterals().get("layout"), "Keyword");
        regularExpressionCategories.put(grammar.getLiterals().get("global"), "Keyword");
        regularExpressionCategories.put(grammar.getLiterals().get("offside"), "Keyword");
        regularExpressionCategories.put(grammar.getLiterals().get("if"), "Keyword");
        regularExpressionCategories.put(grammar.getLiterals().get("start"), "Keyword");
//        regularExpressionCategories.put(grammar.getLiterals().get("left"), "Keyword");
//        regularExpressionCategories.put(grammar.getLiterals().get("right"), "Keyword");
//        regularExpressionCategories.put(grammar.getLiterals().get("non-assoc"), "Keyword");

        regularExpressionCategories.put(grammar.getLiterals().get("!<<"), "!<<");
        regularExpressionCategories.put(grammar.getLiterals().get("!>>"), "!>>");
        regularExpressionCategories.put(grammar.getLiterals().get(">="), ">=");
        regularExpressionCategories.put(grammar.getLiterals().get("<<"), "<<");
        regularExpressionCategories.put(grammar.getLiterals().get(">>"), ">>");
        regularExpressionCategories.put(grammar.getLiterals().get("&&"), "&&");
        regularExpressionCategories.put(grammar.getLiterals().get("||"), "||");
        regularExpressionCategories.put(grammar.getLiterals().get("<="), "<=");
        regularExpressionCategories.put(grammar.getLiterals().get(".r"), ".r");
        regularExpressionCategories.put(grammar.getLiterals().get(".val"), ".val");
        regularExpressionCategories.put(grammar.getLiterals().get("["), "[");
        regularExpressionCategories.put(grammar.getLiterals().get("\\\\"), "\\\\");
        regularExpressionCategories.put(grammar.getLiterals().get("]"), "]");
        regularExpressionCategories.put(grammar.getLiterals().get(".yield"), ".yield");
        regularExpressionCategories.put(grammar.getLiterals().get("!"), "!");
        regularExpressionCategories.put(grammar.getLiterals().get("=="), "==");
        regularExpressionCategories.put(grammar.getLiterals().get("%"), "%");
        regularExpressionCategories.put(grammar.getLiterals().get("("), "(");
        regularExpressionCategories.put(grammar.getLiterals().get(")"), ")");
        regularExpressionCategories.put(grammar.getLiterals().get("*"), "*");
        regularExpressionCategories.put(grammar.getLiterals().get("+"), "+");
        regularExpressionCategories.put(grammar.getLiterals().get(","), ",");
        regularExpressionCategories.put(grammar.getLiterals().get("-"), "-");
        regularExpressionCategories.put(grammar.getLiterals().get("/"), "/");
        regularExpressionCategories.put(grammar.getLiterals().get(":"), ":");
        regularExpressionCategories.put(grammar.getLiterals().get("{"), "{");
        regularExpressionCategories.put(grammar.getLiterals().get(";"), ";");
        regularExpressionCategories.put(grammar.getLiterals().get("|"), "|");
        regularExpressionCategories.put(grammar.getLiterals().get("<"), "<");
        regularExpressionCategories.put(grammar.getLiterals().get("="), "=");
        regularExpressionCategories.put(grammar.getLiterals().get("}"), "}");
        regularExpressionCategories.put(grammar.getLiterals().get("!="), "!=");
        regularExpressionCategories.put(grammar.getLiterals().get(">"), ">");
        regularExpressionCategories.put(grammar.getLiterals().get("?"), "?");
        regularExpressionCategories.put(grammar.getLiterals().get(".l"), ".l");

        regularExpressionCategories.put(grammar.getRegularExpressions().get("LetterOrDigits"), "Identifier");
        regularExpressionCategories.put(grammar.getRegularExpressions().get("Number"), "Number");
        regularExpressionCategories.put(grammar.getRegularExpressions().get("String"), "String");
        regularExpressionCategories.put(grammar.getRegularExpressions().get("RangeChar"), "RangeChar");
        regularExpressionCategories.put(grammar.getRegularExpressions().get("WhiteSpace"), "WhiteSpace");
        regularExpressionCategories.put(grammar.getRegularExpressions().get("SingleLineComment"), "SingleLineComment");
        regularExpressionCategories.put(grammar.getRegularExpressions().get("MultiLineComment"), "MultiLineComment");

        return new IguanaTokenizer(regularExpressionCategories);
    }
}
