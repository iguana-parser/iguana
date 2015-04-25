package org.iguana.sppf;

import org.iguana.grammar.slot.GrammarSlot;

@FunctionalInterface
public interface PackedNodeSet {
	public boolean addPackedNode(GrammarSlot slot, int pivot);
}
