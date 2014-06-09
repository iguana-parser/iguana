package org.jgll.traversal;

import java.util.HashSet;
import java.util.Set;

import org.jgll.grammar.GrammarGraph;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.ListSymbolNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TokenSymbolNode;
import org.jgll.util.Input;

public class AmbiguousNodeCounter implements SPPFVisitor {

	private int count;
	
	private Set<SPPFNode> ambiguousNodes;
	
	private Set<SPPFNode> visitedNodes;

	public AmbiguousNodeCounter(GrammarGraph grammarGraph, Input input) {
		visitedNodes = new HashSet<>();
		ambiguousNodes = new HashSet<>();
	}

	@Override
	public void visit(ListSymbolNode node) {
		countAmbiguousNodes(node);
	}

	@Override
	public void visit(PackedNode node) {
		if (!isVisited(node)) {
			visitedNodes.add(node);
			for(SPPFNode child : node.getChildren()) {
				child.accept(this);
			}
		}
	}

	@Override
	public void visit(IntermediateNode node) {
		countAmbiguousNodes(node);
	}

	@Override
	public void visit(NonterminalSymbolNode node) {
		countAmbiguousNodes(node);
	}

	@Override
	public void visit(TokenSymbolNode node) {
	}
	
	private boolean isVisited(SPPFNode node) {
		return visitedNodes.contains(node);
	}
	
	private void countAmbiguousNodes(NonPackedNode node) {
		if (!isVisited(node)) {
		
			visitedNodes.add(node);
			
			if (node.isAmbiguous()) {
				ambiguousNodes.add(node);
				count++;
			}
			
			for(SPPFNode child : node.getChildren()) {
				child.accept(this);
			}
		}
	}
	
	public Set<SPPFNode> getAmbiguousNodes() {
		return ambiguousNodes;
	}
	
	public int getCount() {
		return count;
	}

}
