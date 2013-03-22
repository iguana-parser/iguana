package org.jgll.grammar;

import org.jgll.lookup.LevelSynchronizedLookupTable;
import org.jgll.parser.AbstractGLLParser;

public class LevelSynchronizedGrammarInterpretter extends AbstractGLLParser {
	
	@Override
	protected void init() {
		lookupTable = new LevelSynchronizedLookupTable(grammar, input.size());
	}

	@Override
	protected void parse(HeadGrammarSlot startSymbol) {
		L0.getInstance().parse(this, input, startSymbol);
	}

}
