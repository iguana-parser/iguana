package org.jgll.disambiguation.conditions;

import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.grammar.symbol.Keyword;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Plus;
import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * S ::= Label !>> ":" 
 *
 * Label ::= [a-z]+ !>> [a-z]
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class FollowRestrictionTest {
	
	private GLLParser parser;
	private Grammar grammar;
	
	@Before
	public void init() {
		Nonterminal S = Nonterminal.withName("S");
		Nonterminal Label = Nonterminal.builder("Label").addPostCondition(RegularExpressionCondition.notFollow(Keyword.from(":"))).build();
		CharacterRange az = CharacterRange.in('a', 'z');
		Plus AZPlus = Plus.builder(az).addPreCondition(RegularExpressionCondition.notFollow(az)).build();

		Grammar.Builder builder = new Grammar.Builder();
		
		Rule r1 = Rule.builder(S).addSymbol(Label).build();		
		Rule r2 = Rule.builder(Label).addSymbol(AZPlus).build();
		builder.addRule(r1).addRule(r2);
		
		grammar = builder.build();
	}
	
	@Test
	public void testParser() {
		Input input = Input.fromString("abc:");
		parser =  ParserFactory.getParser();
		ParseResult result = parser.parse(input, grammar, "S");
		assertTrue(result.isParseError());
	}

}
