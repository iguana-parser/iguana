package org.jgll.util;

import org.jgll.sppf.SPPFNode;

import tom.library.sl.Introspector;

/**
 * SPPFIntrospector enables Tom strategies to traverse an
 * SPPF. 
 * 
 * example:
 * <code>
 * `TopDown(StrategyName()).visit(node, new SPPFIntrospector());
 * </code>
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class SPPFIntrospector implements Introspector {

	@Override
	public Object getChildAt(Object o, int i) {
		return ((SPPFNode) o).childAt(i);
	}

	@Override
	public int getChildCount(Object o) {
		return ((SPPFNode) o).sizeChildren();
	}

	@Override
	public Object[] getChildren(Object o) {
		return ((SPPFNode) o).getChildren().toArray();
	}

	@SuppressWarnings("unchecked")
	@Override
	public  Object setChildAt(Object o, int i, Object child) {
		return o;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object setChildren(Object o, Object[] children) {
		return o;
	}

}