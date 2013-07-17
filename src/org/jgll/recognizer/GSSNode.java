package org.jgll.recognizer;

import java.util.HashSet;
import java.util.Set;

import org.jgll.grammar.GrammarSlot;
import org.jgll.grammar.L0;
import org.jgll.parser.HashFunctions;

public class GSSNode {

	public static final GSSNode U0 = new GSSNode(L0.getInstance(), 0);

	private final GrammarSlot slot;

	private final int inputIndex;

	private final Set<GSSNode> children;
	
	private final Set<Integer> poppedIndices;
	
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
		this.children = new HashSet<>();
		this.poppedIndices = new HashSet<>();
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

	public final GrammarSlot getLabel() {
		return slot;
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

}
