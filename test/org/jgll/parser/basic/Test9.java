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
 * S ::= 'a' A 'c'
 *     | 'a' A 'b'
 *     
 * A ::= 'a'
 * 
 * @author Ali Afroozeh
 *
 */
@RunWith(Parameterized.class)
public class Test9 {

	private Grammar grammar;

	private Nonterminal S = Nonterminal.withName("S");
	private Nonterminal A = Nonterminal.withName("A");
	private Character a = Character.from('a');
	private Character b = Character.from('b');
	private Character c = Character.from('c');
	
	private GLLParser parser;
	
	private Input input;
	
	private ExpectedSPPF expectedSPPF;
	
	public Test9(GLLParser parser, Input input, ExpectedSPPF expectedSPPF) {
		this.parser = parser;
		this.input = input;
		this.expectedSPPF = expectedSPPF;
	}

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
        		{ ParserFactory.getParser(DEFAULT),  Input.fromString("aab"), (ExpectedSPPF) Test9::expectedSPPF },
        		{ ParserFactory.getParser(CONFIG_1), Input.fromString("aab"), (ExpectedSPPF) Test9::expectedSPPF },
        		{ ParserFactory.getParser(CONFIG_2), Input.fromString("aab"), (ExpectedSPPF) Test9::expectedSPPF },
        		{ ParserFactory.getParser(DEFAULT), Input.fromString("aab"), (ExpectedSPPF) Test9::expectedSPPF }
           });
    }
	
	@Before
	public void init() {
		Rule r1 = Rule.builder(S).addSymbols(a, A, c).build();
		Rule r2 = Rule.builder(S).addSymbols(a, A, b).build();
		Rule r3 = Rule.builder(A).addSymbol(a).build();
		grammar = new Grammar.Builder().addRule(r1).addRule(r2).addRule(r3).build();
	}
	
	@Test
	public void test() {
		ParseResult result = parser.parse(input, grammar, "S");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(expectedSPPF.get(parser.getRegistry())));
	}	

	private static SPPFNode expectedSPPF(GrammarSlotRegistry registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("S", 0, 3).init();
		PackedNode node2 = factory.createPackedNode("S ::= a A b .", 2, node1);
		IntermediateNode node3 = factory.createIntermediateNode("S ::= a A . b", 0, 2).init();
		PackedNode node4 = factory.createPackedNode("S ::= a A . b", 1, node3);
		TerminalNode node5 = factory.createTerminalNode("a", 0, 1);
		NonterminalNode node6 = factory.createNonterminalNode("A", 1, 2).init();
		PackedNode node7 = factory.createPackedNode("A ::= a .", 2, node6);
		TerminalNode node8 = factory.createTerminalNode("a", 1, 2);
		node7.addChild(node8);
		node6.addChild(node7);
		node4.addChild(node5);
		node4.addChild(node6);
		node3.addChild(node4);
		TerminalNode node9 = factory.createTerminalNode("b", 2, 3);
		node2.addChild(node3);
		node2.addChild(node9);
		node1.addChild(node2);
		return node1;
	}
}
	