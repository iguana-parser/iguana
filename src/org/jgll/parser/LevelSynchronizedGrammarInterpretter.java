package org.jgll.parser;

import org.jgll.grammar.HeadGrammarSlot;
import org.jgll.grammar.L0;
import org.jgll.lookup.LevelSynchronizedLookupTable;
import org.jgll.lookup.LookupTable;

public class LevelSynchronizedGrammarInterpretter extends AbstractGLLParser {
	
	@Override
	protected void init() {
		lookupTable = new LevelSynchronizedLookupTable(grammar, input.size());
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
