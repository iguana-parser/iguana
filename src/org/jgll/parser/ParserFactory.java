package org.jgll.parser;

import org.jgll.grammar.Grammar;
import org.jgll.parser.lookup.MixArrayHashLookupTable;


public class ParserFactory {
	
	public static GLLParser createRecursiveDescentParser(Grammar grammar) {
		return new GLLParserImpl(new MixArrayHashLookupTable(grammar));
	}
}
