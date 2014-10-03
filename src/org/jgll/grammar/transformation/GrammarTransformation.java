package org.jgll.grammar.transformation;

import org.jgll.grammar.symbol.Rule;

public interface GrammarTransformation {
	
	public Iterable<Rule> transform(Iterable<Rule> rules);
	
	public Iterable<Rule> transform(Rule rule);

}
