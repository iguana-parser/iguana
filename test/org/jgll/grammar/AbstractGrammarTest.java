package org.jgll.grammar;

import java.util.HashSet;
import java.util.Set;

import org.jgll.parser.GrammarInterpreter;
import org.junit.Before;

public abstract class AbstractGrammarTest {

	protected Grammar grammar;
	protected GrammarInterpreter parser;
	protected String outputDir;
	
	@Before
	public void init() {
		grammar = initGrammar();
		parser = new GrammarInterpreter();
		outputDir = System.getProperty("user.home") + "/output";
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
