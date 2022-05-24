package org.iguana.regex.visitor;

import org.iguana.regex.EOF;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InlineReferencesVisitor implements RegularExpressionVisitor<org.iguana.regex.RegularExpression> {

    private final Map<String, org.iguana.regex.RegularExpression> definitions;

    public InlineReferencesVisitor(Map<String, org.iguana.regex.RegularExpression> definitions) {
        this.definitions = definitions;
    }

    @Override
    public org.iguana.regex.RegularExpression visit(org.iguana.regex.Char c) {
        return c;
    }

    @Override
    public org.iguana.regex.RegularExpression visit(org.iguana.regex.CharRange r) {
        return r;
    }

    @Override
    public org.iguana.regex.RegularExpression visit(EOF eof) {
        return eof;
    }

    @Override
    public org.iguana.regex.RegularExpression visit(org.iguana.regex.Epsilon e) {
        return e;
    }

    @Override
    public org.iguana.regex.RegularExpression visit(org.iguana.regex.Star s) {
        org.iguana.regex.RegularExpression newSymbol = s.getSymbol().accept(this);
        return s.copy().setSymbol(newSymbol).build();
    }

    @Override
    public org.iguana.regex.RegularExpression visit(org.iguana.regex.Plus p) {
        org.iguana.regex.RegularExpression newSymbol = p.getSymbol().accept(this);
        return p.copy().setSymbol(newSymbol).build();
    }

    @Override
    public org.iguana.regex.RegularExpression visit(org.iguana.regex.Opt o) {
        org.iguana.regex.RegularExpression newSymbol = o.getSymbol().accept(this);
        return o.copy().setSymbol(newSymbol).build();
    }

    @Override
    public <E extends org.iguana.regex.RegularExpression> org.iguana.regex.RegularExpression visit(org.iguana.regex.Alt<E> alt) {
        List<org.iguana.regex.RegularExpression> newSymbols = alt.getSymbols().stream().map(symbol -> symbol.accept(this)).collect(Collectors.toList());
        return alt.copy().setSymbols((List<E>) newSymbols).build();
    }

    @Override
    public <E extends org.iguana.regex.RegularExpression> org.iguana.regex.RegularExpression visit(org.iguana.regex.Seq<E> seq) {
        List<org.iguana.regex.RegularExpression> newSymbols = seq.getSymbols().stream().map(symbol -> symbol.accept(this)).collect(Collectors.toList());
        return seq.copy().setSymbols((List<E>) newSymbols).build();
    }

    @Override
    public org.iguana.regex.RegularExpression visit(org.iguana.regex.Reference ref) {
        org.iguana.regex.RegularExpression regularExpression = definitions.get(ref.getName());
        if (regularExpression != null) {
            return regularExpression;
        } else {
            return ref;
        }
    }
}
