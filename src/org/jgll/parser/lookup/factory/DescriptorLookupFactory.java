package org.jgll.parser.lookup.factory;

import org.jgll.grammar.GrammarGraph;
import org.jgll.parser.lookup.DescriptorLookup;
import org.jgll.util.Input;

public interface DescriptorLookupFactory {
	
	public DescriptorLookup createDescriptorLookup(GrammarGraph grammar, Input input);

}
