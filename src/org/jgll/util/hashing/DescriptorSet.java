package org.jgll.util.hashing;

import org.jgll.parser.Descriptor;

public class DescriptorSet extends CuckooHashSet<Descriptor> {

	private static final long serialVersionUID = 1L;
	
	private int level;
	
	public DescriptorSet() {}
	
	public DescriptorSet(int initialCapacity) {
		super(initialCapacity);
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
