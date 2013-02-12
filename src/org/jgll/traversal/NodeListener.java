package org.jgll.traversal;


/**
 * 
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public interface NodeListener<T> {
	
	public void startNode(T type);
	
	public Object endNode(T type, Iterable<Object> children);
	
	public Object buildAmbiguityNode(Iterable<Object> children);
	
	public Object terminal(int c);
}