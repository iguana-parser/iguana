package org.jgll.parser.gss;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.sppf.SPPFNode;

public interface GSSEdge {

	public SPPFNode getNode();

	public BodyGrammarSlot getReturnSlot();

	public GSSNode getDestination();

}
