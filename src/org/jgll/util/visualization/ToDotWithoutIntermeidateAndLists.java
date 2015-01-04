package org.jgll.util.visualization;

import static org.jgll.util.visualization.GraphVizUtil.*;

import org.jgll.grammar.GrammarRegistry;
import org.jgll.sppf.ListSymbolNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.traversal.SPPFVisitorUtil;
import org.jgll.util.Input;

public class ToDotWithoutIntermeidateAndLists extends ToDotWithoutIntermediateNodes {
	
	public ToDotWithoutIntermeidateAndLists(GrammarRegistry registry, Input input) {
		super(registry, input);
	}

	@Override
	public void visit(NonterminalNode node) {
		SPPFVisitorUtil.removeIntermediateNode(node);
		
		if(!visited.contains(node)) {
			visited.add(node);
	
			sb.append("\"" + getId(node) + "\"" + String.format(SYMBOL_NODE, replaceWhiteSpace(node.toString())) + "\n");
		
			for(SPPFNode child : node.getChildren()) {
				
				String label = child.getGrammarSlot().toString();
				
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
		
		sb.append("\"" + getId(node) + "\"" + String.format(PACKED_NODE, "") + "\n");
	
		
		for(SPPFNode child : node.getChildren()) {
			String label = child.getGrammarSlot().toString();
			if(!label.startsWith("layout")) {
			  addEdgeToChild(node, child);
			  child.accept(this);
			}
		}
	}
	
	@Override
	public void visit(ListSymbolNode node) {
		SPPFVisitorUtil.removeListSymbolNode(node);
		visit((NonterminalNode)node);
	}
	
}
