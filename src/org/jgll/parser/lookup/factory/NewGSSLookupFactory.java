package org.jgll.parser.lookup.factory;

import org.jgll.grammar.GrammarGraph;
import org.jgll.parser.lookup.GSSLookup;
import org.jgll.parser.lookup.GSSLookupImpl;
import org.jgll.util.Input;


public class NewGSSLookupFactory implements GSSLookupFactory {

	@Override
	public GSSLookup createGSSLookupFactory(GrammarGraph grammar, Input input) {
		return new GSSLookupImpl(input, grammar.getNonterminals().size());
	}

}
