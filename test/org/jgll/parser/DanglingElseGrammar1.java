package org.jgll.parser;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.condition.ContextFreeCondition;
import org.jgll.grammar.ebnf.EBNFUtil;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Group;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.sppf.SPPFNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * S ::= a S b S \ a S
 *     | a S
 *     | s
 * 
 * @author Ali Afroozeh
 *
 */
public class DanglingElseGrammar1 {

	private GrammarGraph grammarGraph;
	private GLLParser parser;
	
	private Nonterminal S = Nonterminal.withName("S");
	private Character s = Character.from('s');
	private Character a = Character.from('a');
	private Character b = Character.from('b');
	private Group group = Group.of(a, S, b, S);
	

	@Before
	public void createGrammar() {
		
		Grammar grammar = new Grammar();
		
		
		Rule rule1 = new Rule(S, list(a, S));
		grammar.addRule(rule1);
		
		Rule rule2 = new Rule(S, list(group.builder().addCondition(ContextFreeCondition.notMatch(a, S)).build()));
		
		grammar.addRules(EBNFUtil.rewrite(rule2));
		
		Rule rule3 = new Rule(S, list(s));
		grammar.addRule(rule3);
		
		grammarGraph = grammar.toGrammarGraph();
	}
	
	@Test
	public void test1() {
		Input input = Input.fromString("aasbs");
		parser = ParserFactory.newParser(grammarGraph, input);
		ParseResult result = parser.parse(input, grammarGraph, "S");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getExpectedSPPF1()));
	}
	
	@Test
	public void test2() {
		Input input = Input.fromString("aaaaasbsbsbs");
		parser = ParserFactory.newParser(grammarGraph, input);
		ParseResult result = parser.parse(Input.fromString("aaaaasbsbsbs"), grammarGraph, "S");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getExpectedSPPF2()));
	}
	
	private SPPFNode getExpectedSPPF1() {
		return null;
	}
	
	private SPPFNode getExpectedSPPF2() {
		return null;
	}

}
