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

package org.iguana.grammar;

import java.io.File;
import java.io.IOException;

import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Start;
import org.iguana.grammar.transformation.DesugarAlignAndOffside;
import org.iguana.grammar.transformation.DesugarPrecedenceAndAssociativity;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.grammar.transformation.LayoutWeaver;
import org.iguana.parser.Iguana;
import org.iguana.parser.ParseResult;
import org.iguana.util.Configuration;
import org.junit.Test;

import iguana.utils.input.Input;

public class TestHaskell {
	
	private static Grammar originalGrammar = Grammar.load(new File("grammars/haskell/haskell"));
	
	private static Nonterminal start = Start.from(Nonterminal.withName("Module"));
	
	private static Grammar grammar;
	
	static {
        DesugarAlignAndOffside desugarAlignAndOffside = new DesugarAlignAndOffside();
        desugarAlignAndOffside.doAlign();

        grammar = desugarAlignAndOffside.transform(originalGrammar);

        grammar = new EBNFToBNF().transform(grammar);

        desugarAlignAndOffside.doOffside();
        grammar = desugarAlignAndOffside.transform(grammar);

        grammar = new DesugarPrecedenceAndAssociativity().transform(grammar);

        grammar = new LayoutWeaver().transform(grammar);
	}
	
	@Test
	public void test() throws IOException {
		Input input = Input.fromPath("/Users/aliafroozeh/Test.hs");
		GrammarGraph grammarGraph = GrammarGraph.from(grammar, input, Configuration.DEFAULT);
		ParseResult result = Iguana.parse(input, grammarGraph, start);
		System.out.println(result);
	}
	
}
