package org.iguana.grammar.transformation;

import org.iguana.grammar.runtime.PrecedenceLevel;
import org.iguana.grammar.runtime.Recursion;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.runtime.RuntimeRule;
import org.iguana.grammar.slot.NonterminalNodeType;
import org.iguana.grammar.symbol.*;

public class DesugarStartSymbol implements GrammarTransformation {

    @Override
    public RuntimeGrammar transform(RuntimeGrammar grammar) {
        Start startSymbol = grammar.getStartSymbol();
        if (startSymbol == null) return grammar;

        RuntimeGrammar.Builder builder = new RuntimeGrammar.Builder(grammar);
        Symbol layout = grammar.getLayout();

        Nonterminal startNonterminal = new Nonterminal.Builder(startSymbol.getName()).setNodeType(NonterminalNodeType.Start).build();

        RuntimeRule startRule;
        if (layout != null)
            startRule = RuntimeRule.withHead(startNonterminal)
                    .addSymbol(layout).addSymbol(Nonterminal.withName(startSymbol.getName())).addSymbol(layout)
                    .setRecursion(Recursion.NON_REC)
                    .setAssociativity(Associativity.UNDEFINED)
                    .setPrecedence(-1)
                    .setPrecedenceLevel(PrecedenceLevel.getFirstAndDone())
                    .setDefinition(startSymbol).build();
        else
            startRule = RuntimeRule.withHead(startNonterminal)
                    .addSymbol(Nonterminal.withName(startSymbol.getName()))
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
