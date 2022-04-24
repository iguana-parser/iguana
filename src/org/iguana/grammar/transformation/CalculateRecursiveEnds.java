package org.iguana.grammar.transformation;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.*;
import org.iguana.traversal.ISymbolVisitor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CalculateRecursiveEnds {

    private final Map<String, Set<String>> leftEnds = new HashMap<>();
    private final Map<String, Set<String>> rightEnds = new HashMap<>();

    public void calculate(Grammar grammar) {
        for (Rule rule : grammar.getRules()) {
            String head = rule.getHead().getName();
            leftEnds.computeIfAbsent(head, k -> new HashSet<>());
            rightEnds.computeIfAbsent(head, k -> new HashSet<>());
            for (PriorityLevel priorityLevel : rule.getPriorityLevels()) {
                for (Alternative alternative : priorityLevel.getAlternatives()) {
                    for (Sequence seq : alternative.seqs()) {
                        if (!seq.isEmpty()) {
                            LeftRecursive leftRecursive = new LeftRecursive();
                            String leftEnd = seq.getSymbols().get(0).accept(leftRecursive);
                            if (leftEnd != null) {
                                leftEnds.get(head).add(leftEnd);
                            }
                            RightRecursive rightRecursive = new RightRecursive();
                            String rightEnd = seq.getSymbols().get(seq.getSymbols().size() - 1).accept(rightRecursive);
                            if (rightEnd != null) {
                                rightEnds.get(head).add(rightEnd);
                            }
                        }
                    }
                }
            }
        }

        fixedPoint(leftEnds);
        fixedPoint(rightEnds);
    }

    public Map<String, Set<String>> getLeftEnds() {
        return leftEnds;
    }

    public Map<String, Set<String>> getRightEnds() {
        return rightEnds;
    }

    private static void fixedPoint(Map<String, Set<String>> map) {
        boolean changed = true;
        while (changed) {
            changed = false;
            for (String head : map.keySet()) {
                Set<String> ends = map.get(head);
                int size = ends.size();
                Set<String> delta = new HashSet<>();
                for (String end : ends) {
                    Set<String> rights = map.get(end);
                    if (rights != null) {
                        for (String right : rights) {
                            if (!right.equals(head))
                                delta.add(right);
                        }
                    }
                }
                ends.addAll(delta);
                if (ends.size() != size)
                    changed = true;
            }
        }
    }

    private static abstract class Recursive implements ISymbolVisitor<String> {
        @Override
        public String visit(Align align) {
            return align.getSymbol().accept(this);
        }

        @Override
        public String visit(Code code) {
            return code.getSymbol().accept(this);
        }

        @Override
        public String visit(Conditional conditional) {
            return conditional.getSymbol().accept(this);
        }

        @Override
        public String visit(IfThen ifThen) {
            return ifThen.getThenPart().accept(this);
        }

        @Override
        public String visit(Ignore ignore) {
            return ignore.getSymbol().accept(this);
        }

        @Override
        public String visit(Nonterminal nonterminal) {
            return nonterminal.getName();
        }

        @Override
        public String visit(Offside offside) {
            return offside.getSymbol().accept(this);
        }

        @Override
        public String visit(Terminal terminal) {
            return null;
        }

        @Override
        public String visit(While whileSymbol) {
            return whileSymbol.getBody().accept(this);
        }

        @Override
        public String visit(Return returnSymbol) {
            return null;
        }

        @Override
        public String visit(Opt opt) {
            return opt.getSymbol().accept(this);
        }

        @Override
        public String visit(Plus plus) {
            return plus.getSymbol().accept(this);
        }


        @Override
        public String visit(Star star) {
            return star.getSymbol().accept(this);
        }

        @Override
        public String visit(Start start) {
            throw new IllegalStateException("EBNF nodes should be removed before this stage");
        }

        @Override
        public String visit(Identifier identifier) {
            return identifier.getName();
        }
    }

    private static class LeftRecursive extends Recursive {
        @Override
        public String visit(Block block) {
            return block.getSymbols()[0].accept(this);
        }

        @Override
        public String visit(IfThenElse ifThenElse) {
            return ifThenElse.getThenPart().accept(this);
        }

        @Override
        public String visit(Alt alt) {
            if (alt.getSymbols().isEmpty()) return null;
            return alt.getSymbols().get(0).accept(this);
        }

        @Override
        public String visit(Group group) {
            if (group.getSymbols().isEmpty()) return null;
            return group.getSymbols().get(0).accept(this);
        }
    }

    private static class RightRecursive extends Recursive {
        @Override
        public String visit(Block block) {
            return block.getSymbols()[block.getSymbols().length - 1].accept(this);
        }

        @Override
        public String visit(IfThenElse ifThenElse) {
            return ifThenElse.getElsePart().accept(this);
        }

        @Override
        public String visit(Alt alt) {
            if (alt.getSymbols().isEmpty()) return null;
            return alt.getSymbols().get(alt.getSymbols().size() - 1).accept(this);
        }

        @Override
        public String visit(Group group) {
            if (group.getSymbols().isEmpty()) return null;
            return group.getSymbols().get(group.getSymbols().size() - 1).accept(this);
        }
    }
}
