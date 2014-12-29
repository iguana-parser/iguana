package org.jgll.grammar.precedence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.patterns.AbstractPattern;
import org.jgll.grammar.patterns.ExceptPattern;
import org.jgll.grammar.patterns.PrecedencePattern;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.util.logging.LoggerWrapper;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

public class OperatorPrecedence {
	
	private static final LoggerWrapper log = LoggerWrapper.getLogger(OperatorPrecedence.class);
	
	private ListMultimap<Nonterminal, Rule> definitions;
	
	private final Map<String, Integer> newNonterminals;
	
	private final ListMultimap<Nonterminal, PrecedencePattern> precednecePatterns;
	
	private final List<ExceptPattern> exceptPatterns;
	
	private final Map<List<List<Symbol>>, Nonterminal> existingAlternates;
	
	public OperatorPrecedence() {
		this.newNonterminals = new HashMap<>();
		this.precednecePatterns = ArrayListMultimap.create();
		this.existingAlternates = new HashMap<>();
		this.exceptPatterns = new ArrayList<>();
	}
	
	public Grammar transform(Grammar grammar) {
		this.definitions = ArrayListMultimap.create(grammar.getDefinitions());

		rewritePrecedencePatterns();
		rewriteExceptPatterns();
		
		for(Nonterminal nonterminal : definitions.keySet()) {
			if(nonterminal.getIndex() > 0) {
				addNewRules(definitions.get(nonterminal));
			}
		}
		
		Grammar.Builder builder = Grammar.builder();
		
		definitions.values().forEach(r -> builder.addRule(r));
		
		return builder.build();
	}
	
	private void rewriteExceptPatterns() {
		rewriteExceptPatterns(groupPatterns(exceptPatterns));
	}
	
	private void rewriteExceptPatterns(Map<ExceptPattern, List<List<Symbol>>> patterns) {
		
		for (Entry<ExceptPattern, List<List<Symbol>>> e : patterns.entrySet()) {
			ExceptPattern pattern = e.getKey();
			
			for (Rule rule : definitions.get(pattern.getNonterminal())) {
				if (match(plain(rule.getBody()), pattern.getParent())) {
					Nonterminal newNonterminal = createNewNonterminal(rule.getBody(), pattern.getPosition(), e.getValue());
					rule.getBody().set(pattern.getPosition(), newNonterminal);
				}
			}
			
			if (newNonterminals.containsKey(pattern.getNonterminal().getName())) {
				
				int index = newNonterminals.get(pattern.getNonterminal().getName());
				
				for (int i = 1; i <= index; i++) {
				
					Nonterminal nonterminal = Nonterminal.builder(pattern.getNonterminal().getName()).setIndex(i).setEbnfList(pattern.getNonterminal().isEbnfList()).build();
					
					for(Rule rule : definitions.get(nonterminal)) {
						
						if(rule.getBody() != null) {
							if (match(plain(rule.getBody()), pattern.getParent())) {
								Nonterminal newNonterminal = createNewNonterminal(rule.getBody(), pattern.getPosition(), e.getValue());
								rule.getBody().set(pattern.getPosition(), newNonterminal);
							}
						}						
					}					
				}
			}
		}
	}

	
	public void addPrecedencePattern(Nonterminal nonterminal, Rule parent, int position, Rule child) {
		PrecedencePattern pattern = new PrecedencePattern(nonterminal, parent.getBody(), position, child.getBody());
		precednecePatterns.put(nonterminal, pattern);
		log.debug("Precedence pattern added %s", pattern);
	}
	
	public void addExceptPattern(Nonterminal nonterminal, Rule parent, int position, Rule child) {
		ExceptPattern pattern = new ExceptPattern(nonterminal, parent.getBody(), position, child.getBody());
		exceptPatterns.add(pattern);
		log.debug("Except pattern added %s", pattern);
	}
	
	/**
	 * Groups filters based on their parent and position.
	 * For example, two filters (E, E * .E, E + E) and
	 * (E, E * .E, E * E) will be grouped as:
	 * (E, E * .E, {E * E, E + E}) 
	 * 
	 * @param patterns
	 * @return
	 */
	private <T extends AbstractPattern> Map<T, List<List<Symbol>>> groupPatterns(Iterable<T> patterns) {
		Map<T, Set<List<Symbol>>> group = new LinkedHashMap<>();
		
		for(T pattern : patterns) {
			Set<List<Symbol>> set = group.get(pattern);
			if(set == null) {
				set = new LinkedHashSet<>();
				group.put(pattern, set);
			}
			set.add(pattern.getChild());
		}
		
		Map<T, List<List<Symbol>>> result = new LinkedHashMap<>();
		for(Entry<T, Set<List<Symbol>>> e : group.entrySet()) {
			result.put(e.getKey(), new ArrayList<>(e.getValue()));
		}
		
		return result;
	}
	
	private void rewritePrecedencePatterns() {
		
		for (Nonterminal nonterminal : precednecePatterns.keySet()) {
//			log.debug("Applying the pattern %s with %d.", entry.getKey(), entry.getValue().size());

			Map<PrecedencePattern, List<List<Symbol>>> patterns = groupPatterns(precednecePatterns.get(nonterminal));
			
			rewriteFirstLevel(nonterminal, patterns);
			rewriteDeeperLevels(nonterminal, patterns);
		}
	}

	private void rewriteDeeperLevels(Nonterminal head, Map<PrecedencePattern, List<List<Symbol>>> patterns) {

		for(Entry<PrecedencePattern, List<List<Symbol>>> e : patterns.entrySet()) {
			
			PrecedencePattern pattern = e.getKey();
			List<List<Symbol>> children = e.getValue();
			
			for (Rule rule : definitions.get(head)) {
				if (pattern.isLeftMost() && match(plain(rule.getBody()), pattern.getParent())) {
					rewriteRightEnds((Nonterminal)rule.getBody().get(0), pattern, children, new HashSet<Nonterminal>());
					rewriteIndirectRightEnds((Nonterminal)rule.getBody().get(0), pattern, children, new HashSet<Nonterminal>());
				}

				if (pattern.isRightMost() && match(plain(rule.getBody()), pattern.getParent())) {
					rewriteLeftEnds((Nonterminal)rule.getBody().get(rule.getBody().size() - 1), pattern, children, new HashSet<Nonterminal>());
					rewriteIndirectLeftEnds((Nonterminal)rule.getBody().get(rule.getBody().size() - 1), pattern, children, new HashSet<Nonterminal>());
				}
			}
		}
	}
	
	private void rewriteLeftEnds(Nonterminal nonterminal, PrecedencePattern pattern, List<List<Symbol>> children, Set<Nonterminal> visited) {
		
		if (visited.contains(nonterminal)) {
			return;
		} else {
			visited.add(nonterminal);
		}
		
		for(Rule rule : definitions.get(nonterminal)) {
			
			List<Symbol> alternate = rule.getBody();
			
			if(alternate == null) {
				continue;
			}
			
			if (alternate.size() == 0 || !(alternate.get(0) instanceof Nonterminal)) {
				continue;
			}

			Nonterminal first = (Nonterminal) alternate.get(0);
			
			if (plainEqual(first, pattern.getNonterminal())) {
				if(contains(first, children)) {
					Nonterminal newNonterminal = createNewNonterminal(alternate, 0, children);
					alternate.set(0, newNonterminal);
					rewriteLeftEnds(newNonterminal, pattern, children, visited);
				}				
			}
		}			
	}
	
	private void rewriteIndirectLeftEnds(Nonterminal nonterminal, PrecedencePattern pattern, List<List<Symbol>> children, Set<Nonterminal> visited) {
		
		if (visited.contains(nonterminal)) {
			return;
		} else {
			visited.add(nonterminal);
		}
		
			
		for(Rule rule : definitions.get(nonterminal)) {
			
			List<Symbol> alternate = rule.getBody();
			
			if(alternate == null) {
				continue;
			}
			
			if (alternate.size() == 0 || !(alternate.get(0) instanceof Nonterminal)) {
				continue;
			}

			Nonterminal first = (Nonterminal) alternate.get(0);
			
			if (plainEqual(first, pattern.getNonterminal())) {
				if(contains(first, children)) {
					Nonterminal newNonterminal = createNewNonterminal(alternate, 0, children);
					alternate.set(0, newNonterminal);
					rewriteLeftEnds(newNonterminal, pattern, children, visited);
				}				
			} else {
				assert pattern.isRightMost();
				rewriteIndirectLeftEnds(first, pattern, children, visited);
			}
		}			
	}
	
	private void rewriteRightEnds(Nonterminal nonterminal, PrecedencePattern pattern, List<List<Symbol>> children, Set<Nonterminal> visited) {
		
		if (visited.contains(nonterminal)) {
			return;
		} else {
			visited.add(nonterminal);
		}
			
		for(Rule rule : definitions.get(nonterminal)) {
			
			List<Symbol> alternate = rule.getBody();
			
			if (alternate == null) {
				continue;
			}
			
			if (alternate.size() == 0 || !(alternate.get(alternate.size() - 1) instanceof Nonterminal)) {
				continue;
			}

			Nonterminal last = (Nonterminal) alternate.get(alternate.size() - 1); 
			
			if (plainEqual(last, pattern.getNonterminal())) {
				if(contains(last, children)) {
					Nonterminal newNonterminal = createNewNonterminal(alternate, alternate.size() - 1, children);
					alternate.set(alternate.size() - 1, newNonterminal);
					rewriteRightEnds(newNonterminal, pattern, children, visited);
				}				
			}
		}
	}
	
	private void rewriteIndirectRightEnds(Nonterminal nonterminal, PrecedencePattern pattern, List<List<Symbol>> children, Set<Nonterminal> visited) {
		
		if (visited.contains(nonterminal)) {
			return;
		} else {
			visited.add(nonterminal);
		}
			
		for(Rule rule : definitions.get(nonterminal)) {
			
			List<Symbol> alternate = rule.getBody();
			
			if (alternate == null) {
				continue;
			}
			
			if (alternate.size() == 0 || !(alternate.get(alternate.size() - 1) instanceof Nonterminal)) {
				continue;
			}

			Nonterminal last = (Nonterminal) alternate.get(alternate.size() - 1); 
			
			if (plainEqual(last, pattern.getNonterminal())) {
				if(contains(last, children)) {
					Nonterminal newNonterminal = createNewNonterminal(alternate, alternate.size() - 1, children);
					alternate.set(alternate.size() - 1, newNonterminal);
					rewriteRightEnds(newNonterminal, pattern, children, visited);
				}				
			} else {
				rewriteIndirectRightEnds(last, pattern, children, visited);
			}
		}
	}
	
	private Nonterminal createNewNonterminal(List<Symbol> alt, int position, List<List<Symbol>> filteredAlternates) {
		
		Nonterminal filteredNonterminal = (Nonterminal) alt.get(position);

		List<List<Symbol>> set = without(filteredNonterminal, filteredAlternates);
		
		return existingAlternates.computeIfAbsent(plain2(set), k -> { 
			Nonterminal newNonterminal = createNewNonterminal(filteredNonterminal);
			List<List<Symbol>> copy = copyAlternates(set);
			existingAlternates.put(plain2(copy), newNonterminal);
			List<Rule> collect = copy.stream().map(l -> Rule.builder(newNonterminal).addSymbols(l).build()).collect(Collectors.toList());
			definitions.putAll(newNonterminal, collect);
			return newNonterminal;
		});
		
	}
	
	private void rewriteFirstLevel(Nonterminal head, Map<PrecedencePattern, List<List<Symbol>>> patterns) {
		
		Map<PrecedencePattern, Nonterminal> freshNonterminals = new LinkedHashMap<>();
		
		Map<List<List<Symbol>>, Nonterminal> map = new HashMap<>();
		
		if(!newNonterminals.containsKey(head.getName())) {
			newNonterminals.put(head.getName(), 0);
		}
		
		// Creating fresh nonterminals
		for(Entry<PrecedencePattern, List<List<Symbol>>> e : patterns.entrySet()) {
			
			PrecedencePattern pattern = e.getKey();
			
			Nonterminal freshNonterminal = map.get(e.getValue());
			
			if(freshNonterminal == null) {
				int index = newNonterminals.get(pattern.getNonterminal().getName());
				freshNonterminal = new Nonterminal.Builder(pattern.getNonterminal().getName())
												  .setIndex(index + 1)
												  .setEbnfList(pattern.getNonterminal().isEbnfList())
												  .build();
				newNonterminals.put(freshNonterminal.getName(), index + 1);
				map.put(e.getValue(), freshNonterminal);
			}

			freshNonterminals.put(pattern, freshNonterminal);
		}
		
		// Replacing nonterminals with their fresh ones
		for(Entry<PrecedencePattern, List<List<Symbol>>> e : patterns.entrySet()) {
			
			for(Rule rule : definitions.get(head)) {
				
				List<Symbol> alt = rule.getBody();
				
				PrecedencePattern pattern = e.getKey();
				
				if(!match(plain(alt), pattern.getParent())) {
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
							a.set(0, new Nonterminal.Builder(freshNonterminals.get(pattern)).addPreConditions(a.get(0).getPreConditions()).build());
						}
					} else {
						copy = copyIndirectAtRight((Nonterminal) alt.get(pattern.getPosition()), pattern.getNonterminal());
						getRightEnds(copy, pattern.getNonterminal(), alternates);
						for(List<Symbol> a : alternates) {
							a.set(a.size() - 1, new Nonterminal.Builder(freshNonterminals.get(pattern)).addPreConditions(a.get(a.size() - 1).getPreConditions()).build());
						}
					}
					
					alt.set(pattern.getPosition(), new Nonterminal.Builder(copy).addPreConditions(alt.get(pattern.getPosition()).getPreConditions()).build());
					
				} else {
					alt.set(pattern.getPosition(), new Nonterminal.Builder(freshNonterminals.get(pattern)).addPreConditions(alt.get(pattern.getPosition()).getPreConditions()).build());
				}
			}
		}
		
		// creating the body of fresh direct nonterminals
		for(Entry<PrecedencePattern, Nonterminal> e : freshNonterminals.entrySet()) {
			PrecedencePattern pattern = e.getKey();
			Nonterminal freshNonterminal = e.getValue();
			List<List<Symbol>> alternates = deepCopy(without(head, patterns.get(pattern)), pattern.getNonterminal());
			definitions.putAll(freshNonterminal, alternates.stream().map(l -> Rule.builder(freshNonterminal).addSymbols(l).build()).collect(Collectors.toList()));
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
		
		if (visited.contains(head)) {
			return;
		} else {
		    visited.add(head);
		}
		
		for (Rule rule : definitions.get(head)) {
			
			List<Symbol> alt = rule.getBody();
			
			if(alt == null || alt.size() == 0) {
				continue;
			}
			
			if (alt.get(0) instanceof Nonterminal) {
				Nonterminal first = (Nonterminal) alt.get(0);
				if (plainEqual(first, nonterminal)) {
					nonterminals.add(alt);
				} else {
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
		} else {
			visited.add(head);
		}
		
		for (Rule rule : definitions.get(head)) {
			
			List<Symbol> alt = rule.getBody();
			
			if(alt == null || alt.size() == 0) {
				continue;
			}
			
			if (alt.get(alt.size() - 1) instanceof Nonterminal) {
				Nonterminal last = (Nonterminal) alt.get(alt.size() - 1);
				if (plainEqual(last, directNonterminal)) {
					alternates.add(alt);
				} else {
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

		List<List<Symbol>> alternates = definitions.get(head).stream().map(r -> r.getBody()).collect(Collectors.toList());
		List<List<Symbol>> copyAlternates = copyAlternates(alternates);
		definitions.putAll(copy, copyAlternates.stream().map(l -> Rule.builder(head).addSymbols(l).build()).collect(Collectors.toList()));
		
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

		List<List<Symbol>> alternates = definitions.get(head).stream().map(r -> r.getBody()).collect(Collectors.toList());
		List<List<Symbol>> copyAlternates = copyAlternates(alternates);
		definitions.putAll(copy, copyAlternates.stream().map(l -> Rule.builder(head).addSymbols(l).build()).collect(Collectors.toList()));
		
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
		
		if(!newNonterminals.containsKey(nonterminal.getName())) {
			newNonterminals.put(nonterminal.getName(), 0);
		}
		
		int index = newNonterminals.get(nonterminal.getName());
		Nonterminal newNonterminal = new Nonterminal.Builder(nonterminal.getName())
												    .setIndex(index + 1)
												    .setEbnfList(nonterminal.isEbnfList())
												    .addPreConditions(nonterminal.getPreConditions())
												    .build();
		newNonterminals.put(nonterminal.getName(), index + 1);
		
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
	
	private List<List<Symbol>> deepCopy(List<List<Symbol>> alternates, Nonterminal nonterminal) {
		return deepCopy(alternates, nonterminal, new HashMap<Nonterminal, Nonterminal>());
	}
	
	private List<List<Symbol>> deepCopy(List<List<Symbol>> alternates, Nonterminal nonterminal, Map<Nonterminal, Nonterminal> map) {
		List<List<Symbol>> copyAlts = new ArrayList<>();
		for(List<Symbol> alternate : alternates) {
			if (alternate != null) {
				copyAlts.add(copy(alternate, nonterminal, map));
			} else {
				copyAlts.add(null);
			}
		}
		return copyAlts;
	}
	
	private List<Symbol> copy(List<Symbol> alternate,  Nonterminal nonterminal, Map<Nonterminal, Nonterminal> map) {
		List<Symbol> copyAlt = new ArrayList<>();
		for(Symbol symbol : alternate) {
			if (symbol instanceof Nonterminal) {
				Nonterminal n = (Nonterminal) symbol;
				if (!plainEqual(n, nonterminal) && n.getIndex() > 0) {
					if (map.containsKey(n)) {
						Nonterminal newNonterminal = map.get(n);
						copyAlt.add(newNonterminal);						
					} else {
						Nonterminal newNonterminal = createNewNonterminal(n);
						map.put(n,  newNonterminal);

						List<List<Symbol>> alternates = definitions.get(n).stream().map(r -> r.getBody()).collect(Collectors.toList());
						List<List<Symbol>> copyAlternates = deepCopy(alternates, nonterminal, map);

						definitions.putAll(newNonterminal, copyAlternates.stream().map(l -> Rule.builder(n).addSymbols(l).build()).collect(Collectors.toList()));
						copyAlt.add(newNonterminal);				
					}
					continue;
				}
			}
			copyAlt.add(symbol);
		}
		return copyAlt;
	}
	
//	private List<Rule> copyAlternates(List<Rule> rules) {
//		List<Rule> copy = new ArrayList<>();
//		rules.forEach(r -> copy.add(r.getBody() == null ? new Rule(r.getHead()) : new Rule(r.getHead(), new ArrayList<>(r.getBody()))));
//		return copy;
//	}
//	
	
	private List<List<Symbol>> copyAlternates(List<List<Symbol>> alternates) {
		
		List<List<Symbol>> copy = new ArrayList<>();
		
		for(List<Symbol> alternate : alternates) {
			
			if(alternate == null) {
				copy.add(null);
			} else {
				List<Symbol> copyAlt = new ArrayList<>();
				for(Symbol symbol : alternate) {
					copyAlt.add(symbol);
				}
				copy.add(copyAlt);				
			}
		}
		
		return copy;
	}
	
	private List<List<Symbol>> without(Nonterminal head, List<List<Symbol>> set) {
		
		List<List<Symbol>> without = new ArrayList<>();
		
		for(Rule rule : definitions.get(head)) {
			
			List<Symbol> alt = rule.getBody();
			
			if(alt == null) {
				without.add(null);
			} 
			else if (!set.contains(plain(alt))) {
				without.add(alt);
			} else {
				without.add(null);
			}
		}
		return without;
	}
	
	public static List<List<Symbol>> plain2(List<List<Symbol>> alternates) {
		List<List<Symbol>> list = new ArrayList<>();
		
		for(List<Symbol> l : alternates) {
			if(l == null) {
				list.add(null);
			} else {
				list.add(plain(l));				
			}
		}
		
		return list;
	}
	
	public static Rule plain(Rule rule) {
		Nonterminal plainHead = (Nonterminal) plain(rule.getHead());
		List<Symbol> plainAlternate = plain(rule.getBody());
		return Rule.builder(plainHead).addSymbols(plainAlternate).build();
	}
	
	public static List<Symbol> plain(List<Symbol> alternate) {
		List<Symbol> plain = new ArrayList<>();
		for(Symbol symbol : alternate) {
			if(symbol instanceof Nonterminal && ((Nonterminal) symbol).getIndex() > 0) {
				Nonterminal nonterminal = new Nonterminal.Builder(symbol.getName()).setEbnfList(((Nonterminal) symbol).isEbnfList()).build();
				plain.add(nonterminal);
			} else {
				plain.add(symbol);
			}
		}
		return plain;
	}
	
	public static Symbol plain(Symbol symbol) {
		if(symbol instanceof Nonterminal && ((Nonterminal) symbol).getIndex() > 0) {
			return new Nonterminal.Builder(symbol.getName()).setEbnfList(((Nonterminal) symbol).isEbnfList()).build();
		} else {
			return symbol;
		}
	}
	
	public static boolean plainEqual(Nonterminal n1, Nonterminal n2) {
		return n1.getName().equals(n2.getName());
	}

	private void addNewRules(List<Rule> rules) {
		rules.forEach(r -> definitions.put(r.getHead(), r));
	}
	
	private boolean contains(Nonterminal nonterminal, List<List<Symbol>> alternates) {
		
		List<Rule> set = definitions.get(nonterminal);
		
		for(Rule rule : set) {
			
			if(rule.getBody() == null) continue;
			
			for(List<Symbol> alt : alternates) {
				
				if(alt == null) continue;
				
				if(plain(rule).equals(alt)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
}
