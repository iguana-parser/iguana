package org.jgll.sppf;

import java.util.ArrayList;
import java.util.List;

import org.jgll.parser.HashFunctions;
import org.jgll.traversal.SPPFVisitor;

/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class PackedNode extends SPPFNode {
	
	private final int id;

	private final int pivot;

	private final SPPFNode parent;
	
	private final List<SPPFNode> children;
	
	private final int hash;
	
	public PackedNode(int id, int pivot, NonPackedNode parent) {
		
		assert id > 0;
		assert pivot >= 0;
		assert parent != null;
		
		this.id = id;
		this.pivot = pivot;
		this.parent = parent;
		
		this.children = new ArrayList<>(2);
		
		this.hash = HashFunctions.defaulFunction().hash(id,
   						  								pivot,
   						  								parent.getId(),
   						  								parent.getLeftExtent(),
   						  								parent.getRightExtent());
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
		
		return  id == other.id &&
		        pivot == other.pivot &&
		        parent.getId() == other.parent.getId() &&
		        parent.getLeftExtent() == other.parent.getLeftExtent() &&
		        parent.getRightExtent() == other.parent.getRightExtent();
	}
	
	public int getPivot() {
		return pivot;
	}
	
	@Override
	public int getId() {
		return id;
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
		return String.format("(%id, %d)", id, getPivot());
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

	@Override
	public SPPFNode getLastChild() {
		return children.get(children.size() - 1);
	}

	@Override
	public SPPFNode getFirstChild() {
		return children.get(0);
	}


}