package org.jgll.util.hashing;

import org.jgll.parser.Descriptor;

public class DescriptorSet extends CuckooHashSet<Descriptor> {

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
		Descriptor desc = (Descriptor) o;
		return o == null || desc.getInputIndex() != level;
	}
	
	@Override
	public boolean add(Descriptor key) {
		level = key.getInputIndex();
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
