package org.iguana.sppf;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.iguana.grammar.slot.GrammarSlot;
import org.iguana.traversal.SPPFVisitor;

/**
 * 
 * @author Ali Afroozeh
 * 
 */
public interface SPPFNode {
	
	public SPPFNode getChildAt(int index);
	
	public List<? extends SPPFNode> getChildren();
		
	public boolean isAmbiguous();

	public int childrenCount();

	public GrammarSlot getGrammarSlot();

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
	 */
	default boolean deepEquals(SPPFNode node) {
		return deepEquals(this, node, new HashSet<>());
	}
	
	static boolean deepEquals(SPPFNode node1, SPPFNode node2, Set<SPPFNode> visited) {

		if (visited.contains(node1)) 
			return true;
		
		visited.add(node1);
				
		if (!node1.equals(node2))
			return false;
		
		if (node1.childrenCount() != node2.childrenCount())
			return false;
		
		if (node1.isAmbiguous() ^ node2.isAmbiguous())
			return false;
		
		if (node1.isAmbiguous() && node2.isAmbiguous())
			return compareAmbiguousNodes(node1, node2, visited);
		
		Iterator<? extends SPPFNode> node1It = node1.getChildren().iterator();		
		Iterator<? extends SPPFNode> node2It = node2.getChildren().iterator();
		
		while (node1It.hasNext() && node2It.hasNext()) {
			SPPFNode node1Child = node1It.next();
			SPPFNode node2Child = node2It.next();
			if (!deepEquals(node1Child, node2Child, visited)) {
				return false;
			}
		}
		
		return true;
	}	
	
	static boolean compareAmbiguousNodes(SPPFNode node1, SPPFNode node2, Set<SPPFNode> visited) {
		
		Iterator<? extends SPPFNode> thisIt = node1.getChildren().iterator();

		outer:
		while(thisIt.hasNext()) {
			SPPFNode thisChild = thisIt.next();
			Iterator<? extends SPPFNode> otherIt = node2.getChildren().iterator();
			while(otherIt.hasNext()) {
				SPPFNode otherChild = otherIt.next();
				if(deepEquals(thisChild, otherChild, visited)) {
					continue outer;
				}
			} 
			return false;
		}
		
		return true;
	}
	
	default boolean isDummy() {
		return false;
	}

}
