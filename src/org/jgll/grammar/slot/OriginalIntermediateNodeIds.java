package org.jgll.grammar.slot;

import java.util.List;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.precedence.OperatorPrecedence;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.util.Tuple;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class OriginalIntermediateNodeIds implements IntermediateNodeIds {
	
	private BiMap<Tuple<Rule, Integer>, Integer> intermediateNodeIds;
	
	private BiMap<String, Integer> names;
	
	private Grammar grammar;
	
	public OriginalIntermediateNodeIds(Grammar grammar) {
		this.grammar = grammar;
		this.intermediateNodeIds = HashBiMap.create();
		this.names = HashBiMap.create();
		calculateIds();
	}

	private void calculateIds() {
		int intermediateId = 0;
		
		for (Rule rule : grammar.getRules()) {
			if (rule.getBody() != null) {
				Rule plainRule = OperatorPrecedence.plain(rule);
				for (int i = 2; i < rule.getBody().size(); i++) {
					Tuple<Rule, Integer> t = Tuple.of(plainRule, i);
					intermediateNodeIds.put(t, intermediateId);
					names.put(getName(t), intermediateId);
					intermediateId++;
				}
			}
		}
	}

	@Override	
	public String getSlotName(int id) {
		return names.inverse().get(id);
	}
	
	private String getName(Tuple<Rule, Integer> t) {
		Rule rule = t.getFirst(); 
		int index = t.getSecond();
		StringBuilder sb = new StringBuilder();
		sb.append(rule.getHead()).append(" ::= ");
		for (int i = 0; i < rule.getBody().size(); i++) {
			if (i == index) sb.append(". ");
			sb.append(rule.getBody().get(i)).append(" ");
		}
		sb.delete(sb.length() - 1, sb.length());
		return sb.toString();
	}

	@Override
	public List<Symbol> getPrefix(int id) {
		Tuple<Rule, Integer> t = intermediateNodeIds.inverse().get(id);
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
		
		return intermediateNodeIds.get(Tuple.of(OperatorPrecedence.plain(rule), index));
	}

	@Override
	public String toString() {
		return intermediateNodeIds.toString();
	}

	@Override
	public Tuple<Rule, Integer> getSlot(int id) {
		return intermediateNodeIds.inverse().get(id);
	}
	
	@Override
	public int getSlotId(String s) {
		return names.get(s);
	}
}
