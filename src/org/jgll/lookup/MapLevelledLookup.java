package org.jgll.lookup;

import java.util.Map;

import org.jgll.grammar.Grammar;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonPackedNodeWithChildren;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.OpenAddressingHashMap;


public class MapLevelledLookup extends DefaultLookup implements LevelledLookup {

	private int currentLevel;
	
	private Map<SPPFNode, SPPFNode>[] levels = new Map[inputSize + 1];
	
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
	public NonPackedNode getNonPackedNode(int grammarIndex, int leftExtent, int rightExtent) {

		NonPackedNode key;
		if(grammarIndex < grammar.getNonterminals().size()) {
			key = new NonterminalSymbolNode(grammarIndex, leftExtent, rightExtent);
		} else {
			key = new IntermediateNode(grammarIndex, leftExtent, rightExtent);
		}
		
		if(levels[rightExtent] == null) {
			levels[rightExtent] = new OpenAddressingHashMap<>();
			levels[rightExtent].put(key, key);
			countNonPackedNodes++;
			return key;
		}
		
		NonPackedNode node = (NonPackedNode) levels[rightExtent].get(key);
		if(node == null) {
			node = key;
			levels[rightExtent].put(key, node);
			countNonPackedNodes++;
		}
		
		return node;
	}
	
	@Override
	public void createPackedNode(int grammarPosition, int pivot, NonPackedNodeWithChildren parent, NonPackedNode leftChild, NonPackedNode rightChild) {
		
		PackedNode packedNode = new PackedNode(grammarPosition, pivot, parent);
		
		if(parent.countPackedNode() == 0) {
			parent.addPackedNode(packedNode, leftChild, rightChild);
		} 
		
		else if(parent.countPackedNode() == 1 && !parent.hasPackedNode(grammarPosition, pivot)) {
			parent.addPackedNode(packedNode, leftChild, rightChild);
			Map<SPPFNode, SPPFNode> map = levels[parent.getRightExtent()];
			map.put(parent.getFirstPackedNode(), parent.getFirstPackedNode());
			map.put(packedNode, packedNode);
		}
		
		if(parent.isAmbiguous() && levels[parent.getRightExtent()].put(packedNode, packedNode) == null) {
			parent.addPackedNode(packedNode, leftChild, rightChild);
		}
	}
	
	public TerminalSymbolNode getTerminalNode(int terminalIndex, int leftExtent, int rightExtent) {

		TerminalSymbolNode key = new TerminalSymbolNode(terminalIndex, leftExtent, rightExtent);

		if(levels[rightExtent] == null) {
			levels[rightExtent] = new OpenAddressingHashMap<>();
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
	public NonterminalSymbolNode getStartSymbol() {
		return (NonterminalSymbolNode) levels[inputSize].get(new NonterminalSymbolNode(grammar.getStartSymbol().getId(), 0, inputSize));
	}

	@Override
	public int sizeNonPackedNodes() {
		return countNonPackedNodes;
	}

}
