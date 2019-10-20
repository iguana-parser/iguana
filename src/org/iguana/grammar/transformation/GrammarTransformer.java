package org.iguana.grammar.transformation;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.*;
import org.iguana.traversal.ISymbolVisitor;
import sun.jvm.hotspot.debugger.cdbg.Sym;

import java.util.ArrayList;
import java.util.List;

public class GrammarTransformer<S extends Symbol, R extends Symbol> {

    private SymbolTransformation symbolTransformation;

    public GrammarTransformer(SymbolTransformation symbolTransformation) {
        this.symbolTransformation = symbolTransformation;
    }

    public Grammar transform(Grammar grammar) {
        Grammar.Builder grammarBuilder = new Grammar.Builder();
        for (Rule rule : grammar.getRules()) {
            Rule.Builder ruleBuilder = new Rule.Builder();
            for (PriorityLevel priorityLevel : rule.getPriorityLevels()) {
                PriorityLevel.Builder priorityLevelBuilder = new PriorityLevel.Builder();
                for (Alternative alt : priorityLevel.getAlternatives()) {
                    Alternative.Builder altBuilder = new Alternative.Builder();
                    for (Sequence seq : alt.seqs()) {
                        Sequence.Builder seqBuilder = new Sequence.Builder();
                        for (Symbol symbol : seq.getSymbols()) {
                        }
                    }
                }
            }
            grammarBuilder.addRule(rule);
        }
        return grammarBuilder.build();
    }

    private Symbol transform(Symbol symbol) {
        boolean childrenChanged = false;

        List<Symbol> transformedChildren = new ArrayList<>();
        for (Symbol child : symbol.getChildren()) {
            Symbol transformedChild = transform(child);
            if (!transformedChild.equals(child)) {
                childrenChanged = true;
            }
            transformedChildren.add(transformedChild);
        }
        return null;
    }

    class SymbolVisitor implements ISymbolVisitor<Symbol> {

        @Override
        public Symbol visit(Align align) {
            Symbol symbolResult = align.getSymbol().accept(this);
            if (symbolTransformation.isDefinedAt(align)) {
                Symbol apply = symbolTransformation.apply(align);
            }

            return null;
        }

        @Override
        public Symbol visit(Block symbol) {
            return null;
        }

        @Override
        public Symbol visit(Code symbol) {
            return null;
        }

        @Override
        public Symbol visit(Conditional symbol) {
            return null;
        }

        @Override
        public Symbol visit(IfThen symbol) {
            return null;
        }

        @Override
        public Symbol visit(IfThenElse symbol) {
            return null;
        }

        @Override
        public Symbol visit(Ignore symbol) {
            return null;
        }

        @Override
        public Symbol visit(Nonterminal symbol) {
            return null;
        }

        @Override
        public Symbol visit(Offside symbol) {
            return null;
        }

        @Override
        public Symbol visit(Terminal symbol) {
            return null;
        }

        @Override
        public Symbol visit(While symbol) {
            return null;
        }

        @Override
        public Symbol visit(Return symbol) {
            return null;
        }

        @Override
        public <E extends Symbol> Symbol visit(Alt<E> symbol) {
            return null;
        }

        @Override
        public Symbol visit(Opt symbol) {
            return null;
        }

        @Override
        public Symbol visit(Plus symbol) {
            return null;
        }

        @Override
        public Symbol visit(Group symbol) {
            return null;
        }

        @Override
        public Symbol visit(Star symbol) {
            return null;
        }

        @Override
        public Symbol visit(Start start) {
            return null;
        }
    }
}


