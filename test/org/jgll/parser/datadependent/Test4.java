package org.jgll.parser.datadependent;

import static org.jgll.datadependent.ast.AST.equal;
import static org.jgll.datadependent.ast.AST.greater;
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
 * 
 * S ::= E(3)
 * 
 * E(p) ::= [p > 2] E(3) '*' E(2) left
 *        > [p > 1] E(2) '+' E(1) left 
 *        | 'a'
 *        
 *
 */

public class Test4 {
	
	private Grammar grammar;

	@Before
	public void init() {
		
		Nonterminal S = Nonterminal.withName("S");
		
		Nonterminal E = Nonterminal.builder("E").addParameters("p").build();
		
		Rule r0 = Rule.withHead(S).addSymbol(Nonterminal.builder(E).apply(integer(3)).build()).build();
		
		Rule r1_1 = Rule.withHead(E)
					.addSymbol(Nonterminal.builder(E).apply(integer(3)).addPreCondition(predicate(greater(var("p"), integer(2)))).build())
					.addSymbol(Character.from('*'))
					.addSymbol(Nonterminal.builder(E).apply(integer(2)).build()).build();
		
		Rule r1_2 = Rule.withHead(E)
				.addSymbol(Nonterminal.builder(E).apply(integer(2)).addPreCondition(predicate(greater(var("p"), integer(1)))).build())
				.addSymbol(Character.from('+'))
				.addSymbol(Nonterminal.builder(E).apply(integer(1)).build()).build();
		
		Rule r1_3 = Rule.withHead(E).addSymbol(Character.from('a')).build();
		
		grammar = Grammar.builder().addRules(r0, r1_1, r1_2, r1_3).build();
		
	}
	
	@Test
	public void test() {
		System.out.println(grammar);
		
		Input input = Input.fromString("a+a*a+a");
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
