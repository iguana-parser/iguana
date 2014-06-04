package org.jgll.parser;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.condition.ContextFreeCondition;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Group;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.transformation.EBNFToBNF;
import org.jgll.sppf.SPPFNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * S ::= a S b S 
 * 	   | a S \ a S b S 
 *     | s
 * 
 * @author Ali Afroozeh
 * 
 */
public class DanglingElseGrammar2 {

	private Grammar grammar;

	private Nonterminal S = Nonterminal.withName("S");
	private Character s = Character.from('s');
	private Character a = Character.from('a');
	private Character b = Character.from('b');
	private Group group = Group.of(a, S);

	@Before
	public void createGrammar() {

		Grammar.Builder builder = new Grammar.Builder();

		Rule rule1 = new Rule(S, list(group.builder().addCondition(ContextFreeCondition.notMatch(a, S, b, S)).build()));
		builder.addRule(rule1);
		
		Rule rule2 = new Rule(S, list(a, S, b, S));
		builder.addRule(rule2);

		Rule rule3 = new Rule(S, list(s));
		builder.addRule(rule3);
		
		EBNFToBNF ebnfToBNF = new EBNFToBNF();
		grammar = ebnfToBNF.transform(builder.build());
	}

	@Test
	public void test() {
		Input input = Input.fromString("aasbs");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "S");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getExpectedSPPF()));
	}

	private SPPFNode getExpectedSPPF() {
		return null;
	}

}
