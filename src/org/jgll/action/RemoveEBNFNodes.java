package org.jgll.action;

import java.util.ArrayList;
import java.util.List;

import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;

public class RemoveEBNFNodes implements VisitAction {
	
	@Override
	public void execute(SPPFNode node) {
		
		if(node instanceof TerminalSymbolNode) {
			return;
		}
		
		if(node instanceof NonPackedNode && ((NonPackedNode) node).isAmbiguous()) {
			return;
		}
		
		// TODO: check commenting this line affects moving the annotations.
		// This is obviously a problem for intermediary EBNF nodes under the packed nodes
		// of an ambiguous nodes. The nodes should be removed before patern matching.
//		if (! (node instanceof SymbolNode)) {
//			return;
//		}
			
		// Because one cannot simply remove/modify the elements of a collection
		// while
		// iterating over it, this list is used to keep track of the elements
		// which should replace
		// the current elements in the collection. Nodes which should not be
		// removed, and the children of
		// the nodes which are removed are added to this collection (implementing the flattening structure).
		List<SPPFNode> newChildren = new ArrayList<SPPFNode>();

		for (SPPFNode child : node.getChildren()) {

			if(child instanceof TerminalSymbolNode) {
				newChildren.add(child);
			} 
			
			else  if (child instanceof NonterminalSymbolNode) {
				
				NonterminalSymbolNode symbolNode = (NonterminalSymbolNode) child;
				
				String label = symbolNode.getLabel();
				
				// Only intermediary nodes resulting from the EBNF to BNF conversion
				// can have +, *, ?, and ) in their symbol name, thus can be removed.
				if (label.endsWith("_*") || 
					label.endsWith("_+") || 
					label.endsWith("_?") ||
					(label.startsWith("(") && label.endsWith(")"))) {
									
					newChildren.addAll(symbolNode.getChildren());
				} else {
					newChildren.add(symbolNode);
				}
			}
		}
		
		node.setChildren(newChildren);	
	}
}