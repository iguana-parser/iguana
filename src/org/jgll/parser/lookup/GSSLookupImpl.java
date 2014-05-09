package org.jgll.parser.lookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.parser.gss.GSSEdge;
import org.jgll.parser.gss.GSSNode;
import org.jgll.parser.gss.GSSNodeFactory;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.SPPFNode;
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

	private final GSSNodeFactory gssNodeFactory;
	
	private final GrammarGraph grammar;
	
	public GSSLookupImpl(GrammarGraph grammar, Input input, GSSNodeFactory gssNodeFactory) {
		this.grammar = grammar;
		this.gssNodeFactory = gssNodeFactory;
		
		long start = System.nanoTime();

		gssNodes = new GSSNode[grammar.getNonterminals().size()][input.length()];

		long end = System.nanoTime();
		log.info("Lookup table initialization: %d ms", (end - start) / 1000_000);
	}

	@Override
	public GSSNode getGSSNode(HeadGrammarSlot head, int inputIndex) {
		GSSNode gssNode = gssNodeFactory.createGSSNode(head, inputIndex);
		gssNodes[head.getId()][inputIndex] = gssNode;		
		return gssNode;
	}
	
	@Override
	public GSSNode hasGSSNode(HeadGrammarSlot head, int inputIndex) {
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
	public boolean getGSSEdge(GSSNode source, GSSNode destination, SPPFNode node, BodyGrammarSlot returnSlot) {
		return source.getGSSEdge(destination, node, returnSlot);
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

	@Override
	public Iterable<GSSEdge> getEdges(GSSNode node) {
		return node.getGSSEdges();
	}
	
	@Override
	public Map<GSSNode, Iterable<GSSEdge>> getEdgesMap() {
		Map<GSSNode, Iterable<GSSEdge>> map = new HashMap<>();
		for(GSSNode node : getGSSNodes()) {
			map.put(node, getEdges(node));
		}
		return map;
	}

}
