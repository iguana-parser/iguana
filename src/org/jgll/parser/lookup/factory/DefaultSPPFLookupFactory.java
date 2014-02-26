package org.jgll.parser.lookup.factory;

import org.jgll.grammar.Grammar;
import org.jgll.parser.lookup.SPPFLookup;
import org.jgll.parser.lookup.SPPFLookupImpl;
import org.jgll.util.Input;


public class DefaultSPPFLookupFactory implements SPPFLookupFactory {

	@Override
	public SPPFLookup createSPPFLookup(Grammar grammar, Input input) {
		return new SPPFLookupImpl(grammar, input);
	}

}
