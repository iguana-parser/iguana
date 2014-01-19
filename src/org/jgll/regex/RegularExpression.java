package org.jgll.regex;

import java.io.Serializable;

import org.jgll.grammar.symbol.Symbol;

public interface RegularExpression extends Serializable, Symbol {

	public Automaton toAutomaton();
	
	public boolean isNullable();
	
	public RegularExpression copy();
	
	/**
	 * An action that should be executed on the final states of
	 * regular expressions created from this automata.  
	 */
	public void addFinalStateAction(StateAction action);
	
}
