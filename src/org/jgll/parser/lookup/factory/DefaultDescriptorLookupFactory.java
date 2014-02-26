package org.jgll.parser.lookup.factory;

import org.jgll.grammar.Grammar;
import org.jgll.parser.lookup.DescriptorLookup;
import org.jgll.parser.lookup.RecursiveDescentDescriptorLookupImpl;
import org.jgll.util.Input;

public class DefaultDescriptorLookupFactory implements DescriptorLookupFactory {

	@Override
	public DescriptorLookup createDescriptorLookup(Grammar grammar, Input input) {
		return new RecursiveDescentDescriptorLookupImpl(grammar, input);
	}

}
