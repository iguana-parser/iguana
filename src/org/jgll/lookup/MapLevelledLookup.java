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
@SuppressWarnings("unchecked")
public class MapLevelledLookup extends DefaultLookup implements LevelledLookup {

	private int currentLevel;
	
//	private Map<SPPFNode, Integer>[] validity;
	
	private Map<SPPFNode, SPPFNode>[] levels;
	
	private int countNonPackedNodes;

	private int longestTerminalChain;
	
	private TerminalSymbolNode[] terminals;
	
	public MapLevelledLookup(Grammar grammar, int inputSize) {
		super(grammar, inputSize);
		longestTerminalChain = grammar.getLongestTerminalChain();
//		validity = new Map[longestTerminalChain];
		levels = new Map[longestTerminalChain];
		
		for(int i = 0; i < longestTerminalChain; i++) {
//			validity[i] = new HashMap<>();
			levels[i] = new HashMap<>();
		}
		
		terminals = new TerminalSymbolNode[2 * inputSize + 1];
	}
	
	@Override
	public void nextLevel(int level) {
		currentLevel = level;
		
		levels = new Map[longestTerminalChain];
		
		for(int i = 0; i < longestTerminalChain; i++) {
			levels[i] = new HashMap<>();
		}
	}
	
	private int indexFor(int inputIndex) {
		return (inputIndex - currentLevel) % longestTerminalChain;
	}
	
	@Override
	public SPPFNode getNonPackedNode(GrammarSlot slot, int leftExtent, int rightExtent) {

		SPPFNode key;
		if(slot.getId() < grammar.getNonterminals().size()) {
			key = new NonterminalSymbolNode(slot, leftExtent, rightExtent);
		} else {
			key = new IntermediateNode(slot, leftExtent, rightExtent);
		}
		
		int index = indexFor(rightExtent);
//		if(validity[index].containsKey(key) && validity[index].get(key) != rightExtent) {
//			validity[index].put(key, rightExtent);
//			levels[index].put(key, key);
//			countNonPackedNodes++;
//			return key;
//		} else {
			SPPFNode value = levels[index].get(key);
			if(value == null) {
				value = key;
//				validity[index].put(key, rightExtent);
				levels[index].put(key, value);
				countNonPackedNodes++;
			}			
			return value;
//		}
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
			Map<SPPFNode, SPPFNode> map = levels[indexFor(parent.getRightExtent())];
			map.put(firstPackedNode, firstPackedNode);
			map.put(packedNode, packedNode);
		}
		
		else if(parent.isAmbiguous() && levels[indexFor(parent.getRightExtent())].put(packedNode, packedNode) == null) {
			parent.addPackedNode(packedNode, leftChild, rightChild);
		}
	}
	
	@Override
	public TerminalSymbolNode getTerminalNode(int terminalIndex, int leftExtent) {
		int index = leftExtent;
		if(terminalIndex != -2) {
			index = leftExtent + 1;
		}

		TerminalSymbolNode terminal = terminals[index];
		if(terminal == null) {
			terminal = new TerminalSymbolNode(terminalIndex, leftExtent);
			countNonPackedNodes++;
			terminals[index] = terminal;
		}
		
		return terminal;
	}

	
	@Override
	public NonterminalSymbolNode getStartSymbol(Nonterminal startSymbol) {
		int index = indexFor(inputSize - 1); 
		if(levels[index] == null) {
			return null;
		}
		return (NonterminalSymbolNode) levels[index].get(new NonterminalSymbolNode(startSymbol, 0, inputSize - 1));
	}

	@Override
	public int sizeNonPackedNodes() {
		return countNonPackedNodes;
	}

}
