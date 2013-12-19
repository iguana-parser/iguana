package org.jgll.traversal;

import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.ListSymbolNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.TokenSymbolNode;

public class AmbiguousNodeCounter implements SPPFVisitor {

	private int count;

	@Override
	public void visit(ListSymbolNode node) {
		if (node.isAmbiguous()) {
			count++;
		}
	}

	@Override
	public void visit(PackedNode node) {
	}

	@Override
	public void visit(IntermediateNode node) {
		if (node.isAmbiguous()) {
			count++;
		}
	}

	@Override
	public void visit(NonterminalSymbolNode node) {
		if (node.isAmbiguous()) {
			count++;
		}
	}

	@Override
	public void visit(TokenSymbolNode node) {
	}
	
	public int getCount() {
		return count;
	}

}
