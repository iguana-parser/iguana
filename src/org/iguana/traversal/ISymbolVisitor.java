package org.iguana.traversal;

import org.iguana.grammar.symbol.Align;
import org.iguana.grammar.symbol.Block;
import org.iguana.grammar.symbol.CharacterRange;
import org.iguana.grammar.symbol.Code;
import org.iguana.grammar.symbol.Conditional;
import org.iguana.grammar.symbol.EOF;
import org.iguana.grammar.symbol.Epsilon;
import org.iguana.grammar.symbol.IfThen;
import org.iguana.grammar.symbol.IfThenElse;
import org.iguana.grammar.symbol.Ignore;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Offside;
import org.iguana.grammar.symbol.Symbol;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.grammar.symbol.While;
import org.iguana.regex.Alt;
import org.iguana.regex.Opt;
import org.iguana.regex.Plus;
import org.iguana.regex.Sequence;
import org.iguana.regex.Star;

public interface ISymbolVisitor<T> {
	
	public T visit(Align symbol);
	
	public T visit(Block symbol);
	
	public T visit(org.iguana.grammar.symbol.Character symbol);
	
	public T visit(CharacterRange symbol);
	
	public T visit(Code symbol);
	
	public T visit(Conditional symbol);
	
	public T visit(EOF symbol);
	
	public T visit(Epsilon symbol);
	
	public T visit(IfThen symbol);
	
	public T visit(IfThenElse symbol);
	
	public T visit(Ignore symbol);
	
	public T visit(Nonterminal symbol);
	
	public T visit(Offside symbol);
	
	public T visit(Terminal symbol);
	
	public T visit(While symbol);
	
	public <E extends Symbol> T visit(Alt<E> symbol);
	
	public T visit(Opt symbol);
	
	public T visit(Plus symbol);
	
	public <E extends Symbol> T visit(Sequence<E> symbol);
	
	public T visit(Star symbol);

}
