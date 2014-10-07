package org.jgll.parser.gss;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.HashFunctions;
import org.jgll.parser.descriptor.Descriptor;
import org.jgll.sppf.NonPackedNode;

/**
 *
 * @author Ali Afroozeh
 * 
 */
public class GSSNode {
	
	private final GrammarSlot slot;

	private final int inputIndex;
	
	private List<GSSNode> children;
	
	private List<NonPackedNode> poppedElements;
	
	/**
	 * Added popped elements are of the form (N, i, j) where N and i are the label and
	 * input input index of the current GSS node and thus are the same for this GSS node.
	 * Therefore, in order to eliminate the duplicates of popped SPPF nodes, we need
	 * to compare their right extent. This bit set is used for this purpose. 
	 * Maybe Hashset implementations are faster. We should figure it out.
	 */
	private Set<Integer> addedPoppedElements;
	
	// TODO: for recursive descent ordering, we need to traverse the GSS edges
	// the way they are added, so we need a hashset with ordering.
	private final List<GSSEdge> gssEdges;

	private Set<Descriptor> descriptors;
	
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
		children = new ArrayList<>();
		poppedElements = new ArrayList<>();
		addedPoppedElements = new HashSet<>();
		gssEdges = new ArrayList<>();
		descriptors = new HashSet<>();
	}
	
	public boolean addToPoppedElements(NonPackedNode node) {
		if(!addedPoppedElements.contains(node.getRightExtent())) {
			poppedElements.add(node);
			addedPoppedElements.add(node.getRightExtent());
			return true;
		} 
		
		return false;
	}
	
	public Iterable<NonPackedNode> getPoppedElements() {
		return poppedElements;
	}
		
	public Iterable<GSSNode> getChildren() {
		return children;
	}

	public void addChild(GSSNode node) {
		children.add(node);
	}
	
	public int sizeChildren() {
		return children.size();
	}
		
	public GrammarSlot getGrammarSlot() {
		return slot;
	}

	public int getInputIndex() {
		return inputIndex;
	}
	
	public boolean getGSSEdge(GSSEdge edge) {
		addChild(edge.getDestination());
		return gssEdges.add(edge);
	}
	
	public int countGSSEdges() {
		return gssEdges.size();
	}
	
	public int countPoppedElements() {
		return poppedElements.size();
	}
	
	public Iterable<GSSEdge> getGSSEdges() {
		return gssEdges;
	}
	
	public boolean equals(Object obj) {
		
		if(this == obj) {
			return true;
		}

		if (!(obj instanceof GSSNode)) {
			return false;
		}
		
		GSSNode other = (GSSNode) obj;

		return  slot == other.getGrammarSlot() &&
				inputIndex == other.getInputIndex();
	}

	public int hashCode() {
		return HashFunctions.defaulFunction().hash(slot.getId(), inputIndex);
	}
	
	public String toString() {
		return String.format("(%s, %d)", slot, inputIndex);
	}

	public int getCountGSSEdges() {
		return gssEdges.size();
	}

	public boolean hasDescriptor(Descriptor descriptor) {
		return !descriptors.add(descriptor);
	}

	public void clearDescriptors() {
		children.clear();;
		poppedElements.clear();;
		addedPoppedElements.clear();
		gssEdges.clear();
		descriptors.clear();;
	}
	
	public static class Key {
		private final int id;
		private final int inputIndex;
		
		public Key(int id, int inputIndex) {
			this.id = id;
			this.inputIndex = inputIndex;
		}
		
		@Override
		public int hashCode() {
			return HashFunctions.defaulFunction().hash(id, inputIndex);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == this) return true;
			
			if (!(obj instanceof Key)) return false;
			
			Key other = (Key) obj;
			
			return other.id == id && other.inputIndex == inputIndex;
		}
	}

}