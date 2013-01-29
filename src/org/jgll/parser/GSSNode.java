package org.jgll.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgll.util.HashCode;

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

	public static final GSSNode DUMMY = new GSSNode(-1, 0);

	private final int label;

	private final int inputIndex;

	private final List<GSSEdge> edges;
	
	/**
	 * precoumputed hash node
	 */
	private final int hash;

	/**
	 * Creates a new {@code GSSNode} with the given {@code label},
	 * {@code position} and {@code index}
	 * 
	 * @param label
	 * @param position
	 * @param inputIndex
	 */
	public GSSNode(int label, int inputIndex) {
		this.label = label;
		this.inputIndex = inputIndex;
		this.edges = new ArrayList<>();
		
		hash = HashCode.hashCode(label, inputIndex);
	}
	
	public void addEdge(GSSEdge edge) {
		edges.add(edge);
	}
	
	public Collection<GSSEdge> getEdges() {
		return edges;
	}

	public final int getLabel() {
		return label;
	}

	public final int getIndex() {
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

		return  other.hash == hash &&
				other.label == label &&
				other.inputIndex == inputIndex;
	}

	@Override
	public int hashCode() {
		return hash;
	}

	@Override
	public String toString() {
		return label + "," + inputIndex;
	}

}
