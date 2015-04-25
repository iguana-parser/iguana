package org.iguana.grammar;

import org.iguana.grammar.symbol.Align;
import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.CharacterRange;
import org.iguana.grammar.symbol.EOF;
import org.iguana.grammar.symbol.Epsilon;
import org.iguana.grammar.symbol.Ignore;
import org.iguana.grammar.symbol.Offside;
import org.iguana.grammar.symbol.Symbol;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.regex.Alt;
import org.iguana.regex.Opt;
import org.iguana.regex.Plus;
import org.iguana.regex.RegularExpression;
import org.iguana.regex.Sequence;
import org.iguana.regex.Star;
import org.iguana.traversal.ISymbolVisitor;

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
