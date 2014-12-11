package org.jgll.grammar;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.TerminalGrammarSlot;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.regex.RegularExpression;

/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class GrammarSlotRegistry {

	private AtomicInteger nextId = new AtomicInteger();
	
	private Map<GrammarSlot, Integer> ids = new HashMap<>();
	
	private Map<Nonterminal, HeadGrammarSlot> nonterminals = new HashMap<>();
	private Map<RegularExpression, TerminalGrammarSlot> terminals = new HashMap<>();
	private Map<String, BodyGrammarSlot> slots = new HashMap<>();
	
	private Map<String, RegularExpression> regularExpressions = new HashMap<>();
	
	public GrammarSlotRegistry(Map<Nonterminal, HeadGrammarSlot> heads, 
			 				   Map<RegularExpression, TerminalGrammarSlot> terminals, 
			 				   Map<String, BodyGrammarSlot> slots) {
		
		if (heads == null) throw new IllegalArgumentException();
		if (terminals == null) throw new IllegalArgumentException();
		if (slots == null) throw new IllegalArgumentException();
		
		this.nonterminals = new HashMap<>(heads);
		this.slots = new HashMap<>(slots);
		this.terminals = new HashMap<>(terminals);
		
		for (Entry<Nonterminal, HeadGrammarSlot> e : heads.entrySet()) {
			ids.put(e.getValue(), nextId.incrementAndGet());
		}

		for (Entry<String, BodyGrammarSlot> e : slots.entrySet()) {
			ids.put(e.getValue(), nextId.incrementAndGet());
		}
		
		for (Entry<RegularExpression, TerminalGrammarSlot> e : terminals.entrySet()) {
			ids.put(e.getValue(), nextId.incrementAndGet());
		}
		
		for (RegularExpression regex : terminals.keySet()) {
			regularExpressions.put(regex.toString(), regex);
		}
	}

	public HeadGrammarSlot getHead(Nonterminal nt) {
		return nonterminals.get(nt);
	}
	
	public TerminalGrammarSlot getTerminal(RegularExpression regex) {
		return terminals.get(regex);
	}

	public BodyGrammarSlot getGrammarSlot(String s) {
		return slots.get(s);
	}
	
	public RegularExpression getRegularExpression(String s) {
		return regularExpressions.get(s);
	}
	
	public int getId(GrammarSlot slot) {
		return ids.get(slot);
	}

}
