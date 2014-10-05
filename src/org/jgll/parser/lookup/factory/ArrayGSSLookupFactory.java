package org.jgll.parser.lookup.factory;

import org.jgll.grammar.GrammarGraph;
import org.jgll.parser.lookup.GSSLookup;
import org.jgll.parser.lookup.HashGSSLookupImpl;
import org.jgll.util.Input;

public class ArrayGSSLookupFactory implements GSSLookupFactory {

	@Override
	public GSSLookup createGSSLookup(GrammarGraph grammar, Input input) {
		return new HashGSSLookupImpl(input, grammar.getGrammarSlots().size());
	}

}
