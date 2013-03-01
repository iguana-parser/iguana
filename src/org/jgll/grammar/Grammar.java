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
	
	private final List<HeadGrammarSlot> nonterminals;
	
	private final List<BodyGrammarSlot> slots;
	
	private final Map<String, HeadGrammarSlot> startSymbols;
	
	private final String name;
	
	private int longestTerminalChain;

	private final Set<HeadGrammarSlot> startSymbolsSet;
	
	public Grammar(String name, List<HeadGrammarSlot> nonterminals, List<BodyGrammarSlot> slots, HeadGrammarSlot startSymbol) {
		this(name, nonterminals, slots, createHashSet(startSymbol));
	}
	
	private static Set<HeadGrammarSlot> createHashSet(HeadGrammarSlot nt) {
		Set<HeadGrammarSlot> set = new HashSet<>();
		set.add(nt);
		return set;
	}
	
	public Grammar(String name, List<HeadGrammarSlot> nonterminals, List<BodyGrammarSlot> slots, Set<HeadGrammarSlot> startSymbols) {
		this.name = name;
		startSymbolsSet = startSymbols;
		this.nonterminals = Collections.unmodifiableList(nonterminals);
		this.slots = Collections.unmodifiableList(slots);
		this.startSymbols = new HashMap<>();
		for(HeadGrammarSlot startSymbol : startSymbols) {
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
		
		for(HeadGrammarSlot nonterminal : nonterminals) {
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
		
		for(HeadGrammarSlot nonterminal : nonterminals) {
			nonterminal.code(writer);
		}
		
		writer.append("}");
	}
	
	public String getName() {
		return name;
	}
	
	public HeadGrammarSlot getNonterminal(int id) {
		return nonterminals.get(id);
	}
		
	public BodyGrammarSlot getGrammarSlot(int id) {
		return slots.get(id - nonterminals.size());
	}
		
	public List<HeadGrammarSlot> getNonterminals() {
		return nonterminals;
	}
	
	public List<BodyGrammarSlot> getGrammarSlots() {
		return slots;
	}
	
	public HeadGrammarSlot getNonterminalByName(String name) {
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
		
		for(HeadGrammarSlot nonterminal : nonterminals) {
			for(BodyGrammarSlot slot : nonterminal.copyAlternates()) {
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
	
	public Set<HeadGrammarSlot> getStartSymbols() {
		return startSymbolsSet;
	}
	
}
