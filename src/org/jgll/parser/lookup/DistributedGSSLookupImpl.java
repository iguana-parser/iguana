package org.jgll.parser.lookup;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.gss.GSSNode;
import org.jgll.sppf.NonPackedNode;

public class DistributedGSSLookupImpl implements GSSLookup {
	
	@Override
	public GSSNode getGSSNode(GrammarSlot head, int inputIndex) {
		return head.getGSSNode(inputIndex);
	}

	@Override
	public GSSNode hasGSSNode(GrammarSlot head, int inputIndex) {
		return head.hasGSSNode(inputIndex);
	}

	@Override
	public boolean addToPoppedElements(GSSNode gssNode, NonPackedNode sppfNode) {
		return gssNode.addToPoppedElements(sppfNode);
	}

	@Override
	public int getGSSNodesCount() {
		return 0;
	}

	@Override
	public int getGSSEdgesCount() {
		return 0;
	}

	@Override
	public Iterable<GSSNode> getGSSNodes() {
		return null;
	}

}
