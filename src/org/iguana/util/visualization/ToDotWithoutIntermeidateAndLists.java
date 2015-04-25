package org.iguana.util.visualization;

import static org.iguana.util.visualization.GraphVizUtil.*;

import org.iguana.sppf.NonterminalNode;
import org.iguana.sppf.PackedNode;
import org.iguana.sppf.SPPFNode;
import org.iguana.traversal.SPPFVisitorUtil;
import org.iguana.util.Input;

public class ToDotWithoutIntermeidateAndLists extends ToDotWithoutIntermediateNodes {
	
	public ToDotWithoutIntermeidateAndLists(Input input) {
		super(input);
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
	
}
