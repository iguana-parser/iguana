
package org.jgll.parser.lookup;

import java.util.ArrayList;
import java.util.List;

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
public class ArrayGSSLookupImpl implements GSSLookup {

	/**
	 * Elements indexed by GSS nodes (Nonterminal index and input index)
	 */
	private GSSNode[][] gssNodes;

	public ArrayGSSLookupImpl(Input input, int size) {
		gssNodes = new GSSNode[size][input.length()];
	}

	@Override
	public GSSNode getGSSNode(GrammarSlot head, int inputIndex) {
		GSSNode gssNode = new GSSNode(head, inputIndex);
		gssNodes[head.getId()][inputIndex] = gssNode;		
		return gssNode;
	}
	
	@Override
	public GSSNode hasGSSNode(GrammarSlot head, int inputIndex) {
		return gssNodes[head.getId()][inputIndex];
	}

	@Override
	public int getGSSNodesCount() {
		int count = 0;

		for (int i = 0; i < gssNodes.length; i++) {
			for (int j = 0; j < gssNodes[i].length; j++) {
				if (gssNodes[i][j] != null) {
					count++;
				}
			}
		}

		return count;
	}

	@Override
	public Iterable<GSSNode> getGSSNodes() {
		List<GSSNode> nodes = new ArrayList<>();

		for (int i = 0; i < gssNodes.length; i++) {
			for (int j = 0; j < gssNodes[i].length; j++) {
				if (gssNodes[i][j] != null) {
					nodes.add(gssNodes[i][j]);
				}
			}
		}

		return nodes;
	}

	@Override
	public int getGSSEdgesCount() {
		int count = 0;

		for (int i = 0; i < gssNodes.length; i++) {
			for (int j = 0; j < gssNodes[i].length; j++) {
				if (gssNodes[i][j] != null) {
					count += gssNodes[i][j].getCountGSSEdges();
				}
			}
		}

		return count;
	}

	@Override
	public boolean addToPoppedElements(GSSNode gssNode, NonPackedNode sppfNode) {
		return gssNode.addToPoppedElements(sppfNode);
	}
}
