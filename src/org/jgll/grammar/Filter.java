package org.jgll.grammar;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Filter {
	
	private final Rule rule;
	
	private final int position;
	
	private final Set<Rule> filteredRules;
	
	private final Filter child;
	
	public Filter(Rule rule, int position, Collection<Rule> filterList, Filter child) {
		
		if(!(rule.getSymbol(position) instanceof Nonterminal)) {
			throw new IllegalArgumentException("Only nonterminals can be filtered.");
		}
		
		for(Rule r : filterList) {
			if(!r.getHead().equals(rule.getSymbol(position))) {
				throw new IllegalArgumentException("The nonterminal at position " + position);
			}
		}
		
		this.rule = rule;
		this.position = position;
		this.filteredRules = new HashSet<>(filterList);
		this.child = child;
	}
	
	public Rule getRule() {
		return rule;
	}
	
	public int getPosition() {
		return position;
	}
	
	public Set<Rule> getFilteredRules() {
		return filteredRules;
	}
	
	public Filter getChild() {
		return child;
	}
}
