package org.jgll.parser.gss;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.L0;
import org.jgll.parser.HashFunctions;
import org.jgll.parser.descriptor.Descriptor;
import org.jgll.sppf.NonPackedNode;

/**
 *
 * @author Ali Afroozeh
 * 
 */
public class GSSNode {
	
	public static final GSSNode U0 = new GSSNode(L0.getInstance(), -1);
	
	private final GrammarSlot head;

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
	private final Set<GSSEdge> gssEdges;

	private final int hash;
	
	private Set<Descriptor> descriptors;
	
	/**
	 * Creates a new {@code GSSNode} with the given {@code label},
	 * {@code position} and {@code index}
	 * 
	 * @param slot
	 * @param inputIndex
	 */
	public GSSNode(GrammarSlot head, int inputIndex) {
		this.head = head;
		this.inputIndex = inputIndex;
		children = new ArrayList<>();
		poppedElements = new ArrayList<>();
		addedPoppedElements = new HashSet<>();
		gssEdges = new HashSet<>();
		
		descriptors = new HashSet<>();
		
		this.hash = HashFunctions.defaulFunction().hash(head.getId(), inputIndex);
	}
	
	public void addToPoppedElements(NonPackedNode node) {
		if(!addedPoppedElements.contains(node.getRightExtent())) {
			poppedElements.add(node);
			addedPoppedElements.add(node.getRightExtent());
		}
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
		return head;
	}

	public int getInputIndex() {
		return inputIndex;
	}
	
	public boolean getGSSEdge(GSSEdge edge) {
		addChild(edge.getDestination());
		return gssEdges.add(edge);
	}
	
	public Iterable<GSSEdge> getGSSEdges() {
		return gssEdges;
//		return new Iterable<GSSEdge>() {
//			
//			@Override
//			public Iterator<GSSEdge> iterator() {
//				return new LinkedList<>(gssEdges).descendingIterator();
//			}
//		};		
	}
	
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

	public int hashCode() {
		return hash;
	}
	
	public String toString() {
		return String.format("(%s, %d)", head, inputIndex);
	}

	public int getCountGSSEdges() {
		return gssEdges.size();
	}

	public boolean addDescriptor(Descriptor descriptor) {
		return descriptors.add(descriptor);
	}

	public void clearDescriptors() {
		descriptors.clear();
	}

}