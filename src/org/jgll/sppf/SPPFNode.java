package org.jgll.sppf;

import java.util.Iterator;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.traversal.SPPFVisitor;
import org.jgll.util.hashing.Level;

/**
 * An SPPF node is a node in an Shared Packed Parse Forest. This data structure
 * is used to store the parse forest that is the result of parsing an input
 * string with a GLL parser. <br />
 * 
 * @author Maarten Manders
 * @author Ali Afroozeh
 * 
 */

public abstract class SPPFNode implements Level {

	private boolean visited;
	
	private Object object;

	public abstract String getLabel();

	public abstract SPPFNode getChildAt(int index);
	
	public abstract Iterable<SPPFNode> getChildren();
	
	public abstract boolean isAmbiguous();

	public abstract int childrenCount();

	public abstract int getLeftExtent();

	public abstract int getRightExtent();

	public abstract void accept(SPPFVisitor visitAction);
	
	public abstract GrammarSlot getGrammarSlot();
	
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
	 * 				its children are equal to the corresponding
	 * 				(same order) children of the given node. 
	 * 
	 * Note: The standard equals method on SPPF nodes only compares
	 * 		 nodes based on their labels, ignoring their children. 
	 * 		 While parsing, the standard equals method is sufficient, 
	 * 		 but for comparing parse trees, the client should use deepEquals.
	 * 		 The deepEquals method is provided for testing purposes.
	 * 
	 */
	public boolean deepEquals(SPPFNode node) {
		
		if(!this.equals(node)) {
			return false;
		}
		
		if(this.childrenCount() != node.childrenCount()) {
			return false;
		}
		
		if(this.isAmbiguous() ^ node.isAmbiguous()) {
			return false;
		}
		
		// Packed nodes are not ordered, so we have to search
		// through the packed nodes of the given node to match
		// a packed node. This implementation may not be efficient for
		// ambiguous nodes having many packed nodes.
		
		if(this.isAmbiguous() && node.isAmbiguous()) {
			Iterator<SPPFNode> thisIt = getChildren().iterator();

			outer:
			while(thisIt.hasNext()) {
				SPPFNode thisChild = thisIt.next();
				Iterator<SPPFNode> otherIt = node.getChildren().iterator();
				while(otherIt.hasNext()) {
					SPPFNode otherChild = otherIt.next();
					if(!thisChild.deepEquals(otherChild)) {
						continue outer;
					}
				} 
				return false;
			}
			
			return true;
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
