package org.jgll.grammar.slot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Symbol;

public class OriginalIntermediateNodeIds implements IntermediateNodeIds {
	
	private Map<List<Symbol>, Integer> intermediateNodeIds;
	
	private Map<Integer, List<Symbol>> idToNameMap;
	
	private Grammar grammar;
	
	public OriginalIntermediateNodeIds(Grammar grammar) {
		this.grammar = grammar;
		this.intermediateNodeIds = new HashMap<>();
		this.idToNameMap = new HashMap<>();
		calculateIds();
	}

	@Override
	public void calculateIds() {
		
	}

	@Override
	public String getSlotName(int id) {
		return null;
	}

	@Override
	public List<Symbol> getSequence(int id) {
		return null;
	}

	@Override
	public int getSlotId(Rule rule, int index) {
		return 0;
	}

	@Override
	public int getSlotId(Rule rule) {
		return 0;
	}

}
