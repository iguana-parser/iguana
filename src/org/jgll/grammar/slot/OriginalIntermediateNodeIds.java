package org.jgll.grammar.slot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.precedence.OperatorPrecedence;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.util.Tuple;

public class OriginalIntermediateNodeIds implements IntermediateNodeIds {
	
	private Map<Tuple<Rule, Integer>, Integer> intermediateNodeIds;
	
	private Map<Integer, Tuple<Rule, Integer>> idToNameMap;
	
	private Grammar grammar;
	
	public OriginalIntermediateNodeIds(Grammar grammar) {
		this.grammar = grammar;
		this.intermediateNodeIds = new HashMap<>();
		this.idToNameMap = new HashMap<>();
		calculateIds();
	}

	@Override
	public void calculateIds() {
		int intermediateId = 0;
		
		for (Rule rule : grammar.getRules()) {
			if (rule.getBody() != null) {
				Rule plainRule = OperatorPrecedence.plain(rule);
				for (int i = 2; i < rule.getBody().size(); i++) {
					Tuple<Rule, Integer> t = Tuple.of(plainRule, i);
					if (!intermediateNodeIds.containsKey(t)) {
						intermediateNodeIds.put(t, intermediateId);
						idToNameMap.put(intermediateId, t);
						intermediateId++;
					}
				}
			}
		}
	}

	@Override
	public String getSlotName(int id) {
		Tuple<Rule, Integer> t = idToNameMap.get(id);
		Rule rule = t.getFirst();
		int index = t.getSecond();
		StringBuilder sb = new StringBuilder();
		sb.append(rule.getHead()).append(" ::= ");
		for (int i = 0; i < rule.getBody().size(); i++) {
			if (i == index) sb.append(". ");
			sb.append(rule.getBody().get(i)).append(" ");
		}
		return sb.toString();
	}

	@Override
	public List<Symbol> getSequence(int id) {
		Tuple<Rule, Integer> t = idToNameMap.get(id);
		return t.getFirst().getBody().subList(0, t.getSecond());
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
		
		return intermediateNodeIds.get(Tuple.of(rule, index));
	}

	@Override
	public int getSlotId(Rule rule) {
		return -1;
	}
	
	@Override
	public String toString() {
		return intermediateNodeIds.toString();
	}

}
