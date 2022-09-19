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

    private final Start start;

    public DesugarStartSymbol(Start start) {
        this.start = start;
    }

    @Override
    public RuntimeGrammar transform(RuntimeGrammar grammar) {
        RuntimeGrammar.Builder builder = new RuntimeGrammar.Builder(grammar);

        Nonterminal startNonterminal = new Nonterminal.Builder(start.getName()).setNodeType(NonterminalNodeType.Start).build();

        RuntimeRule startRule = RuntimeRule.withHead(startNonterminal)
            // TODO: For now we use the label top, but would be good to allow configuring it.
            .addSymbol(new Nonterminal.Builder(start.getStartSymbol()).setLabel("top").build())
            .setRecursion(Recursion.NON_REC)
            .setAssociativity(Associativity.UNDEFINED)
            .setPrecedence(-1)
            .setPrecedenceLevel(PrecedenceLevel.getFirstAndDone())
            .setDefinition(start)
            .build();

        builder.addRule(startRule);
        builder.setGlobals(grammar.getGlobals());
        builder.setStartSymbol(start);
        return builder.build();
    }
}
