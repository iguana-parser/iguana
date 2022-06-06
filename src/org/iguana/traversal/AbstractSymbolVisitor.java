package org.iguana.traversal;

import org.iguana.grammar.symbol.*;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractSymbolVisitor<T> implements ISymbolVisitor<T> {

    public abstract T combine(Symbol symbol, List<T> values);

    public T visitChildren(Symbol symbol) {
        List<T> result = symbol.getChildren().stream().map(s -> s.accept(this)).collect(Collectors.toList());
        return combine(symbol, result);
    }

    @Override
    public T visit(Align align) {
        return visitChildren(align);
    }

    @Override
    public T visit(Block block) {
        return visitChildren(block);
    }

    @Override
    public T visit(Code code) {
        return visitChildren(code);
    }

    @Override
    public T visit(Conditional conditional) {
        return visitChildren(conditional);
    }

    @Override
    public T visit(IfThen ifThen) {
        return visitChildren(ifThen);
    }

    @Override
    public T visit(IfThenElse ifThenElse) {
        return visitChildren(ifThenElse);
    }

    @Override
    public T visit(Ignore ignore) {
        return visitChildren(ignore);
    }

    @Override
    public T visit(Offside offside) {
        return visitChildren(offside);
    }

    @Override
    public T visit(While whileSymbol) {
        return visitChildren(whileSymbol);
    }

    @Override
    public T visit(Alt alt) {
        return visitChildren(alt);
    }

    @Override
    public T visit(Opt opt) {
        return visitChildren(opt);
    }

    @Override
    public T visit(Plus plus) {
        return visitChildren(plus);
    }

    @Override
    public T visit(Group group) {
        return visitChildren(group);
    }

    @Override
    public T visit(Star star) {
        return visitChildren(star);
    }

}
