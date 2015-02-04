package org.jgll.parser.datadependent;

import static org.jgll.datadependent.ast.AST.greaterEq;
import static org.jgll.datadependent.ast.AST.integer;
import static org.jgll.datadependent.ast.AST.var;
import static org.jgll.grammar.condition.DataDependentCondition.predicate;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.util.Configuration;
import org.jgll.util.Input;
import org.jgll.util.Visualization;
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
 * S ::= E(0)
 * 
 * E(p) ::= [4 >= p] E(5) '^' E(4) // right
 *        | [3 >= p] E(3) '*' E(4) // left
 *        | [2 >= p] E(2) '+' E(3) // left
 *        | 'a'
 *  
 */

public class Test6 {
	
	private Grammar grammar;

	@Before
	public void init() {
		
		Nonterminal S = Nonterminal.withName("S");
		
		Nonterminal E = Nonterminal.builder("E").addParameters("p").build();
		
		Character pow = Character.from('^');
		Character star = Character.from('*');
		Character plus = Character.from('+');
		
		Rule r0 = Rule.withHead(S).addSymbol(Nonterminal.builder(E).apply(integer(0)).build()).build();
		
		Rule r1_1 = Rule.withHead(E)
					.addSymbol(Nonterminal.builder(E).apply(integer(5))
							.addPreCondition(predicate(greaterEq(integer(4), var("p")))).build())
					.addSymbol(pow)
					.addSymbol(Nonterminal.builder(E).apply(integer(4)).build()).build();
		
		Rule r1_2 = Rule.withHead(E)
						.addSymbol(Nonterminal.builder(E).apply(integer(3))
								.addPreCondition(predicate(greaterEq(integer(3), var("p")))).build())
						.addSymbol(star)
						.addSymbol(Nonterminal.builder(E).apply(integer(4)).build()).build();
		
		Rule r1_3 = Rule.withHead(E)
				.addSymbol(Nonterminal.builder(E).apply(integer(2))
						.addPreCondition(predicate(greaterEq(integer(2), var("p")))).build())
				.addSymbol(plus)
				.addSymbol(Nonterminal.builder(E).apply(integer(3)).build()).build();
		
		Rule r1_4 = Rule.withHead(E).addSymbol(Character.from('a')).build();
		
		grammar = Grammar.builder().addRules(r0, r1_1, r1_2, r1_3, r1_4).build();
		
	}
	
	@Test
	public void test() {
		System.out.println(grammar);
		
		Input input = Input.fromString("a+a^a^a*a");
		GrammarGraph graph = grammar.toGrammarGraph(input, Configuration.DEFAULT);
		
		GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, graph, Nonterminal.withName("S"));
		
		Visualization.generateGrammarGraph("/Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/", graph);
		
		if (result.isParseSuccess()) {
			Visualization.generateSPPFGraph("/Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/", 
					result.asParseSuccess().getRoot(), parser.getRegistry(), input);
		}
		
	}

}
