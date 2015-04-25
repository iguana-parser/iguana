package org.jgll.datadependent.sppf;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.gss.GSSNodeData;
import org.jgll.sppf.PackedNodeSet;

public class NonterminalNode<T> extends org.jgll.sppf.NonterminalNode {

	private GSSNodeData<T> data;

	public NonterminalNode(GrammarSlot slot, int leftExtent, int rightExtent, PackedNodeSet set, GSSNodeData<T> data) {
		super(slot, leftExtent, rightExtent, set);
		this.data = data;
	}

	public GSSNodeData<T> getData() {
		return data;
	}
}
