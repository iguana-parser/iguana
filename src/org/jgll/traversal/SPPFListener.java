package org.jgll.traversal;

public interface SPPFListener {
	
	public Object startNode(Object object);
	
	public Object endNode(Object object);
	
	public Object startAmbiguityNode();
	
	public Object endAmbiguityNode();
	
	public Object startPackedNode();
	
	public Object endPackedNode();
	
	public Object terminal(int c);
}
