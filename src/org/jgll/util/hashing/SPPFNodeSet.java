package org.jgll.util.hashing;

import org.jgll.sppf.SPPFNode;

public class SPPFNodeSet extends CuckooHashSet<SPPFNode> {

	private static final long serialVersionUID = 1L;
	
	private int level;
	
	@Override
	protected boolean contains(Object key, Object[] table1, Object[] table2) {
		int index = hash1(key);

		if(isEntryEmpty(table1[index])) {
			return false;
		}
		
		if(key.equals(table1[index])) {
			return true;
		}
		
		index = hash2(key);
		
		if(isEntryEmpty(table2[index])) {
			return false;
		}
		
		if(key.equals(table2[index])) {
			return true;
		}
		
		return false;
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
