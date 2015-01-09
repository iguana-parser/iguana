package org.jgll.grammar;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.grammar.slot.TerminalGrammarSlot;
import org.jgll.grammar.symbol.Epsilon;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.regex.RegularExpression;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class GrammarRegistry {

	private AtomicInteger nextId = new AtomicInteger(1);
	
	private BiMap<GrammarSlot, Integer> ids = HashBiMap.create();
	
	private Map<Nonterminal, NonterminalGrammarSlot> nonterminals;
	private Map<RegularExpression, TerminalGrammarSlot> terminals;
	private Map<String, GrammarSlot> slots;
	
	private Map<String, RegularExpression> regularExpressions = new HashMap<>();
	
	public static GrammarRegistry from(Collection<NonterminalGrammarSlot> heads, 
							   Collection<TerminalGrammarSlot> terminals, 
							   Collection<GrammarSlot> slots) {

		return new GrammarRegistry(
			 heads.stream().collect(Collectors.toMap(x -> x.getNonterminal(), x -> x)),
		     terminals.stream().collect(Collectors.toMap(x -> x.getRegularExpression(), x -> x)),
		     slots.stream().collect(Collectors.toMap(x -> x.toString(), x -> x)));
	}
	
	public GrammarRegistry(Map<Nonterminal, NonterminalGrammarSlot> heads, 
			 				   Map<RegularExpression, TerminalGrammarSlot> terminals, 
			 				   Map<String, GrammarSlot> slots) {

		if (heads == null) throw new IllegalArgumentException();
		if (terminals == null) throw new IllegalArgumentException();
		if (slots == null) throw new IllegalArgumentException();
		
		this.nonterminals = new HashMap<>(heads);
		this.slots = new HashMap<>(slots);
		this.terminals = new HashMap<>(terminals);
		
		heads.values().stream().forEach(h -> ids.put(h, nextId.incrementAndGet()));
		terminals.values().stream().forEach(t -> ids.put(t, nextId.incrementAndGet()));
		slots.values().stream().forEach(s -> ids.put(s, nextId.incrementAndGet()));
		
		terminals.keySet().stream().forEach(t -> regularExpressions.put(t.toString(), t));
	}

	public NonterminalGrammarSlot getHead(Nonterminal nt) {
		return nonterminals.get(nt);
	}
	
	public TerminalGrammarSlot getTerminal(RegularExpression regex) {
		return terminals.get(regex);
	}

	public GrammarSlot getGrammarSlot(String s) {
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
