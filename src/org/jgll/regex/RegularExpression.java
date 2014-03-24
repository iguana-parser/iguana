package org.jgll.regex;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.symbol.Range;
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
	
	public Set<Range> getFirstSet();
	
	@Override
	public RegularExpression addConditions(Collection<Condition> conditions);	
}
