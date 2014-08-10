package org.jgll.traversal;

import java.util.HashSet;
import java.util.Set;

import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.ListSymbolNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TokenSymbolNode;

public class AmbiguousNodesVisitor implements SPPFVisitor {

	private Set<NonPackedNode> ambiguousNodes;
	
	private Set<SPPFNode> visitedNodes;

	public AmbiguousNodesVisitor() {
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
	public void visit(NonterminalNode node) {
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
			}
			
			for(SPPFNode child : node.getChildren()) {
				child.accept(this);
			}
		}
	}
	
	public Set<NonPackedNode> getAmbiguousNodes() {
		return ambiguousNodes;
	}

}
