package org.jgll.parser.lookup.factory;

import org.jgll.grammar.GrammarGraph;
import org.jgll.sppf.lookup.GlobalSPPFLookupImpl;
import org.jgll.sppf.lookup.SPPFLookup;
import org.jgll.util.Input;

public class NewSPPFLookupFactory implements SPPFLookupFactory {

	@Override
	public SPPFLookup createSPPFLookup(GrammarGraph grammar, Input input) {
		return new GlobalSPPFLookupImpl();
	}

}
