package org.iguana.iggy;

import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.regex.IguanaTokenizer;
import org.iguana.regex.RegularExpression;
import org.iguana.regex.Seq;

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

        regularExpressionCategories.put(Seq.from("var"), "Keyword");
        regularExpressionCategories.put(Seq.from("else"), "Keyword");
        regularExpressionCategories.put(Seq.from("regex"), "Keyword");
        regularExpressionCategories.put(Seq.from("align"), "Keyword");
        regularExpressionCategories.put(Seq.from("ignore"), "Keyword");
        regularExpressionCategories.put(Seq.from("layout"), "Keyword");
        regularExpressionCategories.put(Seq.from("global"), "Keyword");
        regularExpressionCategories.put(Seq.from("offside"), "Keyword");
        regularExpressionCategories.put(Seq.from("if"), "Keyword");
        regularExpressionCategories.put(Seq.from("start"), "Keyword");
        regularExpressionCategories.put(Seq.from("left"), "Keyword");
        regularExpressionCategories.put(Seq.from("right"), "Keyword");
        regularExpressionCategories.put(Seq.from("non-assoc"), "Keyword");
        regularExpressionCategories.put(Seq.from("put"), "Keyword");
        regularExpressionCategories.put(Seq.from("set"), "Keyword");
        regularExpressionCategories.put(Seq.from("contains"), "Keyword");

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
