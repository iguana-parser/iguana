package org.jgll.regex.automaton;

import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.util.Input;

public interface Action {

	public boolean execute(Input input, int index);
	
	public Set<Condition> getConditions();
	
}
