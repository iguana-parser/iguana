package org.jgll.sppf;

import java.util.List;

import org.jgll.traversal.SPPFVisitor;


/**
 * An SPPF node is a node in an Shared Packed Parse Forest. This data structure
 * is used to store the parse forest that is the result of parsing an input
 * string with a GLL parser. <br />
 * 
 * @author Maarten Manders
 * @author Ali Afroozeh
 * 
 */

public abstract class SPPFNode {
	
	private boolean visited;
	
	private Object object;
		
	public abstract String getId();
	
	public abstract String getLabel();
	
	public abstract List<SPPFNode> getChildren();
	
	public abstract int getLeftExtent();
	
	public abstract int getRightExtent();
	
	public abstract void accept(SPPFVisitor visitAction);
	
	public boolean isVisited() {
		return visited;
	}
	
	public void setVisited(boolean visited) {
		this.visited = visited;
	}
	
	public Object getObject() {
		return object;
	}
	
	public void setObject(Object object) {
		this.object = object;
	}
}
