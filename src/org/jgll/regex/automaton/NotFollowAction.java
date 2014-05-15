package org.jgll.regex.automaton;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.regex.RegexAlt;
import org.jgll.regex.RegularExpression;
import org.jgll.util.Input;

public class NotFollowAction implements Action {
	
	private Set<Condition> conditions;
	private RunnableAutomaton r;
	private RegularExpression regex;

	public NotFollowAction(Set<Condition> conditions) {
		this.conditions = conditions;
		
		List<RegularExpression> notFollows = new ArrayList<>();
		
		for (Condition condition : conditions) {
			notFollows.add(((RegularExpressionCondition) condition).getRegularExpression());
		}
		
		regex = notFollows.size() == 1 ? notFollows.get(0) : RegexAlt.from(notFollows);
		r = regex.getAutomaton().getRunnableAutomaton();
	}
	
	
	@Override
	public boolean execute(Input input, int index) {
		if (index> input.length() - 1)  return false;
		return r.match(input, index) > 0;
	}
	
	@Override
	public Set<Condition> getConditions() {
		return conditions;
	}
	
	@Override
	public String toString() {
		return "!>> " + regex.toString();
	}
	
	@Override
	public int hashCode() {
		return conditions.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (obj == this) return true;
		
		if (! (obj instanceof Action)) return false;
		
		Action other = (Action) obj;
		
		return conditions.equals(other.getConditions());
	}

}
