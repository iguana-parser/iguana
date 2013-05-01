package org.jgll.grammar;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgll.util.Input;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class Grammar implements Serializable {
	
	private static final Logger log = LoggerFactory.getLogger(Grammar.class);
	
	private static final long serialVersionUID = 1L;
	
	private List<HeadGrammarSlot> nonterminals;
	
	private List<BodyGrammarSlot> slots;
	
	/**
	 * A map from nonterminal names to their corresponding head slots.
	 * This map is used to locate head grammar slots by name for parsing
	 * from any arbitrary nonterminal.
	 */
	private Map<String, HeadGrammarSlot> nameToNonterminals;
	
	private Map<String, BodyGrammarSlot> nameToSlots;
	
	private String name;

	private int longestTerminalChain;
	
	
	public Grammar(GrammarBuilder builder) {
		this.name = builder.name;
		this.nonterminals = builder.nonterminals;
		this.slots = builder.slots;
		this.nameToNonterminals = new HashMap<>();
		this.nameToSlots = new HashMap<>();
		
		for(HeadGrammarSlot nontermianl : nonterminals) {
			nameToNonterminals.put(nontermianl.toString(), nontermianl);
		}
		
		for(BodyGrammarSlot slot : slots) {
			nameToSlots.put(slot.toString(), slot);
		}
		
		this.longestTerminalChain = builder.longestTerminalChain;
	}
	
	
	public void code(Writer writer, String packageName) throws IOException {
	
		String header = Input.read(this.getClass().getResourceAsStream("ParserTemplate"));
		header = header.replace("${className}", name)
					   .replace("${packageName}", packageName);
		writer.append(header);
		
		// case L0:
		writer.append("case " + L0.getInstance().getId() + ":\n");
		L0.getInstance().codeParser(writer);
		
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
			nonterminal.codeParser(writer);
		}
		
		writer.append("}");
	}
	
	private static String grammarSlotToString(Rule rule, int index) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(rule.getHead().getName()).append(" ::= ");
		
		for(int i = 0; i < rule.size(); i++) {
			if(i == index) {
				sb.append(". ");
			}
			sb.append(rule.getSymbolAt(i)).append(" ");
		}
		
		sb.delete(sb.length() - 1, sb.length());
		
		if(index == rule.size()) {
			sb.append(" .");
		}
		
		return sb.toString();
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
		return nameToNonterminals.get(name);
	}
	
	public BodyGrammarSlot getGrammarSlotByName(String name) {
		return nameToSlots.get(name);
	}
	
	public int getLongestTerminalChain() {
		return longestTerminalChain;
	}
	
	int filteredNonterminals = 1;
	
	Map<Filter, HeadGrammarSlot> filterNonterminalMap = new HashMap<>();
	
	List<HeadGrammarSlot> newNonterminals = new ArrayList<>();
	
	public void filter(Set<Filter> filters) {
		
		for(HeadGrammarSlot head : nonterminals) {
			for(Alternate alternate : head.getAlternates()) {
				for(Filter filter : filters) {
					filter(alternate, filter);
				}
			}
		}
		
		for(HeadGrammarSlot head : newNonterminals) {
			for(Alternate alternate : head.getAlternates()) {
				for(Filter filter : filters) {
					filter(alternate, filter);
				}
			}			
		}
		
		nonterminals.addAll(newNonterminals);
	}
	
	private void filter(Alternate alternate, Filter filter) {
		if(alternate.match(filter)) {
			
			log.trace("Filter {} matches the alternate {}", filter, alternate);
			
			HeadGrammarSlot newNonterminal = filterNonterminalMap.get(filter);
			
			if(newNonterminal == null) {
				HeadGrammarSlot headToBeFiltered = alternate.getNonterminalAt(filter.getPosition());
				newNonterminal = copy(headToBeFiltered);
				newNonterminal.getNonterminal().setIndex(filteredNonterminals++);
				newNonterminals.add(newNonterminal);
				newNonterminal.removeAlternate(filter.getFilteredRules());
				alternate.setNonterminalAt(filter.getPosition(), newNonterminal);
				filterNonterminalMap.put(filter, newNonterminal);
			} else {
				alternate.setNonterminalAt(filter.getPosition(), newNonterminal);
			}
		}
	}
	
	private HeadGrammarSlot copy(HeadGrammarSlot head) {
		
		HeadGrammarSlot copyHead = new HeadGrammarSlot(nonterminals.size() + filterNonterminalMap.size(), new Nonterminal(head.getNonterminal().getName()));
		
		for(Alternate alternate : head.getAlternates()) {
			copyHead.addAlternate(copy(alternate, copyHead));
		}
		
		return copyHead;
	}
	
	private Alternate copy(Alternate alternate, HeadGrammarSlot head) {
		BodyGrammarSlot copyFirstSlot = copy(alternate.getFirstSlot(), null, head);
		
		BodyGrammarSlot current = alternate.getFirstSlot().next;
		BodyGrammarSlot copy = copyFirstSlot;
		
		while(current != null) {
			copy = copy(current, copy, head);
			current = current.next;
		}
		 
		return new Alternate(copyFirstSlot);
	}
	
	private BodyGrammarSlot copy(BodyGrammarSlot slot, BodyGrammarSlot previous, HeadGrammarSlot head) {

		BodyGrammarSlot copy;
		
		if(slot.isLastSlot()) {
			copy = new LastGrammarSlot(slots.size(), slot.label, slot.position, previous, head, ((LastGrammarSlot) slot).getObject());
		} 
		else if(slot.isNonterminalSlot()) {
			NonterminalGrammarSlot ntSlot = (NonterminalGrammarSlot) slot;
			copy = new NonterminalGrammarSlot(slots.size(), slot.label, slot.position, previous, ntSlot.getNonterminal(), head);
		} 
		else {
			copy = new TerminalGrammarSlot(slots.size(), slot.label, slot.position, previous, ((TerminalGrammarSlot) slot).getTerminal(), head);
		}

		slots.add(copy);
		return copy;
	}
	
	private Iterable<BodyGrammarSlot> getLastSlots(HeadGrammarSlot head) {

		List<BodyGrammarSlot> slots = new ArrayList<>();
		
		for(Alternate alternate : head.getAlternates()) {
			BodyGrammarSlot current = alternate.getFirstSlot();
			while(!current.next.isLastSlot()) {
				current = current.next;
			}
			slots.add(current);
		}
		return slots;
	}
	
	@Override
	public String toString() {
		
		Set<Nonterminal> visitedHeads = new HashSet<>();
		
		Deque<HeadGrammarSlot> todoQueue = new ArrayDeque<>();
		
		StringBuilder sb = new StringBuilder();
		
		for(HeadGrammarSlot head : nonterminals) {
			todoQueue.add(head);
			visitedHeads.add(head.getNonterminal());
		}
		
		while(!todoQueue.isEmpty()) {
			HeadGrammarSlot head = todoQueue.poll();
			
			for(Alternate alternate : head.getAlternates()) {
				BodyGrammarSlot slot = alternate.getFirstSlot();
				sb.append(head + " ::= ");
				BodyGrammarSlot currentSlot = slot;
				do {
					if(currentSlot.isNonterminalSlot()) {
						HeadGrammarSlot nonterminal = ((NonterminalGrammarSlot) currentSlot).getNonterminal();
						if(!visitedHeads.contains(nonterminal.getNonterminal())) {
							todoQueue.add(nonterminal);
							visitedHeads.add(nonterminal.getNonterminal());
						}
					}
					sb.append(" ").append(currentSlot.getSymbolName());
					if(currentSlot.isLastSlot()) {
						sb.append("\n");
					}
				} 
				while((currentSlot = currentSlot.next) != null);
			}

		}
				
		return sb.toString();
	}
		
}
