package org.jgll.parser.lookup.factory;

import org.jgll.grammar.GrammarGraph;
import org.jgll.parser.lookup.DefaultSPPFLookupImpl;
import org.jgll.parser.lookup.SPPFLookup;
import org.jgll.util.Input;


public class DefaultSPPFLookupFactory implements SPPFLookupFactory {

	@Override
	public SPPFLookup createSPPFLookup(GrammarGraph grammar, Input input) {
		return new DefaultSPPFLookupImpl();
	}

}
