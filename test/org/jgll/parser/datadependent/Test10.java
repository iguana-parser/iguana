package org.jgll.parser.datadependent;

import static org.jgll.datadependent.ast.AST.*;
import static org.jgll.grammar.condition.DataDependentCondition.predicate;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Code;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.regex.Alt;
import org.jgll.regex.Star;
import org.jgll.util.Configuration;
import org.jgll.util.Input;
import org.jgll.util.Visualization;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Anastasia Izmaylova
 * 
 * X ::= S
 * 
 * @layout(NoNL)
 * S ::= a:A [a.lExt == 0] print(a.rExt, indent(a.rExt)) b:B [b.lExt == 5] print(b.rExt, indent(b.rExt))
 *  
 * A ::= 'a'
 * B ::= 'b'
 *
 */

public class Test10 {
	
	private Grammar grammar;

	@Before
	public void init() {
		
		Nonterminal X = Nonterminal.withName("X");
		
		Nonterminal NoNL = Nonterminal.withName("NoNL");
		
		Nonterminal S = Nonterminal.withName("S");
		Nonterminal A = Nonterminal.withName("A");
		Nonterminal B = Nonterminal.withName("B");
		
		
		Rule r0 = Rule.withHead(X).addSymbol(S).build();
		
		Rule r1 = Rule.withHead(S)
					.addSymbol(Nonterminal.builder(A).setLabel("a")
							.addPreCondition(predicate(equal(lExt("a"), integer(0)))).build())
					.addSymbol(Code.code(stat(println(rExt("a"), indent(rExt("a"))))))
					.addSymbol(Nonterminal.builder(B).setLabel("b")
							.addPreCondition(predicate(equal(lExt("b"), integer(5)))).build())
					.addSymbol(Code.code(stat(println(rExt("b"), indent(rExt("b"))))))
					
					.setLayout(NoNL).build();
		
		Rule r2 = Rule.withHead(A).addSymbol(Character.from('a')).build();
		Rule r3 = Rule.withHead(B).addSymbol(Character.from('b')).build();
		
		Rule r4 = Rule.withHead(Nonterminal.builder("NoNL").build())
						.addSymbol(Star.builder(Alt.from(Character.from(' '), Character.from('\t')))
								.addPostCondition(RegularExpressionCondition.notFollow(Character.from(' ')))
								.addPostCondition(RegularExpressionCondition.notFollow(Character.from('\t'))).build()).build();
		
		grammar = Grammar.builder().addRules(r0, r1, r2, r3, r4).build();
		
	}
	
	@Test
	public void test() {
		System.out.println(grammar);
		
		Input input = Input.fromString("a    b");
		GrammarGraph graph = grammar.toGrammarGraph(input, Configuration.DEFAULT);
		
		GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, graph, Nonterminal.withName("X"));
		
		Visualization.generateGrammarGraph("/Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/", graph);
		
		if (result.isParseSuccess()) {
			Visualization.generateSPPFGraph("/Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/", 
					result.asParseSuccess().getRoot(), input);
		}
		
	}

}
