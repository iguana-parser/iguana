package org.jgll.parser.gss;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.sppf.NonPackedNode;

public interface GSSEdge {

	public NonPackedNode getNode();

	public GrammarSlot getReturnSlot();

	public GSSNode getDestination();

}
