package org.jgll.parser;

import org.jgll.grammar.Grammar;
import org.jgll.parser.gss.ArrayBasedGSSNodeFactory;
import org.jgll.parser.lookup.DefaultLookupTableImpl;
import org.jgll.util.Input;

public class ParserFactory {
	
	public static GLLParser createRecursiveDescentParser(Grammar grammar, Input input) {
		return new GLLParserImpl(new DefaultLookupTableImpl(grammar, new ArrayBasedGSSNodeFactory(input)));
	}
}
