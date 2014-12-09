package org.jgll.sppf;


public abstract class AbstractSPPFNode implements SPPFNode {
		
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) return true;
		
		if (!(obj instanceof SPPFNode)) return false;
		
		return SPPFUtil.getInstance().equals(this, (SPPFNode) obj);
	}

	@Override
	public int hashCode() {
		return SPPFUtil.getInstance().hash(this);
	}
	
	@Override
	public String toString() {
		return String.format("(%s, %d, %d)", getGrammarSlot(), getLeftExtent(), getRightExtent());
	}
	
}
