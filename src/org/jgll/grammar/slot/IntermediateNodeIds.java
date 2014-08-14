package org.jgll.grammar.slot;

import java.util.List;

import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.util.Tuple;


public interface IntermediateNodeIds {
	
	public String getSlotName(int id);
	
	public List<Symbol> getPrefix(int id);
	
	public Tuple<Rule, Integer> getSlot(int id);
	
	public int getSlotId(Rule rule, int index);
	
	public int getSlotId(String s);
	
}
