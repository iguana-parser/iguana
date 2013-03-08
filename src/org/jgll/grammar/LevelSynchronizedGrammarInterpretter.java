package org.jgll.grammar;

import org.jgll.lookup.LevelSynchronizedLookupTable;

public class LevelSynchronizedGrammarInterpretter extends GrammarInterpreter {
	
	@Override
	protected void init() {
		super.init();
		lookupTable = new LevelSynchronizedLookupTable(grammar, I.length);
	}

}
