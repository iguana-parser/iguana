package org.jgll.parser.basic;

import static org.jgll.util.Configurations.*;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jgll.AbstractParserTest;
import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParseSuccess;
import org.jgll.parser.ParserFactory;
import org.jgll.util.Input;
import org.jgll.util.ParseStatistics;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * 
 * @author Ali Afroozeh
 * 
 */
@RunWith(Parameterized.class)
public class Test16 extends AbstractParserTest {

	@Parameters
    public static Collection<Object[]> data() {
		return originalConfigs.stream().map(c -> new Object[] {
	    		getInput(), 
	    		getGrammar(), 
	    		getStartSymbol(),
	    		ParserFactory.getParser(c, getInput(), getGrammar()),
	    		(Function<GrammarGraph, ParseResult>) Test16::getOriginalParseResult
	    	}).collect(Collectors.toList());
    }
    
    private static Nonterminal getStartSymbol() {
    	return Nonterminal.withName("A");
    }
	
    /**
     * A ::= a A b
     *     | a A c
     *     | a
     */
	public static Grammar getGrammar() {
		Nonterminal A = Nonterminal.withName("A");
		Character a = Character.from('a');
		Character b = Character.from('b');
		Character c  = Character.from('c');
		Rule r1 = Rule.withHead(A).addSymbols(a, A, b).build();
		Rule r2 = Rule.withHead(A).addSymbols(a, A, c).build();
		Rule r3 = Rule.withHead(A).addSymbols(a).build();
		return Grammar.builder().addRules(r1, r2, r3).build();
	}
	
	private static Input getInput() {
		return Input.fromString("aaabb");
	}
		
	public static ParseSuccess getOriginalParseResult(GrammarGraph registry) {
		ParseStatistics statistics = ParseStatistics.builder()
				.setDescriptorsCount(29)
				.setGSSNodesCount(7)
				.setGSSEdgesCount(10)
				.setNonterminalNodesCount(5)
				.setTerminalNodesCount(5)
				.setIntermediateNodesCount(6)
				.setPackedNodesCount(11)
				.setAmbiguousNodesCount(0).build();
		return new ParseSuccess(null, statistics);
	}
	
}
