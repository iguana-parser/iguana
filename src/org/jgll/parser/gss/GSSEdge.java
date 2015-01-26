package org.jgll.parser.gss;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.parser.GLLParser;
import org.jgll.parser.descriptor.Descriptor;
import org.jgll.sppf.NonPackedNode;

public interface GSSEdge {

	public NonPackedNode getNode();

	public BodyGrammarSlot getReturnSlot();

	public GSSNode getDestination();
	
	/**
	 * 
	 * Does the following:
	 * (1) checks conditions associated with the return slot
	 * (2) checks whether the descriptor to be created has been already created (and scheduled) before
	 * (2.1) if yes, returns null
	 * (2.2) if no, creates one and returns it
	 * 
	 */
	public Descriptor addDescriptor(GLLParser parser, GSSNode source, int inputIndex, NonPackedNode sppfNode);

}
