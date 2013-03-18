package org.jgll.util;

import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.ListSymbolNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.traversal.SPPFVisitor;

public class ToJavaCode implements SPPFVisitor<StringBuilder> {

	@Override
	public void visit(TerminalSymbolNode node, StringBuilder t) {
		
	}

	@Override
	public void visit(NonterminalSymbolNode node, StringBuilder t) {
		
	}

	@Override
	public void visit(IntermediateNode node, StringBuilder t) {
		
	}

	@Override
	public void visit(PackedNode node, StringBuilder t) {
		
	}

	@Override
	public void visit(ListSymbolNode node, StringBuilder t) {
		
	}

}
