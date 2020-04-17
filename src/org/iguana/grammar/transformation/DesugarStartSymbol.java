package org.iguana.grammar.transformation;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.slot.NonterminalNodeType;
import org.iguana.grammar.symbol.*;

public class DesugarStartSymbol implements GrammarTransformation {

    @Override
    public Grammar transform(Grammar grammar) {
        Start startSymbol = grammar.getStartSymbol();
        if (startSymbol == null) return grammar;

        Grammar.Builder builder = new Grammar.Builder(grammar);
        Symbol layout = grammar.getLayout();

        Nonterminal startNonterminal = Nonterminal.builder(startSymbol.getName()).setNodeType(NonterminalNodeType.Start).build();

        Rule startRule;
        if (layout != null)
            startRule = Rule.withHead(startNonterminal)
                    .addSymbol(layout).addSymbol(startSymbol.getNonterminal()).addSymbol(layout)
                    .setRecursion(Recursion.NON_REC)
                    .setAssociativity(Associativity.UNDEFINED)
                    .setPrecedence(-1)
                    .setPrecedenceLevel(PrecedenceLevel.getFirstAndDone())
                    .setDefinition(startSymbol).build();
        else
            startRule = Rule.withHead(startNonterminal)
                    .addSymbol(startSymbol.getNonterminal())
                    .setRecursion(Recursion.NON_REC)
                    .setAssociativity(Associativity.UNDEFINED)
                    .setPrecedence(-1)
                    .setPrecedenceLevel(PrecedenceLevel.getFirstAndDone())
                    .setDefinition(startSymbol)
                    .build();

        builder.addRule(startRule);
        return builder.build();
    }
}
