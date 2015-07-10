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
import org.iguana.grammar.slot.EndGrammarSlot;
import org.iguana.grammar.slot.GrammarSlot;
import org.iguana.grammar.slot.NonterminalGrammarSlot;
import org.iguana.parser.gss.GSSNodeData;
import org.iguana.sppf.IntermediateNode;
import org.iguana.sppf.NonPackedNode;
import org.iguana.sppf.NonterminalNode;
import org.iguana.sppf.NonterminalOrIntermediateNode;
import org.iguana.sppf.PackedNode;
import org.iguana.sppf.TerminalNode;
import org.iguana.util.collections.Key;

public interface SPPFLookup {
	
	default NonPackedNode getNode(EndGrammarSlot slot, NonPackedNode child) {
		return getNonterminalNode(slot, child);
	}
	
	default NonPackedNode hasNode(EndGrammarSlot slot, NonPackedNode child) {
		return hasNonterminalNode(slot, child);
	}
	default NonPackedNode getNode(GrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild) {
		if (slot.isFirst())
			return rightChild;
		
		return getIntermediateNode((BodyGrammarSlot) slot, leftChild, rightChild);
	}
	
	default NonPackedNode hasNode(GrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild) {
		if (slot.isFirst())
			return rightChild;
		
		return hasIntermediateNode((BodyGrammarSlot) slot, leftChild, rightChild);
	}
	
	default <T> NonPackedNode getNode(EndGrammarSlot slot, NonPackedNode child, GSSNodeData<T> data, Object value) {
		return getNonterminalNode(slot, child, data, value);
	}
	
	default <T> NonPackedNode hasNode(EndGrammarSlot slot, NonPackedNode child, GSSNodeData<T> data, Object value) {
		return hasNonterminalNode(slot, child, data, value);
	}
	
	default <T> NonPackedNode getNode(GrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild, Environment env) {
		if (slot.isFirst())
			return rightChild;
		
		return getIntermediateNode((BodyGrammarSlot) slot, leftChild, rightChild, env);
	}
	
	default <T> NonPackedNode hasNode(GrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild, Environment env) {
		if (slot.isFirst())
			return rightChild;
		
		return hasIntermediateNode((BodyGrammarSlot) slot, leftChild, rightChild, env);
	}
	
	default <T> NonterminalNode getNonterminalNode(EndGrammarSlot slot, NonPackedNode child) {
		NonterminalNode newNode = getNonterminalNode(slot.getNonterminal(), child.getLeftExtent(), child.getRightExtent());
		addPackedNode(newNode, slot, child.getRightExtent(), child);
		return newNode;
	}
	
	default <T> NonterminalNode hasNonterminalNode(EndGrammarSlot slot, NonPackedNode child) {
		return hasNonterminalNode(slot.getNonterminal(), child.getLeftExtent(), child.getRightExtent());
	}
	
	default <T> NonterminalNode getNonterminalNode(EndGrammarSlot slot, NonPackedNode child, GSSNodeData<T> data, Object value) {
		NonterminalNode newNode = getNonterminalNode(slot.getNonterminal(), child.getLeftExtent(), child.getRightExtent(), data, value);
		addPackedNode(newNode, slot, child.getRightExtent(), child);
		return newNode;
	}
	
	default <T> NonterminalNode hasNonterminalNode(EndGrammarSlot slot, NonPackedNode child, GSSNodeData<T> data, Object value) {
		return hasNonterminalNode(slot.getNonterminal(), child.getLeftExtent(), child.getRightExtent(), data, value);
	}
	
	default IntermediateNode getIntermediateNode(BodyGrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild) {
		NodeCreator<IntermediateNode> creator = (key, val) -> {
			IntermediateNode newNode = createIntermediateNode(slot, leftChild.getLeftExtent(), rightChild.getRightExtent());
			intermediateNodeAdded(newNode);
			return newNode;
		};
		
		IntermediateNode newNode = getIntermediateNode(slot, leftChild.getLeftExtent(), rightChild.getRightExtent(), creator);
		addPackedNode(newNode, slot, rightChild.getLeftExtent(), leftChild, rightChild);
		return newNode;
	}
	
	default NonPackedNode getIntermediateNode2(BodyGrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild) {
		
		if (slot.isFirst())
			return rightChild;
		
		Holder<IntermediateNode> holder = new Holder<>();
		NodeCreator<IntermediateNode> creator = (key, value) -> {
			if (value != null) {
				addPackedNode(value, slot, rightChild.getLeftExtent(), leftChild, rightChild);
				return value;
			} else {
				IntermediateNode newNode = createIntermediateNode(slot, leftChild.getLeftExtent(), rightChild.getRightExtent());
				addPackedNode(newNode, slot, rightChild.getLeftExtent(), leftChild, rightChild);
				intermediateNodeAdded(newNode);
				holder.val = newNode;
				return newNode;				
			}
		};
		
		getIntermediateNode(slot, leftChild.getLeftExtent(), rightChild.getRightExtent(), creator);
		
		return holder.has() ? holder.val : null;
	}
	
	/**
	 * 
	 * If the node exists, attach the packed node and return null.
	 * If the node does not exit, create one, attach the packed node and return it.
	 * 
	 */
	default NonPackedNode getIntermediateNode2(BodyGrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild, Environment env) {
		
		if (slot.isFirst())
			return rightChild;
		
		Holder<IntermediateNode> holder = new Holder<>();
		NodeCreator<IntermediateNode> creator = (key, value) -> {
			if (value != null) {
				addPackedNode(value, slot, rightChild.getLeftExtent(), leftChild, rightChild);
				return value;
			} else {
				IntermediateNode newNode = createIntermediateNode(slot, leftChild.getLeftExtent(), rightChild.getRightExtent());
				addPackedNode(newNode, slot, rightChild.getLeftExtent(), leftChild, rightChild);
				intermediateNodeAdded(newNode);
				holder.val = newNode;
				return newNode;				
			}
		};
		
		getIntermediateNode(slot, leftChild.getLeftExtent(), rightChild.getRightExtent(), env, creator);
		
		return holder.has() ? holder.val : null;
	}	
	
	default IntermediateNode hasIntermediateNode(BodyGrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild) {
		return hasIntermediateNode(slot, leftChild.getLeftExtent(), rightChild.getRightExtent());
	}
	
	default IntermediateNode getIntermediateNode(BodyGrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild, Environment env) {
		NodeCreator<IntermediateNode> creator = (key, val) -> {
			IntermediateNode newNode = createIntermediateNode(slot, leftChild.getLeftExtent(), rightChild.getRightExtent());
			intermediateNodeAdded(newNode);
			return newNode;
		};
		
		IntermediateNode newNode = getIntermediateNode(slot, leftChild.getLeftExtent(), rightChild.getRightExtent(), env, creator);
		addPackedNode(newNode, slot, rightChild.getLeftExtent(), leftChild, rightChild);
		return newNode;
	}
	
	default IntermediateNode hasIntermediateNode(BodyGrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild, Environment env) {
		return hasIntermediateNode(slot, leftChild.getLeftExtent(), rightChild.getRightExtent(), env);
	}
		
	public NonterminalNode getNonterminalNode(NonterminalGrammarSlot slot, int leftExtent, int rightExtent);
	
	public NonterminalNode hasNonterminalNode(NonterminalGrammarSlot slot, int leftExtent, int rightExtent);
	
	public <T> NonterminalNode getNonterminalNode(NonterminalGrammarSlot slot, int leftExtent, int rightExtent, GSSNodeData<T> data, Object value);
	
	public <T> NonterminalNode hasNonterminalNode(NonterminalGrammarSlot slot, int leftExtent, int rightExtent, GSSNodeData<T> data, Object value);
	
	public IntermediateNode getIntermediateNode(BodyGrammarSlot slot, int leftExtent, int rightExtent, NodeCreator<IntermediateNode> creator);
	
	public IntermediateNode hasIntermediateNode(BodyGrammarSlot slot, int leftExtent, int rightExtent);
	
	public IntermediateNode getIntermediateNode(BodyGrammarSlot slot, int leftExtent, int rightExtent, Environment env, NodeCreator<IntermediateNode> creator);
	
	public IntermediateNode hasIntermediateNode(BodyGrammarSlot slot, int leftExtent, int rightExtent, Environment env);
	
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

	default IntermediateNode createIntermediateNode(BodyGrammarSlot slot, int leftExtent, int rightExtent) {
		return new IntermediateNode(slot, leftExtent, rightExtent);
	}
	
	default NonterminalNode createNonterminalNode(NonterminalGrammarSlot slot, int leftExtent, int rightExtent) {
		return new NonterminalNode(slot, leftExtent, rightExtent);
	}
	
	default NonterminalNode createNonterminalNode(NonterminalGrammarSlot slot, int leftExtent, int rightExtent, Object value) {
		return new NonterminalNode(slot, leftExtent, rightExtent, value);
	}
	
	@FunctionalInterface
	public static interface NodeCreator<T extends NonPackedNode> {
		public T create(Key key, T val);
	}

	static class Holder<T> {
		T val;
		public boolean has() { return val != null; }
	}
	
}
