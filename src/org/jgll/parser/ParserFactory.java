package org.jgll.parser;

import org.jgll.grammar.Grammar;
import org.jgll.parser.lookup.RecursiveDescentLookupTable;


public class ParserFactory {
	
	public static GLLParser createRecursiveDescentParser(Grammar grammar) {
		return new GLLParserImpl(new RecursiveDescentLookupTable(grammar));
	}
}
