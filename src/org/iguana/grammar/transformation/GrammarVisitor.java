package org.iguana.grammar.transformation;

import org.iguana.grammar.symbol.*;
import org.iguana.traversal.SymbolToSymbolVisitor;

import java.util.ArrayList;
import java.util.List;

public class GrammarVisitor {

    private final SymbolToSymbolVisitor symbolToSymbolVisitor;

    public GrammarVisitor(SymbolToSymbolVisitor symbolToSymbolVisitor) {
        this.symbolToSymbolVisitor = symbolToSymbolVisitor;
    }

    public List<Rule> transform(List<Rule> rules) {
        List<Rule> result = new ArrayList<>();
        for (Rule rule : rules) {
            result.add(get(rule, transform(rule)));
        }
        return result;
    }

    private Rule transform(Rule rule) {
        Rule.Builder ruleBuilder = rule.copy();
        ruleBuilder.clearPriorityLevels();
        for (PriorityLevel priorityLevel : rule.getPriorityLevels()) {
            PriorityLevel.Builder priorityLevelBuilder = new PriorityLevel.Builder();
            for (Alternative alternative : priorityLevel.getAlternatives()) {
                Alternative.Builder alternativeBuilder = alternative.copy();
                alternativeBuilder.clearSequences();
                for (Sequence sequence : alternative.seqs()) {
                    Sequence.Builder sequenceBuilder = sequence.copy();
                    sequenceBuilder.clearSymbols();
                    for (Symbol symbol : sequence.getSymbols()) {
                        Symbol newSymbol = symbol.accept(symbolToSymbolVisitor);
                        sequenceBuilder.addSymbol(get(symbol, newSymbol));
                    }
                    alternativeBuilder.addSequence(get(sequence, sequenceBuilder.build()));
                }
                priorityLevelBuilder.addAlternative(get(alternative, alternativeBuilder.build()));
            }
            ruleBuilder.addPriorityLevel(get(priorityLevel, priorityLevelBuilder.build()));
        }
        return ruleBuilder.build();
    }

    private <T> T get(T original, T copy) {
        if (original.equals(copy)) return original;
        return copy;
    }
}
