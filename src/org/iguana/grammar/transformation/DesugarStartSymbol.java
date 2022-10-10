package org.iguana.grammar.transformation;

import org.iguana.grammar.runtime.PrecedenceLevel;
import org.iguana.grammar.runtime.Recursion;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.runtime.RuntimeRule;
import org.iguana.grammar.slot.NonterminalNodeType;
import org.iguana.grammar.symbol.Associativity;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Start;

import java.util.List;

public class DesugarStartSymbol implements GrammarTransformation {

    private final List<Start> starts;

    public DesugarStartSymbol(List<Start> starts) {
        this.starts = starts;
    }

    @Override
    public RuntimeGrammar transform(RuntimeGrammar grammar) {
        RuntimeGrammar.Builder builder = new RuntimeGrammar.Builder(grammar);

        for (Start start : starts) {
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
            builder.addStartSymbol(start);
        }

        builder.setGlobals(grammar.getGlobals());
        return builder.build();
    }
}
