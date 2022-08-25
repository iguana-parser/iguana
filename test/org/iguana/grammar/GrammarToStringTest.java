package org.iguana.grammar;

import org.junit.jupiter.api.Test;

import static org.iguana.iggy.IggyParserUtils.fromIggyGrammar;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GrammarToStringTest {

    @Test
    public void test1() {
        String grammarText =
            "E = left  E '*' E\n" +
            "  | left  E '+' E\n";
        Grammar grammar = fromIggyGrammar(grammarText);

        assertEquals(grammarText, grammar.toString());
    }

    @Test
    public void test2() {
        String grammarText =
            "Expr\n" +
            "  = left  Expr '*' Expr\n" +
            "  | left  Expr '+' Expr\n";
        Grammar grammar = fromIggyGrammar(grammarText);

        assertEquals(grammarText, grammar.toString());
    }

    @Test
    public void test3() {
        String grammarText =
            "S = {C ','}*\n" +
            "\n" +
            "C = 'c'\n";
        Grammar grammar = fromIggyGrammar(grammarText);

        assertEquals(grammarText, grammar.toString());
    }

    @Test
    public void test4() {
        String grammarText =
            "S = {C ',' ';' B}*\n" +
            "\n" +
            "B = 'b'\n" +
            "\n" +
            "C = 'c'\n";
        Grammar grammar = fromIggyGrammar(grammarText);

        assertEquals(grammarText, grammar.toString());
    }

    @Test
    public void test5() {
        String grammarText =
            "E = left (E '*' E | E '/' E)\n" +
            "  > left (E '+' E | E '-' E)\n";
        Grammar grammar = fromIggyGrammar(grammarText);

        assertEquals(grammarText, grammar.toString());
    }

    @Test
    public void test6() {
        String grammarText =
            "E =       '-' E\n" +
            "  > right E '^' E\n" +
            "  > left E '*' E\n" +
            "  > left E '+' E\n" +
            "  |      'a'";
        Grammar grammar = fromIggyGrammar(grammarText);
        System.out.println(grammar);
    }

}
