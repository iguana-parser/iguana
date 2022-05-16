package org.iguana.grammar.transformation;

import org.iguana.grammar.runtime.PrecedenceLevel;
import org.iguana.grammar.runtime.Recursion;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.runtime.RuntimeRule;
import org.iguana.grammar.slot.NonterminalNodeType;
import org.iguana.grammar.symbol.Associativity;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Start;

public class DesugarStartSymbol implements GrammarTransformation {

    private final String startSymbol;

    public DesugarStartSymbol(String startSymbol) {
        this.startSymbol = startSymbol;
    }

    @Override
    public RuntimeGrammar transform(RuntimeGrammar grammar) {
        if (startSymbol == null) return grammar;

        RuntimeGrammar.Builder builder = new RuntimeGrammar.Builder(grammar);

        Nonterminal startNonterminal = new Nonterminal.Builder(startSymbol).setNodeType(NonterminalNodeType.Start).build();

        RuntimeRule startRule = RuntimeRule.withHead(startNonterminal)
            .addSymbol(Nonterminal.withName(startSymbol))
            .setRecursion(Recursion.NON_REC)
            .setAssociativity(Associativity.UNDEFINED)
            .setPrecedence(-1)
            .setPrecedenceLevel(PrecedenceLevel.getFirstAndDone())
            .setDefinition(Start.from(startSymbol))
            .build();

        builder.addRule(startRule);
        builder.setGlobals(grammar.getGlobals());
        return builder.build();
    }
}
