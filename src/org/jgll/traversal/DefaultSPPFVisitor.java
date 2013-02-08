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

	protected void visitChildren(SPPFNode node) {
		for(SPPFNode child : node) {
			child.accept(this);
		}
	}
	
	public static void removeIntermediateNode(NonterminalSymbolNode parent, IntermediateNode intermediateNode) {
		if(intermediateNode.isAmbiguous()) {
			
			List<SPPFNode> restOfChildren = new ArrayList<>();
			
			parent.removeChild(intermediateNode);
			
			while(parent.size() > 0) {
				restOfChildren.add(parent.get(0));
				parent.removeChild(parent.get(0));
			}
			
			for(SPPFNode child : intermediateNode) {
				// For each packed node of the intermediate node create a new packed node
				PackedNode pn = (PackedNode) child;
				PackedNode newPackedNode = new PackedNode(parent.getFirstPackedNodeGrammarSlot(), parent.size(), parent);
				for(SPPFNode sn : pn) {
					newPackedNode.addChild(sn);					
				}

				for(SPPFNode c : restOfChildren) {
					newPackedNode.addChild(c);
				}
				
				// Remove the ambiguous intermediate node
				parent.addChild(newPackedNode);
			}
			
		} else {
			parent.replaceWithChildren(intermediateNode);
		}
	}
	
	
	public static void removeIntermediateNode(PackedNode parent, IntermediateNode intermediateNode) {
		if(intermediateNode.isAmbiguous()) {
			
			NonPackedNode parentOfPackedNode = (NonPackedNode) parent.getParent();

			List<SPPFNode> restOfChildren = new ArrayList<>();
			
			parentOfPackedNode.removeChild(parent);
			
			while(parent.size() > 0) {
				restOfChildren.add(parent.get(0));
				parent.removeChild(parent.get(0));
			}
			
			for(SPPFNode child : intermediateNode) {
				// For each packed node of the intermediate node create a new packed node
				PackedNode pn = (PackedNode) child;
				PackedNode newPackedNode = new PackedNode(parentOfPackedNode.getFirstPackedNodeGrammarSlot(), parentOfPackedNode.size(), parentOfPackedNode);
				for(SPPFNode sn : pn) {
					newPackedNode.addChild(sn);					
				}

				for(SPPFNode c : restOfChildren) {
					newPackedNode.addChild(c);
				}
				
				parentOfPackedNode.addChild(newPackedNode);
			}
			
		} else {
			parent.replaceWithChildren(intermediateNode);
		}
	}
	
}
