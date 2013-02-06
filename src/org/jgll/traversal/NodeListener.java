package org.jgll.traversal;

import java.util.List;

/**
 * 
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public interface NodeListener {
	
	public void startNode(Object stored);
	
	public Object endNode(Object stored, Object result);
	
	public Object buildAmbiguityNode(List<Object> result);
	
	public Object terminal(int c, Object result);
}