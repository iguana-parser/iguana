package org.jgll.grammar;

import org.jgll.lookup.RecursiveDescentLookupTable;
import org.jgll.parser.AbstractGLLParser;


public class RecursiveDescentParser extends AbstractGLLParser {

	@Override
	protected void init() {
		lookupTable = new RecursiveDescentLookupTable(grammar, input.size());
	}

	@Override
	protected void parse(HeadGrammarSlot startSymbol) {
		L0.getInstance().parse(this, input, startSymbol);
	}

}
