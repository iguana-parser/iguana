package org.jgll.grammar;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgll.util.InputUtil;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class Grammar implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final List<Nonterminal> nonterminals;
	
	private final List<BodyGrammarSlot> slots;
	
	private final Map<String, Nonterminal> startSymbols;
	
	private final String name;
	
	private int longestTerminalChain;

	private final Set<Nonterminal> startSymbolsSet;
	
	public Grammar(String name, List<Nonterminal> nonterminals, List<BodyGrammarSlot> slots, Nonterminal startSymbol) {
		this(name, nonterminals, slots, createHashSet(startSymbol));
	}
	
	private static Set<Nonterminal> createHashSet(Nonterminal nt) {
		Set<Nonterminal> set = new HashSet<>();
		set.add(nt);
		return set;
	}
	
	public Grammar(String name, List<Nonterminal> nonterminals, List<BodyGrammarSlot> slots, Set<Nonterminal> startSymbols) {
		this.name = name;
		startSymbolsSet = startSymbols;
		this.nonterminals = Collections.unmodifiableList(nonterminals);
		this.slots = Collections.unmodifiableList(slots);
		this.startSymbols = new HashMap<>();
		for(Nonterminal startSymbol : startSymbols) {
			this.startSymbols.put(startSymbol.getName(), startSymbol);
		}
	}
	
	public void code(Writer writer, String packageName) throws IOException {
	
		String header = InputUtil.read(this.getClass().getResourceAsStream("ParserTemplate"));
		header = header.replace("${className}", name)
					   .replace("${packageName}", packageName);
		writer.append(header);
		
		// case L0:
		writer.append("case " + L0.getInstance().getId() + ":\n");
		L0.getInstance().code(writer);
		
		for(Nonterminal nonterminal : nonterminals) {
			writer.append("// " + nonterminal + "\n");
			writer.append("case " + nonterminal.getId() + ":\n");
			writer.append("parse_" + nonterminal.getId() + "();\n");
			writer.append("break;\n");
		}
				
		for(BodyGrammarSlot slot : slots) {
			if(!(slot.previous instanceof TerminalGrammarSlot)) {
				writer.append("// " + slot + "\n");
				writer.append("case " + slot.getId() + ":\n");
				writer.append("parse_" + slot.getId() + "();\n");
				writer.append("break;\n");
			}
		}
		
		writer.append("} } }\n");
		
		for(Nonterminal nonterminal : nonterminals) {
			nonterminal.code(writer);
		}
		
		writer.append("}");
	}
	
	public String getName() {
		return name;
	}
	
	public Nonterminal getNonterminal(int id) {
		return nonterminals.get(id);
	}
		
	public BodyGrammarSlot getGrammarSlot(int id) {
		return slots.get(id - nonterminals.size());
	}
		
	public List<Nonterminal> getNonterminals() {
		return nonterminals;
	}
	
	public List<BodyGrammarSlot> getGrammarSlots() {
		return slots;
	}
	
	public Nonterminal getNonterminalByName(String name) {
		return startSymbols.get(name);
	}
	
	public int getLongestTerminalChain() {
		return longestTerminalChain;
	}
	
	public void setLongestTerminalChain(int longestTerminalChain) {
		this.longestTerminalChain = longestTerminalChain;
	}
	
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		for(Nonterminal nonterminal : nonterminals) {
			for(BodyGrammarSlot slot : nonterminal.getAlternates()) {
				sb.append(nonterminal.getName() + " -> ");
				BodyGrammarSlot next = slot;
				do {
					sb.append(" " + next.getName());
					if(next instanceof LastGrammarSlot) {
						sb.append("\n");
					}
				} 
				while((next = next.next) != null);
			}
			
		}
		
		return sb.toString();
	}
	
	public Set<Nonterminal> getStartSymbols() {
		return startSymbolsSet;
	}
	
}
