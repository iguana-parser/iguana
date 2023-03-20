package org.iguana.grammar.transformation;

import org.iguana.grammar.symbol.Alternative;
import org.iguana.grammar.symbol.Associativity;
import org.iguana.grammar.symbol.PriorityLevel;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.symbol.Sequence;

import java.util.ArrayList;
import java.util.List;

public class TopLevelPrecedenceNonterminals {

    public static List<Rule> addTopLevelPrecedenceRules(List<Rule> rules) {
        List<Rule> newRules = new ArrayList<>();
        for (Rule rule : rules) {
            if (isPrecedenceRule(rule)) {
                Rule newRule = new Rule.Builder(rule.getHead().copy().setName("$_" + rule.getHead().getName()).build())
                    .addPriorityLevel(PriorityLevel.from(Alternative.from(Sequence.from(
                        rule.getHead().copy().setLabel("child").build()
                    )))).build();
                newRules.add(newRule);
            }
        }

        return newRules;
    }

    /**
     * A rule is a precedence rule if one of these conditions is true:
     * - has more than one priority level
     * - has one priority level and there is associativity defined for the alternative or individual sequences
     */
    private static boolean isPrecedenceRule(Rule rule) {
        int numPriorityLevels = rule.getPriorityLevels().size();
        if (numPriorityLevels == 0) return false;
        if (numPriorityLevels > 1) return true;
        List<Alternative> alternatives = rule.getPriorityLevels().get(0).getAlternatives();
        if (alternatives.stream().anyMatch(alternative -> alternative.getAssociativity() != Associativity.UNDEFINED))
            return true;
        return alternatives.stream().flatMap(alternative -> alternative.seqs().stream())
                                    .anyMatch(sequence -> sequence.getAssociativity() != Associativity.UNDEFINED);
    }
}
