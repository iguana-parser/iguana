package iguana.regex.visitor;

import iguana.regex.*;

import java.util.ArrayList;
import java.util.List;

public class GatherReferencesVisitor implements RegularExpressionVisitor<Void> {

    private final List<String> references = new ArrayList<>();

    public List<String> getReferences() {
        return references;
    }

    @Override
    public Void visit(Char c) {
        return null;
    }

    @Override
    public Void visit(CharRange r) {
        return null;
    }

    @Override
    public Void visit(EOF eof) {
        return null;
    }

    @Override
    public Void visit(Epsilon e) {
        return null;
    }

    @Override
    public Void visit(Star s) {
        s.getSymbol().accept(this);
        return null;
    }

    @Override
    public Void visit(Plus p) {
        p.getSymbol().accept(this);
        return null;
    }

    @Override
    public Void visit(Opt o) {
        o.getSymbol().accept(this);
        return null;
    }

    @Override
    public <E extends RegularExpression> Void visit(Alt<E> alt) {
        alt.getSymbols().forEach(symbol -> symbol.accept(this));
        return null;
    }

    @Override
    public <E extends RegularExpression> Void visit(Seq<E> seq) {
        seq.getSymbols().forEach(symbol -> symbol.accept(this));
        return null;
    }

    @Override
    public Void visit(Reference ref) {
        references.add(ref.getName());
        return null;
    }
}
