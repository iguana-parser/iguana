package org.jgll.grammar.conditions;

import static org.jgll.grammar.condition.ConditionFactory.notFollow;
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
 * S ::= Label !>> ":"
 *
 * Label ::= [a-z]+ !>> [a-z]
 * 
 * @author Ali Afroozeh
 *
 */
public class FollowRestrictionTest {
	
	private Grammar grammar;
	private GLLParser rdParser;

	
	@Before
	public void init() {
		Nonterminal S = new Nonterminal("S");
		Nonterminal Label = new Nonterminal("Label");
		Terminal az = new Range('a', 'z');
		
		GrammarBuilder builder = new GrammarBuilder();
		
		Rule r1 = new Rule(S, Label.addCondition(notFollow(new Keyword(":", new int[] {':'}))));
		
		Rule r2 = new Rule(Label, new Plus(az).addCondition(notFollow(az)));
		
		Iterable<Rule> rules = EBNFUtil.rewrite(list(r1, r2));
		builder.addRules(rules);

		grammar = builder.build();
		rdParser =  ParserFactory.createRecursiveDescentParser(grammar);
	}
	
	@org.junit.Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void testRDParser() throws Exception {
		thrown.expect(ParseError.class);
		thrown.expectMessage("Parse error at line:1 column:4");
		rdParser.parse(Input.fromString("abc:"), grammar, "S");
	}

}
