package org.jgll.lookup;

import java.util.HashMap;
import java.util.Map;

import org.jgll.grammar.BodyGrammarSlot;
import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarSlot;
import org.jgll.grammar.Nonterminal;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
//import org.jgll.util.OpenAddressingHashMap;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class MapLevelledLookup extends DefaultLookup implements LevelledLookup {

	private int currentLevel;
	
	@SuppressWarnings("unchecked")
	private Map<SPPFNode, SPPFNode>[] levels = new Map[inputSize];
	
	private int countNonPackedNodes;
	
	public MapLevelledLookup(Grammar grammar, int inputSize) {
		super(grammar, inputSize);	
	}
	
	@Override
	public void nextLevel(int level) {
		levels[currentLevel] = null;
		currentLevel = level;
	}
	
	@Override
	public SPPFNode getNonPackedNode(GrammarSlot slot, int leftExtent, int rightExtent) {

		SPPFNode key;
		if(slot.getId() < grammar.getNonterminals().size()) {
			key = new NonterminalSymbolNode(slot, leftExtent, rightExtent);
		} else {
			key = new IntermediateNode(slot, leftExtent, rightExtent);
		}
		
		if(levels[rightExtent] == null) {
			levels[rightExtent] = new HashMap<>();
			levels[rightExtent].put(key, key);
			countNonPackedNodes++;
			return key;
		}
		
		SPPFNode node = (SPPFNode) levels[rightExtent].get(key);
		if(node == null) {
			node = key;
			levels[rightExtent].put(key, node);
			countNonPackedNodes++;
		}
		
		return node;
	}
	
	@Override
	public void createPackedNode(BodyGrammarSlot grammarPosition, int pivot, NonPackedNode parent, SPPFNode leftChild, SPPFNode rightChild) {
		
		PackedNode packedNode = new PackedNode(grammarPosition, pivot, parent);
		
		if(parent.countPackedNode() == 0) {
			parent.addPackedNode(packedNode, leftChild, rightChild);
		} 
		
		else if(parent.countPackedNode() == 1 && !parent.hasPackedNode(grammarPosition, pivot)) {
			parent.addPackedNode(packedNode, leftChild, rightChild);

			PackedNode firstPackedNode = parent.getFirstPackedNode();
			Map<SPPFNode, SPPFNode> map = levels[parent.getRightExtent()];
			map.put(firstPackedNode, firstPackedNode);
			map.put(packedNode, packedNode);
		}
		
		else if(parent.isAmbiguous() && levels[parent.getRightExtent()].put(packedNode, packedNode) == null) {
			parent.addPackedNode(packedNode, leftChild, rightChild);
		}
	}
	
	@Override
	public TerminalSymbolNode getTerminalNode(int terminalIndex, int leftExtent) {
		
		int rightExtent;
		if(terminalIndex == -2) {
			rightExtent = leftExtent;
		} else {
			rightExtent = leftExtent + 1;
		}

		TerminalSymbolNode key = new TerminalSymbolNode(terminalIndex, leftExtent);

		if(levels[rightExtent] == null) {
			levels[rightExtent] = new HashMap<>();
			levels[rightExtent].put(key, key);
			countNonPackedNodes++;
			return key;
		}

		Map<SPPFNode, SPPFNode> map = levels[rightExtent];
		
		SPPFNode sppfNode = map.get(key);
			
		if(sppfNode == null) {
			sppfNode = key;
			map.put(key, sppfNode);
		}
		
		return (TerminalSymbolNode) sppfNode;
	}

	
	@Override
	public NonterminalSymbolNode getStartSymbol(Nonterminal startSymbol) {
		if(levels[inputSize - 1] == null) {
			return null;
		}
		return (NonterminalSymbolNode) levels[inputSize - 1].get(new NonterminalSymbolNode(startSymbol, 0, inputSize - 1));
	}

	@Override
	public int sizeNonPackedNodes() {
		return countNonPackedNodes;
	}

}
