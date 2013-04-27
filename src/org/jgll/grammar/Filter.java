package org.jgll.grammar;

import java.util.HashSet;
import java.util.Set;

public class Filter {
		
	private final Rule rule;
	private final int position;
	private final Set<Integer> filteredRules;
	
	public Filter(Rule rule, int position, Set<Integer> filterList) {
		
		if(rule == null) {
			throw new IllegalArgumentException("Rule cannot be null.");
		}
		
		if(!(rule.getSymbolAt(position) instanceof Nonterminal)) {
			throw new IllegalArgumentException("Only nonterminals can be filtered.");
		}
		
		if(filterList == null) {
			throw new IllegalArgumentException("The filter list cannot be empty.");
		}
				
		this.rule = rule;
		this.position = position;
		this.filteredRules = new HashSet<>(filterList);
	}
	
	public Set<Integer> getFilteredRules() {
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
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("<").append(rule).append(", ").append(position);
		sb.append(", {");
		for(Integer filteredRule : filteredRules) {
			sb.append(filteredRule).append(", ");
		}
		sb.delete(sb.length() - 2, sb.length());
		sb.append("}");
		sb.append(">");
		return sb.toString();
	}
	
}
