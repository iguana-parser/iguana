package org.jgll.traversal;


/**
 * 
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public interface NodeListener<T, U> {
	
	public void startNode(T type);
	
	public U endNode(T type, Iterable<U> children);
	
	public U buildAmbiguityNode(Iterable<U> children);
	
	public U terminal(int c);
}