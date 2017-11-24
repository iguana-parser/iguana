package org.iguana.grammar.iggy;

import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Start;
import org.iguana.grammar.transformation.DesugarPrecedenceAndAssociativity;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.grammar.transformation.LayoutWeaver;
import org.iguana.parser.Iguana;
import org.iguana.parser.ParseResult;

public class IggyParser {

    public static Grammar getGrammar(String s) {
        return getGrammar(Input.fromString(s));
    }

    public static Grammar getGrammar(Input input) {

        ParseResult result = Iguana.parse(input, GrammarGraph.from(iggyGrammar(), input), start);

        if (result.isParseError())
            throw new RuntimeException(result.asParseError().toString());

        return null;
    }

    private static Start start = Start.from("Definition");

    private static Grammar iggyGrammar() {
        Grammar g = Grammar.load(IggyParser.class.getResourceAsStream("/IggyGrammar"));
        DesugarPrecedenceAndAssociativity precedenceAndAssociativity = new DesugarPrecedenceAndAssociativity();
        precedenceAndAssociativity.setOP2();

        g = new EBNFToBNF().transform(g);
        g = precedenceAndAssociativity.transform(g);
        g = new LayoutWeaver().transform(g);
        g.getStartSymbol(Nonterminal.withName("Definition"));
        return g;
    }

}
