package org.jgll.grammar;

import org.jgll.lookup.RecursiveDescentLookupTable;

public class RecursiveDescentGrammarInterpreter extends GrammarInterpreter {

	@Override
	protected void init() {
		super.init();
		lookupTable = new RecursiveDescentLookupTable(grammar, I.length);
	}	
}
