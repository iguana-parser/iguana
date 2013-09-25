package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.*;

import org.jgll.grammar.symbols.Character;
import org.jgll.grammar.symbols.CharacterClass;
import org.jgll.grammar.symbols.Nonterminal;
import org.jgll.grammar.symbols.Range;
import org.jgll.grammar.symbols.RegularList;
import org.jgll.grammar.symbols.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.recognizer.GLLRecognizer;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * S ::= Float;
 * Float ::= [0-9]+ [.] [0-9]+ !>> [0-9] 
 * 
 * @author Ali Afroozeh
 *
 */
public class RegularListTest3 {
	
	private Grammar grammar;
	
	private GLLParser levelParser;
	private GLLParser rdParser;
	private GLLRecognizer recognizer;

	@Before
	public void init() {
		Nonterminal Float = new Nonterminal("Float");
		Nonterminal S = new Nonterminal("S");
		CharacterClass zero_nine = new CharacterClass(list(new Range('0', '9')));
		
		RegularList regularList = RegularList.plus("[0-9]+", zero_nine);
		
		Rule r0 = new Rule(S, list(Float));
		Rule r1 = new Rule(Float, list(regularList, new Character('.'), regularList));
		
		Rule r2 = new Rule(new Nonterminal("[0-9]+"), list(new Nonterminal("[0-9]+"), new Nonterminal("[0-9]")));
		Rule r3 = new Rule(new Nonterminal("[0-9]+"), list(new Nonterminal("[0-9]")));
		Rule r4 = new Rule(new Nonterminal("[0-9]"), list(zero_nine));

		
		grammar = new GrammarBuilder().addRule(r0).addRule(r1).addRule(r2).addRule(r3).addRule(r4).build();
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
		levelParser.parse(Input.fromString("1234567890123456789012345623434343.1234567890123456789012345678799898989889898"), grammar, "S");
	}
}
