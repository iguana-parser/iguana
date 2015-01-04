package org.jgll.parser.basic;

import static org.jgll.util.Configurations.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarRegistry;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.SPPFNodeFactory;
import org.jgll.sppf.TerminalNode;
import org.jgll.util.Input;
import org.jgll.util.function.ExpectedSPPF;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * 
 * A ::= A A | a | epsilon
 * 
 * 
 * @author Ali Afroozeh
 *
 */
@RunWith(Parameterized.class)
public class Test12 {
	
	private Nonterminal A = Nonterminal.withName("A");
	
	private Grammar grammar;
	
	private GLLParser parser;
	
	private Input input;
	
	private ExpectedSPPF expectedSPPF;
	
	public Test12(GLLParser parser, Input input, ExpectedSPPF expectedSPPF) {
		this.parser = parser;
		this.input = input;
		this.expectedSPPF = expectedSPPF;
	}
	
	@Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
        		{ ParserFactory.getParser(DEFAULT),  Input.empty(), (ExpectedSPPF) Test12::expectedSPPF1 },
        		{ ParserFactory.getParser(CONFIG_1), Input.empty(), (ExpectedSPPF) Test12::expectedSPPF1 },
        		{ ParserFactory.getParser(CONFIG_2), Input.empty(), (ExpectedSPPF) Test12::expectedSPPF1 },
        		{ ParserFactory.getParser(CONFIG_3), Input.empty(), (ExpectedSPPF) Test12::expectedSPPF1 },
        		{ ParserFactory.getParser(DEFAULT),  Input.fromString("a"), (ExpectedSPPF) Test12::expectedSPPF1 },
        		{ ParserFactory.getParser(CONFIG_1), Input.fromString("a"), (ExpectedSPPF) Test12::expectedSPPF1 },
        		{ ParserFactory.getParser(CONFIG_2), Input.fromString("a"), (ExpectedSPPF) Test12::expectedSPPF1 },
        		{ ParserFactory.getParser(CONFIG_3), Input.fromString("a"), (ExpectedSPPF) Test12::expectedSPPF1 }
           });
    }


	@Before
	public void init() {
		Rule r1 = Rule.builder(A).addSymbols(A, A).build();
		Rule r2 = Rule.builder(A).addSymbol(Character.from('a')).build();
		Rule r3 = Rule.builder(A).build();
		grammar = new Grammar.Builder().addRule(r1).addRule(r2).addRule(r3).build();
	}
	
	@Test
	public void testParser1() {
		ParseResult result = parser.parse(input, grammar, "A");
		assertTrue(result.isParseSuccess());
		// TODO: stackoverflow bug due to the cycle in the SPPF. Fix it later!
//		assertTrue(sppf.deepEquals(getSPPF1()));
	}
		
	@Test
	public void testParser2() {
		ParseResult result = parser.parse(input, grammar, "A");
		assertTrue(result.isParseSuccess());
	}
	
	private static SPPFNode expectedSPPF1(GrammarRegistry registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("A", 0, 1).init();
		PackedNode node2 = factory.createPackedNode("A ::= a .", 0, node1);
		TerminalNode node3 = factory.createTerminalNode("a", 0, 1);
		node2.addChild(node3);
		PackedNode node4 = factory.createPackedNode("A ::= A A .", 1, node1);
		NonterminalNode node5 = factory.createNonterminalNode("A", 1, 1).init();
		PackedNode node6 = factory.createPackedNode("A ::= .", 1, node5);
		PackedNode node7 = factory.createPackedNode("A ::= A A .", 1, node5);
		node7.addChild(node5);
		node7.addChild(node5);
		node5.addChild(node6);
		node5.addChild(node7);
		node4.addChild(node1);
		node4.addChild(node5);
		PackedNode node8 = factory.createPackedNode("A ::= A A .", 0, node1);
		NonterminalNode node9 = factory.createNonterminalNode("A", 0, 0).init();
		PackedNode node10 = factory.createPackedNode("A ::= .", 0, node9);
		PackedNode node11 = factory.createPackedNode("A ::= A A .", 0, node9);
		node11.addChild(node9);
		node11.addChild(node9);
		node9.addChild(node10);
		node9.addChild(node11);
		node8.addChild(node9);
		node8.addChild(node1);
		node1.addChild(node2);
		node1.addChild(node4);
		node1.addChild(node8);
		return node1;
	}
}
