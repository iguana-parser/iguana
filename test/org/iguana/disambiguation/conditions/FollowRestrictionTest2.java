package org.iguana.disambiguation.conditions;

import static org.junit.Assert.*;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.condition.RegularExpressionCondition;
import org.iguana.grammar.symbol.CharacterRange;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.parser.GLLParser;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParserFactory;
import org.iguana.regex.Plus;
import org.iguana.regex.Sequence;
import org.iguana.util.Configuration;
import org.iguana.util.Input;
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
		Nonterminal Label = Nonterminal.builder("Label").addPostCondition(RegularExpressionCondition.notFollow(Sequence.from("8"))).build();
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
