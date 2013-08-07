package org.jgll.recognizer;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.L0;
import org.jgll.parser.HashFunctions;
import org.jgll.util.hashing.CuckooHashSet;
import org.jgll.util.hashing.ExternalHasher;
import org.jgll.util.hashing.HashFunction;
import org.jgll.util.hashing.IntegerExternalHasher;

public class GSSNode {
	
	public static final ExternalHasher<GSSNode> externalHasher = new GSSNodeExternalHasher();

	public static final GSSNode U0 = new GSSNode(L0.getInstance(), 0);

	private final GrammarSlot slot;

	private final int inputIndex;

	private final CuckooHashSet<GSSNode> children;
	
	private final CuckooHashSet<Integer> poppedIndices;
	
	/**
	 * Creates a new {@code GSSNode} with the given {@code label},
	 * {@code position} and {@code index}
	 * 
	 * @param slot
	 * @param position
	 * @param inputIndex
	 */
	public GSSNode(GrammarSlot slot, int inputIndex) {
		this.slot = slot;
		this.inputIndex = inputIndex;
		this.children = new CuckooHashSet<>(new GSSNodeExternalHasher());
		this.poppedIndices = new CuckooHashSet<>(IntegerExternalHasher.getInstance());
	}
	
	public boolean hasChild(GSSNode child) {
		return children.contains(child);
	}
	
	public void addChild(GSSNode edge) {
		children.add(edge);
	}
	
	public Iterable<GSSNode> getChildren() {
		return children;
	}
	
	public void addPoppedIndex(int i) {
		poppedIndices.add(i);
	}
	
	public Iterable<Integer> getPoppedIndices() {
		return poppedIndices;
	}

	public final GrammarSlot getGrammarSlot() {
		return slot;
	}

	public final int getInputIndex() {
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

		return  other.slot == slot &&
				other.inputIndex == inputIndex;
	}

	@Override
	public int hashCode() {
		return externalHasher.hash(this, HashFunctions.defaulFunction());
	}
	

	@Override
	public String toString() {
		return slot + "," + inputIndex;
	}
	
	public static class GSSNodeExternalHasher implements ExternalHasher<GSSNode> {

		private static final long serialVersionUID = 1L;

		@Override
		public int hash(GSSNode node, HashFunction f) {
			return f.hash(node.getGrammarSlot().getId(), node.getInputIndex());
		}

		@Override
		public boolean equals(GSSNode g1, GSSNode g2) {
			return g1.slot == g2.slot && g1.inputIndex == g2.inputIndex;
		}
	}

}
