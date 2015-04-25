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
		Nonterminal Label = Nonterminal.builder("Label").addPostCondition(RegularExpressionCondition.notFollow(Sequence.from(":"))).build();
		CharacterRange az = CharacterRange.in('a', 'z');
		Plus AZPlus = Plus.builder(az).addPreCondition(RegularExpressionCondition.notFollow(az)).build();

		Grammar.Builder builder = new Grammar.Builder();
		
		Rule r1 = Rule.withHead(S).addSymbol(Label).build();		
		Rule r2 = Rule.withHead(Label).addSymbol(AZPlus).build();
		builder.addRule(r1).addRule(r2);
		
		grammar = builder.build();
	}
	
	@Test
	public void testParser() {
		Input input = Input.fromString("abc:");
		parser =  ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, grammar, Nonterminal.withName("S"));
		assertTrue(result.isParseError());
	}

}
