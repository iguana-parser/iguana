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

package org.iguana.sppf.lookup;

import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.grammar.slot.GrammarSlot;
import org.iguana.grammar.slot.LastSymbolGrammarSlot;
import org.iguana.grammar.slot.NonterminalGrammarSlot;
import org.iguana.grammar.slot.TerminalGrammarSlot;
import org.iguana.parser.gss.GSSNodeData;
import org.iguana.sppf.DummyNode;
import org.iguana.sppf.IntermediateNode;
import org.iguana.sppf.NonPackedNode;
import org.iguana.sppf.NonterminalNode;
import org.iguana.sppf.NonterminalOrIntermediateNode;
import org.iguana.sppf.PackedNode;
import org.iguana.sppf.TerminalNode;

public interface SPPFLookup {

	public TerminalNode getTerminalNode(TerminalGrammarSlot slot, int leftExtent, int rightExtent);
	
	default TerminalNode getEpsilonNode(TerminalGrammarSlot slot, int inputIndex) {
		return getTerminalNode(slot, inputIndex, inputIndex);
	}
	
	default NonPackedNode getNode(GrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild) {

		// A ::= \alpha .
		if (slot.isLast()) {
			if (leftChild == DummyNode.getInstance()) {
				return getNonterminalNode((LastSymbolGrammarSlot) slot, rightChild);
			} else {
				return getNonterminalNode((LastSymbolGrammarSlot) slot, leftChild, rightChild);				
			}
		}
		
		// A ::= X . \alpha, in this case leftChild is the dummy node. 
		if (slot.isFirst()) {
			return rightChild;
		}
		
		return getIntermediateNode((BodyGrammarSlot) slot, leftChild, rightChild);
	}
	
	
	default <T> NonPackedNode getNode(GrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild, Environment env, GSSNodeData<T> data) {
		// A ::= \alpha .
		if (slot.isLast()) {
			if (leftChild == DummyNode.getInstance()) {
				return data == null? getNonterminalNode((LastSymbolGrammarSlot) slot, rightChild) 
								   : getNonterminalNode((LastSymbolGrammarSlot) slot, rightChild, data);
			} else {
				return data == null? getNonterminalNode((LastSymbolGrammarSlot) slot, leftChild, rightChild) 
						           : getNonterminalNode((LastSymbolGrammarSlot) slot, leftChild, rightChild, data);				
			}
		}
		
		// A ::= X . \alpha, in this case leftChild is the dummy node. 
		if (slot.isFirst()) {
			return rightChild;
		}
		
		return getIntermediateNode((BodyGrammarSlot) slot, leftChild, rightChild, env);
	}
	
	default NonterminalNode getNonterminalNode(LastSymbolGrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild) {
		NonterminalNode newNode = getNonterminalNode(slot.getNonterminal(), leftChild.getLeftExtent(), rightChild.getRightExtent());
		addPackedNode(newNode, slot, leftChild.getRightExtent(), leftChild, rightChild);
		return newNode;
	}
	
	default <T> NonterminalNode getNonterminalNode(LastSymbolGrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild, GSSNodeData<T> data) {
		NonterminalNode newNode = getNonterminalNode(slot.getNonterminal(), leftChild.getLeftExtent(), rightChild.getRightExtent(), data);
		addPackedNode(newNode, slot, leftChild.getRightExtent(), leftChild, rightChild);
		return newNode;
	}

	default <T> NonterminalNode getNonterminalNode(LastSymbolGrammarSlot slot, NonPackedNode child) {
		NonterminalNode newNode = getNonterminalNode(slot.getNonterminal(), child.getLeftExtent(), child.getRightExtent());
		addPackedNode(newNode, slot, child.getRightExtent(), child);
		return newNode;
	}
	
	default <T> NonterminalNode getNonterminalNode(LastSymbolGrammarSlot slot, NonPackedNode child, GSSNodeData<T> data) {
		NonterminalNode newNode = getNonterminalNode(slot.getNonterminal(), child.getLeftExtent(), child.getRightExtent(), data);
		addPackedNode(newNode, slot, child.getRightExtent(), child);
		return newNode;
	}
	
	default IntermediateNode getIntermediateNode(BodyGrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild) {
		IntermediateNode newNode = getIntermediateNode(slot, leftChild.getLeftExtent(), rightChild.getRightExtent());
		addPackedNode(newNode, slot, rightChild.getLeftExtent(), leftChild, rightChild);
		return newNode;
	}
	
	default IntermediateNode getIntermediateNode(BodyGrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild, Environment env) {
		IntermediateNode newNode = getIntermediateNode(slot, leftChild.getLeftExtent(), rightChild.getRightExtent(), env);
		addPackedNode(newNode, slot, rightChild.getLeftExtent(), leftChild, rightChild);
		return newNode;
	}
	
	public NonterminalNode getNonterminalNode(NonterminalGrammarSlot grammarSlot, int leftExtent, int rightExtent);
	
	public <T> NonterminalNode getNonterminalNode(NonterminalGrammarSlot grammarSlot, int leftExtent, int rightExtent, GSSNodeData<T> data);
	
	public IntermediateNode getIntermediateNode(BodyGrammarSlot slot, int leftExtent, int rightExtent);
	
	public IntermediateNode getIntermediateNode(BodyGrammarSlot slot, int leftExtent, int rightExtent, Environment env);
	
	default void addPackedNode(NonterminalOrIntermediateNode parent, GrammarSlot slot, int pivot, NonPackedNode leftChild, NonPackedNode rightChild) {
		PackedNode packedNode = new PackedNode(slot, pivot, parent);
		addPackedNode(parent, leftChild, rightChild, packedNode);
	}
	
	default void addPackedNode(NonterminalOrIntermediateNode parent, NonPackedNode leftChild, NonPackedNode rightChild, PackedNode packedNode) {
		boolean ambiguousBefore = parent.isAmbiguous();
		if (parent.addPackedNode(packedNode, leftChild, rightChild)) {
			packedNodeAdded(packedNode);
			boolean ambiguousAfter = parent.isAmbiguous();
			if (!ambiguousBefore && ambiguousAfter) {
				ambiguousNodeAdded(parent);
			}
		}
	}
	
	default void addPackedNode(NonterminalOrIntermediateNode parent, GrammarSlot slot, int pivot, NonPackedNode child) {
		PackedNode packedNode = new PackedNode(slot, pivot, parent);
		addPackedNode(parent, child, packedNode);
	}
	
	default void addPackedNode(NonterminalOrIntermediateNode parent, NonPackedNode child, PackedNode packedNode) {
		boolean ambiguousBefore = parent.isAmbiguous();
		if (parent.addPackedNode(packedNode, child)) {
			packedNodeAdded(packedNode);
			boolean ambiguousAfter = parent.isAmbiguous();
			if (!ambiguousBefore && ambiguousAfter) {
				ambiguousNodeAdded(parent);
			}
		}
	}
	
	void ambiguousNodeAdded(NonterminalOrIntermediateNode node);
	
	void packedNodeAdded(PackedNode node);
	
	void intermediateNodeAdded(IntermediateNode node);
	
	void nonterminalNodeAdded(NonterminalNode node);
	
	void terminalNodeAdded(TerminalNode node);
	
	public NonterminalNode getStartSymbol(NonterminalGrammarSlot startSymbol, int inputSize);
	
	public int getNonterminalNodesCount();
	
	public int getIntermediateNodesCount();
	
	public int getTerminalNodesCount();
	
	public int getPackedNodesCount();
	
	public int getAmbiguousNodesCount();
	
	public void reset();
	
}
