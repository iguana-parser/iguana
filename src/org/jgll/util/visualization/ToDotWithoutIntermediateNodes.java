package org.jgll.util.visualization;

import static org.jgll.util.visualization.GraphVizUtil.*;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.PackedNode;
import org.jgll.traversal.SPPFVisitor;
import org.jgll.traversal.SPPFVisitorUtil;
import org.jgll.util.Input;

/**
 * Creates a Graphviz's dot format representation of an SPPF node.
 * 
 * @author Ali Afroozeh
 * 
 * @see SPPFVisitor
 */
public class ToDotWithoutIntermediateNodes extends SPPFToDot {
	
	public ToDotWithoutIntermediateNodes(GrammarSlotRegistry registry, Input input) {
		super(registry, input);
	}
	
	@Override
	public void visit(NonterminalNode node) {

		if(!visited.contains(node)) {
			visited.add(node);
			
			if(node.isAmbiguous()) {
				int i = 0;
				while(i < node.childrenCount()) {
					SPPFVisitorUtil.removeIntermediateNode((PackedNode) node.getChildAt(i));
					i++;
				}
			} else {
				SPPFVisitorUtil.removeIntermediateNode(node);
			}
	
			String label = String.format("(%s, %d, %d)", node.getGrammarSlot().toString(), 
					 node.getLeftExtent(), node.getRightExtent());

			sb.append("\"" + getId(node) + "\"" + String.format(SYMBOL_NODE, replaceWhiteSpace(label)) + "\n");
			addEdgesToChildren(node);
			
			SPPFVisitorUtil.visitChildren(node, this);
		}		
	}
	
	@Override
	public void visit(PackedNode node) {

		if(!visited.contains(node)) {
			visited.add(node);
			
			if(node.isAmbiguous()) {
				int i = 0;
				while(i < node.childrenCount()) {
					SPPFVisitorUtil.removeIntermediateNode((PackedNode) node.getChildAt(i));
					i++;
				}
			} else {
				SPPFVisitorUtil.removeIntermediateNode(node);
			}
	
			if(showPackedNodeLabel) {
				sb.append("\"" + getId(node) + "\"" + String.format(PACKED_NODE, replaceWhiteSpace(node.toString())) + "\n");
			} else {
				sb.append("\"" + getId(node) + "\"" + String.format(PACKED_NODE, "") + "\n");
			}
			addEdgesToChildren(node);
			
			SPPFVisitorUtil.visitChildren(node, this);
		}
	}
}
