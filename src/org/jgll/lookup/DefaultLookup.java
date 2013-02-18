package org.jgll.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarSlot;
import org.jgll.parser.GSSEdge;
import org.jgll.parser.GSSNode;
import org.jgll.sppf.SPPFNode;
//import org.jgll.util.OpenAddressingHashSet;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public abstract class DefaultLookup implements LookupTable {
	
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
	protected Map<GSSNode, List<SPPFNode>> poppedElements;

	protected final int inputSize;
	
	public DefaultLookup(Grammar grammar, int inputSize) {
		this.inputSize = inputSize;
		this.grammar = grammar;
		gssNodes = new GSSNode[grammar.getGrammarSlots().size()][];
		gssEdges = new HashSet<>(inputSize);
		poppedElements = new HashMap<GSSNode, List<SPPFNode>>(DEFAULT_HASHMAP_SIZE);
	}

	@Override
	public boolean getGSSEdge(GSSNode source, SPPFNode label, GSSNode destination) {
		GSSEdge key = new GSSEdge(source, label, destination);
		
		if(gssEdges.add(key)) {
			source.addEdge(key);
			return false;
		}
		
		return true;
	}

	@Override
	public GSSNode getGSSNode(GrammarSlot label, int inputIndex) {
		int index = label.getId() - grammar.getNonterminals().size();
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
