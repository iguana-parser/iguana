package org.jgll.util.visualization;

import static org.jgll.util.visualization.GraphVizUtil.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgll.grammar.GrammarGraph;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.ListSymbolNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.traversal.SPPFVisitor;
import org.jgll.traversal.SPPFVisitorUtil;
import org.jgll.util.Input;

/**
 * Creates a Graphviz's dot format representation of an SPPF node in form of
 * its parse trees separately.
 * 
 * The unpacking of an SPPF is in general exponential, so this class should not
 * be used for large inputs.
 * 
 * 
 * @author Ali Afroozeh
 * 
 * @see SPPFVisitor
 */
public class SPPFToDotUnpacked extends ToDot {
	
	private final boolean showPackedNodeLabel;
	
	protected Set<String> parseTrees;
	
	private Map<SPPFNode, Integer> map;
	
	SPPFNode[] permutation;

	private Input input;

	public SPPFToDotUnpacked(Input input) {
		this(input, false);
	}
	
	public SPPFToDotUnpacked(Input input, boolean showPackedNodeLabel) {
		this.input = input;
		this.showPackedNodeLabel = showPackedNodeLabel;
		this.parseTrees = new HashSet<>();
	}
	
	public void visit(SPPFNode node) {

		createPermutationMaps(node);
		
		int j = 0;
		for(SPPFNode sppfNode : map.keySet()) {
			permutation[j++] = sppfNode;
		}
		
		StringBuilder sb = new StringBuilder();
		visit(node, sb);
		parseTrees.add(sb.toString());
		
		int i = 0;
		
		while(true) {
			
			boolean newPermutation = add(i, node);
			
			if(!newPermutation) {
				break;
			}			
		}
	}
	
	private boolean add(int i, SPPFNode node) {
		
		if(i == permutation.length) {
			return false;
		}
		
		if(i == permutation.length - 1 && map.get(permutation[i]) == permutation[i].childrenCount()) {
			return false;
		}
		
		if(map.get(permutation[i]) < permutation[i].childrenCount()) {
			map.put(permutation[i], map.get(permutation[i]) + 1);
			
			if(map.get(permutation[i]) == permutation[i].childrenCount()) {
				for(int k = 0; k <= i; k++) {
					map.put(permutation[k], 0);
				}
				return add(i + 1, node);
			}
			
			StringBuilder sb = new StringBuilder();
			visit(node, sb);
			 			
			parseTrees.add(sb.toString());
			return true;
		}
		
		
		return false;
	}
	
	private void visit(SPPFNode node, StringBuilder sb) {

		if(node instanceof NonterminalNode) {
			visit((NonterminalNode) node, sb);
		} 
		else if(node instanceof PackedNode) {
			visit((PackedNode)node, sb);
		} 
		else if(node instanceof IntermediateNode) {
			visit((IntermediateNode)node, sb); 
		}
	}

	public void visit(NonterminalNode node, StringBuilder sb) {
		
		String label = node.getGrammarSlot().toString();
		
		sb.append("\"" + getId(node) + "\"" + String.format(SYMBOL_NODE, replaceWhiteSpace(label)) + "\n");
		
		if(node.isAmbiguous()) {
			
			int i = 0;
			while(i < node.childrenCount()) {
				SPPFVisitorUtil.removeIntermediateNode((PackedNode) node.getChildAt(i));
				i++;
			}
			
			Integer index = map.get(node);
			SPPFNode child = node.getChildAt(index);
			
			addEdgesToChildren(node, child.getChildren(), sb);
			
			for(SPPFNode childOfPackedNode : child.getChildren()) {
				visit(childOfPackedNode, sb);					
			}
		}
		else {
			SPPFVisitorUtil.removeIntermediateNode(node);
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
	
	public Iterable<String> getResult() {
		return parseTrees;
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
		visit((NonterminalNode) node);
	}
	
	private void createPermutationMaps(SPPFNode node) {
		map = new HashMap<>();
		SPPFVisitor sppfVisitor = new SPPFVisitor() {
			
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
			public void visit(NonterminalNode node) {
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
