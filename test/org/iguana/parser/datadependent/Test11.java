/*
 * Copyright (c) 2015, Ali Afroozeh and Anastasia Izmaylova, Centrum Wiskunde & Informatica (CWI)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this 
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this 
 *    list of conditions and the following disclaimer in the documentation and/or 
 *    other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT 
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
 * OF SUCH DAMAGE.
 *
 */

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
import org.junit.Before;
import org.junit.Test;

import iguana.utils.input.Input;

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
		
		GLLParser parser = ParserFactory.getParser();
		ParseResult result = parser.parse(input, graph, Nonterminal.withName("X"));
		
	}

}
