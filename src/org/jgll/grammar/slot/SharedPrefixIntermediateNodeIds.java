package org.jgll.grammar.slot;

import java.util.List;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.precedence.OperatorPrecedence;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.util.CollectionsUtil;
import org.jgll.util.Tuple;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class SharedPrefixIntermediateNodeIds implements IntermediateNodeIds {
	
	private BiMap<List<Symbol>, Integer> intermediateNodeIds;
	
	public SharedPrefixIntermediateNodeIds(Grammar grammar) {
		this.intermediateNodeIds = HashBiMap.create();
		calculateIds(grammar);
	}
	
	private void calculateIds(Grammar grammar) {
		int intermediateId = 0;
		
		for (Rule rule : grammar.getRules()) {
			if (rule.getBody() != null) {
				for (int i = 2; i < rule.getBody().size(); i++) {
					List<Symbol> prefix = rule.getBody().subList(0, i);
					List<Symbol> plain = OperatorPrecedence.plain(prefix);
					intermediateNodeIds.put(plain, intermediateId);
					intermediateId++;
				}
			}
		}
	}

	@Override
	public int getSlotId(Rule rule, int index) {
		
		List<Symbol> alt = rule.getBody();

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
	public String getSlotName(int id) {
		return CollectionsUtil.listToString(intermediateNodeIds.inverse().get(id), " ");
	}
	
	@Override
	public List<Symbol> getPrefix(int id) {
		return intermediateNodeIds.inverse().get(id);
	}
	
	@Override
	public String toString() {
		return intermediateNodeIds.toString();
	}

	@Override
	public Tuple<Rule, Integer> getSlot(int id) {
		throw new RuntimeException("Should not be here.");
	}

	@Override
	public int getSlotId(String s) {
		throw new RuntimeException("Should not be here.");
	}
}
