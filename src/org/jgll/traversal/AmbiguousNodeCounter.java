package org.jgll.traversal;

import java.util.HashSet;
import java.util.Set;

import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.ListSymbolNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TokenSymbolNode;

public class AmbiguousNodeCounter implements SPPFVisitor {

	private int count;
	
	private Set<SPPFNode> visitedNodes;
	
	public AmbiguousNodeCounter() {
		visitedNodes = new HashSet<>();
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
		return visitedNodes.add(node);
	}
	
	private void countAmbiguousNodes(NonPackedNode node) {
		if (!isVisited(node)) {
		
			visitedNodes.add(node);
			
			if (node.isAmbiguous()) {
				count++;
			}
			
			for(SPPFNode child : node.getChildren()) {
				child.accept(this);
			}
		}
	}
	
	public int getCount() {
		return count;
	}

}
