package org.jgll.parser.datadependent;

/**
 * 
 * @author Anastasia Izmaylova
 * 
 * 
 * S ::= E(2)
 * 
 * E(v) ::= [v > 1] 'a' 'b' 'c'
 *        | [v == 0] A B C
 *        
 * A ::= 'a'
 * 
 * B ::= 'b'
 * 
 * C ::= 'c'
 *
 */

import static org.jgll.datadependent.ast.AST.integer;
import static org.jgll.datadependent.ast.AST.equal;
import static org.jgll.datadependent.ast.AST.greater;
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

public class Test3 {
	
	private Grammar grammar;

	@Before
	public void init() {
		
		Nonterminal S = Nonterminal.withName("S");
		
		Nonterminal E = Nonterminal.builder("E").addParameters("v").build();
		Nonterminal A = Nonterminal.withName("A");
		Nonterminal B = Nonterminal.withName("B");
		Nonterminal C = Nonterminal.withName("C");
		
		
		Rule r0 = Rule.withHead(S).addSymbol(Nonterminal.builder(E).apply(integer(0)).build()).build();
		
		Rule r1_1 = Rule.withHead(E)
					.addSymbol(Character.builder('a').addPreCondition(predicate(greater(var("v"), integer(1)))).build())
					.addSymbol(Character.from('b')).addSymbol(Character.from('c')).build();
		
		Rule r1_2 = Rule.withHead(E)
				.addSymbol(Nonterminal.builder(A).addPreCondition(predicate(equal(var("v"), integer(0)))).build())
				.addSymbol(Nonterminal.builder(B).build())
				.addSymbol(Nonterminal.builder(C).build()).build();
		
		Rule r2 = Rule.withHead(A).addSymbol(Character.from('a')).build();
		Rule r3 = Rule.withHead(B).addSymbol(Character.from('b')).build();
		
		Rule r4 = Rule.withHead(C).addSymbol(Character.from('c')).build();
		
		grammar = Grammar.builder().addRules(r0, r1_1, r1_2, r2, r3, r4).build();
		
	}
	
	@Test
	public void test() {
		System.out.println(grammar);
		
		Input input = Input.fromString("abc");
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
