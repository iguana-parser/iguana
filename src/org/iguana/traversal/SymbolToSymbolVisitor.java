package org.iguana.traversal;

import org.iguana.grammar.condition.Condition;
import org.iguana.grammar.condition.DataDependentCondition;
import org.iguana.grammar.condition.PositionalCondition;
import org.iguana.grammar.condition.RegularExpressionCondition;
import org.iguana.grammar.symbol.Align;
import org.iguana.grammar.symbol.Alt;
import org.iguana.grammar.symbol.Block;
import org.iguana.grammar.symbol.Code;
import org.iguana.grammar.symbol.CodeHolder;
import org.iguana.grammar.symbol.Conditional;
import org.iguana.grammar.symbol.Error;
import org.iguana.grammar.symbol.Group;
import org.iguana.grammar.symbol.Identifier;
import org.iguana.grammar.symbol.IfThen;
import org.iguana.grammar.symbol.IfThenElse;
import org.iguana.grammar.symbol.Ignore;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Offside;
import org.iguana.grammar.symbol.Opt;
import org.iguana.grammar.symbol.Plus;
import org.iguana.grammar.symbol.Return;
import org.iguana.grammar.symbol.Star;
import org.iguana.grammar.symbol.Start;
import org.iguana.grammar.symbol.Symbol;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.grammar.symbol.While;
import org.iguana.regex.Char;
import org.iguana.regex.CharRange;
import org.iguana.regex.EOF;
import org.iguana.regex.Epsilon;
import org.iguana.regex.Reference;
import org.iguana.regex.RegularExpression;
import org.iguana.regex.visitor.RegularExpressionVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public interface SymbolToSymbolVisitor
    extends ISymbolVisitor<Symbol>, IConditionVisitor<Condition>, RegularExpressionVisitor<RegularExpression> {

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

    @Override
    default Symbol visit(Error error) {
        return error;
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

    default List<Condition> visitPreConditions(Symbol symbol) {
        List<Condition> preConditions = new ArrayList<>();
        for (Condition condition : symbol.getPreConditions()) {
            preConditions.add(condition.accept(this));
        }
        return preConditions;
    }

    default List<Condition> visitPostConditions(Symbol symbol) {
        List<Condition> postConditions = new ArrayList<>();
        for (Condition condition : symbol.getPostConditions()) {
            postConditions.add(condition.accept(this));
        }
        return postConditions;
    }

    default RegularExpression visitRegularExpression(RegularExpression regex) {
        List<RegularExpression> newChildren = regex.getChildren().stream().map(r -> r.accept(this))
            .collect(Collectors.toList());
        RegularExpression newRegex = regex.copy().setChildren(newChildren).build();
        if (newRegex.equals(regex)) {
            return regex;
        } else {
            return newRegex;
        }
    }
}
