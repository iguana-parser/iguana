package org.jgll.grammar;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.TerminalGrammarSlot;
import org.jgll.grammar.symbol.Epsilon;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.regex.RegularExpression;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class GrammarSlotRegistry {

	private AtomicInteger nextId = new AtomicInteger(1);
	
	private BiMap<GrammarSlot, Integer> ids = HashBiMap.create();
	
	private Map<Nonterminal, HeadGrammarSlot> nonterminals;
	private Map<RegularExpression, TerminalGrammarSlot> terminals;
	private Map<String, BodyGrammarSlot> slots;
	
	private Map<String, RegularExpression> regularExpressions = new HashMap<>();
	
	public static GrammarSlotRegistry from(Collection<HeadGrammarSlot> heads, 
							   Collection<TerminalGrammarSlot> terminals, 
							   Collection<BodyGrammarSlot> slots) {

		return new GrammarSlotRegistry(
			 heads.stream().collect(Collectors.toMap(x -> x.getNonterminal(), x -> x)),
		     terminals.stream().collect(Collectors.toMap(x -> x.getRegularExpression(), x -> x)),
		     slots.stream().collect(Collectors.toMap(x -> x.toString(), x -> x)));
	}
	
	public GrammarSlotRegistry(Map<Nonterminal, HeadGrammarSlot> heads, 
			 				   Map<RegularExpression, TerminalGrammarSlot> terminals, 
			 				   Map<String, BodyGrammarSlot> slots) {

		if (heads == null) throw new IllegalArgumentException();
		if (terminals == null) throw new IllegalArgumentException();
		if (slots == null) throw new IllegalArgumentException();
		
		this.nonterminals = new HashMap<>(heads);
		this.slots = new HashMap<>(slots);
		this.terminals = new HashMap<>(terminals);
		
		ids.put(Epsilon.TOKEN_ID, 0);
		
		heads.values().stream().forEach(h -> ids.put(h, nextId.incrementAndGet()));
		terminals.values().stream().forEach(t -> ids.put(t, nextId.incrementAndGet()));
		slots.values().stream().forEach(s -> ids.put(s, nextId.incrementAndGet()));
		
		terminals.keySet().stream().forEach(t -> regularExpressions.put(t.toString(), t));
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
	
	public GrammarSlot get(int id) {
		return ids.inverse().get(id);
	}

}
