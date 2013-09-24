package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.*;

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
 * S ::= Id;
 * Id ::= [a-z]+ [0-9]
 * 
 * @author Ali Afroozeh
 *
 */
public class RegularListTest2 {
	
	private Grammar grammar;
	
	private GLLParser levelParser;
	private GLLParser rdParser;
	private GLLRecognizer recognizer;

	@Before
	public void init() {
		Rule r0 = new Rule(new Nonterminal("S"), list(new Nonterminal("Id")));
		
		CharacterClass a_z = new CharacterClass(list(new Range('a', 'z')));
		CharacterClass zero_nine = new CharacterClass(list(new Range('0', '9')));
		Rule r1 = new Rule(new Nonterminal("Id"), list(RegularList.plus("[a-z]+", a_z), zero_nine));
		
		Rule r2 = new Rule(new Nonterminal("[a-z]+"), list(new Nonterminal("[a-z]+"), new Nonterminal("[a-z]")));
		Rule r3 = new Rule(new Nonterminal("[a-z]+"), list(new Nonterminal("[a-z]")));
		Rule r4 = new Rule(new Nonterminal("[a-z]"), list(new CharacterClass(list(new Range('a', 'z')))));

		
		grammar = new GrammarBuilder().addRule(r0).addRule(r1).addRule(r2).addRule(r3).addRule(r4).build();
		levelParser = ParserFactory.levelParser(grammar, 10);
		
	}

	@Test
	public void test1() throws ParseError {
		levelParser.parse(Input.fromString("abcdef9"), grammar, "S");
	}

	@Test
	public void test2() throws ParseError {
		levelParser.parse(Input.fromString("abcdefghij9"), grammar, "S");
	}
	
	@Test
	public void test3() throws ParseError {
		levelParser.parse(Input.fromString("abcdefghijklm9"), grammar, "S");
	}
	
	@Test
	public void test4() throws ParseError {
		levelParser.parse(Input.fromString("abcdefghijklmnopqrstuvwxyz9"), grammar, "S");
	}
}
