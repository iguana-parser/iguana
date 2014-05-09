package org.jgll.parser.lookup.factory;

import org.jgll.grammar.GrammarGraph;
import org.jgll.parser.lookup.SPPFLookup;
import org.jgll.util.Input;


public interface SPPFLookupFactory {

	public SPPFLookup createSPPFLookup(GrammarGraph grammar, Input input);
	
}
