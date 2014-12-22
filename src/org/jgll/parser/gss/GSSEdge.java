package org.jgll.parser.gss;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.sppf.NonPackedNode;

public interface GSSEdge {

	public NonPackedNode getNode();

	public BodyGrammarSlot getReturnSlot();

	public GSSNode getDestination();

}
