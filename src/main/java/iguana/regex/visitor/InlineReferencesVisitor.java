package iguana.regex.visitor;

import iguana.regex.*;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class InlineReferencesVisitor implements RegularExpressionVisitor<RegularExpression> {

    private final Map<String, RegularExpression> definitions;
    private final Set<String> visited;

    public InlineReferencesVisitor(Map<String, RegularExpression> definitions, LinkedHashSet<String> visited) {
        this.definitions = definitions;
        this.visited = visited;
    }

    @Override
    public RegularExpression visit(Char c) {
        return c;
    }

    @Override
    public RegularExpression visit(CharRange r) {
        return r;
    }

    @Override
    public RegularExpression visit(EOF eof) {
        return eof;
    }

    @Override
    public RegularExpression visit(Epsilon e) {
        return e;
    }

    @Override
    public RegularExpression visit(Star s) {
        RegularExpression newSymbol = s.getSymbol().accept(this);
        return s.copyBuilder().setSymbol(newSymbol).build();
    }

    @Override
    public RegularExpression visit(Plus p) {
        RegularExpression newSymbol = p.getSymbol().accept(this);
        return p.copyBuilder().setSymbol(newSymbol).build();
    }

    @Override
    public RegularExpression visit(Opt o) {
        RegularExpression newSymbol = o.getSymbol().accept(this);
        return o.copyBuilder().setSymbol(newSymbol).build();
    }

    @Override
    public <E extends RegularExpression> RegularExpression visit(Alt<E> alt) {
        List<RegularExpression> newSymbols = alt.getSymbols().stream().map(symbol -> symbol.accept(this)).collect(Collectors.toList());
        return alt.copyBuilder().setSymbols((List<E>) newSymbols).build();
    }

    @Override
    public <E extends RegularExpression> RegularExpression visit(Seq<E> seq) {
        List<RegularExpression> newSymbols = seq.getSymbols().stream().map(symbol -> symbol.accept(this)).collect(Collectors.toList());
        return seq.copyBuilder().setSymbols((List<E>) newSymbols).build();
    }

    @Override
    public RegularExpression visit(Reference ref) {
        if (visited.contains(ref.getName())) {
            throw new RuntimeException("Regular expression references cannot be cyclic: " +
                String.join("->", visited) + "->" + ref.getName());
        }
        RegularExpression regularExpression = definitions.get(ref.getName());
        if (regularExpression != null) {
            return regularExpression;
        } else {
            return ref;
        }
    }
}
