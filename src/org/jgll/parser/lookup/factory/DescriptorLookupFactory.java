package org.jgll.parser.lookup.factory;

import org.jgll.grammar.Grammar;
import org.jgll.parser.lookup.DescriptorLookup;
import org.jgll.util.Input;

public interface DescriptorLookupFactory {
	
	public DescriptorLookup createDescriptorLookup(Grammar grammar, Input input);

}
