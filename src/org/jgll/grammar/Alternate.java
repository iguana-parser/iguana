package org.jgll.grammar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class Alternate {
	
	private final List<BodyGrammarSlot> symbols;
	
	private final BodyGrammarSlot firstSlot;

	public Alternate(BodyGrammarSlot firstSlot) {
		
		if(firstSlot == null) {
			throw new IllegalArgumentException("firstSlot cannot be null.");
		}
		
		this.firstSlot = firstSlot;
		
		symbols = new ArrayList<>();
		
		BodyGrammarSlot current = firstSlot;
		while(!current.isLastSlot()) {
			symbols.add(current);
			current = current.next();
		}
		
	}
	
	public BodyGrammarSlot getFirstSlot() {
		return firstSlot;
	}
	
	
	public boolean match(Filter filter) {

		Iterator<BodyGrammarSlot> it1 = symbols.iterator();
		Iterator<Symbol> it2 = filter.getRule().getBody().iterator();
		
		while(it1.hasNext() && it2.hasNext()) {			
			BodyGrammarSlot next1 = it1.next();
			Symbol next2 = it2.next();
			
			if(next1.isTerminalSlot() && next2.isTerminal() ||
			   next1.isNonterminalSlot() && next2.isNonterminal()) {
				return false;
			}
			
			if(! next1.getSymbolName().equals(next2.getName())) {
				return false;
			}
			
			if(next1.isNonterminalSlot() && next2.isNonterminal()) {
				NonterminalGrammarSlot ntSlot = (NonterminalGrammarSlot) next1;
				if(ntSlot.getNonterminal().contains(filter.getFilteredRules())) {
					ntSlot.getNonterminal().removeAlternate(filter.getFilteredRules());
				}
			}
		}
		
		return true;
	}
	
}
