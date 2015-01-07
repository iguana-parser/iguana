package org.jgll.parser.basic;

import static org.jgll.util.Configurations.*;

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
public class Test9 extends AbstractParserTest {
	
	@Parameters
    public static Collection<Object[]> data() {
		List<Object[]> parameters = newConfigs.stream().map(c -> new Object[] {
	    		getInput(), 
	    		getGrammar(), 
	    		getStartSymbol(),
	    		ParserFactory.getParser(c, getInput(), getGrammar()),
	    		(Function<GrammarRegistry, ParseResult>) Test9::getNewParseResult
	    	}).collect(Collectors.toList());
		parameters.addAll(originalConfigs.stream().map(c -> new Object[] {
	    		getInput(), 
	    		getGrammar(), 
	    		getStartSymbol(),
	    		ParserFactory.getParser(c, getInput(), getGrammar()),
	    		(Function<GrammarRegistry, ParseResult>) Test9::getOriginalParseResult
	    	}).collect(Collectors.toList()));
		return parameters;
    }
    
    private static Input getInput() {
    	return Input.fromString("aab");
    }
    
    private static Nonterminal getStartSymbol() {
    	return Nonterminal.withName("S");
    }
	
	private static Grammar getGrammar() {
		Nonterminal S = Nonterminal.withName("S");
		Nonterminal A = Nonterminal.withName("A");
		Character a = Character.from('a');
		Character b = Character.from('b');
		Character c = Character.from('c');
		Rule r1 = Rule.builder(S).addSymbols(a, A, c).build();
		Rule r2 = Rule.builder(S).addSymbols(a, A, b).build();
		Rule r3 = Rule.builder(A).addSymbol(a).build();
		return Grammar.builder().addRule(r1).addRule(r2).addRule(r3).build();
	}
	
	private static ParseSuccess getNewParseResult(GrammarRegistry registry) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(5)
				.setGSSNodesCount(2)
				.setGSSEdgesCount(2)
				.setNonterminalNodesCount(2)
				.setTerminalNodesCount(2)
				.setIntermediateNodesCount(2)
				.setPackedNodesCount(4)
				.setAmbiguousNodesCount(0).build();
		return new ParseSuccess(expectedSPPF(registry), statistics);
	}
	
	private static ParseSuccess getOriginalParseResult(GrammarRegistry registry) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(6)
				.setGSSNodesCount(3)
				.setGSSEdgesCount(2)
				.setNonterminalNodesCount(2)
				.setTerminalNodesCount(2)
				.setIntermediateNodesCount(2)
				.setPackedNodesCount(4)
				.setAmbiguousNodesCount(0).build();
		return new ParseSuccess(expectedSPPF(registry), statistics);
	}
	
	private static NonterminalNode expectedSPPF(GrammarRegistry registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("S", 0, 3);
		PackedNode node2 = factory.createPackedNode("S ::= a A b .", 2, node1);
		IntermediateNode node3 = factory.createIntermediateNode("S ::= a A . b", 0, 2);
		PackedNode node4 = factory.createPackedNode("S ::= a A . b", 1, node3);
		TerminalNode node5 = factory.createTerminalNode("a", 0, 1);
		NonterminalNode node6 = factory.createNonterminalNode("A", 1, 2);
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
	