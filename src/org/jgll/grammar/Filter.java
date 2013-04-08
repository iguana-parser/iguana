package org.jgll.grammar;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Filter {
		
	private final Set<Rule> filteredRules;
	
	private final Filter child;
	
	public Filter(Collection<Rule> filterList, Filter child) {
		
		if(filterList == null) {
			throw new IllegalArgumentException("The filter list cannot be empty.");
		}
		
		this.filteredRules = new HashSet<>(filterList);
		this.child = child;
	}
	
	public Set<Rule> getFilteredRules() {
		return filteredRules;
	}
	
	public Filter getChild() {
		return child;
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + ((child == null) ? 0 : child.hashCode());
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
		
		return child == null ? other.child == null : child.equals(other.child) && 
			   filteredRules.equals(other.filteredRules);
	}	
}
