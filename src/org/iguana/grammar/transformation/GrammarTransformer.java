package org.iguana.grammar.transformation;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.*;

import java.util.ArrayList;
import java.util.List;

public class GrammarTransformer {

    public static Grammar transform(Grammar grammar, SymbolTransformation symbolTransformation) {
        Grammar.Builder grammarBuilder = new Grammar.Builder();
        for (Rule rule : grammar.getRules()) {
            Rule.Builder ruleBuilder = new Rule.Builder(rule.getHead());
            for (PriorityLevel priorityLevel : rule.getPriorityLevels()) {
                PriorityLevel.Builder priorityLevelBuilder = new PriorityLevel.Builder();
                for (Alternative alt : priorityLevel.getAlternatives()) {
                    Alternative.Builder altBuilder = new Alternative.Builder();
                    for (Sequence sequence : alt.seqs()) {
                        Sequence.Builder seqBuilder = new Sequence.Builder();
                        for (Symbol symbol : sequence.getSymbols()) {
                            seqBuilder.addSymbol(transform(symbol, symbolTransformation));
                        }
                        altBuilder.addSequence(seqBuilder.build());
                    }
                    priorityLevelBuilder.addAlternative(altBuilder.build());
                }
                ruleBuilder.addPriorityLevel(priorityLevelBuilder.build());
            }
            grammarBuilder.addRule(ruleBuilder.build());
        }
        return grammarBuilder
                .setStartSymbol(grammar.getStartSymbol())
                .addTerminals(grammar.getTerminals())
                .setLayout(grammar.getLayout())
                .build();
    }

    private static Symbol transform(Symbol symbol, SymbolTransformation symbolTransformation) {
        List<Symbol> transformedChildren = new ArrayList<>();
        for (Symbol child : symbol.getChildren()) {
            transformedChildren.add(transform(child, symbolTransformation));
        }

        if (symbolTransformation.isDefinedAt(symbol)) {
            Symbol transformedSymbol = symbolTransformation.apply(symbol);
            return transformedSymbol.copy().setChildren(transformedChildren).build();
        } else {
            return symbol.copy().setChildren(transformedChildren).build();
        }
    }

}


