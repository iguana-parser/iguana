package org.jgll.grammar.operations;

import java.util.List;
import java.util.Set;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.regex.Alt;
import org.jgll.regex.Opt;
import org.jgll.regex.Plus;
import org.jgll.regex.Sequence;
import org.jgll.regex.Star;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;

public class ReachabilityGraph {

	private ListMultimap<Nonterminal, Rule> definitions;
	
	private final SetMultimap<Nonterminal, Nonterminal> reachabilityGraph;

	public ReachabilityGraph(Grammar grammar) {
		this.reachabilityGraph = HashMultimap.create();
		this.definitions = grammar.getDefinitions();
		calculateReachabilityGraph();
	}
	
	public SetMultimap<Nonterminal, Nonterminal> getReachabilityGraph() {
		return reachabilityGraph;
	}
	
	public Set<Nonterminal> getReachableNonterminals(Nonterminal nt) {
		return reachabilityGraph.get(nt);
	}
	
	/**
	 * 
	 * Calculate the set of nonterminals that are reachable via the alternates of A.
	 * In other words, if A is a nonterminal, reachable nonterminals are all the B's such as
	 * A =>* alpha B gamma. Note that this method does not calculate direct-nullable reachable
	 * nonterminals.
	 * 
	 */
	public Multimap<Nonterminal, Nonterminal> calculateReachabilityGraph() {
		
		Set<Nonterminal> nonterminals = definitions.keySet();
		
		boolean changed = true;
		
		while (changed) {
			
			changed = false;
			
			for (Nonterminal head : nonterminals) {
				reachabilityGraph.get(head);
				
				for (Rule rule : definitions.get(head)) {
					
					List<Symbol> alternate = rule.getBody();
					
					if (alternate == null) {
						continue;
					}
					
					for (Symbol symbol : alternate) {
						
						if (symbol instanceof Nonterminal) {
							changed = add(head, (Nonterminal) symbol, reachabilityGraph);
						} 
						else if (symbol instanceof Star) {
							Star star = (Star) symbol;
							if (star.getSymbol() instanceof Nonterminal)
								changed = add(head, (Nonterminal) star.getSymbol(), reachabilityGraph);
						} 
						else if (symbol instanceof Plus) {
							Plus plus = (Plus) symbol;
							if (plus.getSymbol() instanceof Nonterminal)
								changed = add(head, (Nonterminal) plus.getSymbol(), reachabilityGraph);
						}
						else if (symbol instanceof Sequence<?>) {
							Sequence<?> seq = (Sequence<?>) symbol;
							for (Symbol s : seq.getSymbols()) {
								if (s instanceof Nonterminal)
									changed = add(head, (Nonterminal)s, reachabilityGraph);
							}
						}
						else if (symbol instanceof Opt) {
							Opt opt = (Opt) symbol;
							if (opt.getSymbol() instanceof Nonterminal)
								changed = add(head, (Nonterminal) opt.getSymbol(), reachabilityGraph);
						}
						else if (symbol instanceof Alt) {
							Alt<?> alt = (Alt<?>) symbol;
							for (Symbol s : alt.getSymbols()) {
								if (s instanceof Nonterminal)
									changed = add(head, (Nonterminal) s, reachabilityGraph);
							}
						}
					}
				}
			}
		}
		
		return reachabilityGraph;
	}
	
	private static boolean add(Nonterminal a, Nonterminal nonterminal, SetMultimap<Nonterminal, Nonterminal> reachabilityGraph) {
		boolean changed = false;
		changed |= reachabilityGraph.put(a, nonterminal);
		changed |= reachabilityGraph.putAll(a, reachabilityGraph.get(nonterminal));
		return changed;
	}
	
}
