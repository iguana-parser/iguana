package org.jgll.sppf.lookup;

import org.jgll.datadependent.env.Environment;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.LastSymbolGrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.grammar.slot.TerminalGrammarSlot;
import org.jgll.parser.gss.GSSNodeData;
import org.jgll.sppf.DummyNode;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.NonterminalOrIntermediateNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.TerminalNode;

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
	
	default void reset() {}
	
}
