package org.jgll.grammar;

import java.util.ArrayList;
import java.util.List;

class Alternate {
	
	private final HeadGrammarSlot head;
	
	private final List<BodyGrammarSlot> symbols;
	
	private final BodyGrammarSlot firstSlot;
	
	private final int index;

	public Alternate(HeadGrammarSlot head, BodyGrammarSlot firstSlot, int index) {
		
		if(head == null) {
			throw new IllegalArgumentException("head cannot be null.");
		}
		
		if(firstSlot == null) {
			throw new IllegalArgumentException("firstSlot cannot be null.");
		}
		
		this.head = head;
		this.firstSlot = firstSlot;
		this.index = index;
		
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
	
	public boolean match(Filter filter) {
		
		if(index == filter.getAlternateIndex()) {
			BodyGrammarSlot bodyGrammarSlot = symbols.get(filter.getPosition());
			
			assert bodyGrammarSlot instanceof NonterminalGrammarSlot;
			
			NonterminalGrammarSlot ntSlot = (NonterminalGrammarSlot) bodyGrammarSlot;
			
			if(! ntSlot.getNonterminal().contains(filter.getFilteredRules())) {
				return false;
			}
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(head).append(" ::= ");
		for(BodyGrammarSlot s : symbols) {
			sb.append(s.getSymbolName()).append(" ");
		}
		return sb.toString();
	}
	
}
