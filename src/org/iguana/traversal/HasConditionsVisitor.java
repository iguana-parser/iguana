package org.iguana.traversal;

import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.CharacterRange;
import org.iguana.grammar.symbol.EOF;
import org.iguana.grammar.symbol.Epsilon;
import org.iguana.grammar.symbol.Symbol;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.regex.Alt;
import org.iguana.regex.Opt;
import org.iguana.regex.Plus;
import org.iguana.regex.Sequence;
import org.iguana.regex.Star;

public class HasConditionsVisitor implements RegularExpressionVisitor<Boolean> {

	@Override
	public Boolean visit(Character c) {
		return c.hasConditions();
	}

	@Override
	public Boolean visit(CharacterRange r) {
		return r.hasConditions();
	}

	@Override
	public Boolean visit(EOF eof) {
		return eof.hasConditions();
	}

	@Override
	public Boolean visit(Epsilon e) {
		return e.hasConditions();
	}

	@Override
	public Boolean visit(Terminal t) {
		return t.getRegularExpression().accept(this) || t.hasConditions();
	}

	@Override
	public Boolean visit(Star s) {
		return s.getSymbol().accept(this) || s.hasConditions();
	}

	@Override
	public Boolean visit(Plus p) {
		return p.getSymbol().accept(this) || p.hasConditions();
	}

	@Override
	public Boolean visit(Opt o) {
		return o.getSymbol().accept(this) || o.hasConditions();
	}

	@Override
	public <E extends Symbol> Boolean visit(Alt<E> alt) {
		return alt.getSymbols().stream().anyMatch(s -> s.accept(this)) || alt.hasConditions();
	}

	@Override
	public <E extends Symbol> Boolean visit(Sequence<E> seq) {
		return seq.getSymbols().stream().anyMatch(s -> s.accept(this)) || seq.hasConditions();
	}

}
