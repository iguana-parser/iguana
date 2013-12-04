package org.jgll.parser;

import org.jgll.grammar.Grammar;
import org.jgll.parser.lookup.RecursiveDescentLookupTable2;


public class ParserFactory {
	
	public static GLLParser createRecursiveDescentParser(Grammar grammar) {
		return new GLLParserImpl(new RecursiveDescentLookupTable2(grammar));
	}
}
