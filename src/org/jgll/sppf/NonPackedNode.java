package org.jgll.sppf;

import java.util.ArrayList;
import java.util.List;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.HashFunctions;
import org.jgll.util.hashing.ExternalHasher;
import org.jgll.util.hashing.HashFunction;

/**
 * A NonPackedNode corresponds to nonterminal symbol nodes or
 * intermediate symbols nodes in the GLL paper. These nodes 
 * have the common property of being related to a grammar slot
 * in the body of production rules.
 * 
 * 
 * @author Ali Afroozeh
 * 
 * TODO: rename this class! The current name does not make much sense.
 *
 */

public abstract class NonPackedNode extends SPPFNode {
	
	public static final ExternalHasher<NonPackedNode> externalHasher = new NonPackedNodeExternalHasher();
	public static final ExternalHasher<NonPackedNode> levelBasedExternalHasher = new LevelBasedNonPackedNodeExternalHasher();
	
	protected final GrammarSlot slot;
	
	protected final int leftExtent;
	
	protected final int rightExtent;
	
	private GrammarSlot firstPackedNodeGrammarSlot = null;
	
	private int firstPackedNodePivot;
	
	protected List<SPPFNode> children;
	
	private int countPackedNode;

	public NonPackedNode(GrammarSlot slot, int leftExtent, int rightExtent) {
		this.slot = slot;
		this.leftExtent = leftExtent;
		this.rightExtent = rightExtent;
		this.children = new ArrayList<>(2);
	}
	
	@Override
	public int hashCode() {
		return externalHasher.hash(this, HashFunctions.defaulFunction());
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) {
			return true;
		}
		
		if(!(obj instanceof NonPackedNode)) {
			return false;
		}
		
		NonPackedNode other = (NonPackedNode) obj;

		return  rightExtent == other.rightExtent &&
				slot == other.slot &&
				leftExtent == other.leftExtent;
	}
	
	@Override
	public GrammarSlot getGrammarSlot() {
		return slot;
	}
	
	@Override
	public int getLeftExtent() {
		return leftExtent;
	}

	@Override
	public int getRightExtent() {
		return rightExtent;
	}
	
	@Override
	public String toString() {
		return String.format("(%s, %d, %d)", getLabel(), leftExtent, rightExtent);
	}
	
	@Override
	public String getLabel() {
		return slot.toString();
	}
		
	public PackedNode addSecondPackedNode(PackedNode packedNode, SPPFNode leftChild, SPPFNode rightChild) {
		PackedNode firstPackedNode = getFirstPackedNode();

		for(SPPFNode child : children) {
			firstPackedNode.addChild(child);
		}
		
		children.clear();
		children.add(firstPackedNode);
		
		addChildren(packedNode, leftChild, rightChild);
		children.add(packedNode);
		countPackedNode = 2;
		
		return firstPackedNode;
	}
	
	public void addPackedNode(PackedNode packedNode, SPPFNode leftChild, SPPFNode rightChild) {		
		addChildren(packedNode, leftChild, rightChild);
		countPackedNode++;
		children.add(packedNode);
	}
		
	private static void addChildren(PackedNode parent, SPPFNode leftChild, SPPFNode rightChild) {
		if (!leftChild.equals(DummyNode.getInstance())) {
			parent.addChild(leftChild);
		}
		parent.addChild(rightChild);
	}
	
	@Override
	public boolean isAmbiguous() {
		return countPackedNode > 1;
	}
	
	public void addChild(SPPFNode node) {
		//TODO: change it! PackedNodes cannot be added via this method at parse time.
		if(node instanceof PackedNode) {
			countPackedNode++;
		}
		children.add(node);
	}
	
	public void removeChild(SPPFNode node) {
		children.remove(node);
	}
	
	/**
	 * Replaces the given node with its children.
	 * If this does node have the given node as child,
	 * nothing will happen.  
	 */
	public void replaceWithChildren(SPPFNode node) {
		int index = children.indexOf(node);
		children.remove(node);
		if(index >= 0) {
			for(SPPFNode child : node.getChildren()) {
				children.add(index++, child);				
			}
		}
	}
	
	public PackedNode getFirstPackedNode() {
		if(isAmbiguous()) {
			return (PackedNode) children.get(0);
		}
		return new PackedNode(firstPackedNodeGrammarSlot, firstPackedNodePivot, this);
	}

	public GrammarSlot getFirstPackedNodeGrammarSlot() {
		return firstPackedNodeGrammarSlot;
	}
	
	public int getFirstPackedNodePivot() {
		return firstPackedNodePivot;
	}
	
	@Override
	public SPPFNode getChildAt(int index) {
		if(children.size() > index) {
			return children.get(index);
		}
		return null;
	}
	
	public void addFirstPackedNode(GrammarSlot slot, int pivot) {
		this.firstPackedNodeGrammarSlot = slot;
		this.firstPackedNodePivot = pivot;
		countPackedNode = 1;
	}
	
	@Override
	public Iterable<SPPFNode> getChildren() {
		return children;
	}
	
	@Override
	public int childrenCount() {
		return children.size();
	}
	
	public int getCountPackedNode() {
		return countPackedNode;
	}
	
	@Override
	public int getLevel() {
		return rightExtent;
	}
	
	public static class NonPackedNodeExternalHasher implements ExternalHasher<NonPackedNode> {

		private static final long serialVersionUID = 1L;

		@Override
		public int hash(NonPackedNode nonPackedNode, HashFunction f) {
			return f.hash(nonPackedNode.slot.getId(), nonPackedNode.leftExtent, nonPackedNode.rightExtent);
		}

		@Override
		public boolean equals(NonPackedNode node1, NonPackedNode node2) {
			return  node1.rightExtent == node2.rightExtent &&
					node1.slot == node2.slot &&
					node1.leftExtent == node2.leftExtent;
		}
	}
	
	public static class LevelBasedNonPackedNodeExternalHasher implements ExternalHasher<NonPackedNode> {

		private static final long serialVersionUID = 1L;

		@Override
		public int hash(NonPackedNode nonPackedNode, HashFunction f) {
			return f.hash(nonPackedNode.slot.getId(), nonPackedNode.leftExtent);
		}
		
		@Override
		public boolean equals(NonPackedNode node1, NonPackedNode node2) {
			return  node1.slot == node2.slot &&
					node1.leftExtent == node2.leftExtent;
		}

	}

	
}
