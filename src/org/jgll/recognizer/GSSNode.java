package org.jgll.recognizer;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.L0;
import org.jgll.parser.HashFunctions;
import org.jgll.util.hashing.CuckooHashSet;
import org.jgll.util.hashing.Decomposer;
import org.jgll.util.hashing.IntegerDecomposer;

public class GSSNode {

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
		this.children = new CuckooHashSet<>(new GSSDecomposer());
		this.poppedIndices = new CuckooHashSet<>(IntegerDecomposer.getInstance());
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

		return  other.slot.equals(slot) &&
				other.inputIndex == inputIndex;
	}

	@Override
	public int hashCode() {
		return HashFunctions.defaulFunction().hash(inputIndex, slot.getId());
	}
	

	@Override
	public String toString() {
		return slot + "," + inputIndex;
	}
	
	public static class GSSDecomposer implements Decomposer<GSSNode> {

		private int[] components = new int[2];
		
		@Override
		public int[] toIntArray(GSSNode t) {
			components[0] = t.getGrammarSlot().getId();
			components[1] = t.getInputIndex();
			return components;
		}
	}

}
