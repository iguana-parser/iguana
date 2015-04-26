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

package org.iguana.sppf;

import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.symbol.Nonterminal;

public class SPPFNodeFactory {

	private GrammarGraph grammarGraph;
	private PackedNodeSet set = (x, y) -> true;
	
	public SPPFNodeFactory(GrammarGraph grammarGraph) {
		this.grammarGraph = grammarGraph;
	}
	
	public NonterminalNode createNonterminalNode(String s, int leftExtent, int rightExtent) {
		Nonterminal nonterminal = Nonterminal.withName(s);
		return new NonterminalNode(grammarGraph.getHead(nonterminal), leftExtent, rightExtent, set);
	}
	
	public TerminalNode createEpsilonNode(int inputIndex) {
		return createTerminalNode("epsilon", inputIndex, inputIndex);
	}
	
	public NonterminalNode createNonterminalNode(String s, int index, int leftExtent, int rightExtent) {
		Nonterminal nonterminal = new Nonterminal.Builder(s).setIndex(index).build();
		return new NonterminalNode(grammarGraph.getHead(nonterminal), leftExtent, rightExtent, set);
	}

	public IntermediateNode createIntermediateNode(String s, int leftExtent, int rightExtent) {
		return new IntermediateNode(grammarGraph.getGrammarSlot(s), leftExtent, rightExtent, set);
	}
	
	public TerminalNode createTerminalNode(String s, int leftExtent, int rightExtent) {
		return new TerminalNode(grammarGraph.getTerminal(grammarGraph.getRegularExpression(s)), leftExtent, rightExtent);
	}

	public PackedNode createPackedNode(String s, int pivot, NonPackedNode parent) {
		return new PackedNode(grammarGraph.getGrammarSlot(s), pivot, parent);
	}
	
}
