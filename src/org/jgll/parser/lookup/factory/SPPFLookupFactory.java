package org.jgll.parser.lookup.factory;

import org.jgll.grammar.Grammar;
import org.jgll.parser.lookup.SPPFLookup;
import org.jgll.util.Input;


public interface SPPFLookupFactory {

	public SPPFLookup createSPPFLookup(Grammar grammar, Input input);
	
}
