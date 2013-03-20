package org.jgll.sppf;

import java.util.Iterator;

import org.jgll.traversal.SPPFVisitor;

/**
 * An SPPF node is a node in an Shared Packed Parse Forest. This data structure
 * is used to store the parse forest that is the result of parsing an input
 * string with a GLL parser. <br />
 * 
 * @author Maarten Manders
 * @author Ali Afroozeh
 * 
 */

public abstract class SPPFNode {

	private boolean visited;
	
	private Object object;

	public abstract String getId();

	public abstract String getLabel();

	public abstract SPPFNode get(int index);
	
	public abstract Iterable<SPPFNode> getChildren();
	
	public abstract boolean isAmbiguous();

	public abstract int size();

	public abstract int getLeftExtent();

	public abstract int getRightExtent();

	public abstract void accept(SPPFVisitor visitAction);
	
	public Object getObject() {
		return object;
	}
	
	public void setObject(Object object) {
		this.object = object;
	}
	
	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}
	
	/**
	 * Compares this SPPFNode with the given node and their 
	 * children. 
	 * 
	 * @return true if this node is equal to the given node, and
	 * 				this node has the same number of children as
	 * 				the other node, and children at corresponding
	 * 				indices are equals using the deepEquals method.
	 * 
	 * Note: The standard equals method on SPPF nodes only compares
	 * 		 nodes ignoring their children. While parsing, the standard
	 * 		 equals method is sufficient, but for comparing parse trees,
	 * 		 the client should use deepEquals.
	 * 
	 */
	public boolean deepEquals(SPPFNode node) {
		
		if(!this.equals(node)) {
			return false;
		}
		
		if(size() != node.size()) {
			return false;
		}
		
		Iterator<SPPFNode> thisIt = getChildren().iterator();
		Iterator<SPPFNode> otherIt = node.getChildren().iterator();
		
		while(thisIt.hasNext() && otherIt.hasNext()) {
			SPPFNode thisChild = thisIt.next();
			SPPFNode otherChild = otherIt.next();
			if(!thisChild.deepEquals(otherChild)) {
				return false;
			}
		}
		
		return true;
	}

}
