package org.jgll.util.hashing;

import org.jgll.sppf.SPPFNode;

public class SPPFNodeSet extends CuckooHashSet<SPPFNode> {

	private static final long serialVersionUID = 1L;
	
	private int level;
	
	@Override
	public boolean contains(Object key) {
		return super.contains(key);
	}
	
	@Override
	public boolean isEntryEmpty(Object o) {
		SPPFNode node = (SPPFNode) o;
		return o == null || node.getRightExtent() != level;
	}
	
	@Override
	public boolean add(SPPFNode key) {
		level = key.getRightExtent();
		return super.add(key);
	}

	@Override
	public void clear() {
		size = 0;
		rehashCount = 0;
		enlargeCount = 0;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
}
