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
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.util.Input;
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
public class FollowRestrictionTest extends AbstractGrammarTest {

	@Override
	protected Grammar initGrammar() {
		Nonterminal S = new Nonterminal("S");
		Nonterminal Label = new Nonterminal("Label");
		Terminal az = new Range('a', 'z');
		
		GrammarBuilder builder = new GrammarBuilder();
		
		Rule r1 = new Rule(S, Label.addCondition(notFollow(new Keyword(":", new int[] {':'}))));
		
		Rule r2 = new Rule(Label, new Plus(az).addCondition(notFollow(az)));
		
		Iterable<Rule> rules = EBNFUtil.rewrite(list(r1, r2));
		builder.addRules(rules);

		return builder.build();
	}
	
	@org.junit.Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void test() throws Exception {
		thrown.expect(ParseError.class);
		thrown.expectMessage("Parse error at line:1 column:4");
		NonterminalSymbolNode sppf = levelParser.parse(Input.fromString("abc:"), grammar, "S");
		generateSPPFGraphWithoutIntermeiateNodes(sppf);
	}

}
