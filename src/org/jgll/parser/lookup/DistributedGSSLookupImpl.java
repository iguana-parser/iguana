package org.jgll.parser.lookup;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.gss.GSSEdge;
import org.jgll.parser.gss.GSSNode;
import org.jgll.sppf.NonPackedNode;

public class DistributedGSSLookupImpl implements GSSLookup {
	
	private int countGSSEdges;
	
	private int countGSSNodes;
	
	@Override
	public GSSNode getGSSNode(GrammarSlot head, int inputIndex) {
		countGSSNodes++;
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
		return countGSSNodes;
	}

	@Override
	public int getGSSEdgesCount() {
		return countGSSEdges;
	}

	@Override
	public Iterable<GSSNode> getGSSNodes() {
		return null;
	}

	@Override
	public boolean getGSSEdge(GSSNode node, GSSEdge edge) {
		countGSSEdges++;
		return node.getGSSEdge(edge);
	}

}
