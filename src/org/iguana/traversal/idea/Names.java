package org.iguana.traversal.idea;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.*;
import org.iguana.grammar.transformation.GrammarTransformation;
import org.iguana.regex.*;
import org.iguana.traversal.ISymbolVisitor;

import java.util.*;

/**
 * Created by Anastasia Izmaylova on 20/10/15.
 */

public class Names implements GrammarTransformation {


    @Override
    public Grammar transform(Grammar grammar) {
        List<Rule> rules = new ArrayList<>();

        NameVisitor visitor = new NameVisitor(rules, (Nonterminal) grammar.getLayout());

        for (Rule rule : grammar.getRules())
            rules.add(visitor.visitRule(rule));

        return Grammar.builder()
                .addRules(rules)
                .addEBNFl(grammar.getEBNFLefts())
                .addEBNFr(grammar.getEBNFRights())
                .setLayout(grammar.getLayout())
                .build();
    }

    private static class NameVisitor implements ISymbolVisitor<Symbol> {

        final List<Rule> rules;
        private Set<Nonterminal> heads = new HashSet<>();

        private final Nonterminal layout;

        NameVisitor(List<Rule> rules, Nonterminal layout) {
            this.rules = rules;
            this.layout = layout;
        }

        public Rule visitRule(Rule rule) {
            Rule.Builder builder = rule.copyBuilder();
            List<Symbol> symbols = new ArrayList<>();
            for (Symbol symbol : rule.getBody())
                symbols.add(visitSymbol(symbol));
            return builder.setSymbols(symbols).build();
        }

        public Symbol visitSymbol(Symbol symbol) {
            SymbolBuilder<? extends Symbol> builder = symbol.accept(this).copyBuilder();
            builder.addPreConditions(symbol.getPreConditions());
            builder.addPostConditions(symbol.getPostConditions());
            builder.setLabel(symbol.getLabel());
            return builder.build();
        }

        @Override
        public Symbol visit(Align symbol) {
            return Align.align(visitSymbol(symbol.getSymbol()));
        }

        @Override
        public Symbol visit(Block symbol) {
            return Block.block(Arrays.stream(symbol.getSymbols()).map(s -> visitSymbol(s)).toArray(Symbol[]::new));
        }

        @Override
        public Symbol visit(org.iguana.grammar.symbol.Character symbol) {
            return symbol;
        }

        @Override
        public Symbol visit(CharacterRange symbol) {
            return symbol;
        }

        @Override
        public Symbol visit(Code symbol) {
            return Code.code(visitSymbol(symbol.getSymbol()), symbol.getStatements());
        }

        @Override
        public Symbol visit(Conditional symbol) {
            return Conditional.when(visitSymbol(symbol.getSymbol()), symbol.getExpression());
        }

        @Override
        public Symbol visit(EOF symbol) {
            return symbol;
        }

        @Override
        public Symbol visit(Epsilon symbol) {
            return symbol;
        }

        @Override
        public Symbol visit(IfThen symbol) {
            return IfThen.ifThen(symbol.getExpression(), visitSymbol(symbol.getThenPart()));
        }

        @Override
        public Symbol visit(IfThenElse symbol) {
            return IfThenElse.ifThenElse(symbol.getExpression(), visitSymbol(symbol.getThenPart()), visitSymbol(symbol.getElsePart()));
        }

        @Override
        public Symbol visit(Ignore symbol) {
            return Ignore.ignore(visitSymbol(symbol.getSymbol()));
        }

        @Override
        public Symbol visit(Nonterminal symbol) {
            Map<String, Object> attributes = symbol.getAttributes();
            if (attributes.containsKey("name")) {
                int value = (Integer) attributes.get("name");
                Nonterminal sym = symbol;
                switch (value) {
                    case 0:
                        sym = Nonterminal.withName(symbol.getName() + "$Declaration");
                        if (!heads.contains(sym)) {
                            heads.add(sym);
                            rules.add(Rule.withHead(sym).addSymbol(symbol)
                                        .setLayout(layout).setLayoutStrategy(LayoutStrategy.INHERITED)
                                        .setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED)
                                        .setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.getFirstAndDone())
                                        .build());
                        }
                        return sym;
                    case 1:
                        sym = Nonterminal.withName(symbol.getName() + "$Reference");
                        if (!heads.contains(sym)) {
                            heads.add(sym);
                            rules.add(Rule.withHead(sym).addSymbol(symbol)
                                        .setLayout(layout).setLayoutStrategy(LayoutStrategy.INHERITED)
                                        .setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED)
                                        .setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.getFirstAndDone())
                                        .build());
                        }
                        return sym;
                    default:
                        break;
                }
            }
            return symbol;
        }

        @Override
        public Symbol visit(Offside symbol) {
            return Offside.offside(visitSymbol(symbol.getSymbol()));
        }

        @Override
        public Symbol visit(Terminal symbol) {
            return symbol;
        }

        @Override
        public Symbol visit(While symbol) {
            return While.whileLoop(symbol.getExpression(), visitSymbol(symbol.getBody()));
        }

        @Override
        public Symbol visit(Return symbol) {
            return symbol;
        }

        @Override
        public <E extends Symbol> Symbol visit(Alt<E> symbol) {
            return Alt.from(symbol.getSymbols().stream().map(s -> visitSymbol(s)).toArray(Symbol[]::new));
        }

        @Override
        public Symbol visit(Opt symbol) {
            return Opt.from(visitSymbol(symbol.getSymbol()));
        }

        @Override
        public Symbol visit(Plus symbol) {
            return Plus.builder(visitSymbol(symbol.getSymbol())).addSeparators(symbol.getSeparators()).build();
        }

        @Override
        public <E extends Symbol> Symbol visit(Sequence<E> symbol) {
            return Sequence.from(symbol.getSymbols().stream().map(s -> visitSymbol(s)).toArray(Symbol[]::new));
        }

        @Override
        public Symbol visit(Star symbol) {
            return Star.builder(visitSymbol(symbol.getSymbol())).addSeparators(symbol.getSeparators()).build();
        }
    }
}
