package org.jgll.parser.lookup;

import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TokenSymbolNode;
import org.jgll.util.Input;
import org.jgll.util.hashing.ExternalHasher;
import org.jgll.util.hashing.HashTableFactory;
import org.jgll.util.hashing.IguanaSet;
import org.jgll.util.hashing.hashfunction.HashFunction;
import org.jgll.util.logging.LoggerWrapper;

public class SPPFLookupImpl implements SPPFLookup {
	
	private static final LoggerWrapper log = LoggerWrapper.getLogger(SPPFLookupImpl.class);
	
	private HashTableFactory factory;

	private int tableSize = (int) Math.pow(2, 10);
	
	private final TokenSymbolNode[][] tokenSymbolNodes;
	
	private final IguanaSet<NonterminalNode>[] nonterminalNodes;

	private final IguanaSet<IntermediateNode>[] intermediateNodes;
	
	private int countPackedNodes;
	
	public SPPFLookupImpl(GrammarGraph grammar, Input input) {
		long start = System.nanoTime();
		nonterminalNodes = new IguanaSet[input.length()];
		
		intermediateNodes = new IguanaSet[input.length()];

		tokenSymbolNodes = new TokenSymbolNode[grammar.getCountTokens()][input.length()];
		long end = System.nanoTime();
		log.info("SPPF lookup initialization: %d ms", (end - start) / 1000_000);
		
		factory = HashTableFactory.getFactory();
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

		IguanaSet<NonterminalNode> set = nonterminalNodes[rightExtent];

		if (set == null) {
			
			log.trace("Nonterminal node created: %s", key);
			
			set = factory.newHashSet(tableSize, new ExternalHasher<NonterminalNode>() {

				private static final long serialVersionUID = 1L;

				@Override
				public int hash(NonterminalNode n, HashFunction f) {
					return f.hash(n.getId(), n.getLeftExtent());
				}

				@Override
				public boolean equals(NonterminalNode n1, NonterminalNode n2) {
					return n1.getId() == n2.getId() &&
						   n1.getLeftExtent() == n2.getLeftExtent();
				}
			});
			nonterminalNodes[rightExtent] = set;
			set.add(key);
			return key;
		}

		NonterminalNode oldValue = set.add(key);
		if (oldValue == null) {
			log.trace("Nonterminal node created: %s", key);
			oldValue = key;
		}

		return oldValue;
	}

	@Override
	public NonterminalNode findNonterminalNode(HeadGrammarSlot grammarSlot, int leftExtent, int rightExtent) {
		
		IguanaSet<NonterminalNode> set = nonterminalNodes[rightExtent];

		if (set == null) {
			return null;
		}

		NonterminalNode key = grammarSlot.createSPPFNode(grammarSlot.getNodeId(), grammarSlot.getFirstSlots().length, leftExtent, rightExtent);
		return set.get(key);
	}

	@Override
	public IntermediateNode getIntermediateNode(BodyGrammarSlot grammarSlot, int leftExtent, int rightExtent) {
		IntermediateNode key = new IntermediateNode(grammarSlot.getNodeId(), leftExtent, rightExtent);

		IguanaSet<IntermediateNode> set = intermediateNodes[rightExtent];

		if (set == null) {
			set = factory.newHashSet(tableSize, new ExternalHasher<IntermediateNode>() {

				private static final long serialVersionUID = 1L;

				@Override
				public int hash(IntermediateNode n, HashFunction f) {
					return f.hash(n.getId(), n.getLeftExtent());
				}

				@Override
				public boolean equals(IntermediateNode n1, IntermediateNode n2) {
					return n1.getId() == n2.getId() &&
						   n1.getLeftExtent() == n2.getLeftExtent();
				}
			});
			intermediateNodes[rightExtent] = set;
			set.add(key);
			return key;
		}

		IntermediateNode oldValue = set.add(key);
		if (oldValue == null) {
			oldValue = key;
		}

		return oldValue;
	}

	@Override
	public IntermediateNode findIntermediateNode(BodyGrammarSlot grammarSlot, int leftExtent, int rightExtent) {
		IguanaSet<IntermediateNode> set = intermediateNodes[rightExtent];

		if (set == null) {
			return null;
		}

		IntermediateNode key = new IntermediateNode(grammarSlot.getId(), leftExtent, rightExtent);
		return set.get(key);
	}
	
	@Override
	public void addPackedNode(NonterminalNode parent, LastGrammarSlot slot, int pivot, SPPFNode leftChild, SPPFNode rightChild) {
		if (parent.addPackedNode(slot.getNodeId(), pivot, leftChild, rightChild)) countPackedNodes++;
	}
	
	@Override
	public void addPackedNode(IntermediateNode parent, BodyGrammarSlot slot, int pivot, SPPFNode leftChild, SPPFNode rightChild) {
		if (parent.addPackedNode(pivot, leftChild, rightChild)) countPackedNodes++;
	}
	
	@Override
	public NonterminalNode getStartSymbol(HeadGrammarSlot startSymbol, int inputSize) {
		if (nonterminalNodes[inputSize - 1] == null) {
			return null;
		}
		return nonterminalNodes[inputSize - 1].get(new NonterminalNode(startSymbol.getNodeId(), startSymbol.getFirstSlots().length, 0, inputSize - 1));
	}

	@Override
	public int getNonterminalNodesCount() {
		int count = 0;
		for(IguanaSet<NonterminalNode> set : nonterminalNodes) {
			if(set != null) {
				count += set.size();
			}
		}
		return count;
	}

	@Override
	public int getIntermediateNodesCount() {
		int count = 0;
		for(IguanaSet<IntermediateNode> set : intermediateNodes) {
			if(set != null) {
				count += set.size();
			}
		}
		return count;
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
}
