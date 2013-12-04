package org.jgll.util.dot;

import static org.jgll.util.dot.GraphVizUtil.*;

import org.jgll.sppf.ListSymbolNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.traversal.SPPFVisitorUtil;
import org.jgll.util.Input;

public class ToDotWithoutIntermeidateAndLists extends ToDotWithoutIntermediateNodes {
	
	public ToDotWithoutIntermeidateAndLists(Input input) {
		super(input);
	}

	@Override
	public void visit(NonterminalSymbolNode node) {
		SPPFVisitorUtil.removeIntermediateNode(node);
		
		if(!node.isVisited()) {
			node.setVisited(true);
	
			sb.append("\"" + getId(node) + "\"" + String.format(SYMBOL_NODE, replaceWhiteSpace(node.toString())) + "\n");
		
			for(SPPFNode child : node.getChildren()) {
				if(!child.getLabel().startsWith("layout")) {
				  addEdgeToChild(node, child);
				  child.accept(this);
				}
			}
		}
	}
	
	@Override
	public void visit(PackedNode node) {
		SPPFVisitorUtil.removeIntermediateNode(node);
		
		if(!node.isVisited()) {
			node.setVisited(true);
	
			sb.append("\"" + getId(node) + "\"" + String.format(PACKED_NODE, "") + "\n");
		
			for(SPPFNode child : node.getChildren()) {
				if(!child.getLabel().startsWith("layout")) {
				  addEdgeToChild(node, child);
				  child.accept(this);
				}
			}
		}
	}
	
	@Override
	public void visit(ListSymbolNode node) {
		SPPFVisitorUtil.removeListSymbolNode(node);
		visit((NonterminalSymbolNode)node);
	}
	
}
