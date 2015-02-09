package org.jgll.traversal;

import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.grammar.symbol.CodeBlock;
import org.jgll.grammar.symbol.EOF;
import org.jgll.grammar.symbol.Epsilon;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.grammar.symbol.Terminal;
import org.jgll.regex.Alt;
import org.jgll.regex.Opt;
import org.jgll.regex.Plus;
import org.jgll.regex.Sequence;
import org.jgll.regex.Star;

public interface ISymbolVisitor<T> {
	
	public T visit(org.jgll.grammar.symbol.Character symbol);
	
	public T visit(CharacterRange symbol);
	
	public T visit(CodeBlock symbol);
	
	public T visit(EOF symbol);
	
	public T visit(Epsilon symbol);
	
	public T visit(Nonterminal symbol);
	
	public T visit(Terminal symbol);
	
	public <E extends Symbol> T visit(Alt<E> symbol);
	
	public T visit(Opt symbol);
	
	public T visit(Plus symbol);
	
	public <E extends Symbol> T visit(Sequence<E> symbol);
	
	public T visit(Star symbol);

}
