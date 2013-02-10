package org.jgll.sppf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jgll.grammar.GrammarSlot;

/**
 * A NonPackedNode corresponds to nonterminal symbol nodes or
 * intermediate symbols nodes in the GLL paper. These nodes 
 * have the common property of being related to a grammar slot
 * in the body of production rules.
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public abstract class NonPackedNode extends SPPFNode {
	
	protected final GrammarSlot slot;
	
	protected final int leftExtent;
	
	protected final int rightExtent;
	
	protected final List<SPPFNode> children;
	
	private GrammarSlot firstPackedNodeGrammarSlot = null;
	
	public NonPackedNode(GrammarSlot slot, int leftExtent, int rightExtent) {
		this.slot = slot;
		this.leftExtent = leftExtent;
		this.rightExtent = rightExtent;
		children = new ArrayList<>(2);		
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		result += 31 * result + slot.getId();
		result += 31 * result + leftExtent;
		result += 31 * result + rightExtent;
		return result;
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

		return  slot.equals(other.slot) &&
				leftExtent == other.leftExtent &&
				rightExtent == other.rightExtent;
	}
	
	public GrammarSlot getGrammarSlot() {
		return slot;
	}
	
	public int getLeftExtent() {
		return leftExtent;
	}

	public int getRightExtent() {
		return rightExtent;
	}
	
	public String toString() {
		return String.format("(%s, %d, %d)", getLabel(), leftExtent, rightExtent);
	}
	
	@Override
	public String getId() {
		return slot.getId() + "," + leftExtent + "," + rightExtent;
	}
	
	@Override
	public String getLabel() {
		return slot.toString();
	}
	
	public void addPackedNode(PackedNode newPackedNode, SPPFNode leftChild, SPPFNode rightChild) {
		
		int packedNodeCount = countPackedNode();
		
		// Don't store the first packed node as the node may not be ambiguous
		if(packedNodeCount == 0) {
			firstPackedNodeGrammarSlot = newPackedNode.getGrammarSlot();
			if(!leftChild.equals(DummyNode.getInstance())) {
				children.add(leftChild);
			}
			children.add(rightChild);
		}
		
		// When the second packed node is about to be added, create the first packed
		// node and add it. Then create the next packed node.
		else if (packedNodeCount == 1) {
			
			PackedNode firstPackedNode = getFirstPackedNode();

			for(SPPFNode child : children) {
				firstPackedNode.addChild(child);
			}
			
			children.clear();
			children.add(firstPackedNode);
			
			// the second packed node
			addChildren(newPackedNode, leftChild, rightChild);
			children.add(newPackedNode);
		} 
		
		else {
			addChildren(newPackedNode, leftChild, rightChild);
			children.add(newPackedNode);
		}
		
	}
	
	public boolean hasPackedNode(GrammarSlot grammarSlot, int pivot) {
		return firstPackedNodeGrammarSlot.equals(grammarSlot) &&
			   getPivot() == pivot;
	}
	
	private static void addChildren(PackedNode parent, SPPFNode leftChild, SPPFNode rightChild) {
		if (!leftChild.equals(DummyNode.getInstance())) {
			parent.addChild(leftChild);
		}
		parent.addChild(rightChild);
	}
	
	public boolean isAmbiguous() {
		return countPackedNode() > 1;
	}
	
	public void addChild(SPPFNode node) {
		children.add(node);
	}
	
	public void removeChild(SPPFNode node) {
		children.remove(node);
	}
	
	public void replaceWithChildren(SPPFNode node) {
		int index = children.indexOf(node);
		children.remove(node);
		if(index > 0) {
			for(SPPFNode child : node) {
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
	public SPPFNode get(int index) {
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
	public Iterator<SPPFNode> iterator() {
		return new Iterator<SPPFNode>() {

			int i = 0;
			
			@Override
			public boolean hasNext() {
				return i < children.size();
			}

			@Override
			public SPPFNode next() {
				return children.get(i++);
			}

			@Override
			public void remove() {
				children.remove(i);
			}
		};
	}

}
