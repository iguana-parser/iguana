package org.jgll.grammar;

import java.util.HashSet;
import java.util.Set;

class Filter {
		
	private final HeadGrammarSlot nonterminal;
	private final int alternateIndex;
	private final int position;
	private final Set<Integer> filteredRules;
	
	public Filter(HeadGrammarSlot nonterminal, int alternateIndex, int position, Set<Integer> filterList) {
		
		if(nonterminal == null) {
			throw new IllegalArgumentException("Rule cannot be null.");
		}
		
		if(filterList == null) {
			throw new IllegalArgumentException("The filter list cannot be empty.");
		}
				
		this.nonterminal = nonterminal;
		this.alternateIndex = alternateIndex;
		this.position = position;
		this.filteredRules = new HashSet<>(filterList);
	}
	
	public Set<Integer> getFilteredRules() {
		return filteredRules;
	}
	
	public HeadGrammarSlot getNonterminal() {
		return nonterminal;
	}
	
	public int getAlternateIndex() {
		return alternateIndex;
	}

	public Alternate getFilterAlternate(int index) {

		if(!filteredRules.contains(index)) {
			throw new IllegalArgumentException("The index " + index + " does not exist in " + filteredRules);
		}
		
		return nonterminal.getAlternateAt(index);
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
		sb.append("<").append(nonterminal).append(", ")
		  .append(nonterminal.getAlternateAt(alternateIndex)).append(", ").append(position);
		sb.append(", {");
		for(Integer filteredRule : filteredRules) {
			sb.append(nonterminal.getAlternateAt(filteredRule)).append(", ");
		}
		sb.delete(sb.length() - 2, sb.length());
		sb.append("}");
		sb.append(">");
		return sb.toString();
	}
	
}
