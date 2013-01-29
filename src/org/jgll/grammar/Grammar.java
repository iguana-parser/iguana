package org.jgll.grammar;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public final class Grammar implements Serializable {
	
	private final List<Nonterminal> nonterminals;
	
	private final List<GrammarSlot> slots;
	
	private final Nonterminal startSymbol;
	
	private final String name;
	
	public Grammar(String name, List<Nonterminal> nonterminals, List<GrammarSlot> slots, Nonterminal startSymbol) {
		this.name = name;
		this.nonterminals = Collections.unmodifiableList(nonterminals);
		this.slots = Collections.unmodifiableList(slots);
		this.startSymbol = startSymbol;
	}
	
	public String getName() {
		return name;
	}
	
	public Nonterminal getNonterminal(int id) {
		return nonterminals.get(id);
	}
		
	public GrammarSlot getGrammarSlot(int id) {
		return slots.get(id - nonterminals.size());
	}
	
	public Nonterminal getStartSymbol() {
		return startSymbol;
	}
	
	public List<Nonterminal> getNonterminals() {
		return nonterminals;
	}
	
	public List<GrammarSlot> getGrammarSlots() {
		return slots;
	}

}
