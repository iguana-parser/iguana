package org.jgll.parser;

import org.jgll.grammar.HeadGrammarSlot;
import org.jgll.grammar.L0;
import org.jgll.lookup.LookupTable;
import org.jgll.lookup.RecursiveDescentLookupTable;


public class RecursiveDescentParser extends AbstractGLLParser {

	@Override
	protected void init() {
		lookupTable = new RecursiveDescentLookupTable(grammar, input.size());
	}

	@Override
	protected void parse(HeadGrammarSlot startSymbol) {
		L0.getInstance().parse(this, input, startSymbol);
	}

	@Override
	public LookupTable getLookupTable() {
		return lookupTable;
	}

}
