package org.jgll.parser.gss;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.parser.HashFunctions;
import org.jgll.sppf.NonPackedNode;


class ArrayBasedGSSNode implements GSSNode {

	private final HeadGrammarSlot head;

	private final int inputIndex;
	
	private List<GSSNode> children;
	
	private List<NonPackedNode> poppedElements;
	
	/**
	 * Added popped elements are of the form (N, i, j) where N and i are the label and
	 * input input index of the current GSS node and thus are the same for this GSS node.
	 * Therefore, in order to eliminate the duplicates of popped SPPF nodes, we need
	 * to compare their right extent. This bit set is used for this purpose. 
	 * Maybe Hashset implemetations are faster. We should figure it out.
	 */
	private BitSet addedPoppedElements;

	private final int hash;
	
	/**
	 * Creates a new {@code GSSNode} with the given {@code label},
	 * {@code position} and {@code index}
	 * 
	 * @param slot
	 * @param inputIndex
	 */
	public ArrayBasedGSSNode(HeadGrammarSlot head, int inputIndex, int inputSize) {
		this.head = head;
		this.inputIndex = inputIndex;
		children = new ArrayList<>();
		poppedElements = new ArrayList<>();
		addedPoppedElements = new BitSet();
		
		this.hash = GSSNode.externalHasher.hash(this, HashFunctions.defaulFunction());
	}
	
	@Override
	public void addToPoppedElements(NonPackedNode node) {
		if(!addedPoppedElements.get(node.getRightExtent())) {
			poppedElements.add(node);
			addedPoppedElements.set(node.getRightExtent());
		}
	}
	
	@Override
	public Iterable<NonPackedNode> getPoppedElements() {
		return poppedElements;
	}
		
	@Override
	public Iterable<GSSNode> getChildren() {
		return children;
	}

	@Override
	public void addChild(GSSNode node) {
		children.add(node);
	}
	
	@Override
	public int sizeChildren() {
		return children.size();
	}
		
	@Override
	public GrammarSlot getGrammarSlot() {
		return head;
	}

	@Override
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

		return  head == other.getGrammarSlot() &&
				inputIndex == other.getInputIndex();
	}

	@Override
	public int hashCode() {
		return hash;
	}
	
	@Override
	public String toString() {
		return "(" + head + "," + inputIndex + ")";
	}
	
}
