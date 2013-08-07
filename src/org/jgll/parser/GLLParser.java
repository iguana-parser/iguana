package org.jgll.parser;

import org.jgll.grammar.Grammar;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.util.Input;

/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public interface GLLParser {
	
	public NonterminalSymbolNode parse(Input input, Grammar grammar, String startSymbolName) throws ParseError;
	
}
