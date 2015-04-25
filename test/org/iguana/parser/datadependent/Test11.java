package org.iguana.parser.datadependent;

import static org.iguana.datadependent.ast.AST.equal;
import static org.iguana.datadependent.ast.AST.indent;
import static org.iguana.datadependent.ast.AST.integer;
import static org.iguana.datadependent.ast.AST.lExt;
import static org.iguana.datadependent.ast.AST.println;
import static org.iguana.datadependent.ast.AST.rExt;
import static org.iguana.datadependent.ast.AST.stat;
import static org.iguana.grammar.condition.DataDependentCondition.predicate;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.condition.RegularExpressionCondition;
import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.Code;
import org.iguana.grammar.symbol.LayoutStrategy;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.parser.GLLParser;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParserFactory;
import org.iguana.regex.Alt;
import org.iguana.regex.Star;
import org.iguana.util.Configuration;
import org.iguana.util.Input;
import org.iguana.util.Visualization;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Anastasia Izmaylova
 * 
 * X ::= S
 * 
 * @layout(NoNL)
 * S ::= a:'a' [a.lExt == 0] print(a.rExt, indent(a.rExt)) b:'b' [b.lExt == 5] print(b.rExt, indent(b.rExt))
 *
 */

public class Test11 {
	
	private Grammar grammar;

	@Before
	public void init() {
		
		Nonterminal X = Nonterminal.withName("X");
		
		Nonterminal NoNL = Nonterminal.withName("NoNL");
		
		Nonterminal S = Nonterminal.withName("S");		
		
		Rule r0 = Rule.withHead(X).addSymbol(S).build();
		
		Rule r1 = Rule.withHead(S)
					.addSymbol(Code.code(Character.builder('a').setLabel("a")
											.addPreCondition(predicate(equal(lExt("a"), integer(0)))).build(),
										 stat(println(rExt("a"), indent(rExt("a"))))))
					.addSymbol(NoNL) // TODO: Should be removed
					.addSymbol(Code.code(Character.builder('b').setLabel("b")
												.addPreCondition(predicate(equal(lExt("b"), integer(5)))).build(),
										 stat(println(rExt("b"), indent(rExt("b"))))))
					
					.setLayout(NoNL).setLayoutStrategy(LayoutStrategy.FIXED).build();
		
		Rule r2 = Rule.withHead(Nonterminal.builder("NoNL").build())
						.addSymbol(Star.builder(Alt.from(Character.from(' '), Character.from('\t')))
								.addPostCondition(RegularExpressionCondition.notFollow(Character.from(' ')))
								.addPostCondition(RegularExpressionCondition.notFollow(Character.from('\t'))).build()).build();
		
		grammar = Grammar.builder().addRules(r0, r1, r2).build();
		
	}
	
	@Test
	public void test() {
		System.out.println(grammar);
		
		grammar = new EBNFToBNF().transform(grammar);
		
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
