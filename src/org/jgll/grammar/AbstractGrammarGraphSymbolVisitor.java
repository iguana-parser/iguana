package org.jgll.grammar;

import org.jgll.grammar.symbol.Align;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.grammar.symbol.EOF;
import org.jgll.grammar.symbol.Epsilon;
import org.jgll.grammar.symbol.Ignore;
import org.jgll.grammar.symbol.Offside;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.grammar.symbol.Terminal;
import org.jgll.regex.Alt;
import org.jgll.regex.Opt;
import org.jgll.regex.Plus;
import org.jgll.regex.RegularExpression;
import org.jgll.regex.Sequence;
import org.jgll.regex.Star;
import org.jgll.traversal.ISymbolVisitor;

public abstract class AbstractGrammarGraphSymbolVisitor implements ISymbolVisitor<Void> {
	
	@Override
	public Void visit(Align symbol) {
		return null;
	}

	public abstract Void visit(RegularExpression symbol);

	@Override
	public Void visit(Character symbol) {
		return visit((RegularExpression) symbol);
	}

	@Override
	public Void visit(CharacterRange symbol) {
		return visit((RegularExpression) symbol);
	}

	@Override
	public Void visit(EOF symbol) {
		return visit((RegularExpression) symbol);
	}

	@Override
	public Void visit(Epsilon symbol) {
		return visit((RegularExpression) symbol);
	}

	@Override
	public Void visit(Terminal symbol) {
		return visit((RegularExpression) symbol);
	}
	
	@Override
	public Void visit(Ignore symbol) {
		return null;
	}
	
	@Override
	public Void visit(Offside symbol) {
		return null;
	}

	@Override
	public <E extends Symbol> Void visit(Alt<E> symbol) {
		return null;
	}

	@Override
	public Void visit(Opt symbol) {
		return null;
	}

	@Override
	public Void visit(Plus symbol) {
		return null;
	}

	@Override
	public <E extends Symbol> Void visit(Sequence<E> symbol) {
		return null;
	}

	@Override
	public Void visit(Star symbol) {
		return null;
	}

}
