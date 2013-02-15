package org.jgll.traversal;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public interface Node {

	public int getStart();
	
	public int getOffset();

	public int getLineNumber();
	
	public int getColumn();	
}
