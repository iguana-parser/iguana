package org.jgll.parser.lookup;

import java.util.HashMap;
import java.util.Map;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.gss.GSSEdge;
import org.jgll.parser.gss.GSSNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.util.Input;

/**
 * 
 * 
 * @author Ali Afroozeh
 * 
 */
public class HashGSSLookupImpl implements GSSLookup {

	/**
	 * Elements indexed by GSS nodes (Nonterminal index and input index)
	 */
	private Map<GSSNode, GSSNode> gssNodes;
	
	public HashGSSLookupImpl(Input input, int size) {
		gssNodes = new HashMap<>();
	}

	@Override
	public GSSNode getGSSNode(GrammarSlot head, int inputIndex) {
		GSSNode gssNode = new GSSNode(head, inputIndex);
		gssNodes.put(new GSSNode(head, inputIndex), gssNode);		
		return gssNode;
	}
	
	@Override
	public GSSNode hasGSSNode(GrammarSlot head, int inputIndex) {
		return gssNodes.get(new GSSNode(head, inputIndex));
	}

	@Override
	public int getGSSNodesCount() {
		return gssNodes.size();
	}

	@Override
	public Iterable<GSSNode> getGSSNodes() {
		return gssNodes.values();
	}

	@Override
	public int getGSSEdgesCount() {
		int count = 0;
		
		for (GSSNode gssNode : gssNodes.values()) {
			count += gssNode.getCountGSSEdges();
		}

		return count;
	}

	@Override
	public boolean addToPoppedElements(GSSNode gssNode, NonPackedNode sppfNode) {
		return gssNode.addToPoppedElements(sppfNode);
	}

	@Override
	public boolean getGSSEdge(GSSNode gssNode, GSSEdge edge) {
		return gssNode.getGSSEdge(edge);
	}

}
