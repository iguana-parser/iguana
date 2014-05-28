package org.jgll.grammar.slot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.precedence.OperatorPrecedence;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.util.CollectionsUtil;

public class NewIntermediateNodeIds implements IntermediateNodeIds {
	
	private int intermediateId = 0;
	
	private Map<List<Symbol>, Integer> intermediateNodeIds;
	
	private Map<Integer, List<Symbol>> idToNameMap;
	
	private Grammar grammar;

	public NewIntermediateNodeIds(Grammar grammar) {
		this.grammar = grammar;
		this.intermediateNodeIds = new HashMap<>();
		this.idToNameMap = new HashMap<>();
		calculateIds();
	}
	
	@Override
	public void calculateIds() {
		for (Rule rule : grammar.getRules()) {
			if (rule.getBody() != null) {
				for (int i = 2; i < rule.getBody().size(); i++) {
					List<Symbol> prefix = rule.getBody().subList(0, i);
					List<Symbol> plain = OperatorPrecedence.plain(prefix);
					if (!intermediateNodeIds.containsKey(plain)) {
						intermediateNodeIds.put(plain, intermediateId);
						idToNameMap.put(intermediateId, plain);
						intermediateId++;
					}
				}
			}
		}
	}

	@Override
	public int getSlotId(List<Symbol> alt, int index) {

		if(alt.size() <= 2 || index <= 1) {
			return -1;
		}

		// Last grammar slot
		if(index == alt.size()) {
			return -1;
		}

		return intermediateNodeIds.get(OperatorPrecedence.plain(alt.subList(0, index)));
	}
	
	@Override
	public int getSlotId(List<Symbol> alt) {
		return intermediateNodeIds.get(alt);
	}

	@Override
	public String getSlotName(int id) {
		return CollectionsUtil.listToString(idToNameMap.get(id), " ");
	}
	
	@Override
	public List<Symbol> getSequence(int id) {
		
		return null;
	}
	
}
