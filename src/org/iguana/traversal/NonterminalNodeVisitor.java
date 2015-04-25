package org.jgll.traversal;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalNode;

public class NonterminalNodeVisitor implements SPPFVisitor {

	private Set<SPPFNode> visitedNodes;
	private final Consumer<NonterminalNode> c;

	public static NonterminalNodeVisitor create(Consumer<NonterminalNode> c) {
		return new NonterminalNodeVisitor(c);
	}
	
	private NonterminalNodeVisitor(Consumer<NonterminalNode> c) {
		this.c = c;
		visitedNodes = new HashSet<>();
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
		visitChildren(node);
	}

	@Override
	public void visit(NonterminalNode node) {
		c.accept(node);
		visitChildren(node);
	}

	@Override
	public void visit(TerminalNode node) {
	}
	
	private boolean isVisited(SPPFNode node) {
		return visitedNodes.contains(node);
	}	
	
	private void visitChildren(NonPackedNode node) {
		if (!isVisited(node)) {
			visitedNodes.add(node);
			node.getChildren().forEach(c -> c.accept(this));
		}
	}
	
}
