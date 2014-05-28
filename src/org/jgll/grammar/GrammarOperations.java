package org.jgll.grammar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgll.grammar.symbol.Alt;
import org.jgll.grammar.symbol.EOF;
import org.jgll.grammar.symbol.Epsilon;
import org.jgll.grammar.symbol.Group;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Range;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.regex.RegularExpression;
import org.jgll.util.Tuple;
import org.jgll.util.trie.Edge;
import org.jgll.util.trie.Node;
import org.jgll.util.trie.Trie;

public class GrammarOperations {
	
	private Grammar grammar;

	private Map<Nonterminal, Set<RegularExpression>> firstSets;
	
	private Map<Nonterminal, Set<RegularExpression>> followSets;

	private Map<Nonterminal, Set<Nonterminal>> reachabilityGraph;

	private Map<Nonterminal, List<Set<RegularExpression>>> predictionSets;
	
	private Set<Nonterminal> nullableNonterminals;
	
	public GrammarOperations(Grammar grammar) {
		this.grammar = grammar;
		this.firstSets = new HashMap<>();
		this.nullableNonterminals = new HashSet<>();
		this.followSets = new HashMap<>();
		this.predictionSets = new HashMap<>();
		
		calculateFirstSets();
		calculateFollowSets();
		calcualtePredictionSets();
	}
	
	public Map<Nonterminal, Set<RegularExpression>> getFirstSets() {
		return firstSets;
	}
	
	public Map<Nonterminal, Set<RegularExpression>> getFollowSets() {
		return followSets;
	}
	
	public Map<Nonterminal, List<Set<RegularExpression>>> getPredictionSets() {
		return predictionSets;
	}
	
	public Map<Nonterminal, Set<Nonterminal>> getReachabilityGraph() {
		return reachabilityGraph;
	}
	
	private void calculateFirstSets() {
		
		Set<Nonterminal> nonterminals = grammar.getNonterminals();
		for (Nonterminal head : nonterminals) {
			firstSets.put(head, new HashSet<RegularExpression>());
		}
		
		boolean changed = true;

		while (changed) {
			
			changed = false;
			
			for (Nonterminal head : nonterminals) {
				Set<RegularExpression> firstSet = firstSets.get(head);
				for (List<Symbol> alternate : grammar.getAlternatives(head)) {
					changed |= addFirstSet(head, firstSet, alternate, 0);
				}
			}
		}
	}
	
	/**
	 * Adds the first set of the current slot to the given set.
	 * 
	 * @param firstSet
	 * @param currentSlot
	 * @param changed
	 * 
	 * @return true if adding any new terminals are added to the first set.
	 */
	private boolean addFirstSet(Nonterminal head, Set<RegularExpression> firstSet, List<Symbol> alternate, int index) {

		boolean changed = false;
		
		if(alternate == null) {
			return false;
		}
		 
		//TODO: check if it is allowed or is it a good idea to enforce the instantiation of Epsilon.
		if (alternate.size() == 0) {
			nullableNonterminals.add(head);
			return firstSet.add(Epsilon.getInstance());
		}
		
		for(int i = index; i < alternate.size(); i++) {
			Symbol symbol = alternate.get(i);

			if (symbol instanceof RegularExpression) {
				RegularExpression regularExpression = (RegularExpression) symbol;
				changed |= firstSet.add(regularExpression);
				if (!regularExpression.isNullable()) {
					break;
				}
			}
			
			// Nonterminal
			else if (symbol instanceof Nonterminal) {
				Nonterminal nonterminal = (Nonterminal) symbol;
				
				Set<RegularExpression> set = new HashSet<>(firstSets.get(nonterminal));
				set.remove(Epsilon.getInstance());
				changed |= firstSet.addAll(set);
				if (!isNullable(nonterminal)) {
					break;
				}
			}
		}
		
		if (isChainNullable(alternate, 0)) {
			nullableNonterminals.add(head);
			changed |= firstSet.add(Epsilon.getInstance());
		}
		
		return changed;
	}
	
	private boolean isNullable(Nonterminal nt) {
		return nullableNonterminals.contains(nt);
	}
	
	/**
	 * 
	 * Checks if a grammar slot is nullable. This check is performed until
	 * the end of the alternate: isChainNullable(X ::= alpha . beta) says if the
	 * part beta is nullable.
	 *   
	 */
	private boolean isChainNullable(List<Symbol> alternate, int index) {
		
		if(index >= alternate.size()) {
			return true;
		}
		
		for(int i = index; i < alternate.size(); i++) {
			Symbol s = alternate.get(i);
			
			if(s instanceof RegularExpression) {
				if(!((RegularExpression)s).isNullable()) {
					return false;
				}
			} else {
				if(!isNullable((Nonterminal) s)) {
					return false;
				}				
			}
		}

		return true;
	}
		
	private void calculateFollowSets() {
		
		Set<Nonterminal> nonterminals = grammar.getNonterminals();
		
		for (Nonterminal head : nonterminals) {
			followSets.put(head, new HashSet<RegularExpression>());
		}
		
		boolean changed = true;

		while (changed) {
			
			changed = false;
			
			for (Nonterminal head : nonterminals) {

				for (List<Symbol> alternate : grammar.getAlternatives(head)) {
					
					if(alternate == null || alternate.size() == 0) {
						continue;
					}
					
					for(int i = 0; i < alternate.size(); i++) {
					
						Symbol symbol = alternate.get(i);
						
						if(symbol instanceof Nonterminal) {
							Nonterminal nonterminal = (Nonterminal) symbol;

							// For rules of the form X ::= alpha B beta, add the
							// first set of beta to
							// the follow set of B.
							Set<RegularExpression> followSet = followSets.get(nonterminal);
							changed |= addFirstSet(nonterminal, followSet, alternate, i + 1);

							// If beta is nullable, then add the follow set of X
							// to the follow set of B.
							if (isChainNullable(alternate, i + 1)) {
								changed |= followSet.addAll(followSets.get(head));
							}
						}
					}
				}
			}
		}

		for (Nonterminal head : nonterminals) {
			// Remove the epsilon which may have been added from nullable
			// nonterminals
			followSets.get(head).remove(Epsilon.getInstance());

			// Add the EOF to all nonterminals as each nonterminal can be used
			// as the start symbol.
			followSets.get(head).add(EOF.getInstance());
		}
	}
	
	private void calcualtePredictionSets() {

		for(Nonterminal nonterminal : grammar.getNonterminals()) {
			List<List<Symbol>> alternates = grammar.getAlternatives(nonterminal);
			
			List<Set<RegularExpression>> list = new ArrayList<>();

			for(List<Symbol> alternate : alternates) {
				
				Set<RegularExpression> predictionSet = new HashSet<>();

				if(alternate == null) {
					list.add(predictionSet);
					continue;
				}
				
				for(int i = 0; i < alternate.size(); i++) {
					Symbol symbol = alternate.get(i);
					if(symbol instanceof Nonterminal) {
						predictionSet.addAll(firstSets.get(symbol));
						if(!firstSets.get(symbol).contains(Epsilon.getInstance())) {
							break;
						}
					} 
					else if (symbol instanceof RegularExpression) {
						RegularExpression regex = (RegularExpression) symbol;
						predictionSet.add(regex);
						if(!regex.isNullable()) {
							break;
						}
					}
				}
				
				if(isChainNullable(alternate, 0)) {
					predictionSet.addAll(followSets.get(nonterminal));
				}
				predictionSet.remove(Epsilon.getInstance());
				list.add(predictionSet);
			}

			predictionSets.put(nonterminal, list);
		}
	}
	
	public Set<Nonterminal> calculateLLNonterminals() {

		Set<Nonterminal> nonterminals = grammar.getNonterminals();
		
		Set<Nonterminal> ll1Nonterminals = new HashSet<>();
		
		Set<Nonterminal> ll1SubGrammarNonterminals = new HashSet<>();
		
		// Calculating character level predictions
		Map<Tuple<Nonterminal, Integer>, Set<Integer>> predictions = new HashMap<>();
		
		for (Nonterminal head : nonterminals) {

			int alternateIndex = 0;
			for(List<Symbol> alt : grammar.getAlternatives(head)) {
			
				// Calculate the prediction set for the alternate
				Set<RegularExpression> s = new HashSet<>();
				addFirstSet(head, s, alt, 0);
				if(s.contains(Epsilon.getInstance())) {
					s.addAll(followSets.get(head));
				}

				// Expand ranges into integers
				Set<Integer> set = new HashSet<>();
				for(RegularExpression r : s) {
					set.addAll(convert(r.getFirstSet()));
				}
				
				predictions.put(Tuple.of(head, alternateIndex), set);
				
				alternateIndex++;
			}			
		}
		
		for (Nonterminal head : nonterminals) {
			if(isLL1(head, predictions, grammar)) {
				ll1Nonterminals.add(head);
			}
		}
		
		for (Nonterminal head : nonterminals) {
			if(ll1Nonterminals.contains(head)) {
				boolean ll1SubGrammar = true;
				for(Nonterminal reachableHead : reachabilityGraph.get(head)) {
					if(!ll1Nonterminals.contains(reachableHead)) {
						ll1SubGrammar = false;
					}
				}
				if(ll1SubGrammar) {
					ll1SubGrammarNonterminals.add(head);
				}
			}
		}
		
		return ll1SubGrammarNonterminals;
	}
	
	/**
	 * Converts a 
	 * @param set
	 * @return
	 */
	private static Set<Integer> convert(Set<Range> set) {
		Set<Integer> integerSet = new HashSet<>();
		for(Range range : set) {
			for(int i = range.getStart(); i < range.getEnd(); i++) {
				integerSet.add(i);
			}
		}
		return integerSet;
	}
	
    private static boolean isLL1(Nonterminal nonterminal, Map<Tuple<Nonterminal, Integer>, Set<Integer>> predictions,
    							 Grammar grammar) {
    	
    	int size = grammar.getAlternatives(nonterminal).size();
    	
    	// If there is only one alternate
		if(size == 1) {
        	return true;
        }
        
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
            	if(i != j) {
            		HashSet<Integer> intersection = new HashSet<>(predictions.get(Tuple.of(nonterminal, i)));
            		intersection.retainAll(predictions.get(Tuple.of(nonterminal, j)));
        			if(!intersection.isEmpty()) {
        				return false;
                    }
            	}
            }
        }

        return true;
    }
		
	/**
	 * 
	 * Calculate the set of nonterminals that are reachable via the alternates of A.
	 * In other words, if A is a nonterminal, reachable nonterminals are all the B's such as
	 * A =>* alpha B gamma. Note that this method does not calculate direct-nullable reachable
	 * nonterminals.
	 * 
	 */
	public static Map<Nonterminal, Set<Nonterminal>> calculateReachabilityGraph(Grammar grammar) {
		
		Set<Nonterminal> nonterminals = grammar.getNonterminals();
		
		Map<Nonterminal, Set<Nonterminal>> reachabilityGraph = new HashMap<>();
		
		for (Nonterminal head : nonterminals) {
			reachabilityGraph.put(head, new HashSet<Nonterminal>());
		}
		
		boolean changed = true;
		while (changed) {
			
			changed = false;
			
			for (Nonterminal head : nonterminals) {
				Set<Nonterminal> set = reachabilityGraph.get(head);
				for (List<Symbol> alternate : grammar.getAlternatives(head)) {
					
					if(alternate == null) {
						continue;
					}
					
					for(Symbol symbol : alternate) {
						if(symbol instanceof Nonterminal) {
							Nonterminal nonterminal = (Nonterminal) symbol;
							changed |= set.add(nonterminal);
							changed |= set.addAll(reachabilityGraph.get(nonterminal));
						} 
					}
				}
			}
		}
		
		return reachabilityGraph;
	}
	
	public Grammar leftFactorize() {
		
		Grammar leftFactorized = new Grammar();
		
		for (Nonterminal nonterminal : grammar.getNonterminals()) {
		
			Trie<Symbol> trie = new Trie<>();

			Node<Symbol> node = trie.getRoot();
			for (List<Symbol> alternative : grammar.getAlternatives(nonterminal)) {
				node = trie.getRoot();
				for (Symbol s : alternative) {
					node = trie.add(node, s);
				}
				trie.add(node, Epsilon.getInstance());
			}

			leftFactorized.addRule(new Rule(nonterminal, retrieve1(trie.getRoot())));
		}
		
		return leftFactorized;
	}
	
	private static Symbol retrieve1(Node<Symbol> node) {
		
		if (node.size() == 0) return null;
		
		if (node.size() == 1 && node.getEdges().get(0).getLabel() == Epsilon.getInstance()) return null;

		List<Symbol> outer = new ArrayList<>();
		
		for (Edge<Symbol> edge : node.getEdges()) {
			List<Symbol> inner = new ArrayList<>();
			inner.add(edge.getLabel());
			Symbol next = retrieve1(edge.getDestination());
			if (next != null) {
				inner.add(next);
			}
			if (inner.size() == 1){
				outer.add(inner.get(0));
			} else {
				outer.add(Group.of(inner));
			}
		}
		if (outer.size() == 1){
			return outer.get(0);
		} else {
			return new Alt(outer);
		}
	}
	
	private static List<List<Symbol>> retrieve2(Node<Symbol> node, Set<Rule> newRules) {
		
		if (node.size() == 0) return null;
		
		if (node.size() == 1 && node.getEdges().get(0).getLabel() == Epsilon.getInstance()) return null;

		List<Symbol> outer = new ArrayList<>();
		
		for (Edge<Symbol> edge : node.getEdges()) {
			List<Symbol> inner = new ArrayList<>();
			inner.add(edge.getLabel());
			
			retrieve2(edge.getDestination(), newRules);
			
//			Symbol next = 
//			if (next != null) {
//				inner.add(next);
//			}
			if (inner.size() == 1){
				outer.add(inner.get(0));
			} else {
				outer.add(Group.of(inner));
			}
		}
		
		return null;
		
//		if (outer.size() == 1){
//			return outer.get(0);
//		} else {
//			return new Alt(outer);
//		}
	}

	
}
