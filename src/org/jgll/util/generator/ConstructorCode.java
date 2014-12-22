package org.jgll.util.generator;

import java.util.Set;
import java.util.stream.Collectors;

import org.jgll.grammar.GrammarSlotRegistry;

/**
 * 
 * 
 * @author Ali Afroozeh
 * 
 */
public interface ConstructorCode {
	
	public String getConstructorCode(GrammarSlotRegistry registry);
	
	default String getConstructorCode(Set<? extends ConstructorCode> set, GrammarSlotRegistry registry) {
		return "Sets.newHashSet(" + set.stream().map(a -> a.getConstructorCode(registry)).collect(Collectors.joining(", ")) + ")";
	}
	
}
