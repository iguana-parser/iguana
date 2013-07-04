package org.jgll.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarSlot;
import org.jgll.grammar.HeadGrammarSlot;
import org.jgll.parser.GSSNode;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.ListSymbolNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;

/**
 * 
 * This class provides a skeletal implementation of {@link LookupTable}.
 * Based on how descriptors are removed from the set of
 * descriptors, e.g., using a stack or a queue, the implementation of
 * LookupTables may vary. This class only provides an implementation for 
 * lookup functionalities which are the same for different remove
 * strategies.
 * 
 * @author Ali Afroozeh
 *
 */
public abstract class AbstractLookupTable implements LookupTable {
	
	protected final Grammar grammar;
	
	private GSSNode[][] gssNodes;
	
	/**
	 * The popElements corresponds to P in the algorithm which keeps the links
	 * between a GSSNode and the SPPFNodes which are links to other GSSNodes.
	 */
	protected Map<GSSNode, List<SPPFNode>> poppedElements;

	protected final int inputSize;
	
	protected final int slotsSize;
	
	protected int gssEdgesCount;
	
	public AbstractLookupTable(Grammar grammar, int inputSize) {
		this.inputSize = inputSize;
		this.grammar = grammar;
		gssNodes = new GSSNode[grammar.getGrammarSlots().size()][];
		poppedElements = new HashMap<GSSNode, List<SPPFNode>>(inputSize);
		slotsSize = grammar.getGrammarSlots().size();
	}

	@Override
	public boolean getGSSEdge(GSSNode source, SPPFNode label, GSSNode destination) {
		 boolean added = source.getGSSEdge(label, destination);
		 if(added) {
			 gssEdgesCount++;
		 }
		 return added;
	}

	@Override
	public GSSNode getGSSNode(GrammarSlot label, int inputIndex) {
		int index = label.getId();
		if(gssNodes[index] == null) {
			gssNodes[index] = new GSSNode[inputSize];
		}
		if(gssNodes[index][inputIndex] == null) {
			gssNodes[index][inputIndex] = new GSSNode(label, inputIndex);
		} 
		
		return gssNodes[index][inputIndex];
	}

	@Override
	public void addToPoppedElements(GSSNode gssNode, SPPFNode sppfNode) {
		
		// Add (cu, cn) to P
		List<SPPFNode> nodeList = poppedElements.get(gssNode);
		if (nodeList == null) {
			nodeList = new ArrayList<SPPFNode>();
			poppedElements.put(gssNode, nodeList);
		}
		nodeList.add(sppfNode);
	}
	
	@Override
	public List<SPPFNode> getEdgeLabels(GSSNode gssNode) {
		return poppedElements.get(gssNode);
	}

	@Override
	public int getGSSNodesCount() {
		return getGSSNodes().size();
	}
	
	@Override
	public int getGSSEdgesCount() {
		return gssEdgesCount;
	}
	
	@Override
	public Collection<GSSNode> getGSSNodes() {
		Collection<GSSNode> list = new ArrayList<>();
		for(int i = 0; i < gssNodes.length; i++) {
			if(gssNodes[i] != null) {
			for(int j = 0; j < gssNodes[i].length; j++) {
				if(gssNodes[i][j] != null) {
					list.add(gssNodes[i][j]);
				}
			}
			}
		}
		return list;
	}
	
	protected NonPackedNode createNonPackedNode(GrammarSlot slot, int leftExtent, int rightExtent) {
		NonPackedNode key;
		if(slot instanceof HeadGrammarSlot) {
			HeadGrammarSlot head = (HeadGrammarSlot) slot;
			if(head.getNonterminal().isEbnfList()) {
				key = new ListSymbolNode(slot, leftExtent, rightExtent);
			} else {
				key = new NonterminalSymbolNode(slot, leftExtent, rightExtent);
			}
		} else {
			key = new IntermediateNode(slot, leftExtent, rightExtent);
		}
		return key;
	}
}
