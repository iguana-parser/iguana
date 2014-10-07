package org.jgll.parser.lookup.factory;

import org.jgll.grammar.GrammarGraph;
import org.jgll.parser.lookup.GSSLookup;
import org.jgll.util.Input;


public interface GSSLookupFactory {
	
	public GSSLookup createGSSLookup(GrammarGraph grammar, Input input);

}
