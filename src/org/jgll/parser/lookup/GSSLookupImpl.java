package org.jgll.parser.lookup;

import java.util.ArrayList;
import java.util.List;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.gss.GSSNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.util.Input;
import org.jgll.util.logging.LoggerWrapper;

/**
 * 
 * This implementation is optimized for input files with an average number of
 * line of codes (less than 3000).
 * 
 * 
 * 
 * @author Ali Afroozeh
 * 
 */
public class GSSLookupImpl implements GSSLookup {

	private static final LoggerWrapper log = LoggerWrapper.getLogger(GSSLookupImpl.class);

	/**
	 * Elements indexed by GSS nodes (Nonterminal index and input index)
	 */
	private GSSNode[][] gssNodes;

	public GSSLookupImpl(Input input, int size) {
		
		long start = System.nanoTime();

		gssNodes = new GSSNode[size][input.length()];

		long end = System.nanoTime();
		log.info("Lookup table initialization: %d ms", (end - start) / 1000_000);
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
	public void addToPoppedElements(GSSNode gssNode, NonPackedNode sppfNode) {
		gssNode.addToPoppedElements(sppfNode);
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
