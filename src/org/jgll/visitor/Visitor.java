package org.jgll.visitor;

import org.jgll.action.VisitAction;
import org.jgll.sppf.SPPFNode;

/**
 * Provides an interface for SPPF visitors. A vistor visits SPPF nodes in 
 * the order dictated by the concrete implementation and applies visit actions
 * (instances of {@link VisitAction}) on them in order. 
 * 
 * @author Ali Afroozeh
 * 
 * @see VisitAction
 * @see TopDownVisitor
 * @see BottomUpVisitor
 * @see InnerMostVisitor
 * @see OnceTopDownVisitor
 * @see OnceBottomUpVisitor
 * @see OnceInnerMostVisitor

 */
public interface Visitor {

	/**
	 * Applies the provided visit actions in order on the given
	 * SPPF node.
	 * 
	 * @param node the given SPPF node
	 * @param visitActions an array of {@link VisitAction} instances
	 */
	public void visit(SPPFNode node, VisitAction...visitActions);
	
}
