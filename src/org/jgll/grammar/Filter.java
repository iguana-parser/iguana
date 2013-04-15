package org.jgll.grammar;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Filter {
		
	private final Rule rule;
	private final int position;
	private final Set<Rule> filteredRules;
	
	public Filter(Rule rule, int position, Collection<Rule> filterList) {
		
		if(rule == null) {
			throw new IllegalArgumentException("Rule cannot be null.");
		}
		
		if(!(rule.getSymbolAt(position) instanceof Nonterminal)) {
			throw new IllegalArgumentException("Only nonterminals can be filtered.");
		}
		
		if(filterList == null) {
			throw new IllegalArgumentException("The filter list cannot be empty.");
		}
		
		for(Rule r : filterList) {
			if(!r.getHead().equals(rule.getSymbolAt(position))) {
				throw new IllegalArgumentException("The nonterminal at position " + position + " should be " + rule.getSymbolAt(position));
			}
		}
		
		this.rule = rule;
		this.position = position;
		this.filteredRules = new HashSet<>(filterList);
	}
	
	public Set<Rule> getFilteredRules() {
		return filteredRules;
	}
	
	public Rule getRule() {
		return rule;
	}
	
	public int getPosition() {
		return position;
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + filteredRules.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof Filter)) {
			return false;
		}
		
		Filter other = (Filter) obj;
		
		return filteredRules.equals(other.filteredRules);
	}	
}
