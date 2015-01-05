package org.jgll.parser.basic;

import static org.jgll.util.Configurations.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

import org.jgll.AbstractParserTest;
import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarRegistry;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.ParseResult;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.SPPFNodeFactory;
import org.jgll.sppf.TerminalNode;
import org.jgll.util.Configuration;
import org.jgll.util.Input;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * S ::= A A b
 *     
 * A ::= 'a' | epsilon
 * 
 * @author Ali Afroozeh
 *
 */
@RunWith(Parameterized.class)
public class Test11 extends AbstractParserTest {

	public Test11(
			Configuration config,
			Input input,
			Grammar grammar,
			Function<GrammarRegistry, SPPFNode> expectedSPPF) {
		super(config, input, grammar, expectedSPPF);
	}

	@Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
        		{ DEFAULT,  Input.fromString("ab"), getGrammar(), (Function<GrammarRegistry, SPPFNode>) Test11::expectedSPPF },
        		{ CONFIG_1, Input.fromString("ab"), getGrammar(), (Function<GrammarRegistry, SPPFNode>) Test11::expectedSPPF },
        		{ CONFIG_2, Input.fromString("ab"), getGrammar(), (Function<GrammarRegistry, SPPFNode>) Test11::expectedSPPF },
        		{ CONFIG_3, Input.fromString("ab"), getGrammar(), (Function<GrammarRegistry, SPPFNode>) Test11::expectedSPPF }
           });
    }
	
	private static Grammar getGrammar() {
		Nonterminal S = Nonterminal.withName("S");
		Nonterminal A = Nonterminal.withName("A");
		Character a = Character.from('a');
		Character b = Character.from('b');
		Rule r1 = Rule.builder(S).addSymbols(A, A, b).build();
		Rule r2 = Rule.builder(A).addSymbol(a).build();
		Rule r3 = Rule.builder(A).build();
		return Grammar.builder().addRule(r1).addRule(r2).addRule(r3).build();
	}
	
	@Test
	public void testParser() {
		ParseResult result = parser.parse(input, grammar, "S");
		assertTrue(result.isParseSuccess());
		assertEquals(1, result.asParseSuccess().getParseStatistics().getCountAmbiguousNodes());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(expectedSPPF.apply(parser.getRegistry())));
	}
	
	private static SPPFNode expectedSPPF(GrammarRegistry registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("S", 0, 0, 2);
		PackedNode node2 = factory.createPackedNode("S ::= A A b .", 1, node1);
		IntermediateNode node3 = factory.createIntermediateNode("S ::= A A . b", 0, 1);
		PackedNode node4 = factory.createPackedNode("S ::= A A . b", 1, node3);
		NonterminalNode node5 = factory.createNonterminalNode("A", 0, 0, 1);
		PackedNode node6 = factory.createPackedNode("A ::= a .", 1, node5);
		TerminalNode node7 = factory.createTerminalNode("a", 0, 1);
		node6.addChild(node7);
		node5.addChild(node6);
		NonterminalNode node8 = factory.createNonterminalNode("A", 0, 1, 1);
		PackedNode node9 = factory.createPackedNode("A ::= .", 1, node8);
		TerminalNode node10 = factory.createEpsilonNode(1);
		node9.addChild(node10);
		node8.addChild(node9);
		node4.addChild(node5);
		node4.addChild(node8);
		PackedNode node11 = factory.createPackedNode("S ::= A A . b", 0, node3);
		NonterminalNode node12 = factory.createNonterminalNode("A", 0, 0, 0);
		PackedNode node13 = factory.createPackedNode("A ::= .", 0, node12);
		TerminalNode node14 = factory.createEpsilonNode(0);
		node13.addChild(node14);
		node12.addChild(node13);
		node11.addChild(node12);
		node11.addChild(node5);
		node3.addChild(node4);
		node3.addChild(node11);
		TerminalNode node16 = factory.createTerminalNode("b", 1, 2);
		node2.addChild(node3);
		node2.addChild(node16);
		node1.addChild(node2);
		return node1;
	}
}
	