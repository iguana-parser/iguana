package org.jgll.regex;

import java.io.Serializable;

import org.jgll.grammar.symbol.Symbol;

public interface RegularExpression extends Serializable, Symbol {

	public Automaton toAutomaton();
	
	public boolean isNullable();
	
	public RegularExpression copy();
	
}
