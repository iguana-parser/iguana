package org.jgll.grammar;

import java.util.HashSet;
import java.util.Set;

class Filter {
		
	private final HeadGrammarSlot head;
	private final int alternateIndex;
	private final int position;
	private final Set<Integer> filteredRules;
	
	private NonterminalGrammarSlot nonterminalSlot;
	
	public Filter(HeadGrammarSlot head, int alternateIndex, int position, Set<Integer> filterList) {
		
		if(head == null) {
			throw new IllegalArgumentException("Rule cannot be null.");
		}
		
		if(filterList == null) {
			throw new IllegalArgumentException("The filter list cannot be empty.");
		}
				
		this.head = head;
		this.alternateIndex = alternateIndex;
		this.position = position;
		this.filteredRules = new HashSet<>(filterList);
		
		nonterminalSlot = (NonterminalGrammarSlot) head.getAlternateAt(alternateIndex).getBodyGrammarSlotAt(position);
	}
	
	public Set<Integer> getFilteredRules() {
		return filteredRules;
	}
	
	public boolean match() {
		return nonterminalSlot.getNonterminal().contains(filteredRules);
	}
	
	/**
	 * The name of the nonterminal at the given position which should be filtered 
	 */
	public String getNonterminalName() {
		return nonterminalSlot.getNonterminal().getNonterminal().getName();
	}
	
	public HeadGrammarSlot getNonterminal() {
		return nonterminalSlot.getNonterminal();
	}
	
	public void setNewNonterminal(HeadGrammarSlot slot) {
		nonterminalSlot.setNonterminal(slot);
	}
	
	public int getAlternateIndex() {
		return alternateIndex;
	}
	
	public void addFilterRule(int index) {
		filteredRules.add(index);
	}

	public Alternate getFilterAlternate(int index) {

		if(!filteredRules.contains(index)) {
			throw new IllegalArgumentException("The index " + index + " does not exist in " + filteredRules);
		}
		
		return head.getAlternateAt(index);
	}
	
	public int getPosition() {
		return position;
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + head.hashCode();
		result = 31 * result + alternateIndex;
		result = 31 * result + position;
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
		
		return head.equals(other.head) &&
			   alternateIndex == other.alternateIndex &&
			   position == other.position;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("<").append(head).append(", ")
		  .append(head.getAlternateAt(alternateIndex)).append(", ").append(position);
		sb.append(", {");
		for(Integer filteredRule : filteredRules) {
			sb.append(head.getAlternateAt(filteredRule)).append(", ");
		}
		sb.delete(sb.length() - 2, sb.length());
		sb.append("}");
		sb.append(">");
		return sb.toString();
	}
	
}
