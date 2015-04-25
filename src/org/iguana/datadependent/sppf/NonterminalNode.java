package org.iguana.datadependent.sppf;

import org.iguana.grammar.slot.GrammarSlot;
import org.iguana.parser.gss.GSSNodeData;
import org.iguana.sppf.PackedNodeSet;

public class NonterminalNode<T> extends org.iguana.sppf.NonterminalNode {

	private GSSNodeData<T> data;

	public NonterminalNode(GrammarSlot slot, int leftExtent, int rightExtent, PackedNodeSet set, GSSNodeData<T> data) {
		super(slot, leftExtent, rightExtent, set);
		this.data = data;
	}

	public GSSNodeData<T> getData() {
		return data;
	}
}
