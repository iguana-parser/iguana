package org.jgll.sppf;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.GrammarSlot;
import org.jgll.parser.HashFunctions;
import org.jgll.util.hashing.CuckooHashSet;

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
	
	protected final GrammarSlot slot;
	
	protected final int leftExtent;
	
	protected final int rightExtent;
	
	protected final List<SPPFNode> children;
	
	private GrammarSlot firstPackedNodeGrammarSlot = null;
	
	private Set<PackedNode> packedNodesSet;

	private final int hash;
	
	public NonPackedNode(GrammarSlot slot, int leftExtent, int rightExtent) {
		this.slot = slot;
		this.leftExtent = leftExtent;
		this.rightExtent = rightExtent;
		this.children = new ArrayList<>();
		this.hash = HashFunctions.defaulFunction().hash(slot.getId(), leftExtent, rightExtent);
	}
	
	@Override
	public int hashCode() {
		return hash;
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

		return  hash == other.hash &&
				rightExtent == other.rightExtent &&
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
	
	public void addPackedNode(GrammarSlot packedNodeSlot, int pivot, SPPFNode leftChild, SPPFNode rightChild) {
		
		int packedNodeCount = countPackedNode();
		
		// Don't store the first packed node as the node may not be ambiguous
		if(packedNodeCount == 0) {
			firstPackedNodeGrammarSlot = packedNodeSlot;
			if(!leftChild.equals(DummyNode.getInstance())) {
				children.add(leftChild);
			}
			children.add(rightChild);
		}
		
		// When the second packed node is about to be added, create the first packed
		// node and add it. Then create the next packed node.
		else if (packedNodeCount == 1) {
			
			if(firstPackedNodeGrammarSlot.equals(packedNodeSlot) && getPivot() == pivot) {
				return;
			}
			
			PackedNode firstPackedNode = getFirstPackedNode();

			for(SPPFNode child : children) {
				firstPackedNode.addChild(child);
			}
			
			children.clear();
			children.add(firstPackedNode);
			
			PackedNode secondPackedNode = new PackedNode(packedNodeSlot, pivot, this);
			addChildren(secondPackedNode, leftChild, rightChild);
			children.add(secondPackedNode);
			
//			packedNodesIndex = new BitSet(range * slots);
			packedNodesSet = new CuckooHashSet<>();
			packedNodesSet.add(firstPackedNode);
			packedNodesSet.add(secondPackedNode);
		} 
		
		else {
			
			PackedNode packedNode = new PackedNode(packedNodeSlot, pivot, this);

			if(packedNodesSet.add(packedNode)) {
				addChildren(packedNode, leftChild, rightChild);
				children.add(packedNode);
			}
			
		}
		
	}
		
	private static void addChildren(PackedNode parent, SPPFNode leftChild, SPPFNode rightChild) {
		if (!leftChild.equals(DummyNode.getInstance())) {
			parent.addChild(leftChild);
		}
		parent.addChild(rightChild);
	}
	
	@Override
	public boolean isAmbiguous() {
		return countPackedNode() > 1;
	}
	
	public void addChild(SPPFNode node) {
		if(node instanceof PackedNode) {
			firstPackedNodeGrammarSlot = ((PackedNode) node).getGrammarSlot();
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
	
	public int countPackedNode() {
		if(firstPackedNodeGrammarSlot == null) {
			return 0;
		} else {
			return children.get(0) instanceof PackedNode ? children.size() : 1;
		}
	}
	
	public PackedNode getFirstPackedNode() {
		if(isAmbiguous()) {
			return (PackedNode) children.get(0);
		}
		return new PackedNode(firstPackedNodeGrammarSlot, getPivot(), this);
	}

	public GrammarSlot getFirstPackedNodeGrammarSlot() {
		return firstPackedNodeGrammarSlot;
	}
	
	@Override
	public SPPFNode getChildAt(int index) {
		if(children.size() > index) {
			return children.get(index);
		}
		return null;
	}
	
	private int getPivot() {
		if(children.size() == 1) {
			return children.get(0).getLeftExtent();
		} else if (children.size() == 2) {
			return children.get(1).getLeftExtent();
		}
		
		throw new RuntimeException("Should not be here!");
	}
	
	@Override
	public Iterable<SPPFNode> getChildren() {
		return children;
	}
	
	@Override
	public int childrenCount() {
		return children.size();
	}
	
}
