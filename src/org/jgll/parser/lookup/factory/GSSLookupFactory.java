package org.jgll.parser.lookup.factory;

import org.jgll.grammar.Grammar;
import org.jgll.parser.lookup.GSSLookup;
import org.jgll.util.Input;


public interface GSSLookupFactory {
	
	public GSSLookup createGSSLookupFactory(Grammar grammar, Input input);

}
