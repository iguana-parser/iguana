package org.iguana.traversal;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.CharacterRange;
import org.iguana.grammar.symbol.EOF;
import org.iguana.grammar.symbol.Epsilon;
import org.iguana.grammar.symbol.Symbol;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.regex.Alt;
import org.iguana.regex.Opt;
import org.iguana.regex.Plus;
import org.iguana.regex.RegularExpression;
import org.iguana.regex.Sequence;
import org.iguana.regex.Star;

public class RegularExpressionOptimizer implements RegularExpressionVisitor<RegularExpression> {

	@Override
	public RegularExpression visit(Character c) {
		return c;
	}

	@Override
	public RegularExpression visit(CharacterRange r) {
		if (r.getStart() == r.getEnd())
			return Character.builder(r.getStart()).addConditions(r).build();
			
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
	public RegularExpression visit(Terminal t) {
		return Terminal.builder(t.getRegularExpression().accept(this)).addConditions(t).build();
	}

	@Override
	public RegularExpression visit(Star s) {
		return Star.builder(s.getSymbol().accept(this)).addConditions(s).build();
	}

	@Override
	public RegularExpression visit(Plus p) {
		return Plus.builder(p.getSymbol().accept(this)).addConditions(p).build();
	}

	@Override
	public RegularExpression visit(Opt o) {
		return Opt.builder(o.getSymbol().accept(this)).addConditions(o).build();
	}

	@Override
	public <E extends Symbol> RegularExpression visit(Alt<E> alt) {
		if (alt.size() == 1) {
			// Somehow fix this annoying thing: addConditions returns <? extends Symbol> which cannot be verified by type checker
			return (RegularExpression) alt.getSymbols().get(0).copyBuilder().addConditions(alt).build();
		}
		return Alt.builder(alt.getSymbols().stream()
										   .flatMap(s -> { if (s instanceof Alt && s.getPostConditions().isEmpty()) return ((Alt<?>)s).getSymbols().stream(); else return Stream.of(s);})
				                           .map(s -> s.accept(this))
				                           .collect(Collectors.toList())).addConditions(alt).build();
	}
	
	@Override
	public <E extends Symbol> RegularExpression visit(Sequence<E> seq) {
		if (seq.size() == 1)
			return (RegularExpression) seq.getSymbols().get(0).copyBuilder().addConditions(seq).build();
		
		return Sequence.builder(seq.getSymbols().stream().map(s -> s.accept(this)).collect(Collectors.toList())).addConditions(seq).build();
	}

}
