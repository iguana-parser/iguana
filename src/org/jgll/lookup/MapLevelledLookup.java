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
	
	private Map<SPPFNode, SPPFNode>[] levels;
	
	private int countNonPackedNodes;

	private int longestTerminalChain;
	
	private TerminalSymbolNode[] terminals;
	
	public MapLevelledLookup(Grammar grammar, int inputSize) {
		super(grammar, inputSize);
		longestTerminalChain = grammar.getLongestTerminalChain();
		levels = new Map[longestTerminalChain + 1];
		
		for(int i = 0; i < longestTerminalChain + 1; i++) {
			levels[i] = new HashMap<>();
		}
		
		terminals = new TerminalSymbolNode[2 * inputSize];
	}
	
	@Override
	public void nextLevel() {
		levels[indexFor(currentLevel)] = new HashMap<>();
		currentLevel++;
	}
	
	private int indexFor(int inputIndex) {
		return inputIndex % (longestTerminalChain + 1);
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
			SPPFNode value = levels[index].get(key);
			if(value == null) {
				value = key;
				levels[index].put(key, value);
				countNonPackedNodes++;
			}			
			return value;
	}
	
//	@Override
//	public void createPackedNode(BodyGrammarSlot grammarPosition, int pivot, NonPackedNode parent, SPPFNode leftChild, SPPFNode rightChild) {
//		
//		PackedNode packedNode = new PackedNode(grammarPosition, pivot, parent);
//		
//		if(parent.countPackedNode() == 0) {
//			parent.addPackedNode(packedNode, leftChild, rightChild);
//		} 
//		
//		else if(parent.countPackedNode() == 1 && !parent.hasPackedNode(grammarPosition, pivot)) {
//			parent.addPackedNode(packedNode, leftChild, rightChild);
//
//			PackedNode firstPackedNode = parent.getFirstPackedNode();
//			Map<SPPFNode, SPPFNode> map = levels[indexFor(parent.getRightExtent())];
//			map.put(firstPackedNode, firstPackedNode);
//			map.put(packedNode, packedNode);
//		}
//		
//		else if(parent.isAmbiguous() && levels[indexFor(parent.getRightExtent())].put(packedNode, packedNode) == null) {
//			parent.addPackedNode(packedNode, leftChild, rightChild);
//		}
//	}
	
	@Override
	public TerminalSymbolNode getTerminalNode(int terminalIndex, int leftExtent) {
		int index = 2 * leftExtent;
		if(terminalIndex != -2) {
			index = 2 * leftExtent + 1;
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
