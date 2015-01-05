package org.jgll.util.generator;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jgll.grammar.GrammarRegistry;

/**
 * 
 * 
 * @author Ali Afroozeh
 * 
 */
public interface ConstructorCode {
	
	public String getConstructorCode(GrammarRegistry registry);
	
	default String getConstructorCode(Set<? extends ConstructorCode> set, GrammarRegistry registry) {
		return "Sets.newHashSet(" + set.stream().map(a -> a.getConstructorCode(registry)).collect(Collectors.joining(", ")) + ")";
	}
	
	default String getConstructorCode(List<? extends ConstructorCode> list, GrammarRegistry registry) {
		return "Arrays.asList(" + list.stream().map(a -> a.getConstructorCode(registry)).collect(Collectors.joining(", ")) + ")";
	}

	
}
