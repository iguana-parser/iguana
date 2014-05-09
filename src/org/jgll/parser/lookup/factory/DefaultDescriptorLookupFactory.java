package org.jgll.parser.lookup.factory;

import org.jgll.grammar.GrammarGraph;
import org.jgll.parser.lookup.DescriptorLookup;
import org.jgll.parser.lookup.DescriptorLookupImpl;
import org.jgll.util.Input;

public class DefaultDescriptorLookupFactory implements DescriptorLookupFactory {

	@Override
	public DescriptorLookup createDescriptorLookup(GrammarGraph grammar, Input input) {
		return new DescriptorLookupImpl(grammar, input);
	}

}
