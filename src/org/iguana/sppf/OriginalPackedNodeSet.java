package org.jgll.sppf;

import java.util.HashSet;
import java.util.Set;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.util.collections.IntKey2;
import org.jgll.util.hashing.hashfunction.IntHash2;


public class OriginalPackedNodeSet implements PackedNodeSet {
	
	private final Set<IntKey2> set;
	private final IntHash2 f;
	
	public OriginalPackedNodeSet(int inpuSize) {
		this.set = new HashSet<>();
		this.f = (x, y) -> x * inpuSize + y;
	}
	
	@Override
	public boolean addPackedNode(GrammarSlot slot, int pivot) {
		return set.add(IntKey2.from(slot.getId(), pivot, f));
	}

}
