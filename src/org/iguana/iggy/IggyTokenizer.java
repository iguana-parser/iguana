package org.iguana.iggy;

import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.regex.IguanaTokenizer;
import org.iguana.regex.RegularExpression;

import java.util.HashMap;
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
        Map<RegularExpression, String> regularExpressionCategories = new HashMap<>();
        regularExpressionCategories.put(grammar.getRegularExpressions().get("LetterOrDigits"), "Identifier");
        regularExpressionCategories.put(grammar.getRegularExpressions().get("Number"), "Number");
        regularExpressionCategories.put(grammar.getRegularExpressions().get("String"), "String");
        regularExpressionCategories.put(grammar.getRegularExpressions().get("RangeChar"), "RangeChar");
        regularExpressionCategories.put(grammar.getRegularExpressions().get("WhiteSpace"), "WhiteSpace");
        regularExpressionCategories.put(grammar.getRegularExpressions().get("SingleLineComment"), "SingleLineComment");
        regularExpressionCategories.put(grammar.getRegularExpressions().get("MultiLineComment"), "MultiLineComment");

        regularExpressionCategories.put(grammar.getLiterals().get("!<<"), "!<<");
        regularExpressionCategories.put(grammar.getLiterals().get("!>>"), "!>>");
        regularExpressionCategories.put(grammar.getLiterals().get(">="), ">=");
        regularExpressionCategories.put(grammar.getLiterals().get("<<"), "<<");
        regularExpressionCategories.put(grammar.getLiterals().get(">>"), ">>");
        regularExpressionCategories.put(grammar.getLiterals().get("&&"), "&&");
        regularExpressionCategories.put(grammar.getLiterals().get("||"), "||");
        regularExpressionCategories.put(grammar.getLiterals().get("<="), "<=");
        regularExpressionCategories.put(grammar.getLiterals().get("var"), "var");
        regularExpressionCategories.put(grammar.getLiterals().get(".r"), ".r");
        regularExpressionCategories.put(grammar.getLiterals().get("else"), "else");
        regularExpressionCategories.put(grammar.getLiterals().get("regex"), "regex");
        regularExpressionCategories.put(grammar.getLiterals().get(".val"), ".val");
        regularExpressionCategories.put(grammar.getLiterals().get("align"), "align");
        regularExpressionCategories.put(grammar.getLiterals().get("["), "[");
        regularExpressionCategories.put(grammar.getLiterals().get("\\\\"), "\\\\");
        regularExpressionCategories.put(grammar.getLiterals().get("]"), "]");
        regularExpressionCategories.put(grammar.getLiterals().get("if"), "if");
        regularExpressionCategories.put(grammar.getLiterals().get(".yield"), ".yield");
        regularExpressionCategories.put(grammar.getLiterals().get("ignore"), "ignore");
        regularExpressionCategories.put(grammar.getLiterals().get("!"), "!");
        regularExpressionCategories.put(grammar.getLiterals().get("=="), "==");
        regularExpressionCategories.put(grammar.getLiterals().get("%"), "%");
        regularExpressionCategories.put(grammar.getLiterals().get("("), "(");
        regularExpressionCategories.put(grammar.getLiterals().get(")"), ")");
        regularExpressionCategories.put(grammar.getLiterals().get("layout"), "layout");
        regularExpressionCategories.put(grammar.getLiterals().get("*"), "*");
        regularExpressionCategories.put(grammar.getLiterals().get("global"), "global");
        regularExpressionCategories.put(grammar.getLiterals().get("+"), "+");
        regularExpressionCategories.put(grammar.getLiterals().get(","), ",");
        regularExpressionCategories.put(grammar.getLiterals().get("-"), "-");
        regularExpressionCategories.put(grammar.getLiterals().get("/"), "/");
        regularExpressionCategories.put(grammar.getLiterals().get("offside"), "offside");
        regularExpressionCategories.put(grammar.getLiterals().get(":"), ":");
        regularExpressionCategories.put(grammar.getLiterals().get("{"), "{");
        regularExpressionCategories.put(grammar.getLiterals().get(";"), ";");
        regularExpressionCategories.put(grammar.getLiterals().get("|"), "|");
        regularExpressionCategories.put(grammar.getLiterals().get("<"), "<");
        regularExpressionCategories.put(grammar.getLiterals().get("="), "=");
        regularExpressionCategories.put(grammar.getLiterals().get("}"), "}");
        regularExpressionCategories.put(grammar.getLiterals().get("!="), "!=");
        regularExpressionCategories.put(grammar.getLiterals().get("start"), "start");
        regularExpressionCategories.put(grammar.getLiterals().get(">"), ">");
        regularExpressionCategories.put(grammar.getLiterals().get("?"), "?");
        regularExpressionCategories.put(grammar.getLiterals().get(".l"), ".l");
        return new IguanaTokenizer(regularExpressionCategories);
    }
}
