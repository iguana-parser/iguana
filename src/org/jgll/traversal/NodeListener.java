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
	
	public Object endNode(T type, Iterable<T> children);
	
	public Object buildAmbiguityNode(Iterable<T> children);
	
	public Object terminal(int c);
}