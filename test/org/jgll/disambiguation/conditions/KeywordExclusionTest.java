package org.jgll.disambiguation.conditions;

import static org.jgll.util.CollectionsUtil.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarBuilder;
import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.grammar.ebnf.EBNFUtil;
import org.jgll.grammar.slot.factory.GrammarSlotFactoryImpl;
import org.jgll.grammar.slot.factory.GrammarSlotFactory;
import org.jgll.grammar.symbol.Keyword;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Plus;
import org.jgll.grammar.symbol.Range;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.regex.RegexAlt;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * 
 * Id ::= [a-z]+ !>> [a-z] \ { "if", "when", "do", "while"}
 * 
 * @author Ali Afroozeh
 *
 */
public class KeywordExclusionTest {
	
	private Grammar grammar;

	@Before
	public void init() {
		Nonterminal Id = new Nonterminal("Id");
		Range az = new Range('a', 'z');
		
		Keyword iff = new Keyword("if", "if");
		Keyword when = new Keyword("when", "when");
		Keyword doo = new Keyword("do", "do");
		Keyword whilee = new Keyword("while", "while");
		
		GrammarSlotFactory factory = new GrammarSlotFactoryImpl();
		GrammarBuilder builder = new GrammarBuilder(factory);

		RegexAlt<Keyword> alt = new RegexAlt<>(iff, when, doo, whilee);
		Rule r1 = new Rule(Id, new Plus(az).withCondition(RegularExpressionCondition.notFollow(az)).withCondition(RegularExpressionCondition.notMatch(alt)));
		
		Iterable<Rule> rules = EBNFUtil.rewrite(list(r1));
		builder.addRules(rules);

		grammar = builder.build();
	}
	
	@org.junit.Rule
	public ExpectedException thrown = ExpectedException.none();

	
	@Test
	public void testWhen() throws ParseError {
		Input input = Input.fromString("when");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		
		thrown.expect(ParseError.class);
		parser.parse(input, grammar, "Id");
	}
	
	@Test
	public void testIf() throws ParseError {
		Input input = Input.fromString("if");		
		GLLParser parser = ParserFactory.newParser(grammar, input);
		
		thrown.expect(ParseError.class);
		parser.parse(input, grammar, "Id");
	}
	
	@Test
	public void testDo() throws ParseError {
		Input input = Input.fromString("do");
		GLLParser parser = ParserFactory.newParser(grammar, input);

		thrown.expect(ParseError.class);
		parser.parse(input, grammar, "Id");
	}
	
	@Test
	public void testWhile() throws ParseError {
		Input input = Input.fromString("while");
		GLLParser parser = ParserFactory.newParser(grammar, input);

		thrown.expect(ParseError.class);
		parser.parse(input, grammar, "Id");
	}

}
