package org.jgll.parser.gss;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.parser.Descriptor;
import org.jgll.parser.HashFunctions;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.SPPFNode;


class DefaultGSSNodeImpl implements GSSNode {

	private final HeadGrammarSlot head;

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
	public DefaultGSSNodeImpl(HeadGrammarSlot head, int inputIndex, int inputSize) {
		this.head = head;
		this.inputIndex = inputIndex;
		children = new ArrayList<>();
		poppedElements = new ArrayList<>();
		addedPoppedElements = new HashSet<>();
		gssEdges = new HashSet<>();
		
		descriptors = new HashSet<>();
		
		this.hash = GSSNode.externalHasher.hash(this, HashFunctions.defaulFunction());
	}
	
	@Override
	public void addToPoppedElements(NonPackedNode node) {
		if(!addedPoppedElements.contains(node.getRightExtent())) {
			poppedElements.add(node);
			addedPoppedElements.add(node.getRightExtent());
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
	public HeadGrammarSlot getGrammarSlot() {
		return head;
	}

	@Override
	public int getInputIndex() {
		return inputIndex;
	}
	
	@Override
	public boolean getGSSEdge(GSSNode destination, SPPFNode node, BodyGrammarSlot returnSlot) {
		GSSEdge edge = new GSSEdge(returnSlot, node, destination);
		addChild(destination);
		return gssEdges.add(edge);
	}
	
	@Override
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

	@Override
	public int getCountGSSEdges() {
		return gssEdges.size();
	}

	@Override
	public boolean addDescriptor(Descriptor descriptor) {
		return descriptors.add(descriptor);
	}

	@Override
	public void clearDescriptors() {
		descriptors.clear();
	}
	
}
