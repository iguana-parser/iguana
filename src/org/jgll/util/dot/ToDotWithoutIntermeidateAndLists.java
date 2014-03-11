package org.jgll.util.dot;

import static org.jgll.util.dot.GraphVizUtil.PACKED_NODE;
import static org.jgll.util.dot.GraphVizUtil.SYMBOL_NODE;

import org.jgll.grammar.Grammar;
import org.jgll.sppf.ListSymbolNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.traversal.SPPFVisitorUtil;
import org.jgll.util.Input;

public class ToDotWithoutIntermeidateAndLists extends ToDotWithoutIntermediateNodes {
	
	public ToDotWithoutIntermeidateAndLists(Grammar grammar, Input input) {
		super(grammar, input);
	}

	@Override
	public void visit(NonterminalSymbolNode node) {
		SPPFVisitorUtil.removeIntermediateNode(node);
		
		if(!node.isVisited()) {
			node.setVisited(true);
	
			sb.append("\"" + getId(node) + "\"" + String.format(SYMBOL_NODE, replaceWhiteSpace(node.toString())) + "\n");
		
			for(SPPFNode child : node.getChildren()) {
				
				String label = grammar.getNonterminalById(child.getId()).getName();
				
				if(!label.startsWith("layout")) {
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
				String label = grammar.getNonterminalById(child.getId()).getName();
				if(!label.startsWith("layout")) {
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
