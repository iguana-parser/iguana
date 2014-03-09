package org.jgll.grammar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.jgll.grammar.patterns.PrecedencePattern;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.util.logging.LoggerWrapper;

public class OperatorPrecedence {
	
	private static final LoggerWrapper log = LoggerWrapper.getLogger(OperatorPrecedence.class);
	
	private Map<Nonterminal, Set<List<Symbol>>> definitions;
	
	private Map<PrecedencePattern, Set<List<Symbol>>> patterns;
	
	private Map<Nonterminal, Integer> newNonterminals;
	
	public OperatorPrecedence(Map<Nonterminal, 
							  Set<List<Symbol>>> definitions, 
							  Map<PrecedencePattern, Set<List<Symbol>>> patterns) {
		this.definitions = definitions;
		this.patterns = patterns;
		newNonterminals = new HashMap<>();
	}
	
	public Map<Nonterminal, Set<List<Symbol>>> rewrite() {
		for(Nonterminal nonterminal : definitions.keySet()) {
			rewriteFirstLevel(nonterminal);
		}
		return definitions;
	}

	private void rewriteFirstLevel(Nonterminal head) {
		
		// This is complicated shit! Document it for the future reference.
		Map<PrecedencePattern, Nonterminal> freshNonterminals = new LinkedHashMap<>();
		
		Map<Set<List<Symbol>>, Nonterminal> map = new HashMap<>();
		
		
		Set<Nonterminal> newNonterminals = new HashSet<>();
		
		int index = 0;
		
		// Creating fresh nonterminals
		for(Entry<PrecedencePattern, Set<List<Symbol>>> e : patterns.entrySet()) {
			
			PrecedencePattern pattern = e.getKey();
			
			Nonterminal freshNonterminal = map.get(e.getValue());
			
			if(freshNonterminal == null) {
				freshNonterminal = new Nonterminal(pattern.getNonterminal().getName(), ++index);
				newNonterminals.add(freshNonterminal);
				map.put(e.getValue(), freshNonterminal);
			}

			freshNonterminals.put(pattern, freshNonterminal);
		}
		
		// Replacing nonterminals with their fresh ones
		for(Entry<PrecedencePattern, Set<List<Symbol>>> e : patterns.entrySet()) {
			
			for(List<Symbol> alt : definitions.get(head)) {
				
				PrecedencePattern pattern = e.getKey();
				
				if(!match(alt, pattern.getParent())) {
					continue;
				}
				
				log.trace("Applying the pattern %s on %s.", pattern, alt);
				
				if (!pattern.isDirect()) {
					
					Nonterminal copy;
					
					Set<List<Symbol>> alternates = new LinkedHashSet<>();
					if(pattern.isLeftMost()) {
						copy = copyIndirectAtLeft((Nonterminal) alt.get(pattern.getPosition()), pattern.getNonterminal());
						getLeftEnds(copy, pattern.getNonterminal(), alternates);
						for(List<Symbol> a : alternates) {
							a.set(0, freshNonterminals.get(pattern));
						}
					} else {
						copy = copyIndirectAtRight((Nonterminal) alt.get(pattern.getPosition()), pattern.getNonterminal());
						getRightEnds(copy, pattern.getNonterminal(), alternates);
						for(List<Symbol> a : alternates) {
							a.set(a.size() - 1, freshNonterminals.get(pattern));
						}
					}
					
					alt.set(pattern.getPosition(), copy);
					
				} else {
					alt.set(pattern.getPosition(), freshNonterminals.get(pattern));
				}
			}
		}
		
		// creating the body of fresh direct nonterminals
		for(Entry<PrecedencePattern, Nonterminal> e : freshNonterminals.entrySet()) {
			PrecedencePattern pattern = e.getKey();
			
			Nonterminal freshNontermianl = e.getValue();
			
			Set<List<Symbol>> alternates = without(head, patterns.get(pattern));
//			List<Alternate> copyAlternates = copyAlternates(freshNontermianl, alternates);

//			for(List<Symbol> alt : alternates) {
//				
//				((LastGrammarSlot) a.getLastSlot().next()).setAlternateIndex(copyAlternates.indexOf(a));
//			}
//			
//			freshNontermianl.setAlternates(copyAlternates);
			definitions.put(freshNontermianl, alternates);
		}
	}
	
	/**
	 * 
	 * Returns a list of all nonterminals with the given name which are
	 * reachable from the given head and are on the left-most end.
	 * 
	 * @param head
	 * @param directName
	 * @param alternates
	 */
	private void getLeftEnds(Nonterminal head, Nonterminal nonterminal, Set<List<Symbol>> nonterminals) {
		getLeftEnds(head, nonterminal, nonterminals, new HashSet<Nonterminal>());
	}
	
	private void getLeftEnds(Nonterminal head, Nonterminal nonterminal, Set<List<Symbol>> nonterminals, Set<Nonterminal> visited) {
		
		if(visited.contains(head)) {
			return;
		}
		
		for (List<Symbol> alt : definitions.get(head)) {
			if (alt.get(0) instanceof Nonterminal) {
				Nonterminal first = (Nonterminal) alt.get(0);
				if (first.equals(nonterminal)) {
					nonterminals.add(alt);
				} else {
					visited.add(first);
					getLeftEnds(first, nonterminal, nonterminals, visited);
				}
			}
		}
	}

	/**
	 * 
	 * Returns a list of all nonterminals with the given name which are
	 * reachable from the given head and are on the right-most end.
	 * 
	 * @param head
	 * @param directNonterminal
	 * @param alternates
	 */
	private void getRightEnds(Nonterminal head, Nonterminal directNonterminal, Set<List<Symbol>> alternates) {
		getRightEnds(head, directNonterminal, alternates, new HashSet<Nonterminal>());
	}
	
	private void getRightEnds(Nonterminal head, Nonterminal directNonterminal, Set<List<Symbol>> alternates, Set<Nonterminal> visited) {
		
		if(visited.contains(head)) {
			return;
		}
		
		for (List<Symbol> alt : definitions.get(head)) {
			if (alt.get(alt.size() - 1) instanceof NonterminalGrammarSlot) {
				Nonterminal last = (Nonterminal) alt.get(alt.size() - 1);
				if (last.equals(directNonterminal)) {
					alternates.add(alt);
				} else {
					visited.add(last);
					getRightEnds(last, directNonterminal, alternates, visited);
				}
			}
		}
	}
	
	
	private Nonterminal copyIndirectAtLeft(Nonterminal head, Nonterminal directNonterminal) {
		return copyIndirectAtLeft(head, directNonterminal, new HashMap<Nonterminal, Nonterminal>());
	}

	private Nonterminal copyIndirectAtRight(Nonterminal head, Nonterminal directNonterminal) {
		return copyIndirectAtRight(head, directNonterminal, new HashMap<Nonterminal, Nonterminal>());
	}
	
	private Nonterminal copyIndirectAtLeft(Nonterminal head, Nonterminal directName, HashMap<Nonterminal, Nonterminal> map) {
		
		Nonterminal copy = map.get(head);
		if(copy != null) {
			return copy;
		}
		
		copy = createNewNonterminal(head);
		map.put(head, copy);

		Set<List<Symbol>> copyAlternates = copyAlternates(definitions.get(head));
		definitions.put(copy, copyAlternates);
		
		for(List<Symbol> alt : copyAlternates) {
			if(alt.get(0) instanceof Nonterminal) {
				Nonterminal nonterminal = (Nonterminal) alt.get(0);
				// Leave the direct nonterminal, copy indirect ones
				if(!nonterminal.equals(directName)) {
					alt.set(0, copyIndirectAtLeft(nonterminal, directName, map));
				}
			}
		}
		
		return copy;
	}
	
	private Nonterminal copyIndirectAtRight(Nonterminal head, Nonterminal directNonterminal, HashMap<Nonterminal, Nonterminal> map) {
		
		Nonterminal copy = map.get(head);
		if(copy != null) {
			return copy;
		}
		
		copy = createNewNonterminal(head);
		map.put(head, copy);
		
		Set<List<Symbol>> copyAlternates = copyAlternates(definitions.get(head));
		definitions.put(copy, copyAlternates);
		
		for(List<Symbol> alt : copyAlternates) {
			if(alt.get(alt.size() - 1) instanceof Nonterminal) {
				Nonterminal nonterminal = (Nonterminal) alt.get(alt.size() - 1);
				
				// Leave the direct nonterminal, copy indirect ones
				if(!nonterminal.equals(directNonterminal)) {
					alt.set(alt.size() - 1, copyIndirectAtLeft(nonterminal, directNonterminal, map));
				}
			}
		}
		
		return copy;
	}
	
	private Nonterminal createNewNonterminal(Nonterminal nonterminal) {
		Integer index = newNonterminals.get(nonterminal);
		Nonterminal newNonterminal = new Nonterminal(nonterminal.getName(), index + 1);
		newNonterminals.put(nonterminal, index + 1);
		return newNonterminal;
	}
	
	public boolean match(List<Symbol> list1, List<Symbol> list2) {
		
		if(list1.size() != list2.size()) {
			return false;
		}
		
		for(int i = 0; i < list1.size(); i++) {
			if(!list1.get(i).equals(list2.get(i))) {
				return false;
			}
		}
		return true;
	}
	
	private Set<List<Symbol>> copyAlternates(Set<List<Symbol>> alternates) {
		Set<List<Symbol>> copy = new LinkedHashSet<>();
		
		for(List<Symbol> alternate : alternates) {
			List<Symbol> copyAlt = new ArrayList<>();
			for(Symbol symbol : alternate) {
				copyAlt.add(symbol);
			}
			copy.add(copyAlt);
		}
		
		return copy;
	}
	
	private Set<List<Symbol>> without(Nonterminal head, Set<List<Symbol>> list) {
		Set<List<Symbol>> set = new HashSet<>(definitions.get(head));
		set.retainAll(list);
		return set;
	}

	
}
