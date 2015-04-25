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
		
		Character hat = Character.from('^');
		Character star = Character.from('*');
		Character plus = Character.from('+');
		
		Rule r0 = Rule.withHead(S).addSymbol(Nonterminal.builder(E).apply(integer(0)).build()).build();
		
		Rule r1_1 = Rule.withHead(E)
					.addSymbol(Nonterminal.builder(E).apply(integer(5))
							.addPreCondition(predicate(greaterEq(integer(4), var("p")))).build())
					.addSymbol(hat)
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
					result.asParseSuccess().getRoot(), input);
		}
		
	}

}
