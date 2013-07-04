package org.jgll.parser;

import java.util.Set;

import org.jgll.grammar.GrammarSlot;
import org.jgll.grammar.L0;
import org.jgll.sppf.SPPFNode;
import org.jgll.util.hashing.CuckooHashSet;

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
public class GSSNode {

	/**
	 * The initial GSS node
	 */
	public static final GSSNode U0 = new GSSNode(L0.getInstance(), 0);

	private final GrammarSlot slot;

	private final int inputIndex;

	private final Set<GSSEdge> gssEdges;
	
	private final int hash;
	
	private final Set<SPPFNode> poppedElements;
	
	/**
	 * Creates a new {@code GSSNode} with the given {@code label},
	 * {@code position} and {@code index}
	 * 
	 * @param slot
	 * @param inputIndex
	 */
	public GSSNode(GrammarSlot slot, int inputIndex) {
		this.slot = slot;
		this.inputIndex = inputIndex;
		this.gssEdges = new CuckooHashSet<>();
		this.poppedElements = new CuckooHashSet<>();
		
		this.hash = HashFunctions.defaulFunction().hash(slot.getId(), inputIndex);
	}
	
	public boolean hasGSSEdge(SPPFNode label, GSSNode destination) {
		return !gssEdges.add(new GSSEdge(label, destination));
	}
	
	public void addToPopElements(SPPFNode sppfNode) {
		poppedElements.add(sppfNode);
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
	
	public Iterable<SPPFNode> getPoppedElements() {
		return poppedElements;
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

		return  hash == other.hash &&
				slot == other.slot &&
				inputIndex == other.inputIndex;
	}

	@Override
	public int hashCode() {
		return hash;
	}

	@Override
	public String toString() {
		return slot + "," + inputIndex;
	}

}
