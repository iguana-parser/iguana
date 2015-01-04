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
 * 
 * A ::= A A | a | epsilon
 * 
 * 
 * @author Ali Afroozeh
 *
 */
@RunWith(Parameterized.class)
public class Test12 extends AbstractParserTest {
	
	public Test12(
			Configuration config,
			Input input,
			Grammar grammar,
			Function<GrammarRegistry, SPPFNode> expectedSPPF) {
		super(config, input, grammar, expectedSPPF);
	}


	@Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
        		{ DEFAULT,  Input.empty(), getGrammar(), (Function<GrammarRegistry, SPPFNode>) Test12::expectedSPPF1 },
        		{ CONFIG_1, Input.empty(), getGrammar(), (Function<GrammarRegistry, SPPFNode>) Test12::expectedSPPF1 },
        		{ CONFIG_2, Input.empty(), getGrammar(), (Function<GrammarRegistry, SPPFNode>) Test12::expectedSPPF1 },
        		{ CONFIG_3, Input.empty(), getGrammar(), (Function<GrammarRegistry, SPPFNode>) Test12::expectedSPPF1 },
        		{ DEFAULT,  Input.fromString("a"), getGrammar(), (Function<GrammarRegistry, SPPFNode>) Test12::expectedSPPF1 },
        		{ CONFIG_1, Input.fromString("a"), getGrammar(), (Function<GrammarRegistry, SPPFNode>) Test12::expectedSPPF1 },
        		{ CONFIG_2, Input.fromString("a"), getGrammar(), (Function<GrammarRegistry, SPPFNode>) Test12::expectedSPPF1 },
        		{ CONFIG_3, Input.fromString("a"), getGrammar(), (Function<GrammarRegistry, SPPFNode>) Test12::expectedSPPF1 }
           });
    }


	private static Grammar getGrammar() {
		Nonterminal A = Nonterminal.withName("A");
		Rule r1 = Rule.builder(A).addSymbols(A, A).build();
		Rule r2 = Rule.builder(A).addSymbol(Character.from('a')).build();
		Rule r3 = Rule.builder(A).build();
		return Grammar.builder().addRule(r1).addRule(r2).addRule(r3).build();
	}
	
	@Test
	public void testParser() {
		ParseResult result = parser.parse(input, grammar, "A");
		assertTrue(result.isParseSuccess());
	}
	
	private static SPPFNode expectedSPPF1(GrammarRegistry registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("A", 0, 1);
		PackedNode node2 = factory.createPackedNode("A ::= a .", 0, node1);
		TerminalNode node3 = factory.createTerminalNode("a", 0, 1);
		node2.addChild(node3);
		PackedNode node4 = factory.createPackedNode("A ::= A A .", 1, node1);
		NonterminalNode node5 = factory.createNonterminalNode("A", 1, 1);
		PackedNode node6 = factory.createPackedNode("A ::= .", 1, node5);
		PackedNode node7 = factory.createPackedNode("A ::= A A .", 1, node5);
		node7.addChild(node5);
		node7.addChild(node5);
		node5.addChild(node6);
		node5.addChild(node7);
		node4.addChild(node1);
		node4.addChild(node5);
		PackedNode node8 = factory.createPackedNode("A ::= A A .", 0, node1);
		NonterminalNode node9 = factory.createNonterminalNode("A", 0, 0);
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
