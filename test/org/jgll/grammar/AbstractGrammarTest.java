package org.jgll.grammar;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;

public abstract class AbstractGrammarTest {

	protected Grammar grammar;
	
	@Before
	public void init() {
		grammar = initGrammar();
	}
	
	protected abstract Grammar initGrammar();

	@SafeVarargs
	protected static <T> Set<T> set(T...objects) {
		Set<T>  set = new HashSet<>();
		for(T t : objects) {
			set.add(t);
		}
		return set;
	}
	
}
