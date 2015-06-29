package org.iguana.traversal;

import org.iguana.grammar.symbol.Align;
import org.iguana.grammar.symbol.Block;
import org.iguana.grammar.symbol.Character;
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
import org.iguana.grammar.symbol.Return;
import org.iguana.grammar.symbol.Symbol;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.grammar.symbol.While;
import org.iguana.regex.Alt;
import org.iguana.regex.Opt;
import org.iguana.regex.Plus;
import org.iguana.regex.Sequence;
import org.iguana.regex.Star;

public interface RegularExpressionVisitor<T> extends ISymbolVisitor<T> {
	
	public T visit(Character c);
	
	public T visit(CharacterRange r);
	
	public T visit(EOF eof);
	
	public T visit(Epsilon e);
	
	public T visit(Terminal t);
	
	public T visit(Star s);
	
	public T visit(Plus p);
	
	public T visit(Opt o);
	
	public <E extends Symbol> T visit(Alt<E> symbol);
	
	public <E extends Symbol> T visit(Sequence<E> symbol);

	default T visit(Align symbol) { throw new RuntimeException(); }
	
	default T visit(Block symbol) { throw new RuntimeException(); }
	
	default T visit(Code symbol) { throw new RuntimeException(); }
	
	default T visit(Conditional symbol) { throw new RuntimeException(); }
	
	default T visit(IfThen symbol) { throw new RuntimeException(); }
	
	default T visit(IfThenElse symbol) { throw new RuntimeException(); }
	
	default T visit(Ignore symbol) { throw new RuntimeException(); }
	
	default T visit(Nonterminal symbol) { throw new RuntimeException(); }
	
	default T visit(Offside symbol) { throw new RuntimeException(); }
	
	default T visit(While symbol) { throw new RuntimeException(); }
	
	default T visit(Return symbol) { throw new RuntimeException(); }
	
}
