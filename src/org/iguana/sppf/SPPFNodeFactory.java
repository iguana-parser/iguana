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
import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.grammar.slot.NonterminalGrammarSlot;
import org.iguana.grammar.slot.TerminalGrammarSlot;

public class SPPFNodeFactory {

    private GrammarGraph grammarGraph;

    public SPPFNodeFactory(GrammarGraph graph) {
        this.grammarGraph = graph;
    }

	public NonterminalNode createNonterminalNode(String head, String slot, NonPackedNode child) {
		PackedNode packedNode = new PackedNode((BodyGrammarSlot) grammarGraph.getSlot(slot));
		packedNode.setLeftChild(child);
		NonterminalNode node = new NonterminalNode((NonterminalGrammarSlot) grammarGraph.getSlot(head));
		node.addPackedNode(packedNode);
		return node;
	}

	public NonterminalNode createNonterminalNode(String head, BodyGrammarSlot slot, NonPackedNode child, Object value) {
		PackedNode packedNode = new PackedNode(slot);
		packedNode.setLeftChild(child);
		NonterminalNodeWithValue node = new NonterminalNodeWithValue((NonterminalGrammarSlot) grammarGraph.getSlot(head), value);
		node.addPackedNode(packedNode);
		return  node;
	}

	public IntermediateNode createIntermediateNode(String slot, NonPackedNode leftChild, NonPackedNode rightChild) {
		PackedNode packedNode = new PackedNode((BodyGrammarSlot) grammarGraph.getSlot(slot));
		packedNode.setLeftChild(leftChild);
		packedNode.setRightChild(rightChild);
		IntermediateNode node = new IntermediateNode();
		node.addPackedNode(packedNode);
		return node;
	}
	
	public TerminalNode createTerminalNode(String slot, int leftExtent, int rightExtent) {
		return new TerminalNode((TerminalGrammarSlot) grammarGraph.getSlot(slot), leftExtent, rightExtent);
	}

	public PackedNode createPackedNode(String slot, NonPackedNode leftChild) {
		PackedNode packedNode = new PackedNode((BodyGrammarSlot) grammarGraph.getSlot(slot));
		packedNode.setLeftChild(leftChild);
		return packedNode;
	}

    public PackedNode createPackedNode(String slot, NonPackedNode leftChild, NonPackedNode rightChild) {
    	PackedNode packedNode = new PackedNode((BodyGrammarSlot) grammarGraph.getSlot(slot));
    	packedNode.setLeftChild(leftChild);
    	packedNode.setRightChild(rightChild);
        return packedNode;
    }
	
}
