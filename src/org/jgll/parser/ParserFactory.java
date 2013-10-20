package org.jgll.parser;

import org.jgll.grammar.Grammar;
import org.jgll.parser.lookup.LevelBasedHashLookup;
import org.jgll.parser.lookup.LevelBasedMixLookup;
import org.jgll.parser.lookup.RecursiveDescentLookupTable;


public class ParserFactory {
	
	private static final int MAX_SLOTS = 100_000;

	public static GLLParser createRecursiveDescentParser(Grammar grammar) {
		return new GLLParserImpl(new RecursiveDescentLookupTable(grammar), Integer.MAX_VALUE);
	}
	
	public static GLLParser createLevelParser(Grammar grammar) {
		if(grammar.getGrammarSlots().size() < MAX_SLOTS) {
			return new GLLParserImpl(new LevelBasedMixLookup(grammar));
		} else {
			return new GLLParserImpl(new LevelBasedHashLookup(grammar));			
		}
	}
	
	public static GLLParser createLevelParser(Grammar grammar, int ringSize) {
		if(grammar.getGrammarSlots().size() < MAX_SLOTS) {
			return new GLLParserImpl(new LevelBasedMixLookup(grammar, ringSize), ringSize);
		} else {
			return new GLLParserImpl(new LevelBasedHashLookup(grammar, ringSize), ringSize);
		}
	}
	
}
