package org.iguana.parser.datadependent;

import static org.iguana.datadependent.ast.AST.integer;
import static org.iguana.datadependent.ast.AST.println;
import static org.iguana.datadependent.ast.AST.stat;
import static org.iguana.datadependent.ast.AST.string;
import static org.iguana.datadependent.ast.AST.var;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.Code;
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
 * Ambiguous
 * 
 * X ::= S(1,2)
 * 
 * S(a,b) ::= l:A print(l,a,b) B
 *          | l1:C l2:A print(l1,l2,a,b) D
 *           
 * A ::= 'a' 'a' | 'a'
 * B ::= 'b' | 'a' 'b'
 * C ::= 'a'
 * D ::= 'b'
 *
 */

public class Test2 {
	
	private Grammar grammar;

	@Before
	public void init() {
		
		Nonterminal X = Nonterminal.withName("X");
		
		Nonterminal S = Nonterminal.builder("S").addParameters("a", "b").build();
		Nonterminal A = Nonterminal.withName("A");
		Nonterminal B = Nonterminal.withName("B");
		Nonterminal C = Nonterminal.withName("C");
		Nonterminal D = Nonterminal.withName("D");
		
		
		Rule r0 = Rule.withHead(X).addSymbol(Nonterminal.builder(S).apply(integer(1), integer(2)).build()).build();
		
		Rule r1_1 = Rule.withHead(S)
					.addSymbol(Code.code(Nonterminal.builder(A).setLabel("l").setVariable("x").build(),
										 stat(println(string("PRINT1: "), var("l"), var("a"), var("b")))))
					
					.addSymbol(B).build();
		
		Rule r1_2 = Rule.withHead(S)
				.addSymbol(Nonterminal.builder(C).setLabel("l1").build())
				.addSymbol(Code.code(Nonterminal.builder(A).setLabel("l2").build(),
									 stat(println(string("PRINT2: "), var("l1"), var("l2"), var("a"), var("b")))))
				.addSymbol(D).build();
		
		Rule r2_1 = Rule.withHead(A).addSymbol(Character.from('a')).addSymbol(Character.from('a')).build();
		Rule r2_2 = Rule.withHead(A).addSymbol(Character.from('a')).build();
		Rule r3_1 = Rule.withHead(B).addSymbol(Character.from('b')).build();
		Rule r3_2 = Rule.withHead(B).addSymbol(Character.from('a')).addSymbol(Character.from('b')).build();
		
		Rule r4 = Rule.withHead(C).addSymbol(Character.from('a')).build();
		Rule r5 = Rule.withHead(D).addSymbol(Character.from('b')).build();
		
		grammar = Grammar.builder().addRules(r0, r1_1, r1_2, r2_1, r2_2, r3_1, r3_2, r4, r5).build();
		
	}
	
	@Test
	public void test() {
		System.out.println(grammar);
		
// 		FIXME: Graph builder for Code symbol

//		Input input = Input.fromString("aab");
//		GrammarGraph graph = grammar.toGrammarGraph(input, Configuration.DEFAULT);
//		
//		GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
//		ParseResult result = parser.parse(input, graph, Nonterminal.withName("X"));
//		
//		Visualization.generateGrammarGraph("/Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/", graph);
//		
//		if (result.isParseSuccess()) {
//			Visualization.generateSPPFGraph("/Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/", 
//					result.asParseSuccess().getRoot(), input);
//		}
		
	}

}
