package org.jgll.parser;

import java.util.ArrayList;
import java.util.List;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.L0;
import org.jgll.sppf.NonPackedNode;
import org.jgll.util.hashing.CuckooHashSet;
import org.jgll.util.hashing.ExternalHasher;
import org.jgll.util.hashing.Level;
import org.jgll.util.hashing.hashfunction.HashFunction;

/**
 * A {@code GSSNode} is a representation of a node in an Graph Structured Stack.
 * A {@code GSSNode} is defined by a three tuple ({@code label},
 * {@code position}, {@code index}). <br/> 
 * 
 * {@code label} is a label of a GLL Parser
 * state, {@code position} is an integer array of length three indication a
 * position in a grammar and {@code index} is an index in the input string. <br />
 * A {@code GSSNode} has children which are labeled by an {@code SPPFNode}.
 * These children define the structure of a Graph Structured Stack.
 * 
 * @author Maarten Manders
 * @author Ali Afroozeh
 * 
 */
public class GSSNode implements Level {
	
	public static final ExternalHasher<GSSNode> externalHasher = new GSSNodeExternalHasher();
	public static final ExternalHasher<GSSNode> levelBasedExternalHasher = new LevelBasedGSSNodeExternalHasher();

	/**
	 * The initial GSS node
	 */
	public static final GSSNode U0 = new GSSNode(L0.getInstance(), 0, NonPackedNode.externalHasher);

	private final GrammarSlot slot;

	private final int inputIndex;

	private List<GSSEdge> gssEdges;
	
	private CuckooHashSet<NonPackedNode> poppedElements;
	
	
	public static GSSNode levelBasedGSSNode(GrammarSlot slot, int inputIndex) {
		return new GSSNode(slot, inputIndex, NonPackedNode.levelBasedExternalHasher);
	}
	
	public static GSSNode recursiveDescentGSSNode(GrammarSlot slot, int inputIndex) {
		return new GSSNode(slot, inputIndex, NonPackedNode.externalHasher);
	}
	
	/**
	 * Creates a new {@code GSSNode} with the given {@code label},
	 * {@code position} and {@code index}
	 * 
	 * @param slot
	 * @param inputIndex
	 */
	private GSSNode(GrammarSlot slot, int inputIndex, ExternalHasher<NonPackedNode> hasher) {
		this.slot = slot;
		this.inputIndex = inputIndex;
		this.gssEdges = new ArrayList<>();
		this.poppedElements = new CuckooHashSet<>(hasher);
	}
		
	public void addGSSEdge(GSSEdge edge) {
		gssEdges.add(edge);
	}
	
	public Iterable<GSSEdge> getEdges() {
		return gssEdges;
	}
		
	public int getCountEdges() {
		return gssEdges.size();
	}

	public GrammarSlot getGrammarSlot() {
		return slot;
	}

	public int getInputIndex() {
		return inputIndex;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}

		if (!(obj instanceof GSSNode)) {
			return false;
		}
		
		GSSNode other = (GSSNode) obj;

		return  slot == other.slot &&
				inputIndex == other.inputIndex;
	}

	@Override
	public int hashCode() {
		return externalHasher.hash(this, HashFunctions.defaulFunction());
	}
	
	@Override
	public String toString() {
		return "(" + slot + "," + inputIndex + ")";
	}

	@Override
	public int getLevel() {
		return inputIndex;
	}
	
	public void addToPoppedElements(NonPackedNode node) {
		poppedElements.add(node);
	}
	
	public Iterable<NonPackedNode> getPoppedElements() {
		return poppedElements;
	}
	
	public static class GSSNodeExternalHasher implements ExternalHasher<GSSNode> {
		
		private static final long serialVersionUID = 1L;

		@Override
		public int hash(GSSNode node, HashFunction f) {
			return f.hash(node.slot.getId(), node.inputIndex);
		}

		@Override
		public boolean equals(GSSNode g1, GSSNode g2) {
			return g1.slot.getId() == g2.slot.getId() &&
				   g1.inputIndex == g2.inputIndex;
		}
	}
	
	public static class LevelBasedGSSNodeExternalHasher implements ExternalHasher<GSSNode> {
		
		private static final long serialVersionUID = 1L;

		@Override
		public int hash(GSSNode node, HashFunction f) {
			return f.hash(node.slot.getId());
		}

		@Override
		public boolean equals(GSSNode g1, GSSNode g2) {
			return g1.slot == g2.slot;
		}
	}


}
