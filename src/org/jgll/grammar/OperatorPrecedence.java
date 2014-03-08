package org.jgll.grammar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.jgll.grammar.patterns.PrecedencePattern;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.grammar.symbol.Alternate;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.util.logging.LoggerWrapper;

public class OperatorPrecedence {
	
	private static final LoggerWrapper log = LoggerWrapper.getLogger(OperatorPrecedence.class);
	
	private Map<Nonterminal, Set<List<Symbol>>> definitions;
	
	private Map<PrecedencePattern, Set<List<Symbol>>> patterns;
	
	public OperatorPrecedence(Map<Nonterminal, 
							  Set<List<Symbol>>> definitions, 
							  Map<PrecedencePattern, Set<List<Symbol>>> patterns) {
		this.definitions = definitions;
		this.patterns = patterns;
	}
	
	public void rewritePatterns() {
		for(Nonterminal nonterminal : definitions.keySet()) {
			rewriteFirstLevel(nonterminal);
		}
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
					
					List<List<Symbol>> alternates = new ArrayList<>();
					if(pattern.isLeftMost()) {
						copy = copyIndirectAtLeft(alt.get(pattern.getPosition()), pattern.getNonterminal());
						getLeftEnds(copy, pattern.getNonterminal(), alternates);
						for(List<Symbol> a : alternates) {
							a.set(0, freshNonterminals.get(pattern));
						}
					} else {
						copy = copyIndirectAtRight(alt.get(pattern.getPosition()), pattern.getNonterminal());
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
			
			HeadGrammarSlot freshNontermianl = e.getValue();
			
			Set<Alternate> alternates = head.without(patterns.get(pattern));
			List<Alternate> copyAlternates = copyAlternates(freshNontermianl, alternates);

			for(Alternate a : copyAlternates) {
				((LastGrammarSlot) a.getLastSlot().next()).setAlternateIndex(copyAlternates.indexOf(a));
			}
			
			freshNontermianl.setAlternates(copyAlternates);
			existingAlternates.put(new HashSet<>(copyAlternates), freshNontermianl);
		}
	}
	
	private HeadGrammarSlot copyIndirectAtLeft(HeadGrammarSlot head, Nonterminal directNonterminal) {
		return copyIndirectAtLeft(head, directNonterminal, new HashMap<HeadGrammarSlot, HeadGrammarSlot>());
	}

	private HeadGrammarSlot copyIndirectAtRight(HeadGrammarSlot head, Nonterminal directNonterminal) {
		return copyIndirectAtRight(head, directNonterminal, new HashMap<HeadGrammarSlot, HeadGrammarSlot>());
	}
	
	private HeadGrammarSlot copyIndirectAtLeft(HeadGrammarSlot head, Nonterminal directName, HashMap<HeadGrammarSlot, HeadGrammarSlot> map) {
		
		HeadGrammarSlot copy = map.get(head);
		if(copy != null) {
			return copy;
		}
		
		copy = grammarSlotFactory.createHeadGrammarSlot(head.getNonterminal(), definitions.get(head.getNonterminal()), firstSets, followSets, predictionSets);
		addNewNonterminal(copy);
		map.put(head, copy);
		
		List<Alternate> copyAlternates = copyAlternates(copy, head.getAlternates());
		copy.setAlternates(copyAlternates);
		
		for(Alternate alt : copyAlternates) {
			if(alt.getSlotAt(0) instanceof NonterminalGrammarSlot) {
				HeadGrammarSlot nonterminal = ((NonterminalGrammarSlot)alt.getSlotAt(0)).getNonterminal();
				// Leave the direct nonterminal, copy indirect ones
				if(!nonterminal.getNonterminal().equals(directName)) {
					alt.setNonterminalAt(0, copyIndirectAtLeft(nonterminal, directName, map));
				}
			}
		}
		
		return copy;
	}
	
	private HeadGrammarSlot copyIndirectAtRight(HeadGrammarSlot head, Nonterminal directNonterminal, HashMap<HeadGrammarSlot, HeadGrammarSlot> map) {
		
		HeadGrammarSlot copy = map.get(head);
		if(copy != null) {
			return copy;
		}
		
		copy = grammarSlotFactory.createHeadGrammarSlot(head.getNonterminal(), definitions.get(head.getNonterminal()), firstSets, followSets, predictionSets);
		addNewNonterminal(copy);
		map.put(head, copy);
		
		List<Alternate> copyAlternates = copyAlternates(copy, head.getAlternates());
		copy.setAlternates(copyAlternates);
		
		for(Alternate alt : copyAlternates) {
			if(alt.getSlotAt(alt.size() - 1) instanceof NonterminalGrammarSlot) {
				HeadGrammarSlot nonterminal = ((NonterminalGrammarSlot)alt.getSlotAt(alt.size() - 1)).getNonterminal();
				// Leave the direct nonterminal, copy indirect ones
				if(!nonterminal.getNonterminal().equals(directNonterminal)) {
					alt.setNonterminalAt(alt.size() - 1, copyIndirectAtLeft(nonterminal, directNonterminal, map));
				}
			}
		}
		
		return copy;
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

	
}
