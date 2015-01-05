package org.jgll.parser.basic;

import static org.jgll.util.Configuration.*;
import static org.jgll.util.Configurations.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

import org.jgll.AbstractParserTest;
import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarRegistry;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParseSuccess;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNodeFactory;
import org.jgll.sppf.TerminalNode;
import org.jgll.util.Configuration;
import org.jgll.util.Input;
import org.jgll.util.ParseStatistics;
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
public class Test1 extends AbstractParserTest {
	
    public Test1(Configuration config, Input input, Grammar grammar,
			Function<GrammarRegistry, ParseResult> expectedResult) {
		super(config, input, grammar, expectedResult);
	}

	@Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
        		{ DEFAULT,  Input.empty(), getGrammar(), (Function<GrammarRegistry, ParseResult>) Test1::getParseResult },
        		{ CONFIG_1, Input.empty(), getGrammar(), (Function<GrammarRegistry, ParseResult>) Test1::getParseResult },
        		{ CONFIG_2, Input.empty(), getGrammar(), (Function<GrammarRegistry, ParseResult>) Test1::getParseResult },
        		{ CONFIG_3, Input.empty(), getGrammar(), (Function<GrammarRegistry, ParseResult>) Test1::getParseResult }
           });
    }
	
	public static Grammar getGrammar() {
		Nonterminal A = Nonterminal.withName("A");
		Rule r1 = Rule.builder(A).build();
		return Grammar.builder().addRule(r1).build();
	}
	
	@Test
	public void testNullable() {
		assertTrue(grammar.isNullable(Nonterminal.withName("A")));
	}
	
	@Test
	public void testParser() {
		ParseResult result = parser.parse(input, grammar, "A");
		assertEquals(expectedResult.apply(parser.getRegistry()), result);
	}
	
	private static ParseSuccess getParseResult(GrammarRegistry registry) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(1)
				.setGSSNodesCount(1)
				.setGSSEdgesCount(0)
				.setNonterminalNodesCount(1)
				.setTerminalNodesCount(1)
				.setIntermediateNodesCount(0)
				.setPackedNodesCount(1)
				.setAmbiguousNodesCount(0).build();
		return new ParseSuccess(expectedSPPF(registry), statistics);
	}
	
	private static NonterminalNode expectedSPPF(GrammarRegistry registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("A", 0, 0, 0);
		PackedNode node2 = factory.createPackedNode("A ::= .", 0, node1);
		TerminalNode node3 = factory.createEpsilonNode(0);
		node2.addChild(node3);
		node1.addChild(node2);
		return node1;
	}

}