package org.iguana.grammar.transformation;

import org.iguana.grammar.Grammar;

@FunctionalInterface
public interface GrammarTransformation {
	public Grammar transform(Grammar grammar);
}
