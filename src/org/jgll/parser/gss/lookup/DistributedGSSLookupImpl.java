package org.jgll.parser.gss.lookup;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.gss.GSSEdge;
import org.jgll.parser.gss.GSSNode;
import org.jgll.parser.gss.GSSNodeData;
import org.jgll.sppf.NonPackedNode;

public class DistributedGSSLookupImpl extends AbstractGSSLookup {
	
	@Override
	public GSSNode getGSSNode(GrammarSlot slot, int inputIndex) {
		countGSSNodes++;
		GSSNode gssNode = slot.getGSSNode(inputIndex);
		return gssNode;
	}

	@Override
	public GSSNode hasGSSNode(GrammarSlot slot, int inputIndex) {
		return slot.hasGSSNode(inputIndex);
	}

	@Override
	public boolean addToPoppedElements(GSSNode gssNode, NonPackedNode sppfNode) {
		return gssNode.addToPoppedElements(sppfNode);
	}

	@Override
	public boolean getGSSEdge(GSSNode node, GSSEdge edge) {
		countGSSEdges++;
		return node.getGSSEdge(edge);
	}
	
	/**
	 * 
	 * Data-dependent GLL parsing
	 * 
	 */
	@Override
	public <T> GSSNode getGSSNode(GrammarSlot slot, int inputIndex, GSSNodeData<T> data) {
		countGSSNodes++;
		GSSNode gssNode = slot.getGSSNode(inputIndex, data);
		return gssNode;
	}

	@Override
	public <T> GSSNode hasGSSNode(GrammarSlot slot, int inputIndex, GSSNodeData<T> data) {	
		return slot.hasGSSNode(inputIndex, data);
	}

}
