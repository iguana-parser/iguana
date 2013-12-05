package org.jgll.sppf;

import java.util.ArrayList;
import java.util.List;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.HashFunctions;
import org.jgll.traversal.SPPFVisitor;
import org.jgll.util.hashing.ExternalHasher;
import org.jgll.util.hashing.hashfunction.HashFunction;

/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class PackedNode extends SPPFNode {
	
	public static final ExternalHasher<PackedNode> externalHasher = new PackedNodeExternalHasher();
	public static final ExternalHasher<PackedNode> levelBasedExternalHasher = new PackedNodeLevelBasedExternalHasher();
	public static final ExternalHasher<PackedNode> InsideParentHasher = new InsideParentExternalHasher();
	
	private final GrammarSlot slot;

	private final int pivot;

	private final SPPFNode parent;
	
	private final List<SPPFNode> children;
	
	private final int hash;
	
	public PackedNode(GrammarSlot slot, int pivot, NonPackedNode parent) {
		
		assert slot != null;
		assert pivot < 0;
		assert parent != null;
		
		this.slot = slot;
		this.pivot = pivot;
		this.parent = parent;
		
		this.children = new ArrayList<>(2);
		
		this.hash = externalHasher.hash(this, HashFunctions.defaulFunction());
	}
			
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}

		if (!(obj instanceof PackedNode)) {
			return false;
		}
		
		PackedNode other = (PackedNode) obj;
		
		return  externalHasher.equals(this, other);
	}
	
	public int getPivot() {
		return pivot;
	}
	
	@Override
	public GrammarSlot getGrammarSlot() {
		return slot;
	}
	
	public SPPFNode getParent() {
		return parent;
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
		if(index >= 0) {
			for(SPPFNode child : node.getChildren()) {
				children.add(index++, child);				
			}
		}
	}

	@Override
	public int hashCode() {
		return hash;
	}
	
	@Override
	public String toString() {
		return String.format("(%s, %d)", getLabel(), getPivot());
	}
	
	@Override
	public String getLabel() {
		return slot.toString();
	}
	
	@Override
	public int getLeftExtent() {
		return parent.getLeftExtent();
	}

	@Override
	public int getRightExtent() {
		return parent.getRightExtent();
	}

	@Override
	public void accept(SPPFVisitor visitAction) {
		visitAction.visit(this);
	}

	@Override
	public SPPFNode getChildAt(int index) {
		if(children.size() > index) {
			return children.get(index);
		}
		return null;
	}

	@Override
	public int childrenCount() {
		return children.size();
	}

	@Override
	public Iterable<SPPFNode> getChildren() {
		return children;
	}

	@Override
	public boolean isAmbiguous() {
		return false;
	}

	public static class PackedNodeExternalHasher implements ExternalHasher<PackedNode> {

		private static final long serialVersionUID = 1L;

		@Override
		public int hash(PackedNode packedNode, HashFunction f) {
 			return f.hash(packedNode.slot.getId(),
   						  packedNode.pivot,
						  packedNode.parent.getGrammarSlot().getId(),
						  packedNode.parent.getLeftExtent(),
						  packedNode.parent.getRightExtent());
		}

		@Override
		public boolean equals(PackedNode node1, PackedNode node2) {
			return  node1.slot == node2.slot &&
			        node1.pivot == node2.pivot &&
			        node1.parent.getGrammarSlot() == node2.parent.getGrammarSlot() &&
			        node1.parent.getLeftExtent() == node2.parent.getLeftExtent() &&
			        node1.parent.getRightExtent() == node2.parent.getRightExtent();
		}
	}
	
	public static class PackedNodeLevelBasedExternalHasher implements ExternalHasher<PackedNode> {

		private static final long serialVersionUID = 1L;

		@Override
		public int hash(PackedNode packedNode, HashFunction f) {
 			return f.hash(packedNode.slot.getId(),
   						  packedNode.pivot,
						  packedNode.parent.getGrammarSlot().getId(),
						  packedNode.parent.getLeftExtent());
		}

		@Override
		public boolean equals(PackedNode node1, PackedNode node2) {
			return  node1.slot == node2.slot &&
			        node1.pivot == node2.pivot &&
			        node1.parent.getGrammarSlot() == node2.parent.getGrammarSlot() &&
			        node1.parent.getLeftExtent() == node2.parent.getLeftExtent();
		}
	}
	
	/**
	 * Hash code for packed nodes when they are considered in the context of their parents.
	 *
	 */
	public static class InsideParentExternalHasher implements ExternalHasher<PackedNode> {

		private static final long serialVersionUID = 1L;

		@Override
		public int hash(PackedNode packedNode, HashFunction f) {
 			return f.hash(packedNode.slot.getId(), packedNode.pivot);
		}

		@Override
		public boolean equals(PackedNode node1, PackedNode node2) {
			return node1.slot == node2.slot &&
			       node1.pivot == node2.pivot;
		}
	}

	@Override
	public SPPFNode getLastChild() {
		return children.get(children.size() - 1);
	}

	@Override
	public SPPFNode getFirstChild() {
		return children.get(0);
	}


}