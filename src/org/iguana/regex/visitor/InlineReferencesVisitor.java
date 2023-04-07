package org.iguana.regex.visitor;

import org.iguana.regex.EOF;
import org.iguana.regex.NewLine;
import org.iguana.regex.RegularExpression;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InlineReferencesVisitor implements RegularExpressionVisitor<RegularExpression> {

    private final Map<String, RegularExpression> definitions;

    public InlineReferencesVisitor(Map<String, RegularExpression> definitions) {
        this.definitions = definitions;
    }

    @Override
    public RegularExpression visit(org.iguana.regex.Char c) {
        return c;
    }

    @Override
    public RegularExpression visit(org.iguana.regex.CharRange r) {
        return r;
    }

    @Override
    public RegularExpression visit(EOF eof) {
        return eof;
    }

    @Override
    public RegularExpression visit(NewLine newLine) {
        return newLine;
    }

    @Override
    public RegularExpression visit(org.iguana.regex.Epsilon e) {
        return e;
    }

    @Override
    public RegularExpression visit(org.iguana.regex.Star s) {
        RegularExpression newSymbol = s.getSymbol().accept(this);
        return s.copy().setSymbol(newSymbol).build();
    }

    @Override
    public RegularExpression visit(org.iguana.regex.Plus p) {
        RegularExpression newSymbol = p.getSymbol().accept(this);
        return p.copy().setSymbol(newSymbol).build();
    }

    @Override
    public RegularExpression visit(org.iguana.regex.Opt o) {
        RegularExpression newSymbol = o.getSymbol().accept(this);
        return o.copy().setSymbol(newSymbol).build();
    }

    @Override
    public <E extends RegularExpression> RegularExpression visit(org.iguana.regex.Alt<E> alt) {
        List<RegularExpression> newSymbols = alt.getSymbols().stream().map(symbol -> symbol.accept(this))
            .collect(Collectors.toList());
        return alt.copy().setSymbols((List<E>) newSymbols).build();
    }

    @Override
    public <E extends RegularExpression> RegularExpression visit(org.iguana.regex.Seq<E> seq) {
        List<RegularExpression> newSymbols = seq.getSymbols().stream().map(symbol -> symbol.accept(this))
            .collect(Collectors.toList());
        return seq.copy().setSymbols((List<E>) newSymbols).build();
    }

    @Override
    public RegularExpression visit(org.iguana.regex.Reference ref) {
        RegularExpression regularExpression = definitions.get(ref.getName());
        if (regularExpression != null) {
            return regularExpression;
        } else {
            return ref;
        }
    }
}
