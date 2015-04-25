package org.iguana.traversal;

import java.util.HashSet;
import java.util.Set;

import org.iguana.sppf.IntermediateNode;
import org.iguana.sppf.NonPackedNode;
import org.iguana.sppf.NonterminalNode;
import org.iguana.sppf.PackedNode;
import org.iguana.sppf.SPPFNode;
import org.iguana.sppf.TerminalNode;

public class AmbiguousNodesVisitor implements SPPFVisitor {

	private Set<NonPackedNode> ambiguousNodes;
	
	private Set<SPPFNode> visitedNodes;

	public AmbiguousNodesVisitor() {
		visitedNodes = new HashSet<>();
		ambiguousNodes = new HashSet<>();
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
	public void visit(TerminalNode node) {
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
