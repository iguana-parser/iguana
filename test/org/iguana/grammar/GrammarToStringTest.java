package org.iguana.grammar;

import org.junit.jupiter.api.Test;

import static org.iguana.iggy.IggyParserUtils.fromIggyGrammar;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GrammarToStringTest {

    @Test
    public void test1() {
        String grammarText =
            "E = left E '*' E\n" +
            "  | left E '+' E\n" +
            "  ;\n";
        Grammar grammar = fromIggyGrammar(grammarText);

        assertEquals(grammarText, grammar.toString());
    }

    @Test
    public void test2() {
        String grammarText =
            "Expr\n" +
            "  = left Expr '*' Expr\n" +
            "  | left Expr '+' Expr\n" +
            "  ;\n";
        Grammar grammar = fromIggyGrammar(grammarText);

        assertEquals(grammarText, grammar.toString());
    }

    @Test
    public void test3() {
        String grammarText =
            "S = {C ','}*\n" +
            "  ;\n" +
            "\n" +
            "C = 'c'\n" +
            "  ;\n";
        Grammar grammar = fromIggyGrammar(grammarText);

        assertEquals(grammarText, grammar.toString());
    }

    @Test
    public void test4() {
        String grammarText =
            "S = {C ',' ';' B}*\n" +
            "  ;\n" +
            "\n" +
            "B = 'b'\n" +
            "  ;\n" +
            "\n" +
            "C = 'c'\n" +
            "  ;\n";
        Grammar grammar = fromIggyGrammar(grammarText);

        assertEquals(grammarText, grammar.toString());
    }

    @Test
    public void test5() {
        String grammarText =
            "E = left (E '*' E\n" +
            "  |       E '/' E)\n" +
            "  > left (E '+' E\n" +
            "  |       E '-' E)\n" +
            "  ;\n";
        Grammar grammar = fromIggyGrammar(grammarText);

        // TODO: fix this when adding a proper contextual pretty-printer for the grammar.
        // We don't need to print the inner associativity values when it's equal to the outer one.
        String expected =
            "E = left (left E '*' E\n" +
            "  |       left E '/' E)\n" +
            "  > left (left E '+' E\n" +
            "  |       left E '-' E)\n" +
            "  ;\n";
        assertEquals(expected, grammar.toString());
    }

    @Test
    public void test6() {
        String grammarText =
            "E = '-' E  %UnaryMin\n" +
            "  > right E '^' E  %Power\n" +
            "  > left E '*' E  %Multiply\n" +
            "  > left E '+' E  %Plus\n" +
            "  | 'a'  %Literal\n" +
            "  ;\n";
        Grammar grammar = fromIggyGrammar(grammarText);

        assertEquals(grammarText, grammar.toString());
    }
}
