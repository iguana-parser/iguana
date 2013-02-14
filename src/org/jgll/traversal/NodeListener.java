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
	
	public Result<U> endNode(T type, Iterable<U> children, Node node);
	
	public Result<U> buildAmbiguityNode(Iterable<U> children, Node node);
	
	public Result<U> terminal(int c, Node node);
}