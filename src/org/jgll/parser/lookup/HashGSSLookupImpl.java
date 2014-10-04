package org.jgll.parser.lookup;

import java.util.HashMap;
import java.util.Map;

import org.jgll.grammar.slot.GrammarSlot;
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
//	private GSSNode[][] gssNodes;
	private Map<GSSNode.Key, GSSNode> gssNodes;
	
	public HashGSSLookupImpl(Input input, int size) {
		gssNodes = new HashMap<>();
	}

	@Override
	public GSSNode getGSSNode(GrammarSlot head, int inputIndex) {
		GSSNode gssNode = new GSSNode(head, inputIndex);
		gssNodes.put(new GSSNode.Key(head.getId(), inputIndex), gssNode);		
		return gssNode;
	}
	
	@Override
	public GSSNode hasGSSNode(GrammarSlot head, int inputIndex) {
		return gssNodes.get(new GSSNode.Key(head.getId(), inputIndex));
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
	public Iterable<NonPackedNode> getPoppedElementsOf(GSSNode gssNode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterable<GSSNode> getChildren(GSSNode node) {
		return node.getChildren();
	}

}
