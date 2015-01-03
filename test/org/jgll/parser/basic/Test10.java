package org.jgll.parser.basic;

import static org.jgll.util.Configurations.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.IntermediateNode;
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
 * S ::= A B C
 *     | A B D
 *     
 * A ::= 'a'
 * B ::= 'b'
 * C ::= 'c'
 * D ::= 'c'
 * 
 * @author Ali Afroozeh
 *
 */
@RunWith(Parameterized.class)
public class Test10 {

	private Grammar grammar;

	private Nonterminal S = Nonterminal.withName("S");
	private Nonterminal A = Nonterminal.withName("A");
	private Nonterminal B = Nonterminal.withName("B");
	private Nonterminal C = Nonterminal.withName("C");
	private Nonterminal D = Nonterminal.withName("D");
	private Character a = Character.from('a');
	private Character b = Character.from('b');
	private Character c = Character.from('c');
	
	private GLLParser parser;
	
	private Input input;
	
	private ExpectedSPPF expectedSPPF;
	
	public Test10(GLLParser parser, Input input, ExpectedSPPF expectedSPPF) {
		this.parser = parser;
		this.input = input;
		this.expectedSPPF = expectedSPPF;
	}

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
        		{ ParserFactory.getParser(DEFAULT),  Input.fromString("abc"), (ExpectedSPPF) Test10::expectedSPPF },
        		{ ParserFactory.getParser(CONFIG_1), Input.fromString("abc"), (ExpectedSPPF) Test10::expectedSPPF },
        		{ ParserFactory.getParser(CONFIG_2), Input.fromString("abc"), (ExpectedSPPF) Test10::expectedSPPF },
        		{ ParserFactory.getParser(CONFIG_3), Input.fromString("abc"), (ExpectedSPPF) Test10::expectedSPPF }
           });
    }

	
	@Before
	public void init() {
		Rule r1 = Rule.builder(S).addSymbols(A, B, C).build();
		Rule r2 = Rule.builder(S).addSymbols(A, B, D).build();
		Rule r3 = Rule.builder(A).addSymbol(a).build();
		Rule r4 = Rule.builder(B).addSymbol(b).build();
		Rule r5 = Rule.builder(C).addSymbol(c).build();
		Rule r6 = Rule.builder(D).addSymbol(c).build();
		
		grammar = new Grammar.Builder().addRule(r1).addRule(r2).addRule(r3).addRule(r4).addRule(r5).addRule(r6).build();
	}
	
	@Test
	public void test() {
		ParseResult result = parser.parse(input, grammar, "S");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(expectedSPPF.get(parser.getRegistry())));
	}
	
	private static SPPFNode expectedSPPF(GrammarSlotRegistry registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("S", 0, 0, 3).init();
		PackedNode node2 = factory.createPackedNode("S ::= A B C .", 2, node1);
		IntermediateNode node3 = factory.createIntermediateNode("S ::= A B . C", 0, 2).init();
		PackedNode node4 = factory.createPackedNode("S ::= A B . C", 1, node3);
		NonterminalNode node5 = factory.createNonterminalNode("A", 0, 0, 1).init();
		PackedNode node6 = factory.createPackedNode("A ::= a .", 1, node5);
		TerminalNode node7 = factory.createTerminalNode("a", 0, 1);
		node6.addChild(node7);
		node5.addChild(node6);
		NonterminalNode node8 = factory.createNonterminalNode("B", 0, 1, 2).init();
		PackedNode node9 = factory.createPackedNode("B ::= b .", 2, node8);
		TerminalNode node10 = factory.createTerminalNode("b", 1, 2);
		node9.addChild(node10);
		node8.addChild(node9);
		node4.addChild(node5);
		node4.addChild(node8);
		node3.addChild(node4);
		NonterminalNode node11 = factory.createNonterminalNode("C", 0, 2, 3).init();
		PackedNode node12 = factory.createPackedNode("C ::= c .", 3, node11);
		TerminalNode node13 = factory.createTerminalNode("c", 2, 3);
		node12.addChild(node13);
		node11.addChild(node12);
		node2.addChild(node3);
		node2.addChild(node11);
		PackedNode node14 = factory.createPackedNode("S ::= A B D .", 2, node1);
		IntermediateNode node15 = factory.createIntermediateNode("S ::= A B . D", 0, 2).init();
		PackedNode node16 = factory.createPackedNode("S ::= A B . D", 1, node15);
		node16.addChild(node5);
		node16.addChild(node8);
		node15.addChild(node16);
		NonterminalNode node19 = factory.createNonterminalNode("D", 0, 2, 3).init();
		PackedNode node20 = factory.createPackedNode("D ::= c .", 3, node19);
		node20.addChild(node13);
		node19.addChild(node20);
		node14.addChild(node15);
		node14.addChild(node19);
		node1.addChild(node2);
		node1.addChild(node14);
		return node1;
	}
}
	