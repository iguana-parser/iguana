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
	
	public HeadGrammarSlot getNonterminalAt(int index) {
		BodyGrammarSlot bodyGrammarSlot = symbols.get(index);
		
		if(!bodyGrammarSlot.isNonterminalSlot()) {
			throw new RuntimeException("The symbol at " + index + " should be a nonterminal.");
		}
		
		return ((NonterminalGrammarSlot)bodyGrammarSlot).getHead();
	}
	
	public void setNonterminalAt(int index, HeadGrammarSlot head) {
		BodyGrammarSlot bodyGrammarSlot = symbols.get(index);
		
		if(!bodyGrammarSlot.isNonterminalSlot()) {
			throw new RuntimeException("The symbol at " + index + " should be a nonterminal.");
		}
		
		((NonterminalGrammarSlot)bodyGrammarSlot).setNonterminal(head);
	}
	
	public Alternate copy() {
		
		BodyGrammarSlot copyFirstSlot = firstSlot.copy(null);
		
		BodyGrammarSlot current = firstSlot.next;
		BodyGrammarSlot copy = copyFirstSlot;
		
		while(current != null) {
			copy = current.copy(copy);
			current = current.next;
		}
		 
		return new Alternate(copyFirstSlot);
	}

	
	public boolean match(Filter filter) {

		Iterator<BodyGrammarSlot> it1 = symbols.iterator();
		Iterator<Symbol> it2 = filter.getRule().getBody().iterator();
		
		int i = 0;
		
		while(it1.hasNext() && it2.hasNext()) {			
			BodyGrammarSlot alternateSymbol = it1.next();
			Symbol filterSymbol = it2.next();
			
			if((alternateSymbol.isTerminalSlot() && filterSymbol.isNonterminal()) ||
			   (alternateSymbol.isNonterminalSlot() && filterSymbol.isTerminal())) {
				return false;
			}
			
			if(alternateSymbol.isTerminalSlot()) {
				continue;
			}

			NonterminalGrammarSlot ntSlot = (NonterminalGrammarSlot) alternateSymbol;
			
			if(! ntSlot.getNonterminal().getNonterminal().getName().equals(filterSymbol.getName())) {
				return false;
			}
			
			if(filter.getPosition() == i) {
				if(! ntSlot.getNonterminal().contains(filter.getFilteredRules())) {
					return false;
				}
			}
			
			i++; 
		}
		
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(BodyGrammarSlot s : symbols) {
			sb.append(s.getSymbolName()).append(" ");
		}
		return sb.toString();
	}
	
}
