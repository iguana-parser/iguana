package org.jgll.grammar.conditions;

import static org.jgll.grammar.condition.ConditionFactory.*;
import static org.jgll.util.collections.CollectionsUtil.*;

import org.jgll.grammar.AbstractGrammarTest;
import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarBuilder;
import org.jgll.grammar.Keyword;
import org.jgll.grammar.Nonterminal;
import org.jgll.grammar.Plus;
import org.jgll.grammar.Range;
import org.jgll.grammar.Rule;
import org.jgll.grammar.Terminal;
import org.jgll.grammar.ebnf.EBNFUtil;
import org.jgll.parser.ParseError;
import org.jgll.util.Input;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * 
 * Id ::= [a-z]+ !>> [a-z] \ { "if", "when", "do", "while"}
 * 
 * @author Ali Afroozeh
 *
 */
public class KeywordExclusionTest extends AbstractGrammarTest {

	@Override
	protected Grammar initGrammar() {
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

		return builder.build();
	}
	
	@org.junit.Rule
	public ExpectedException thrown = ExpectedException.none();

	
	@Test
	public void testWhen() throws ParseError {
		thrown.expect(ParseError.class);
		thrown.expectMessage("Parse error at line:1 column:4");
		levelParser.parse(Input.fromString("when"), grammar, "Id");
	}
	
	@Test
	public void testIf() throws ParseError {
		thrown.expect(ParseError.class);
		thrown.expectMessage("Parse error at line:1 column:2");
		levelParser.parse(Input.fromString("if"), grammar, "Id");
	}

	
	@Test
	public void testDo() throws ParseError {
		thrown.expect(ParseError.class);
		thrown.expectMessage("Parse error at line:1 column:2");
		levelParser.parse(Input.fromString("do"), grammar, "Id");
	}

	
	@Test
	public void testWhile() throws ParseError {
		thrown.expect(ParseError.class);
		thrown.expectMessage("Parse error at line:1 column:5");
		levelParser.parse(Input.fromString("while"), grammar, "Id");
	}


}
