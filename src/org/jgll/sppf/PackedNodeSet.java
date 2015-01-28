package org.jgll.sppf;

import org.jgll.grammar.slot.GrammarSlot;

@FunctionalInterface
public interface PackedNodeSet {
	public boolean addPackedNode(GrammarSlot slot, int pivot);
}
