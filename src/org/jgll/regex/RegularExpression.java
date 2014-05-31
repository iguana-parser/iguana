package org.jgll.regex;

import java.io.Serializable;
import java.util.Set;

import org.jgll.grammar.symbol.Range;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.regex.automaton.Automaton;

public interface RegularExpression extends Serializable, Symbol {

	public Automaton getAutomaton();

	public boolean isNullable();
	
	public Set<Range> getFirstSet();
	
	public Object getObject();
	
	public String getLabel();
	
	/**
	 * The set of characters (ranges) that cannot follow this regular expressions. 
	 */
	public Set<Range> getNotFollowSet();
	
}
