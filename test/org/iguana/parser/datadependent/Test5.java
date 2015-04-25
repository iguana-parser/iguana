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
 * Operator precedence
 * 
 * l - by left recursive E
 * r - by right recursive E
 * 
 * S ::= E(0,0)
 * 
 * E(l,r) ::= [3 >= l, 3 >= r] E(3,0) '+' E(l,4) // propagate only l to the right as '+' is defined as left associative
 *          | [2 >= l] '-' E(0,0)
 *          | 'a'
 * 
 * 			  E(0,0)
 *           /   |   \
 *       E(3,0)  +  E(0,4)
 *      /   |   \
 *  E(3,0)  +  E(3,4)
 * 
 */

public class Test5 {
	
	private Grammar grammar;

	@Before
	public void init() {
		
		Nonterminal S = Nonterminal.withName("S");
		
		Nonterminal E = Nonterminal.builder("E").addParameters("l", "r").build();
		
		Rule r0 = Rule.withHead(S).addSymbol(Nonterminal.builder(E).apply(integer(0), integer(0)).build()).build();
		
		Rule r1_1 = Rule.withHead(E)
					.addSymbol(Nonterminal.builder(E).apply(integer(3), integer(0))
							.addPreCondition(predicate(greaterEq(integer(3), var("l"))))
							.addPreCondition(predicate(greaterEq(integer(3), var("r")))).build())
					.addSymbol(Character.from('+'))
					.addSymbol(Nonterminal.builder(E).apply(var("l"), integer(4)).build()).build();
		
		Rule r1_2 = Rule.withHead(E)
				.addSymbol(Character.builder('-')
					.addPreCondition(predicate(greaterEq(integer(2), var("l")))).build())
				.addSymbol(Nonterminal.builder(E).apply(integer(0), integer(0)).build()).build();
		
		Rule r1_3 = Rule.withHead(E).addSymbol(Character.from('a')).build();
		
		grammar = Grammar.builder().addRules(r0, r1_1, r1_2, r1_3).build();
		
	}
	
	@Test
	public void test() {
		System.out.println(grammar);
		
		Input input = Input.fromString("a+-a+a");
		// Input input = Input.fromString("a+a+a");
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
