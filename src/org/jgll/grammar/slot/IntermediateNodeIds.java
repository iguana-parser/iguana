package org.jgll.grammar.slot;

import java.util.List;

import org.jgll.grammar.symbol.Symbol;


public interface IntermediateNodeIds {
	
	public void calculateIds();
	
	public String getSlotName(int id);
	
	public List<Symbol> getSequence(int id);
	
	public int getSlotId(List<Symbol> alt, int index);
	
	public int getSlotId(List<Symbol> alt);
	
}
