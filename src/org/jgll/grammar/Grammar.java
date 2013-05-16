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
import java.util.Map.Entry;
import java.util.Set;

import org.jgll.util.Input;
import org.jgll.util.Tuple;
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
	
	private Map<String, Set<Filter>> filters;
	
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
		
		this.filters = new HashMap<>();
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
	
	Map<Tuple<String, Set<Integer>>, HeadGrammarSlot> filterNonterminalMap2 = new HashMap<>();
	
	List<HeadGrammarSlot> newNonterminals = new ArrayList<>();
	
	Map<NonterminalGrammarSlot, Integer> secondLevel = new HashMap<>();
	
	Map<Set<Alternate>, HeadGrammarSlot> map = new HashMap<>();
	
	public void filter() {
		for(Entry<String, Set<Filter>> entry : filters.entrySet()) {
			filter(nameToNonterminals.get(entry.getKey()), entry.getValue());
		}
	}
	
	private void filter(HeadGrammarSlot head, Iterable<Filter> filters) {
		for(Filter filter : filters) {
			for(Alternate alt : head.getAlternates()) {
				if(match(filter, alt)) {
					log.debug("{} matched {}", filter, alt);
										
					HeadGrammarSlot filteredNonterminal = alt.getNonterminalAt(filter.getPosition());
					
					HeadGrammarSlot newNonterminal = map.get(filteredNonterminal.without(filter.getChild()));
					if(newNonterminal == null) {
						newNonterminal = rewrite(alt, filter.getPosition(), filter.getChild());						
						filter(newNonterminal, filters);
						if(filter.isLeftMost()) {
							rewriteRightEnds(newNonterminal, filter.getChild());
						}
					} else {
						alt.setNonterminalAt(filter.getPosition(), newNonterminal);
					}
				}
			}
		}
	}
	
	private HeadGrammarSlot rewrite(Alternate alt, int position, List<Symbol> filteredAlternate) {
		HeadGrammarSlot filteredNonterminal = alt.getNonterminalAt(position);
		HeadGrammarSlot newNonterminal = new HeadGrammarSlot(newNonterminals.size(), filteredNonterminal.getNonterminal());
		alt.setNonterminalAt(position, newNonterminal);
		newNonterminals.add(newNonterminal);
		
		List<Alternate> copy = copyAlternates(newNonterminal, filteredNonterminal.getAlternates());
		newNonterminal.setAlternates(copy);
		newNonterminal.remove(filteredAlternate);
		map.put(new HashSet<>(copyAlternates(newNonterminal, copy)), newNonterminal);
		return newNonterminal;
	}
	
	private boolean match(Filter f, Alternate alt) {
		if(alt.match(f.getParent())) {
			if(alt.getNonterminalAt(f.getPosition()).contains(f.getChild())) {
				return true;
			}
		}
		return false;
	}
	
	private void rewriteRightEnds(HeadGrammarSlot head, List<Symbol> filteredAlternate) {
		for(Alternate alternate : head.getAlternates()) {
			if(! (alternate.isBinary(head) || alternate.isUnaryPrefix(head))) {
				continue;
			}
			HeadGrammarSlot nonterminal = ((NonterminalGrammarSlot) alternate.getLastSlot()).getNonterminal();
			
			if(nonterminal.contains(filteredAlternate)) {
				HeadGrammarSlot filteredNonterminal = alternate.getNonterminalAt(alternate.size() - 1);
				
				HeadGrammarSlot newNonterminal = map.get(filteredNonterminal.without(filteredAlternate));
				if(newNonterminal == null) {
					newNonterminal = rewrite(alternate, alternate.size() - 1, filteredAlternate);
					rewriteRightEnds(newNonterminal, filteredAlternate);
				} else {
					alternate.setNonterminalAt(alternate.size() - 1, newNonterminal);
				}
			}
		}
	}

	
	private List<Alternate> copyAlternates(HeadGrammarSlot head, List<Alternate> list) {
		List<Alternate> copyList = new ArrayList<>();
		for(Alternate alt : list) {
			copyList.add(copyAlternate(alt, head));
		}
		return copyList;
	}
	
	private Alternate copyAlternate(Alternate alternate, HeadGrammarSlot head) {
		BodyGrammarSlot copyFirstSlot = copySlot(alternate.getFirstSlot(), null, head);
		
		BodyGrammarSlot current = alternate.getFirstSlot().next;
		BodyGrammarSlot copy = copyFirstSlot;
		
		while(current != null) {
			copy = copySlot(current, copy, head);
			current = current.next;
		}
		 
		return new Alternate(copyFirstSlot);
	}
	
	private BodyGrammarSlot copySlot(BodyGrammarSlot slot, BodyGrammarSlot previous, HeadGrammarSlot head) {

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
		
	@SafeVarargs
	protected static <T> Set<T> set(T...objects) {
		Set<T>  set = new HashSet<>();
		for(T t : objects) {
			set.add(t);
		}
		return set;
	}


	/**
	 * 
	 * Adds the given filter to the set of filters. If a filter with the same nonterminal, alternate index, and
	 * alternate index already exists, only the given filter alternates are added to the existing filter,
	 * effectively updating the filter.
	 * 
	 * @param nonterminal
	 * @param alternateIndex
	 * @param position
	 * @param filterdAlternates
	 * 
	 */
	public void addFilter(String nonterminal, List<Symbol> parent, int position, List<Symbol> filteredAlternate) {
		Filter filter = new Filter(parent, position, filteredAlternate);

		if(filters.containsKey(nonterminal)) {
			filters.get(nonterminal).add(filter);
		} 
		else {
			Set<Filter> set = new HashSet<>();
			set.add(filter);
			filters.put(nonterminal, set);
		}
	}
	
	@Override
	public String toString() {
		
		Set<HeadGrammarSlot> visitedHeads = new HashSet<>();
		
		Deque<HeadGrammarSlot> todoQueue = new ArrayDeque<>();
		
		StringBuilder sb = new StringBuilder();
		
		for(HeadGrammarSlot head : nonterminals) {
			todoQueue.add(head);
			visitedHeads.add(head);
		}
		
		while(!todoQueue.isEmpty()) {
			HeadGrammarSlot head = todoQueue.poll();
			
			for(Alternate alternate : head.getAlternates()) {
				sb.append(getName(head)).append(" ::= ");
				BodyGrammarSlot currentSlot = alternate.getFirstSlot();
				while(!currentSlot.isLastSlot()){
					if(currentSlot.isNonterminalSlot()) {
						HeadGrammarSlot nonterminal = ((NonterminalGrammarSlot) currentSlot).getNonterminal();
						if(!visitedHeads.contains(nonterminal)) {
							todoQueue.add(nonterminal);
							visitedHeads.add(nonterminal);
						}
					}
					sb.append(" ").append(getName(currentSlot));
					currentSlot = currentSlot.next;
				}
				sb.append("\n");
			}

		}
				
		return sb.toString();
	}
	
	private String getName(BodyGrammarSlot slot) {
		if(slot.isNonterminalSlot()) {
			return getName(((NonterminalGrammarSlot) slot).getNonterminal());
		}
		
		return slot.getSymbol().toString();
	}
	
	/**
	 * If the nonterminal is introduced while rewriting, adds its index to it. 
	 */
	private String getName(HeadGrammarSlot head) {
		return newNonterminals.contains(head) ? head.getNonterminal().getName() + (newNonterminals.indexOf(head) + 1) : head.getNonterminal().getName();			
	}
		
}
