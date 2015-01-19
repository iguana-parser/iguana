package org.jgll.disambiguation.conditions;

import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.grammar.symbol.Keyword;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.regex.Plus;
import org.jgll.util.Configuration;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * S ::= Label !>> "8" [0-9]
 *
 * Label ::= [a-z]+ !>> [a-z]
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class FollowRestrictionTest2 {
	
	private GLLParser parser;
	private Grammar grammar;
	
	@Before
	public void init() {
		
		Nonterminal S = Nonterminal.withName("S");
		Nonterminal Label = Nonterminal.builder("Label").addPostCondition(RegularExpressionCondition.notFollow(Keyword.from("8"))).build();
		CharacterRange az = CharacterRange.in('a', 'z');
		CharacterRange zero_nine = CharacterRange.in('0', '9');
		Plus AZPlus = Plus.builder(az).addPreCondition(RegularExpressionCondition.notFollow(az)).build();
		
		Rule r1 = Rule.withHead(S).addSymbols(Label, zero_nine).build();
		Rule r2 = Rule.withHead(Label).addSymbol(AZPlus).build();

		grammar = Grammar.builder().addRule(r1).addRule(r2).build();
	}
	
	@Test
	public void testParser1() {
		Input input = Input.fromString("abc8");
		parser =  ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, grammar, Nonterminal.withName("S"));
		assertTrue(result.isParseError());
	}
	
	@Test
	public void testParser2() {
		Input input = Input.fromString("abc3");
		parser =  ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, grammar, Nonterminal.withName("S"));
		assertTrue(result.isParseError());
	}


}
