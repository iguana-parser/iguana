package org.jgll.util.generator;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 
 * 
 * @author Ali Afroozeh
 * 
 */
public interface ConstructorCode {
	
	public String getConstructorCode();
	
	default String getConstructorCode(Set<? extends ConstructorCode> set) {
		return "Sets.newHashSet(" + set.stream().map(a -> a.getConstructorCode()).collect(Collectors.joining(", ")) + ")";
	}
	
	default String getConstructorCode(List<? extends ConstructorCode> list) {
		return "Arrays.asList(" + list.stream().map(a -> a.getConstructorCode()).collect(Collectors.joining(", ")) + ")";
	}

	
}
