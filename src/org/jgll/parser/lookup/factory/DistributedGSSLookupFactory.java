package org.jgll.parser.lookup.factory;

import org.jgll.grammar.GrammarGraph;
import org.jgll.parser.lookup.DistributedGSSLookupImpl;
import org.jgll.parser.lookup.GSSLookup;
import org.jgll.util.Input;

public class DistributedGSSLookupFactory implements GSSLookupFactory {

	@Override
	public GSSLookup createGSSLookup(GrammarGraph grammar, Input input) {
		return new DistributedGSSLookupImpl();
	}

}
