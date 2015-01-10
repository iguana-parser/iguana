package org.jgll.parser.basic;

import static org.jgll.util.Configurations.*;
import static org.junit.Assert.*;

import java.util.Collection;
import java.util.List;
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
import org.jgll.sppf.IntermediateNode;
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
 * A ::= 'a' B 'c'
 *     | C
 *     
 * B ::= 'b'
 * 
 * C ::= 'a' C
 *     | 'c'
 * 
 * @author Ali Afroozeh
 *
 */
@RunWith(Parameterized.class)
public class Test8 extends AbstractParserTest {


	@Parameters
    public static Collection<Object[]> data() {
		List<Object[]> parameters = 
			all_configs.stream().map(c -> new Object[] {
	    		getInput1(), 
	    		getGrammar(), 
	    		getStartSymbol(),
	    		ParserFactory.getParser(c, getInput1(), getGrammar()),
	    		(Function<GrammarRegistry, ParseResult>) Test8::getParseResult1
	    	}).collect(Collectors.toList());
		parameters.addAll(all_configs.stream().map(c -> new Object[] {
	    		getInput2(), 
	    		getGrammar(), 
	    		getStartSymbol(),
	    		ParserFactory.getParser(c, getInput1(), getGrammar()),
	    		(Function<GrammarRegistry, ParseResult>) Test8::getParseResult2
	    	}).collect(Collectors.toList()));
		return parameters;
    }
    
    private static Input getInput1() {
    	return Input.fromString("abc");
    }
    
    private static Input getInput2() {
    	return Input.fromString("aaaac");
    }
    
    private static Nonterminal getStartSymbol() {
    	return Nonterminal.withName("A");
    }
	
	private static Grammar getGrammar() {
		Nonterminal A = Nonterminal.withName("A");
		Nonterminal B = Nonterminal.withName("B");
		Nonterminal C = Nonterminal.withName("C");
		Character a = Character.from('a');
		Character b = Character.from('b');
		Character c = Character.from('c');
		Rule r1 = Rule.builder(A).addSymbols(a, B, c).build();
		Rule r2 = Rule.builder(A).addSymbol(C).build();
		Rule r3 = Rule.builder(B).addSymbol(b).build();
		Rule r4 = Rule.builder(C).addSymbols(a, C).build();
		Rule r5 = Rule.builder(C).addSymbol(c).build();
		return Grammar.builder().addRule(r1).addRule(r2).addRule(r3).addRule(r4).addRule(r5).build();
	}
	
	@Test
	public void testNullable() {
		assertFalse(grammar.isNullable(Nonterminal.withName("A")));
		assertFalse(grammar.isNullable(Nonterminal.withName("B")));
		assertFalse(grammar.isNullable(Nonterminal.withName("C")));
	}
	
	@Test
	public void testLL1() {
//		assertTrue(grammarGraph.isLL1SubGrammar(A));
//		assertTrue(grammarGraph.isLL1SubGrammar(B));
//		assertTrue(grammarGraph.isLL1SubGrammar(C));
	}
	
	private static ParseSuccess getParseResult1(GrammarRegistry registry) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(8)
				.setGSSNodesCount(4)
				.setGSSEdgesCount(3)
				.setNonterminalNodesCount(2)
				.setTerminalNodesCount(3)
				.setIntermediateNodesCount(1)
				.setPackedNodesCount(3)
				.setAmbiguousNodesCount(0).build();
		return new ParseSuccess(expectedSPPF1(registry), statistics);
	}
	
	private static ParseSuccess getParseResult2(GrammarRegistry registry) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(18)
				.setGSSNodesCount(7)
				.setGSSEdgesCount(6)
				.setNonterminalNodesCount(6)
				.setTerminalNodesCount(5)
				.setIntermediateNodesCount(0)
				.setPackedNodesCount(6)
				.setAmbiguousNodesCount(0).build();
		return new ParseSuccess(expectedSPPF2(registry), statistics);
	}
	
	private static NonterminalNode expectedSPPF1(GrammarRegistry registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("A", 0, 3);
		PackedNode node2 = factory.createPackedNode("A ::= a B c .", 2, node1);
		IntermediateNode node3 = factory.createIntermediateNode("A ::= a B . c", 0, 2);
		PackedNode node4 = factory.createPackedNode("A ::= a B . c", 1, node3);
		TerminalNode node5 = factory.createTerminalNode("a", 0, 1);
		NonterminalNode node6 = factory.createNonterminalNode("B", 1, 2);
		PackedNode node7 = factory.createPackedNode("B ::= b .", 2, node6);
		TerminalNode node8 = factory.createTerminalNode("b", 1, 2);
		node7.addChild(node8);
		node6.addChild(node7);
		node4.addChild(node5);
		node4.addChild(node6);
		node3.addChild(node4);
		TerminalNode node9 = factory.createTerminalNode("c", 2, 3);
		node2.addChild(node3);
		node2.addChild(node9);
		node1.addChild(node2);
		return node1;
	}
	
	private static NonterminalNode expectedSPPF2(GrammarRegistry registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("A", 0, 5);
		PackedNode node2 = factory.createPackedNode("A ::= C .", 5, node1);
		NonterminalNode node3 = factory.createNonterminalNode("C", 0, 5);
		PackedNode node4 = factory.createPackedNode("C ::= a C .", 1, node3);
		TerminalNode node5 = factory.createTerminalNode("a", 0, 1);
		NonterminalNode node6 = factory.createNonterminalNode("C", 1, 5);
		PackedNode node7 = factory.createPackedNode("C ::= a C .", 2, node6);
		TerminalNode node8 = factory.createTerminalNode("a", 1, 2);
		NonterminalNode node9 = factory.createNonterminalNode("C", 2, 5);
		PackedNode node10 = factory.createPackedNode("C ::= a C .", 3, node9);
		TerminalNode node11 = factory.createTerminalNode("a", 2, 3);
		NonterminalNode node12 = factory.createNonterminalNode("C", 3, 5);
		PackedNode node13 = factory.createPackedNode("C ::= a C .", 4, node12);
		TerminalNode node14 = factory.createTerminalNode("a", 3, 4);
		NonterminalNode node15 = factory.createNonterminalNode("C", 4, 5);
		PackedNode node16 = factory.createPackedNode("C ::= c .", 5, node15);
		TerminalNode node17 = factory.createTerminalNode("c", 4, 5);
		node16.addChild(node17);
		node15.addChild(node16);
		node13.addChild(node14);
		node13.addChild(node15);
		node12.addChild(node13);
		node10.addChild(node11);
		node10.addChild(node12);
		node9.addChild(node10);
		node7.addChild(node8);
		node7.addChild(node9);
		node6.addChild(node7);
		node4.addChild(node5);
		node4.addChild(node6);
		node3.addChild(node4);
		node2.addChild(node3);
		node1.addChild(node2);
		return node1;
	}
	
}
	