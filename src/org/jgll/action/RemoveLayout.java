package org.jgll.action;

import java.util.ArrayList;
import java.util.List;

import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;

public class RemoveLayout implements VisitAction {
	
	
	@Override
	public void execute(SPPFNode node) {
		node.setChildren(getNonLayoutChildren(node));
	}
	
	private List<SPPFNode> getNonLayoutChildren(SPPFNode node) {
		
		List<SPPFNode> nonLayoutChildren = new ArrayList<SPPFNode>();
		
		for(SPPFNode child : node.getChildren()) {

			if(!(child instanceof NonterminalSymbolNode)) {
				continue;
			}
			
			String symbolName = node.getLabel();
			
			if(!symbolName.equals("Layout") ) {
				nonLayoutChildren.add(child);
			}

		}

		return nonLayoutChildren;
	}
		
}
