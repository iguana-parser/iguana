package org.jgll.parser;

import org.jgll.grammar.Grammar;
import org.jgll.parser.gss.ArrayBasedGSSNodeFactory;
import org.jgll.parser.lookup.GSSLookupImpl;
import org.jgll.util.Input;

public class ParserFactory {
	
	public static GLLParser newParser(Grammar grammar, Input input) {
		return new GLLParserImpl(new GSSLookupImpl(grammar, new ArrayBasedGSSNodeFactory(input)));
	}
}
