package org.jgll.util.dot;

import static org.jgll.util.dot.GraphVizUtil.SYMBOL_NODE;

import org.jgll.grammar.Grammar;
import org.jgll.sppf.NonterminalSymbolNode;
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
	
	public ToDotWithoutIntermediateNodes(Grammar grammar, Input input) {
		super(grammar, input);
	}
	
	@Override
	public void visit(NonterminalSymbolNode node) {
		if(!node.isVisited()) {
			node.setVisited(true);
			
			if(node.isAmbiguous()) {
				
				int i = 0;
				while(i < node.childrenCount()) {
					SPPFVisitorUtil.removeIntermediateNode((PackedNode) node.getChildAt(i));
					i++;
				}
			} else {
				SPPFVisitorUtil.removeIntermediateNode(node);
			}
	
			String label = grammar.getNonterminalById(node.getId()).getName();
			sb.append("\"" + getId(node) + "\"" + String.format(SYMBOL_NODE, replaceWhiteSpace(label)) + "\n");
			addEdgesToChildren(node);
			
			SPPFVisitorUtil.visitChildren(node, this);
		}		
	}
}
