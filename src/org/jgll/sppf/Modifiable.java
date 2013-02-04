package org.jgll.sppf;

import java.util.List;

public interface Modifiable {
	
	public abstract void addChild(SPPFNode node);

	public abstract void replaceByChildren(SPPFNode node);

	public abstract int sizeChildren();

	public abstract List<SPPFNode> getChildren();
	
	public abstract SPPFNode firstChild();
	
	public abstract SPPFNode childAt(int index);
	
	public abstract void removeChild(SPPFNode node);
	
	public abstract void removeChildren(List<SPPFNode> node);
	
	public abstract void setChildren(List<SPPFNode> children);
}
