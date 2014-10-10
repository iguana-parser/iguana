package org.jgll.parser.lookup.factory;

import org.jgll.grammar.GrammarGraph;
import org.jgll.parser.lookup.ArrayGSSLookupImpl;
import org.jgll.parser.lookup.GSSLookup;
import org.jgll.util.Input;

public class ArrayGSSLookupFactory implements GSSLookupFactory {

	@Override
	public GSSLookup createGSSLookup(GrammarGraph grammar, Input input) {
		return new ArrayGSSLookupImpl(input, grammar.getGrammarSlots().size());
	}

}
