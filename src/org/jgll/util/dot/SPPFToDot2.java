package org.jgll.util.dot;

import static org.jgll.util.dot.GraphVizUtil.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.ListSymbolNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.RegularExpressionNode;
import org.jgll.sppf.RegularListNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
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
public class SPPFToDot2 extends ToDot {
	
	private final boolean showPackedNodeLabel;
	
	protected List<StringBuilder> list;
	
	private Map<SPPFNode, Integer> map;
	
	SPPFNode[] permutation;

	private Input input;
	
	public SPPFToDot2(Input input) {
		this(input, false);
	}
	
	public SPPFToDot2(Input input, boolean showPackedNodeLabel) {
		this.input = input;
		this.showPackedNodeLabel = showPackedNodeLabel;
		this.list = new ArrayList<>();
	}
	
	public void visit(SPPFNode node) {

		createPermutationMaps(node);
		
		int j = 0;
		for(SPPFNode sppfNode : map.keySet()) {
			permutation[j++] = sppfNode;
		}
		
		StringBuilder sb = new StringBuilder();
		visit(node, sb);
		list.add(sb);
		
		int i = 0;
		while(i < permutation.length) {
			if(map.get(permutation[i]) < permutation[i].childrenCount()) {
				map.put(permutation[i], map.get(permutation[i]) + 1);
				sb = new StringBuilder();
				visit(node, sb);
				list.add(sb);
			}  
			else {
				i++;
			}
		}
	}
	
	private void visit(SPPFNode node, StringBuilder sb) {

		if(node instanceof TerminalSymbolNode) {
			visit((TerminalSymbolNode) node, sb);
		} 
		else if(node instanceof NonterminalSymbolNode) {
			visit((NonterminalSymbolNode) node, sb);
		} 
		else if(node instanceof PackedNode) {
			visit((PackedNode)node, sb);
		} 
		else if(node instanceof IntermediateNode) {
			visit((IntermediateNode)node, sb); 
		}
	}

	public void visit(TerminalSymbolNode node, StringBuilder sb) {
		String label = node.getLabel();
		// Replaces the Java-style unicode char for epsilon with the graphviz one
		label.replace("\u03B5", "&epsilon;");
		sb.append("\"" + getId(node) + "\"" + String.format(SYMBOL_NODE, replaceWhiteSpace(label)) + "\n");
	}

	public void visit(NonterminalSymbolNode node, StringBuilder sb) {
		SPPFVisitorUtil.removeIntermediateNode(node);
		SPPFVisitorUtil.removeCollapsibleNode(node);
		
		sb.append("\"" + getId(node) + "\"" + String.format(SYMBOL_NODE, replaceWhiteSpace(node.getLabel())) + "\n");
		
		if(node.isAmbiguous()) {
			Integer index = map.get(node);
			SPPFNode child = node.getChildAt(index);
			
			if(child == null) {
				System.out.println("WTF?");
			}
			
			SPPFVisitorUtil.removeIntermediateNode((PackedNode) child);
			SPPFVisitorUtil.removeCollapsibleNode((PackedNode) child);
			
			addEdgesToChildren(node, child.getChildren(), sb);
			
			for(SPPFNode childOfPackedNode : child.getChildren()) {
				visit(childOfPackedNode, sb);					
			}
		}
		else {
			addEdgesToChildren(node, node.getChildren(), sb);
			
			for(SPPFNode child : node.getChildren()) {
				visit(child, sb);
			}
		}
	}

	public void visit(IntermediateNode node, StringBuilder sb) {
		sb.append("\"" + getId(node) + "\"" + String.format(INTERMEDIATE_NODE, replaceWhiteSpace(node.toString())) + "\n");
		addEdgesToChildren(node, node.getChildren(), sb);

		for(SPPFNode child : node.getChildren()) {
			visit(child, sb);
		}
	}

	public void visit(PackedNode node, StringBuilder sb) {

		if(showPackedNodeLabel) {
			sb.append("\"" + getId(node) + "\"" + String.format(PACKED_NODE, replaceWhiteSpace(node.toString())) + "\n");
		} else {
			sb.append("\"" + getId(node) + "\"" + String.format(PACKED_NODE, "") + "\n");
		}
		addEdgesToChildren(node, node.getChildren(), sb);
		
		for(SPPFNode child : node.getChildren()) {
			visit(child, sb);
		}
	}
	
	public List<StringBuilder> getResult() {
		return list;
	}
	
	protected void addEdgesToChildren(SPPFNode node, Iterable<SPPFNode> children, StringBuilder sb) {
		for (SPPFNode child : children) {
			addEdgeToChild(node, child, sb);
		}
	}
	
	protected void addEdgesToChildrenWithNode(SPPFNode node, Iterable<SPPFNode> children, StringBuilder sb) {
		for (SPPFNode child : children) {
			addEdgeToChild(node, child, sb);
			
		}
	}
	
	protected void addEdgeToChild(SPPFNode parentNode, SPPFNode childNode, StringBuilder sb) {
		sb.append(EDGE + "\"" + getId(parentNode) + "\"" + "->" + "{\"" + getId(childNode) + "\"}" + "\n");
	}
	
	protected String replaceWhiteSpace(String s) {
		return s.replace("\\", "\\\\").replace("\t", "\\\\t").replace("\n", "\\\\n").replace("\r", "\\\\r").replace("\"", "\\\"");
	}

	public void visit(ListSymbolNode node) {
		visit((NonterminalSymbolNode) node);
	}
	
	public void visit(RegularExpressionNode node, StringBuilder sb) {
		node.setVisited(true);

		sb.append("\"" + getId(node) + "\"" + String.format(SYMBOL_NODE, "\\\"" + input.subString(node.getLeftExtent(), node.getRightExtent()) + "\\\"" + "\n"));
	}
	
	private void createPermutationMaps(SPPFNode node) {
		map = new HashMap<>();
		SPPFVisitor sppfVisitor = new SPPFVisitor() {
			
			@Override
			public void visit(RegularExpressionNode node) {
				SPPFVisitorUtil.visitChildren(node, this);
			}
			
			@Override
			public void visit(RegularListNode node) {
				SPPFVisitorUtil.visitChildren(node, this);
			}
			
			@Override
			public void visit(ListSymbolNode node) {
				SPPFVisitorUtil.visitChildren(node, this);
			}
			
			@Override
			public void visit(PackedNode node) {
				SPPFVisitorUtil.visitChildren(node, this);
			}
			
			@Override
			public void visit(IntermediateNode node) {
				SPPFVisitorUtil.visitChildren(node, this);
			}
			
			@Override
			public void visit(NonterminalSymbolNode node) {
				if(node.isAmbiguous()) {
					map.put(node, 0);
				}
				SPPFVisitorUtil.visitChildren(node, this);
			}
			
			@Override
			public void visit(TerminalSymbolNode node) {}
		};
		
		node.accept(sppfVisitor);
		
		permutation = new SPPFNode[map.size()];		
	}
	
}
