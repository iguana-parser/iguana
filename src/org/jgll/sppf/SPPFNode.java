package org.jgll.sppf;

import java.util.List;

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
	
	public abstract void addChild(SPPFNode node);

	public abstract void replaceByChildren(SPPFNode node);

	public abstract int sizeChildren();

	public abstract List<SPPFNode> getChildren();
	
	public abstract SPPFNode firstChild();
	
	public abstract SPPFNode childAt(int index);
	
	public abstract void removeChild(SPPFNode node);
	
	public abstract void removeChildren(List<SPPFNode> node);
	
	public abstract void setChildren(List<SPPFNode> children);
	
	public abstract String getId();
	
	public abstract String getLabel();

	private boolean visited;

	public void setVisited() {
		visited = true;
	}

	public void clearVisited() {
		visited = false;
	}

	public boolean isVisited() {
		return visited;
	}

}
