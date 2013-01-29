package org.jgll.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgll.grammar.Grammar;
import org.jgll.parser.GSSEdge;
import org.jgll.parser.GSSNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonPackedNodeWithChildren;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.OpenAddressingHashSet;

public abstract class DefaultLookup implements Lookup {
	
	/**
	 * Default 
	 * TODO: allow the user to control it.
	 */
	public static final int DEFAULT_HASHMAP_SIZE = 1024;
	
	protected final Grammar grammar;
	
	private GSSNode[][] gssNodes;
	
	private Set<GSSEdge> gssEdges;
	
	/**
	 * The popElements corresponds to P in the algorithm which keeps the links
	 * between a GSSNode and the SPPFNodes which are links to other GSSNodes.
	 */
	protected Map<GSSNode, List<NonPackedNode>> poppedElements;

	protected final int inputSize;
	
	public DefaultLookup(Grammar grammar, int inputSize) {
		this.inputSize = inputSize;
		this.grammar = grammar;
		gssNodes = new GSSNode[grammar.getGrammarSlots().size()][];
		gssEdges = new OpenAddressingHashSet<>(inputSize);
		poppedElements = new HashMap<GSSNode, List<NonPackedNode>>(DEFAULT_HASHMAP_SIZE);
	}

	public abstract TerminalSymbolNode getTerminalNode(int terminalIndex, int leftExtent, int rightExtent);
	
	public abstract NonPackedNode getNonPackedNode(int grammarIndex, int leftExtent, int rightExtent);

	@Override
	public boolean getGSSEdge(GSSNode source, NonPackedNode label, GSSNode destination) {
		GSSEdge key = new GSSEdge(source, label, destination);
		
		if(gssEdges.add(key)) {
			source.addEdge(key);
			return false;
		}
		
		return true;
	}

	@Override
	public GSSNode getGSSNode(int label, int inputIndex) {
		int index = label - grammar.getNonterminals().size();
		if(gssNodes[index] == null) {
			gssNodes[index] = new GSSNode[inputSize + 1];
		}
		if(gssNodes[index][inputIndex] == null) {
			gssNodes[index][inputIndex] = new GSSNode(label, inputIndex);
		} 
		
		return gssNodes[index][inputIndex];
	}

	@Override
	public void addToPoppedElements(GSSNode gssNode, NonPackedNode sppfNode) {
		
		// Add (cu, cn) to P
		List<NonPackedNode> nodeList = poppedElements.get(gssNode);
		if (nodeList == null) {
			nodeList = new ArrayList<NonPackedNode>();
			poppedElements.put(gssNode, nodeList);
		}
		nodeList.add(sppfNode);
	}
	
	public abstract void createPackedNode(int grammarPosition, int pivot, NonPackedNodeWithChildren parent, NonPackedNode leftChild, NonPackedNode rightChild);
	
	@Override
	public List<NonPackedNode> getEdgeLabels(GSSNode gssNode) {
		return poppedElements.get(gssNode);
	}

	public abstract NonterminalSymbolNode getStartSymbol();

	public abstract int sizeNonPackedNodes();
	
	@Override
	public int countGSSNodes() {
		return getGSSNodes().size();
	}
	
	@Override
	public int countGSSEdges() {
		return gssEdges.size();
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
}
