package org.jgll.grammar.transformation;

import org.jgll.grammar.Grammar;

@FunctionalInterface
public interface GrammarTransformation {
	public Grammar transform(Grammar grammar);
}
