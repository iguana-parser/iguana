package org.jgll.grammar.regularlist;

import static org.jgll.util.CollectionsUtil.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarBuilder;
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
import org.junit.Before;
import org.junit.Test;

/**
 * S ::= Id;
 * Id ::= [a-z]+ !>> [a-z]
 * 
 * @author Ali Afroozeh
 *
 */
public class PlusRegularListTest1 {
	
	private Grammar grammar1;
	private Grammar grammar2;
	
	private GLLParser levelParser;
	private GLLParser rdParser;
	private GLLRecognizer recognizer;

	@Before
	public void init() {
		Rule r1 = new Rule(new Nonterminal("S"), list(new Nonterminal("Id")));
		Rule r2 = new Rule(new Nonterminal("Id"), list(new Nonterminal("[a-z]+")));
		
		Rule r3 = new Rule(new Nonterminal("[a-z]+"), list(RegularList.plus("[a-z]+", new CharacterClass(list(new Range('a', 'z'))))));
		
		Rule r4 = new Rule(new Nonterminal("[a-z]+"), list(new Nonterminal("[a-z]+"), new Nonterminal("[a-z]")));
		Rule r5 = new Rule(new Nonterminal("[a-z]+"), list(new Nonterminal("[a-z]")));
		Rule r6 = new Rule(new Nonterminal("[a-z]"), list(new CharacterClass(list(new Range('a', 'z')))));
		
		grammar1 = new GrammarBuilder().addRule(r1).addRule(r2).addRule(r3).build();
		grammar2 = new GrammarBuilder().addRule(r1).addRule(r2).addRule(r4).addRule(r5).addRule(r6).build();
		
		levelParser = ParserFactory.levelParser(grammar1, 10);
	}

	@Test
	public void test1() throws ParseError {
		NonterminalSymbolNode sppf = levelParser.parse(Input.fromString("abcdef"), grammar1, "S");
		levelParser.parse(Input.fromString("abcdef"), grammar2, "S");
	}

	@Test
	public void test2() throws ParseError {
		levelParser.parse(Input.fromString("abcdefghij"), grammar1, "S");
		levelParser.parse(Input.fromString("abcdefghij"), grammar2, "S");
	}
	
	@Test
	public void test3() throws ParseError {
		levelParser.parse(Input.fromString("abcdefghijklm"), grammar1, "S");
		levelParser.parse(Input.fromString("abcdefghijklm"), grammar2, "S");
	}
	
	@Test
	public void test4() throws ParseError {
		levelParser.parse(Input.fromString("abcdefghijklmnopqrstuvwxyz"), grammar1, "S");
		levelParser.parse(Input.fromString("abcdefghijklmnopqrstuvwxyz"), grammar2, "S");
	}
}
