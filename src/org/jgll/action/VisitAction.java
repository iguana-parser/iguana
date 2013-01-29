package org.jgll.action;

import org.jgll.sppf.SPPFNode;

/**
 * Provides a standard interface based on the command pattern for
 * executing actions in visitors of an SPPF.
 * 
 * @author Ali Afroozeh
 *
 * @see org.jgll.visitor.Visitor
 *
 */
public interface VisitAction {

	/**
	 * Executes an action on the given SPPF node.
	 *
	 * @throws RuntimeException implementors should throw a runtime exception
	 * 							if the action cannot be executed on the given
	 * 							node.
	 */
	public void execute(SPPFNode node);
	
}
