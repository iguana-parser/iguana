package org.jgll.traversal;

import java.util.ArrayList;
import java.util.List;

import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.ListSymbolNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;

/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class SPPFVisitorUtil {
	
	public static void visitChildren(SPPFNode node, SPPFVisitor visitor) {
		for(SPPFNode child : node.getChildren()) {
			child.accept(visitor);
		}
	}

	/**
	 * Removes the intermediate nodes under a nonterminal symbol node.
	 * 
	 * If the intermediate node is not ambiguous, the node is simply replaced
	 * by its children.
	 * <br>
	 * If the intermediate node is ambiguous, children of each of its packed nodes
	 * and its other children are merged to create new packed nodes for the parent.
	 * 
	 *               N                                          N
	 *             /   \                                    /        \
	 *            I     Other   =>                        P            P
	 *          /   \                                   / | \        / | \
	 *         P     P                               c1 c2 Other  c3 c4 Other
	 *        / \   / \
	 *       c1 c2 c3 c4
	 *       
	 * The parent node is visited again to remove any newly introduced intermediate node. 
	 * The process terminates when all intermediate nodes are removed.
	 *       
	 */
	public static void removeIntermediateNode(NonterminalSymbolNode node) {
		if(node.getChildAt(0) instanceof IntermediateNode) {
			
			IntermediateNode intermediateNode = (IntermediateNode) node.getChildAt(0);

			if(intermediateNode.isAmbiguous()) {
				List<SPPFNode> restOfChildren = new ArrayList<>();

				node.removeChild(intermediateNode);

				while(node.childrenCount() > 0) {
					restOfChildren.add(node.getChildAt(0));
					node.removeChild(node.getChildAt(0));
				}

				for(SPPFNode child : intermediateNode.getChildren()) {
					// For each packed node of the intermediate node create a new packed node
					PackedNode pn = (PackedNode) child;
					PackedNode newPackedNode = new PackedNode(node.getFirstPackedNodeGrammarSlot(), node.childrenCount(), node);
					for(SPPFNode sn : pn.getChildren()) {
						newPackedNode.addChild(sn);					
					}

					for(SPPFNode c : restOfChildren) {
						newPackedNode.addChild(c);
					}

					node.addChild(newPackedNode);
					removeIntermediateNode(newPackedNode);
				}

			} else {
				node.replaceWithChildren(intermediateNode);
				removeIntermediateNode(node);
			}
		}
	}


	/**
	 * Removes the intermediate nodes under a packed node. 
     *
	 * If the intermediate node is not ambiguous, the node is simply replaced
	 * by its children. 
	 * 
	 * If the intermediate node is ambiguous, then the parent, which is a 
	 * packed node, should be replaced by n new packed nodes, where 
	 * n is the number of packed nodes under the intermediate node.
	 * The children of each packed node under the intermediate node are
	 * merged with other children of the parent packed node and form a 
	 * new packed node which will be added to the parent of the parent packed
	 * node. 
	 *  
	 * 
	 *                  N                   
	 *                /   \
	 *               P     P...                                        N         
	 *             /   \                                    /          |          \
	 *            I     Other   =>                        P            P          Other
	 *          /   \                                   / | \        / | \        
	 *         P     P                               c1 c2 Other  c3 c4 Other     
	 *        / \   / \
	 *       c1 c2 c3 c4
	 *       
	 *       
	 * The parent or the original parent packed node is visited again to 
	 * remove any newly introduced intermediate node. 
	 * The process terminates when all intermediate nodes are removed.
	 * 
	 */
	public static void removeIntermediateNode(PackedNode parent) {
		if(parent.getChildAt(0) instanceof IntermediateNode) {

			IntermediateNode intermediateNode = (IntermediateNode) parent.getChildAt(0);

			if(intermediateNode.isAmbiguous()) {
				NonPackedNode parentOfPackedNode = (NonPackedNode) parent.getParent();

				List<SPPFNode> restOfChildren = new ArrayList<>();

				parentOfPackedNode.removeChild(parent);
				parent.removeChild(intermediateNode);

				for(SPPFNode sn : parent.getChildren()) {
					restOfChildren.add(sn);
				}

				for(SPPFNode child : intermediateNode.getChildren()) {
					// For each packed node of the intermediate node create a new packed node
					PackedNode pn = (PackedNode) child;
					PackedNode newPackedNode = new PackedNode(parentOfPackedNode.getFirstPackedNodeGrammarSlot(), parentOfPackedNode.childrenCount(), parentOfPackedNode);
					for(SPPFNode sn : pn.getChildren()) {
						newPackedNode.addChild(sn);					
					}

					for(SPPFNode c : restOfChildren) {
						newPackedNode.addChild(c);
					}

					parentOfPackedNode.addChild(newPackedNode);
					removeIntermediateNode(newPackedNode);
				}

			} else {
				parent.replaceWithChildren(intermediateNode);
				removeIntermediateNode(parent);
			}
		}
	}

	public static void removeListSymbolNode(ListSymbolNode node) {
		if(!node.isAmbiguous()) {
			removeIntermediateNode(node);
			if(node.getChildAt(0) instanceof ListSymbolNode) {
				ListSymbolNode child = (ListSymbolNode) node.getChildAt(0);
				node.replaceWithChildren(child);
				removeListSymbolNode(node);
			}
		}
	}
	
	public static void removeListSymbolNode(PackedNode node) {
		removeIntermediateNode(node);
		if(node.getChildAt(0) instanceof ListSymbolNode) {
			ListSymbolNode child = (ListSymbolNode) node.getChildAt(0);
			node.replaceWithChildren(child);
			removeListSymbolNode(node);
		}
	}
	
}