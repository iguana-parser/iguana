package org.iguana.traversal;

import org.iguana.grammar.condition.Condition;
import org.iguana.grammar.condition.DataDependentCondition;
import org.iguana.grammar.condition.PositionalCondition;
import org.iguana.grammar.condition.RegularExpressionCondition;
import org.iguana.grammar.symbol.Alt;
import org.iguana.grammar.symbol.Opt;
import org.iguana.grammar.symbol.Plus;
import org.iguana.grammar.symbol.Star;
import org.iguana.grammar.symbol.*;
import org.iguana.regex.*;
import org.iguana.regex.visitor.RegularExpressionVisitor;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface SymbolToSymbolVisitor extends ISymbolVisitor<Symbol>, IConditionVisitor<Condition>, RegularExpressionVisitor<RegularExpression> {

    @Override
    default Symbol visit(Align symbol) {
        return visitSymbol(symbol);
    }

    @Override
    default Symbol visit(Block symbol) {
        return visitSymbol(symbol);
    }

    @Override
    default Symbol visit(Code symbol) {
        return visitSymbol(symbol);
    }

    @Override
    default Symbol visit(Conditional symbol) {
        return visitSymbol(symbol);
    }

    @Override
    default Symbol visit(IfThen symbol) {
        return visitSymbol(symbol);
    }

    @Override
    default Symbol visit(IfThenElse symbol) {
        return visitSymbol(symbol);
    }

    @Override
    default Symbol visit(Ignore symbol) {
        return visitSymbol(symbol);
    }

    @Override
    default Symbol visit(Nonterminal symbol) {
        return visitSymbol(symbol);
    }

    @Override
    default Symbol visit(Offside symbol) {
        return visitSymbol(symbol);
    }

    @Override
    default Symbol visit(Terminal symbol) {
        return visitSymbol(symbol);
    }

    @Override
    default Symbol visit(While symbol) {
        return visitSymbol(symbol);
    }

    @Override
    default Symbol visit(Return symbol) {
        return visitSymbol(symbol);
    }

    @Override
    default Symbol visit(Alt symbol) {
        return visitSymbol(symbol);
    }

    @Override
    default Symbol visit(Opt symbol) {
        return visitSymbol(symbol);
    }

    @Override
    default Symbol visit(Plus symbol) {
        return visitSymbol(symbol);
    }

    @Override
    default Symbol visit(Group symbol) {
        return visitSymbol(symbol);
    }

    @Override
    default Symbol visit(Star symbol) {
        return visitSymbol(symbol);
    }

    @Override
    default Symbol visit(Start symbol) {
        return visitSymbol(symbol);
    }

    @Override
    default Symbol visit(Identifier symbol) {
        return symbol;
    }

    @Override
    default Condition visit(DataDependentCondition condition) {
        return condition;
    }

    @Override
    default Condition visit(PositionalCondition condition) {
        return condition;
    }

    @Override
    default Condition visit(RegularExpressionCondition condition) {
        RegularExpression newRegex = condition.getRegularExpression().accept(this);
        Condition newCondition = new RegularExpressionCondition(condition.getType(), newRegex);
        if (newCondition.equals(condition)) {
            return condition;
        } else {
            return newCondition;
        }
    }

    @Override
    default RegularExpression visit(Char c) {
        return c;
    }

    @Override
    default RegularExpression visit(CharRange r) {
        return r;
    }

    @Override
    default RegularExpression visit(EOF eof) {
        return eof;
    }

    @Override
    default RegularExpression visit(Epsilon e) {
        return e;
    }

    @Override
    default RegularExpression visit(org.iguana.regex.Star s) {
        return visitRegularExpression(s);
    }

    @Override
    default RegularExpression visit(org.iguana.regex.Plus p) {
        return visitRegularExpression(p);
    }

    @Override
    default RegularExpression visit(org.iguana.regex.Opt o) {
        return visitRegularExpression(o);
    }

    @Override
    default <E extends RegularExpression> RegularExpression visit(org.iguana.regex.Alt<E> alt) {
        return visitRegularExpression(alt);
    }

    @Override
    default <E extends RegularExpression> RegularExpression visit(org.iguana.regex.Seq<E> seq) {
        return visitRegularExpression(seq);
    }

    @Override
    default RegularExpression visit(Reference ref) {
        return ref;
    }

    @Override
    default Symbol visit(CodeHolder symbol) {
        return symbol;
    }

    default Symbol visitSymbol(Symbol symbol) {
        List<Symbol> newChildren = symbol.getChildren().stream().map(s -> s.accept(this)).collect(Collectors.toList());
        Symbol newSymbol = symbol.copy()
            .setChildren(newChildren)
            .setPreConditions(visitPreConditions(symbol))
            .setPostConditions(visitPostConditions(symbol))
            .build();

        if (newSymbol.equals(symbol)) {
            return symbol;
        } else {
            return newSymbol;
        }
    }

    default Set<Condition> visitPreConditions(Symbol symbol) {
        Set<Condition> preConditions = new LinkedHashSet<>();
        for (Condition condition : symbol.getPreConditions()) {
            preConditions.add(condition.accept(this));
        }
        return preConditions;
    }

    default Set<Condition> visitPostConditions(Symbol symbol) {
        Set<Condition> postConditions = new LinkedHashSet<>();
        for (Condition condition : symbol.getPostConditions()) {
            postConditions.add(condition.accept(this));
        }
        return postConditions;
    }

    default RegularExpression visitRegularExpression(RegularExpression regex) {
        List<RegularExpression> newChildren = regex.getChildren().stream().map(r -> r.accept(this)).collect(Collectors.toList());
        RegularExpression newRegex = regex.copy().setChildren(newChildren).build();
        if (newRegex.equals(regex)) {
            return regex;
        } else {
            return newRegex;
        }
    }
}
