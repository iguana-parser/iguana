package org.jgll.parser.basic;

import static org.jgll.util.Configurations.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarSlotRegistry;
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
 * A ::= epsilon
 * 
 * @author Ali Afroozeh
 *
 */

@RunWith(Parameterized.class)
public class Test1 {
	
	private Grammar grammar;

	private Nonterminal A = Nonterminal.withName("A");
	
	private GLLParser parser;
	
	private Input input;
	
	private ExpectedSPPF expectedSPPF;
	
	public Test1(GLLParser parser, Input input, ExpectedSPPF expectedSPPF) {
		this.parser = parser;
		this.input = input;
		this.expectedSPPF = expectedSPPF;
	}

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
        		{ ParserFactory.getParser(DEFAULT), Input.empty(), (ExpectedSPPF) Test1::expectedSPPF },
        		{ ParserFactory.getParser(CONFIG_1), Input.empty(), (ExpectedSPPF) Test1::expectedSPPF },
        		{ ParserFactory.getParser(CONFIG_2), Input.empty(), (ExpectedSPPF) Test1::expectedSPPF },
        		{ ParserFactory.getParser(CONFIG_3), Input.empty(), (ExpectedSPPF) Test1::expectedSPPF }
           });
    }
	
	@Before
	public void init() {
		Rule r1 = Rule.builder(A).build();
		grammar = new Grammar.Builder().addRule(r1).build();
	}
	
	@Test
	public void testNullable() {
		assertTrue(grammar.isNullable(A));
	}
	
	@Test
	public void testParser() {
		ParseResult result = parser.parse(input, grammar, "A");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(expectedSPPF.get(parser.getRegistry())));
	}
	
	private static SPPFNode expectedSPPF(GrammarSlotRegistry registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("A", 0, 0, 0).init();
		PackedNode node2 = factory.createPackedNode("A ::= .", 0, node1);
		TerminalNode node3 = factory.createEpsilonNode(0);
		node2.addChild(node3);
		node1.addChild(node2);
		return node1;
	}

}