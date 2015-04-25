package org.iguana.parser.datadependent;

import static org.iguana.datadependent.ast.AST.greaterEq;
import static org.iguana.datadependent.ast.AST.integer;
import static org.iguana.datadependent.ast.AST.var;
import static org.iguana.grammar.condition.DataDependentCondition.predicate;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.parser.GLLParser;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParserFactory;
import org.iguana.util.Configuration;
import org.iguana.util.Input;
import org.iguana.util.Visualization;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Anastasia Izmaylova
 * 
 * S ::= E(0,0)
 * 
 * E(l,r) ::=[4 >= r] E(4,r) z // propagate r down as this is a deep case: x ( ( a w ) z )
 *         | [3 >= l] x E(0,3) // can be affected by left E  => constraint on l
 *         | [2 >= r] E(0,0) w // can be affected by right E => constraint on r
 *         | a
 *
 */

public class Test8 {
	
	private Grammar grammar;

	@Before
	public void init() {
		
		Nonterminal S = Nonterminal.withName("S");
		
		Nonterminal E = Nonterminal.builder("E").addParameters("l", "r").build();
		
		Character z = Character.from('z');
		Character w = Character.from('w');
		
		Rule r0 = Rule.withHead(S).addSymbol(Nonterminal.builder(E).apply(integer(0), integer(0)).build()).build();
		
		Rule r1_1 = Rule.withHead(E)
					.addSymbol(Nonterminal.builder(E).apply(integer(4), var("r"))
						.addPreCondition(predicate(greaterEq(integer(4), var("r")))).build())
					.addSymbol(z).build();
		
		Rule r1_2 = Rule.withHead(E)
					.addSymbol(Character.builder('x')
							.addPreCondition(predicate(greaterEq(integer(3), var("l")))).build())
					.addSymbol(Nonterminal.builder(E).apply(integer(0), integer(3)).build()).build();
		
		Rule r1_3 = Rule.withHead(E)
					.addSymbol(Nonterminal.builder(E).apply(integer(0), integer(0))
						.addPreCondition(predicate(greaterEq(integer(2), var("r")))).build())
					.addSymbol(w).build();
		
		Rule r1_4 = Rule.withHead(E).addSymbol(Character.from('a')).build();
		
		grammar = Grammar.builder().addRules(r0, r1_1, r1_2, r1_3, r1_4).build();
		
	}
	
	@Test
	public void test() {
		System.out.println(grammar);
		
		Input input = Input.fromString("xawz");
		GrammarGraph graph = grammar.toGrammarGraph(input, Configuration.DEFAULT);
		
		GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, graph, Nonterminal.withName("S"));
		
		Visualization.generateGrammarGraph("/Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/", graph);
		
		if (result.isParseSuccess()) {
			Visualization.generateSPPFGraph("/Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/", 
					result.asParseSuccess().getRoot(), input);
		}
		
	}

}
