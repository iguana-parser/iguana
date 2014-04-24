package org.jgll.regex;

import java.io.Serializable;
import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.symbol.Range;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.regex.automaton.Automaton;

public interface RegularExpression extends Serializable, Symbol {

	public Automaton getAutomaton();

	public boolean isNullable();
	
	public Set<Range> getFirstSet();
	
	@Override
	public RegularExpression withConditions(Set<Condition> conditions);
	
	@Override
	public RegularExpression withCondition(Condition condition);
	
	@Override
	public RegularExpression withoutConditions();
	
}
