package org.jgll.util;

import static org.jgll.util.GraphVizUtil.SYMBOL_NODE;

import org.jgll.sppf.ListSymbolNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.traversal.SPPFVisitorUtil;

public class ToDotWithoutIntermeidateAndLists extends ToDotWithoutIntermediateNodes {
	
	@Override
	public void visit(NonterminalSymbolNode node) {
		super.visit(node);
		if(!node.isVisited()) {
			node.setVisited(true);
	
			sb.append("\"" + getId(node) + "\"" + String.format(SYMBOL_NODE, replaceWhiteSpace(node.toString())) + "\n");
			
			for (SPPFNode child : node.getChildren()) {
				if(!(child instanceof TerminalSymbolNode && ((TerminalSymbolNode) child).getMatchedChar() == -2)) {
					addEdgeToChild(node, child);					
				}
			}
			
			SPPFVisitorUtil.visitChildren(node, this);
		}
	}
	
	@Override
	public void visit(ListSymbolNode node) {
		SPPFVisitorUtil.removeListSymbolNode(node);
		visit((NonterminalSymbolNode)node);
	}
	
}
