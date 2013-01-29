package org.jgll.action;

import static org.jgll.util.GraphVizUtil.EDGE;
import static org.jgll.util.GraphVizUtil.PACKED_NODE;
import static org.jgll.util.GraphVizUtil.SYMBOL_NODE;

import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;

/**
 * Creates a Graphviz's dot format representation of an SPPF node.
 * 
 * @author Ali Afroozeh
 * 
 * @see VisitAction
 */
public class ToDot implements VisitAction {
	
	private StringBuilder sb;
	
	public ToDot(StringBuilder sb) {
		this.sb = sb;
	}
	
	@Override
	public void execute(SPPFNode node) {
		
		if (node instanceof NonPackedNode) {
			sb.append("\"" + node.getId() + "\"" + String.format(SYMBOL_NODE, replaceWhiteSpace(node.toString()) + addAnnotations(node)) + "\n");
			addEdgesToChildren(node);
			
		} else if (node instanceof PackedNode) {
			sb.append("\"" + node.getId() + "\"" + String.format(PACKED_NODE, replaceWhiteSpace(node.toString())) + "\n");
			addEdgesToChildren(node);
		}
	}
	
	/**
	 * Adds edges to the given node's children
	 * 
	 * @param node the parent node
	 */
	private void addEdgesToChildren(SPPFNode node) {
		for (SPPFNode child : node.getChildren()) {
			sb.append(EDGE + "\"" + node.getId() + "\"" + "->" + "{\"" + child.getId() + "\"}" + "\n");
		}
	}
	
	private String replaceWhiteSpace(String s) {
		return s.replace("\\", "\\\\").replace("\t", "\\\\t").replace("\n", "\\\\n").replace("\r", "\\\\r").replace("\"", "\\\"");
	}
	
	private String addAnnotations(SPPFNode node) {

//		if(! (node instanceof SymbolNode)) {
//			throw new IllegalArgumentException("The input to this method should be a symbol node.");
//		}
//		
//		SymbolNode symbolNode = (SymbolNode) node;
//		StringBuilder sb = new StringBuilder();
		
		
//		if(symbolNode.getActionRules() != null) {
//			sb.append("\\n");
//			sb.append("[");
//			
//			for(ActionRule annotation : symbolNode.getActionRules()) {
//				sb.append(annotation.toString());
//				sb.append(",\\n");
//			}
//			sb.delete(sb.length() - 3, sb.length());
//			sb.append("]");
//		}
		
		return "";
	}

}
