package org.jgll.traversal;

import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.TerminalNode;

/**
 * Provides a standard interface based on the command pattern for
 * executing actions in visitors of an SPPF.
 * 
 * @author Ali Afroozeh
 *
 * @see org.jgll.visitor.Visitor
 *
 */
public interface SPPFVisitor {

	public void visit(TerminalNode node);

	public void visit(NonterminalNode node);
	
	public void visit(IntermediateNode node);
	
	public void visit(PackedNode node);
	
}
