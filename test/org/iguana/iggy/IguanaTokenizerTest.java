package org.iguana.iggy;

import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.regex.IguanaTokenizer;
import org.iguana.regex.RegularExpression;
import org.iguana.utils.input.Input;
import org.iguana.utils.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IguanaTokenizerTest {

    @Test
    public void test() throws IOException {
        RuntimeGrammar grammar = IggyParser.iggyGrammar().toRuntimeGrammar();

        String path = Paths.get("src/resources/Iguana.iggy").toAbsolutePath().toString();
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

        String inputString = FileUtils.readFile(path);

        IguanaTokenizer iguanaTokenizer = new IguanaTokenizer(regularExpressionCategories, Input.fromString(inputString), 0);
        StringBuilder sb = new StringBuilder();
        while (iguanaTokenizer.hasNextToken()) {
            sb.append(iguanaTokenizer.nextToken().getLexeme());
        }
        assertEquals(inputString, sb.toString());

    }
}
