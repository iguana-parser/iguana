package org.jgll.sppf;

import java.util.Iterator;
import java.util.List;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.traversal.SPPFVisitor;

/**
 * 
 * @author Ali Afroozeh
 * 
 */

public interface SPPFNode {
	
	public SPPFNode getChildAt(int index);
	
	public List<SPPFNode> getChildren();
		
	public boolean isAmbiguous();

	public int childrenCount();

	public GrammarSlot getGrammarSlot();

	public int getLeftExtent();

	public int getRightExtent();
	
	public void accept(SPPFVisitor visitAction);
	
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
	 * 		 The deepEquals method is mainly meant for testing of the parser output
	 *       and should not be used while parsing.
	 * 
	 */
	// TODO: doesn't work in case SPPF has a cycle
	// TODO: create a set of packed nodes and then compare them instead of iterator stuff below.
	default boolean deepEquals(SPPFNode node) {
		
		if(!this.equals(node))
			return false;
		
		if(this.childrenCount() != node.childrenCount())
			return false;
		
		if(this.isAmbiguous() ^ node.isAmbiguous())
			return false;
		
		if(this.isAmbiguous() && node.isAmbiguous()) {
			return compareAmbiguousNodes(this, node);
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
	
	
	// Packed nodes are not ordered, so we have to search
	// through the packed nodes of the given node to match
	// a packed node. This implementation may not be efficient for
	// ambiguous nodes having many packed nodes.
	static boolean compareAmbiguousNodes(SPPFNode node1, SPPFNode node2) {
		
		Iterator<SPPFNode> thisIt = node1.getChildren().iterator();

		outer:
		while(thisIt.hasNext()) {
			SPPFNode thisChild = thisIt.next();
			Iterator<SPPFNode> otherIt = node2.getChildren().iterator();
			while(otherIt.hasNext()) {
				SPPFNode otherChild = otherIt.next();
				if(thisChild.deepEquals(otherChild)) {
					continue outer;
				}
			} 
			return false;
		}
		
		return true;
		
	}
	
}
