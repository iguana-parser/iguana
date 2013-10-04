package org.jgll.grammar.symbol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.EpsilonGrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.KeywordGrammarSlot;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.grammar.slot.TerminalGrammarSlot;
import org.jgll.util.hashing.HashFunctionBuilder;
import org.jgll.util.hashing.hashfunction.MurmurHash3;

public class Alternate implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private final List<BodyGrammarSlot> symbols;
	
	private final BodyGrammarSlot firstSlot;
	
	private BodyGrammarSlot condition;
	
	public Alternate(BodyGrammarSlot firstSlot) {
		
		if(firstSlot == null) {
			throw new IllegalArgumentException("firstSlot cannot be null.");
		}
		
		this.firstSlot = firstSlot;

		symbols = new ArrayList<>();
		
		BodyGrammarSlot current = firstSlot;
		
		if(firstSlot instanceof LastGrammarSlot) {
			symbols.add(firstSlot);
		}
		
		while(!(current instanceof LastGrammarSlot)) {
			symbols.add(current);
			current = current.next();
		}
	}
	
	public void setCondition(BodyGrammarSlot condition) {
		this.condition = condition;
	}
	
	public BodyGrammarSlot getCondition() {
		return condition;
	}
	
	public Symbol getSymbolAt(int index) {
		return symbols.get(index).getSymbol();
	}
	
	public BodyGrammarSlot getFirstSlot() {
		return firstSlot;
	}
	
	/**
	 * @return true if the alternate is of the form A ::= epsilon
	 */
	public boolean isEmpty() {
		return firstSlot instanceof EpsilonGrammarSlot;
	}
	
	public boolean isNullable() {
		if (isEmpty()) return true;
		
		BodyGrammarSlot slot = firstSlot;
		while(!(slot instanceof LastGrammarSlot)) {
			
			if(slot instanceof TerminalGrammarSlot || slot instanceof KeywordGrammarSlot)
				return false;
			
			if(slot instanceof NonterminalGrammarSlot && !slot.isNullable())
				return false;
			
			slot = slot.next();
		}
		
		return true;
	}
	
	public BodyGrammarSlot getSlotAt(int index) {
		return symbols.get(index);
	}
	
	/**
	 * ::= alpha . x
	 * 
	 * @return
	 */
	public BodyGrammarSlot getLastSlot() {
		return symbols.get(symbols.size() - 1);
	}
	
	public int size() {
		return symbols.size();
	}
	
	public HeadGrammarSlot getNonterminalAt(int index) {
		BodyGrammarSlot bodyGrammarSlot = symbols.get(index);
		
		if(!(bodyGrammarSlot instanceof NonterminalGrammarSlot)) {
			throw new RuntimeException("The symbol at " + index + " should be a nonterminal.");
		}
		
		return ((NonterminalGrammarSlot)bodyGrammarSlot).getNonterminal();
	}
	
	public void setNonterminalAt(int index, HeadGrammarSlot head) {
		BodyGrammarSlot bodyGrammarSlot = symbols.get(index);
		
		if(!(bodyGrammarSlot instanceof NonterminalGrammarSlot)) {
			throw new RuntimeException("The symbol at " + index + " should be a nonterminal.");
		}
		
		((NonterminalGrammarSlot)bodyGrammarSlot).setNonterminal(head);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(BodyGrammarSlot s : symbols) {
			sb.append(s.getSymbol()).append(" ");
		}
		return sb.toString();
	}
	
	public boolean isBinary(HeadGrammarSlot head) {
		if(! (symbols.get(0) instanceof NonterminalGrammarSlot && symbols.get(symbols.size() - 1) instanceof NonterminalGrammarSlot)) {
			return false;
		}
		
		NonterminalGrammarSlot firstNonterminal = (NonterminalGrammarSlot) symbols.get(0);
		NonterminalGrammarSlot lastNonterminal = (NonterminalGrammarSlot) symbols.get(symbols.size() - 1);
		
		return head.getNonterminal().equals(firstNonterminal.getNonterminal().getNonterminal()) &&
			   head.getNonterminal().equals(lastNonterminal.getNonterminal().getNonterminal());
	}
	
	/**
	 * 
	 * Returns true if the alternate is of the form op E.
	 * In other words, head = symbols[symobls.size - 1]
	 * 
	 */
	public boolean isUnaryPrefix(HeadGrammarSlot head) {
		
		if(isBinary(head)) {
			return false;
		}
		
		int index = symbols.size() - 1;
		if(! (symbols.get(index) instanceof NonterminalGrammarSlot)) {
			return false;
		}
		
		NonterminalGrammarSlot firstNonterminal = (NonterminalGrammarSlot) symbols.get(index);
		
		return head.getNonterminal().equals(firstNonterminal.getNonterminal().getNonterminal());
	}
	
	public boolean isUnaryPostfix(HeadGrammarSlot head) {
		if(isBinary(head)) {
			return false;
		}
		
		int index = 0;
		if(! (symbols.get(index) instanceof NonterminalGrammarSlot)) {
			return false;
		}
		
		NonterminalGrammarSlot lastNonterminal = (NonterminalGrammarSlot) symbols.get(index);
		
		return head.getNonterminal().equals(lastNonterminal.getNonterminal().getNonterminal());
	}
	
	public boolean match(List<Symbol> list) {
		
		if(list.size() != symbols.size()) {
			return false;
		}
		
		for(int i = 0; i < symbols.size(); i++) {
			if(!symbols.get(i).getSymbol().equals(list.get(i))) {
				return false;
			}
		}
		return true;
	}
	
	public boolean match(Set<List<Symbol>> set) {
		for(List<Symbol> list : set) {
			if(match(list)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		HashFunctionBuilder hashBuilder = new HashFunctionBuilder(new MurmurHash3());
		
		BodyGrammarSlot current = firstSlot;

		while(current != null) {
			
			if(current instanceof NonterminalGrammarSlot) {
				hashBuilder.addInt(((NonterminalGrammarSlot) current).getNonterminal().getNonterminal().hashCode());				
			} 
			else if(current instanceof TerminalGrammarSlot) {
				hashBuilder.addInt(((TerminalGrammarSlot) current).getTerminal().hashCode());
			} 
			else if(current instanceof KeywordGrammarSlot) {
				hashBuilder.addInt(((KeywordGrammarSlot) current).getKeyword().hashCode());
			} 
			else {
				// Last grammar slot
				hashBuilder.addInt(0);
			}
			
			current = current.next();
		}
		
		return hashBuilder.hash();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		
		if(!(obj instanceof Alternate)) {
			return false;
		}
		
		Alternate other = (Alternate) obj;
		
		if(this.size() != other.size()) {
			return false;
		}
		
		BodyGrammarSlot thisSlot = firstSlot;
		BodyGrammarSlot otherSlot = other.firstSlot;
		
		while(!(thisSlot instanceof LastGrammarSlot)) {
			if(!thisSlot.isNameEqual(otherSlot)) {
				return false;
			}
			
			thisSlot = thisSlot.next();
			otherSlot = otherSlot.next();
		}
		
		return true;
	}
	
	public Iterable<BodyGrammarSlot> getSymbols() {
		
		return new Iterable<BodyGrammarSlot>() {
			
			@Override
			public Iterator<BodyGrammarSlot> iterator() {
				
				return new Iterator<BodyGrammarSlot>() {
					
					private BodyGrammarSlot current = firstSlot;

					@Override
					public boolean hasNext() {
						return current != null;
					}

					@Override
					public BodyGrammarSlot next() {
						BodyGrammarSlot s = current;
						current = current.next();
						return s;
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}
		};
	}
	
}
