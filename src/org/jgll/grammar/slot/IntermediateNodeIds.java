package org.jgll.grammar.slot;

import java.util.List;

import org.jgll.grammar.symbol.Symbol;

public interface IntermediateNodeIds {
	
	public void calculateIds();
	
	public int getSlotId(List<Symbol> alt, int index);
	
	public String getSlotName(int id);
	
}
