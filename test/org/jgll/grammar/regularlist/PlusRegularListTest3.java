package org.jgll.grammar.regularlist;

import static org.jgll.util.CollectionsUtil.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarBuilder;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.CharacterClass;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Range;
import org.jgll.grammar.symbol.RegularList;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.recognizer.GLLRecognizer;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.util.Input;
import org.jgll.util.Visualization;
import org.junit.Before;
import org.junit.Test;

/**
 * S ::= Float;
 * Float ::= [0-9]+ [.] [0-9]+ !>> [0-9] 
 * 
 * @author Ali Afroozeh
 *
 */
public class PlusRegularListTest3 {
	
	private Grammar grammar;
	
	private GLLParser levelParser;
	private GLLParser rdParser;
	private GLLRecognizer recognizer;

	@Before
	public void init() {
		Nonterminal S = new Nonterminal("S");
		Nonterminal Float = new Nonterminal("Float");
		CharacterClass zero_nine = new CharacterClass(list(new Range('0', '9')));		
		RegularList regularList = RegularList.plus("[0-9]+", zero_nine);
		
		Rule r1 = new Rule(S, list(Float));
		Rule r2 = new Rule(Float, list(new Nonterminal("[0-9]+"), new Character('.'), new Nonterminal("[0-9]+")));
		
		Rule r3 = new Rule(new Nonterminal("[0-9]+"), list(regularList));
		
		grammar = new GrammarBuilder().addRule(r1).addRule(r2).addRule(r3).build();
		levelParser = ParserFactory.levelParser(grammar, 10);
	}

	@Test
	public void test1() throws ParseError {
		levelParser.parse(Input.fromString("123.123"), grammar, "S");
	}

	@Test
	public void test2() throws ParseError {
		levelParser.parse(Input.fromString("123456789012345.12345678901234567"), grammar, "S");
	}
	
	@Test
	public void test3() throws ParseError {
		NonterminalSymbolNode sppf = levelParser.parse(Input.fromString("1234567890123456789012345623434343.1234567890123456789012345678799898989889898"), grammar, "S");
		Visualization.generateSPPFGraph("/Users/aliafroozeh/output", sppf);
	}
}
