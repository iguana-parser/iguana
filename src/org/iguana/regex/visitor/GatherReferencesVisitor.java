package org.iguana.regex.visitor;

import org.iguana.regex.EOF;

import java.util.ArrayList;
import java.util.List;

public class GatherReferencesVisitor implements RegularExpressionVisitor<Void> {

    private final List<String> references = new ArrayList<>();

    public List<String> getReferences() {
        return references;
    }

    @Override
    public Void visit(org.iguana.regex.Char c) {
        return null;
    }

    @Override
    public Void visit(org.iguana.regex.CharRange r) {
        return null;
    }

    @Override
    public Void visit(EOF eof) {
        return null;
    }

    @Override
    public Void visit(org.iguana.regex.Epsilon e) {
        return null;
    }

    @Override
    public Void visit(org.iguana.regex.Star s) {
        s.getSymbol().accept(this);
        return null;
    }

    @Override
    public Void visit(org.iguana.regex.Plus p) {
        p.getSymbol().accept(this);
        return null;
    }

    @Override
    public Void visit(org.iguana.regex.Opt o) {
        o.getSymbol().accept(this);
        return null;
    }

    @Override
    public <E extends org.iguana.regex.RegularExpression> Void visit(org.iguana.regex.Alt<E> alt) {
        alt.getSymbols().forEach(symbol -> symbol.accept(this));
        return null;
    }

    @Override
    public <E extends org.iguana.regex.RegularExpression> Void visit(org.iguana.regex.Seq<E> seq) {
        seq.getSymbols().forEach(symbol -> symbol.accept(this));
        return null;
    }

    @Override
    public Void visit(org.iguana.regex.Reference ref) {
        references.add(ref.getName());
        return null;
    }
}
