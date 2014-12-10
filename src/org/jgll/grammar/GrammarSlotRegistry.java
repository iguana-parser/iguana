package org.jgll.grammar;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jgll.grammar.slot.BodyGrammarSlot;
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

	private Map<HeadGrammarSlot, Integer> nonterminalIndexes = new HashMap<>();
	private Map<TerminalGrammarSlot, Integer> terminalsIndexes = new HashMap<>();
	
	private Map<Nonterminal, HeadGrammarSlot> nonterminals = new HashMap<>();
	private Map<RegularExpression, TerminalGrammarSlot> terminals = new HashMap<>();
	
	public GrammarSlotRegistry(Set<HeadGrammarSlot> heads, Set<TerminalGrammarSlot> terminalSlots) {
		
		for (HeadGrammarSlot head : heads) {
			nonterminals.put(head.getNonterminal(), head);
			nonterminalIndexes.put(head, nonterminalIndexes.size());
		}
		
		for (TerminalGrammarSlot terminal : terminalSlots) {
			terminals.put(terminal.getRegularExpression(), terminal);
			terminalsIndexes.put(terminal, terminals.size());
		}
	}

	public HeadGrammarSlot getHead(Nonterminal nt) {
		return nonterminals.get(nt);
	}
	
	public TerminalGrammarSlot getTerminal(RegularExpression regex) {
		return terminals.get(regex);
	}

	public BodyGrammarSlot getGrammarSlot(String s) {
		return null;
	}
	
	public RegularExpression getRegularExpression(String s) {
		return null;
	}
	
	public int getId(HeadGrammarSlot nt) {
		return nonterminalIndexes.get(nt);
	}
	
	public int getId(TerminalGrammarSlot t) {
		return terminalsIndexes.get(t);
	}
	
	public int getId(BodyGrammarSlot s) {
		return -1;
	}
	
}
