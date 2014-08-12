package org.jgll.parser.lookup;

import java.util.HashMap;
import java.util.Map;

import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TokenSymbolNode;
import org.jgll.util.Input;
import org.jgll.util.logging.LoggerWrapper;

public class SPPFLookupImpl implements SPPFLookup {
	
	private static final LoggerWrapper log = LoggerWrapper.getLogger(SPPFLookupImpl.class);
	
	private final TokenSymbolNode[][] tokenSymbolNodes;
	
	private final Map<NonterminalNode, NonterminalNode> nonterminalNodes;

	private final Map<IntermediateNode, IntermediateNode> intermediateNodes;
	
	private int countPackedNodes;
	
	private int countAmbiguousNodes;
	
	public SPPFLookupImpl(GrammarGraph grammar, Input input) {
		long start = System.nanoTime();

		nonterminalNodes = new HashMap<>();
		intermediateNodes = new HashMap<>();

		tokenSymbolNodes = new TokenSymbolNode[grammar.getCountTokens()][input.length()];
		long end = System.nanoTime();
		log.info("SPPF lookup initialization: %d ms", (end - start) / 1000_000);
	}

	@Override
	public TokenSymbolNode getTokenSymbolNode(int tokenID, int inputIndex, int length) {
		TokenSymbolNode node = tokenSymbolNodes[tokenID][inputIndex];
		if (node == null) {
			node = new TokenSymbolNode(tokenID, inputIndex, length);
			tokenSymbolNodes[tokenID][inputIndex] = node;
		}
		return node;
	}
	
	@Override
	public TokenSymbolNode findTokenSymbolNode(int tokenID, int inputIndex, int length) {
		return tokenSymbolNodes[tokenID][inputIndex];
	}

	@Override
	public NonterminalNode getNonterminalNode(HeadGrammarSlot grammarSlot, int leftExtent, int rightExtent) {
		NonterminalNode key = grammarSlot.createSPPFNode(grammarSlot.getNodeId(), grammarSlot.getFirstSlots().length, leftExtent, rightExtent);
		NonterminalNode value = nonterminalNodes.get(key);
		if (value == null) {
			value = key;
			log.trace("Nonterminal node created: %s", key);
			nonterminalNodes.put(key, value);
		}
		return value;
	}

	@Override
	public NonterminalNode findNonterminalNode(HeadGrammarSlot grammarSlot, int leftExtent, int rightExtent) {		
		NonterminalNode key = grammarSlot.createSPPFNode(grammarSlot.getNodeId(), grammarSlot.getFirstSlots().length, leftExtent, rightExtent);
		return nonterminalNodes.get(key);
	}

	@Override
	public IntermediateNode getIntermediateNode(BodyGrammarSlot grammarSlot, int leftExtent, int rightExtent) {
		IntermediateNode key = new IntermediateNode(grammarSlot.getNodeId(), leftExtent, rightExtent);
		IntermediateNode value = intermediateNodes.get(key);
		
		if (value == null) {
			value = key;
			log.trace("Intermediate node created: %s", key);
			intermediateNodes.put(key, value);
		}
		return value;
	}

	@Override
	public IntermediateNode findIntermediateNode(BodyGrammarSlot grammarSlot, int leftExtent, int rightExtent) {
		IntermediateNode key = new IntermediateNode(grammarSlot.getId(), leftExtent, rightExtent);
		return intermediateNodes.get(key);
	}
	
	@Override
	public void addPackedNode(NonterminalNode parent, LastGrammarSlot slot, int pivot, SPPFNode leftChild, SPPFNode rightChild) {
		boolean ambiguousBefore = parent.isAmbiguous();
		if (parent.addPackedNode(slot.getNodeId(), pivot, leftChild, rightChild)) {
			countPackedNodes++;
			boolean ambiguousAfter = parent.isAmbiguous();
			if (!ambiguousBefore && ambiguousAfter) countAmbiguousNodes++;
		}
	}
	
	@Override
	public void addPackedNode(IntermediateNode parent, BodyGrammarSlot slot, int pivot, SPPFNode leftChild, SPPFNode rightChild) {
		boolean ambiguousBefore = parent.isAmbiguous();
		if (parent.addPackedNode(pivot, leftChild, rightChild)) {
			countPackedNodes++;
			boolean ambiguousAfter = parent.isAmbiguous();
			if (!ambiguousBefore && ambiguousAfter) countAmbiguousNodes++;
		}
	}
	
	@Override
	public NonterminalNode getStartSymbol(HeadGrammarSlot startSymbol, int inputSize) {
		return nonterminalNodes.get(new NonterminalNode(startSymbol.getNodeId(), startSymbol.getFirstSlots().length, 0, inputSize - 1));
	}

	@Override
	public int getNonterminalNodesCount() {
		return nonterminalNodes.size();
	}

	@Override
	public int getIntermediateNodesCount() {
		return intermediateNodes.size();
	}

	@Override
	public int getTokenNodesCount() {
		int count = 0;
		for(int i = 0; i < tokenSymbolNodes.length; i++) {
			for(int j = 0; j < tokenSymbolNodes[i].length; j++) {
				if(tokenSymbolNodes[i][j] != null) {
					count++;
				}
			}
		}
		return count;
	}

	@Override
	public int getPackedNodesCount() {
		return countPackedNodes;
	}
	
	@Override
	public int getAmbiguousNodesCount() {
		return countAmbiguousNodes;
	}
}
