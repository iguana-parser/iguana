package org.iguana.grammar.transformation;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.*;
import org.iguana.traversal.AbstractSymbolVisitor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CollectUsedRegularExpressionIdentifiers extends AbstractSymbolVisitor<Void> {

    private final Grammar grammar;
    private final Set<String> usedReferences = new HashSet<>();
    private final Set<String> regularExpressionReferences;

    public CollectUsedRegularExpressionIdentifiers(Grammar grammar) {
        this.grammar = grammar;
        regularExpressionReferences = grammar.getRegularExpressions().keySet();
    }

    public Set<String> collect() {
        for (Rule rule : grammar.getRules()) {
            for (PriorityLevel priorityLevel : rule.getPriorityLevels()) {
                for (Alternative alternative : priorityLevel.getAlternatives()) {
                    for (Sequence seq : alternative.seqs()) {
                        for (Symbol symbol : seq.getSymbols()) {
                            symbol.accept(this);
                        }
                    }
                }
            }
        }
        return usedReferences;
    }

    @Override
    public Void visit(Identifier identifier) {
        if (regularExpressionReferences.contains(identifier.getName())) {
            usedReferences.add(identifier.getName());
        }
        return null;
    }

    @Override
    public Void combine(Symbol symbol, List<Void> values) {
        return null;
    }

    @Override
    public Void visit(Nonterminal nonterminal) {
        return null;
    }

    @Override
    public Void visit(Terminal terminal) {
        return null;
    }

    @Override
    public Void visit(Return returnSymbol) {
        return null;
    }

    @Override
    public Void visit(Start start) {
        return null;
    }
}
