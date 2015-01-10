package org.jgll.parser.basic;

import static org.jgll.util.Configurations.*;
import static org.junit.Assert.*;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jgll.AbstractParserTest;
import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarRegistry;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParseSuccess;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNodeFactory;
import org.jgll.sppf.TerminalNode;
import org.jgll.util.Input;
import org.jgll.util.ParseStatistics;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * A ::= B C
 * B ::= 'b'
 * C ::= 'c'
 * 
 * @author Ali Afroozeh
 *
 */
@RunWith(Parameterized.class)
public class Test6 extends AbstractParserTest {
	
	@Parameters
    public static Collection<Object[]> data() {
		return all_configs.stream().map(c -> new Object[] {
	    		getInput(), 
	    		getGrammar(), 
	    		getStartSymbol(),
	    		ParserFactory.getParser(c, getInput(), getGrammar()),
	    		(Function<GrammarRegistry, ParseResult>) Test6::getParseResult
	    	}).collect(Collectors.toList());
    }
    
    private static Input getInput() {
    	return Input.fromString("bc");
    }
	
	private static Grammar getGrammar() {
		Nonterminal A = Nonterminal.withName("A");
		Nonterminal B = Nonterminal.withName("B");
		Nonterminal C = Nonterminal.withName("C");
		Character b = Character.from('b');
		Character c = Character.from('c');

		Rule r1 = Rule.builder(A).addSymbols(B, C).build();
		Rule r2 = Rule.builder(B).addSymbol(b).build();
		Rule r3 = Rule.builder(C).addSymbol(c).build();
		
		return Grammar.builder().addRule(r1).addRule(r2).addRule(r3).build();
	}
	
	@Test
	public void testNullable() {
		assertFalse(grammar.isNullable(Nonterminal.withName("A")));
		assertFalse(grammar.isNullable(Nonterminal.withName("B")));
		assertFalse(grammar.isNullable(Nonterminal.withName("C")));
	}
	
	private static Nonterminal getStartSymbol() {
		return Nonterminal.withName("A");
	}
	
	private static ParseSuccess getParseResult(GrammarRegistry registry) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(5)
				.setGSSNodesCount(3)
				.setGSSEdgesCount(2)
				.setNonterminalNodesCount(3)
				.setTerminalNodesCount(2)
				.setIntermediateNodesCount(0)
				.setPackedNodesCount(3)
				.setAmbiguousNodesCount(0).build();
		return new ParseSuccess(expectedSPPF(registry), statistics);
	}
	
	@Test
	public void testLL1() {
//		assertTrue(grammarGraph.isLL1SubGrammar(A));
//		assertTrue(grammarGraph.isLL1SubGrammar(B));
//		assertTrue(grammarGraph.isLL1SubGrammar(C));
	}
		
	private static NonterminalNode expectedSPPF(GrammarRegistry registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("A", 0, 2);
		PackedNode node2 = factory.createPackedNode("A ::= B C .", 1, node1);
		NonterminalNode node3 = factory.createNonterminalNode("B", 0, 1);
		PackedNode node4 = factory.createPackedNode("B ::= b .", 1, node3);
		TerminalNode node5 = factory.createTerminalNode("b", 0, 1);
		node4.addChild(node5);
		node3.addChild(node4);
		NonterminalNode node6 = factory.createNonterminalNode("C", 1, 2);
		PackedNode node7 = factory.createPackedNode("C ::= c .", 2, node6);
		TerminalNode node8 = factory.createTerminalNode("c", 1, 2);
		node7.addChild(node8);
		node6.addChild(node7);
		node2.addChild(node3);
		node2.addChild(node6);
		node1.addChild(node2);
		return node1;
	}
	
}