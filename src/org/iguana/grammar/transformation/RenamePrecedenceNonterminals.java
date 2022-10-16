package org.iguana.grammar.transformation;

import org.iguana.grammar.symbol.*;
import org.iguana.traversal.SymbolToSymbolVisitor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RenamePrecedenceNonterminals {

    public List<Rule> transformPrecedenceRule(List<Rule> rules) {
        Set<String> renamingMap = new HashSet<>();
        for (Rule rule : rules) {
            if (isPrecedenceRule(rule)) {
                renamingMap.add(rule.getHead().getName());
            }
        }
        return null;
    }

    static class RenamingVisitor implements SymbolToSymbolVisitor {

        private final Set<String> renamingMap;

        RenamingVisitor(Set<String> renamingMap) {
            this.renamingMap = renamingMap;
        }

        @Override
        public Symbol visit(Nonterminal symbol) {
            if (renamingMap.contains(symbol.getName())) {
                return symbol.copy().setName("$" + symbol.getName()).build();
            }
            return symbol;
        }
    }

    /**
     * A rule is a precedence rule if one of these conditions is true:
     * - has more than one priority level
     * - has one priority level and there is associativity defined for the alternative or individual sequences
     */
    private boolean isPrecedenceRule(Rule rule) {
        int numPriorityLevels = rule.getPriorityLevels().size();
        if (numPriorityLevels == 0) return false;
        if (numPriorityLevels > 1) return true;
        List<Alternative> alternatives = rule.getPriorityLevels().get(0).getAlternatives();
        if (alternatives.stream().anyMatch(alternative -> alternative.getAssociativity() != Associativity.UNDEFINED))
            return true;
        return alternatives.stream().flatMap(alternative -> alternative.seqs().stream()).anyMatch(sequence -> sequence.getAssociativity() != Associativity.UNDEFINED);
    }
}
