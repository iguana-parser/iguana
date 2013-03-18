package org.jgll.grammar;

import org.jgll.lookup.RecursiveDescentLookupTable;
import org.jgll.parser.AbstractGLLParser;
import org.jgll.sppf.DummyNode;


public class RecursiveDescentParser extends AbstractGLLParser {

	@Override
	protected void init() {
		lookupTable = new RecursiveDescentLookupTable(grammar, input.size());
	}

	@Override
	protected void parse(HeadGrammarSlot startSymbol) {
		startSymbol.parse(this, input, u0, DummyNode.getInstance(), 0);
	}

}
