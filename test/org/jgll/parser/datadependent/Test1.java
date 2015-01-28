package org.jgll.parser.datadependent;

import static org.jgll.datadependent.ast.AST.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.symbol.CodeBlock;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Rule;
import org.jgll.util.Configuration;
import org.jgll.util.Input;
import org.jgll.util.Visualization;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Anastasia Izmaylova
 * 
 * X ::= S(1,2)
 * 
 * S(a,b) ::= l:A print(l,a,b) B
 * A ::= 'a'
 * B ::= 'b'
 *
 */

public class Test1 {
	
	private Grammar grammar;

	@Before
	public void init() {
		
		Nonterminal X = Nonterminal.withName("X");
		
		Nonterminal S = Nonterminal.builder("S").addParameters("a", "b").build();
		Nonterminal A = Nonterminal.withName("A");
		Nonterminal B = Nonterminal.withName("B");
		
		
		Rule r0 = Rule.withHead(X).addSymbol(Nonterminal.builder(S).apply(integer(1), integer(2)).build()).build();
		
		Rule r1 = Rule.withHead(S)
					.addSymbol(Nonterminal.builder(A).setLabel("l").setVariable("x").build())
					.addSymbol(CodeBlock.code(stat(println(var("l"), var("a"), var("b")))))
					.addSymbol(B).build();
		
		Rule r2 = Rule.withHead(A).addSymbol(Character.from('a')).build();
		Rule r3 = Rule.withHead(B).addSymbol(Character.from('b')).build();
		
		grammar = Grammar.builder().addRules(r0, r1, r2, r3).build();
		
	}
	
	@Test
	public void test() {
		System.out.println(grammar);
		GrammarGraph graph = grammar.toGrammarGraph(Input.fromString("ab"), Configuration.DEFAULT);
		System.out.println(graph);
		// Visualization.generateGrammarGraph("/Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/", graph);
	}

}
