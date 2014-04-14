package org.jgll.regex;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.symbol.Range;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.regex.automaton.Automaton;

public interface RegularExpression extends Serializable, Symbol {

	public Automaton toAutomaton();

	public boolean isNullable();
	
	public Set<Range> getFirstSet();
	
	@Override
	public RegularExpression clone();
	
	@Override
	public RegularExpression addCondition(Condition condition);
	
	@Override
	public RegularExpression addConditions(Collection<Condition> conditions);
	
}
