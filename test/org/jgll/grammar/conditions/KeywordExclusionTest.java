package org.jgll.grammar.conditions;

import static org.jgll.grammar.condition.ConditionFactory.notFollow;
import static org.jgll.grammar.condition.ConditionFactory.notMatch;
import static org.jgll.util.CollectionsUtil.list;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarBuilder;
import org.jgll.grammar.ebnf.EBNFUtil;
import org.jgll.grammar.symbol.Keyword;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Plus;
import org.jgll.grammar.symbol.Range;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Terminal;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
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
	private GLLParser parser;

	@Before
	public void init() {
		Nonterminal Id = new Nonterminal("Id");
		Terminal az = new Range('a', 'z');
		
		Keyword iff = new Keyword("if", "if");
		Keyword when = new Keyword("when", "when");
		Keyword doo = new Keyword("do", "do");
		Keyword whilee = new Keyword("while", "while");
		
		GrammarBuilder builder = new GrammarBuilder();
		
		Rule r1 = new Rule(Id, new Plus(az).addCondition(notFollow(az)).addCondition(notMatch(iff, when, doo, whilee)));
		
		Iterable<Rule> rules = EBNFUtil.rewrite(list(r1));
		builder.addRules(rules);

		grammar = builder.build();
		
		parser = ParserFactory.createRecursiveDescentParser(grammar);
	}
	
	@org.junit.Rule
	public ExpectedException thrown = ExpectedException.none();

	
	@Test
	public void testWhen() throws ParseError {
		thrown.expect(ParseError.class);
		thrown.expectMessage("Parse error at line:1 column:4");
		parser.parse(Input.fromString("when"), grammar, "Id");
	}
	
	@Test
	public void testIf() throws ParseError {
		thrown.expect(ParseError.class);
		thrown.expectMessage("Parse error at line:1 column:2");
		parser.parse(Input.fromString("if"), grammar, "Id");
	}
	
	@Test
	public void testDo() throws ParseError {
		thrown.expect(ParseError.class);
		thrown.expectMessage("Parse error at line:1 column:2");
		parser.parse(Input.fromString("do"), grammar, "Id");
	}
	
	@Test
	public void testWhile() throws ParseError {
		thrown.expect(ParseError.class);
		thrown.expectMessage("Parse error at line:1 column:5");
		parser.parse(Input.fromString("while"), grammar, "Id");
	}

}
