package org.jgll.traversal;

import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.ListSymbolNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.TerminalSymbolNode;

/**
 * Provides a standard interface based on the command pattern for
 * executing actions in visitors of an SPPF.
 * 
 * @author Ali Afroozeh
 *
 * @see org.jgll.visitor.Visitor
 *
 */
public interface SPPFVisitor<T> {

	public void visit(TerminalSymbolNode node, T t);

	public void visit(NonterminalSymbolNode node, T t);
	
	public void visit(IntermediateNode node, T t);
	
	public void visit(PackedNode node, T t);
	
	public void visit(ListSymbolNode node, T t);
}
