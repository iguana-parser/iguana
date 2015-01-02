package org.jgll.sppf.lookup;

import org.jgll.sppf.SPPFNode;


@FunctionalInterface
public interface NodeAddedAction<T extends SPPFNode> {
	public void execute(T node);
}
