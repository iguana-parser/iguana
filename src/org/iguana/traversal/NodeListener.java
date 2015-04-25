package org.iguana.traversal;


/**
 * 
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public interface NodeListener<T, U> {
	
	public void startNode(T type);
	
	public Result<U> endNode(T type, Iterable<U> children, PositionInfo node);
	
	public Result<U> buildAmbiguityNode(Iterable<U> children, PositionInfo node);
	
	public Result<U> terminal(int c, PositionInfo node);
}