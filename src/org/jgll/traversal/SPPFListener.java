package org.jgll.traversal;

import java.util.List;

public interface SPPFListener {
	
	public void startNode(Object object);
	
	public Object endNode(Object object);
	
	public Object buildAmbiguityNode(List<Object> list);
	
	public Object terminal(int c);
}