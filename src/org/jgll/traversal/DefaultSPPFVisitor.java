package org.jgll.traversal;

import java.util.ArrayList;
import java.util.List;

import org.jgll.sppf.IntermediateNode;
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
public abstract class DefaultSPPFVisitor implements SPPFVisitor {
	
	public static int i;

	protected void visitChildren(SPPFNode node) {
		for(SPPFNode child : node.getChildren()) {
			child.accept(this);
		}
	}


	/**
	 * Removes the intermediate nodes under a nonterminal symbol node.
	 * 
	 * If the intermediate node is not ambiguous, the node is simply replaced
	 * by its children.
	 * <br>
	 * If the intermediate node is ambiguous, chilren of each of its packed nodes
	 * and its other children are merged to create new packed nodes for the parent.
	 * 
	 * 			     N                                           N
	 *             /   \                                    /        \
	 *            I     Other   =>                        P            P
	 *          /   \							        / | \        / | \
	 *         P     P                               c1 c2 Other  c3 c4 Other
	 *        / \   / \
	 *       c1 c2 c3 c4
	 *       
	 * The parent node is visited again to remove any newly introduced intermediate node. 
	 * The process terminates when all intermediate nodes are removed.
	 *       
	 */
	protected void removeIntermediateNode(NonterminalSymbolNode parent) {
		if(parent.get(0) instanceof IntermediateNode) {
			
			IntermediateNode intermediateNode = (IntermediateNode) parent.get(0);

			if(intermediateNode.isAmbiguous()) {
				i++;
				List<SPPFNode> restOfChildren = new ArrayList<>();

				parent.removeChild(intermediateNode);

				while(parent.size() > 0) {
					restOfChildren.add(parent.get(0));
					parent.removeChild(parent.get(0));
				}

				for(SPPFNode child : intermediateNode.getChildren()) {
					// For each packed node of the intermediate node create a new packed node
					PackedNode pn = (PackedNode) child;
					PackedNode newPackedNode = new PackedNode(parent.getFirstPackedNodeGrammarSlot(), parent.size(), parent);
					for(SPPFNode sn : pn.getChildren()) {
						newPackedNode.addChild(sn);					
					}

					for(SPPFNode c : restOfChildren) {
						newPackedNode.addChild(c);
					}

					parent.addChild(newPackedNode);
					removeIntermediateNode(newPackedNode);
				}

			} else {
				parent.replaceWithChildren(intermediateNode);
				removeIntermediateNode(parent);
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
	 * 			     P     P...                                        N         
	 *             /   \                                    /          |          \
	 *            I     Other   =>                        P            P          Other
	 *          /   \							        / | \        / | \
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
	protected void removeIntermediateNode(PackedNode parent) {
		if(parent.get(0) instanceof IntermediateNode) {

			IntermediateNode intermediateNode = (IntermediateNode) parent.get(0);

			if(intermediateNode.isAmbiguous()) {
				i++;
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
					PackedNode newPackedNode = new PackedNode(parentOfPackedNode.getFirstPackedNodeGrammarSlot(), parentOfPackedNode.size(), parentOfPackedNode);
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

}