package org.jgll.grammar.transformation;

import java.util.ArrayList;
import java.util.List;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.symbol.Alt;
import org.jgll.grammar.symbol.Epsilon;
import org.jgll.grammar.symbol.Group;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.util.trie.Edge;
import org.jgll.util.trie.Node;
import org.jgll.util.trie.Trie;

public class LeftFactorize implements GrammarTransformation {
	
	public Grammar transform(Grammar grammar) {
		
		Grammar.Builder builder = new Grammar.Builder();
		
		for (Nonterminal nonterminal : grammar.getNonterminals()) {
		
			Trie<Symbol> trie = new Trie<>();

			Node<Symbol> node = trie.getRoot();
			for (Rule rule : grammar.getAlternatives(nonterminal)) {
				List<Symbol> alternative = rule.getBody();
				node = trie.getRoot();
				for (Symbol s : alternative) {
					node = trie.add(node, s);
				}
				trie.add(node, Epsilon.getInstance());
			}

			builder.addRule(Rule.withHead(nonterminal).addSymbols(retrieve(trie.getRoot())).build());
		}
		
		return builder.build();
	}
	
	private static Symbol retrieve(Node<Symbol> node) {
		
		if (node.size() == 0) return null;
		
		if (node.size() == 1 && node.getEdges().get(0).getLabel() == Epsilon.getInstance()) return null;

		List<Symbol> outer = new ArrayList<>();
		
		for (Edge<Symbol> edge : node.getEdges()) {
			List<Symbol> inner = new ArrayList<>();
			inner.add(edge.getLabel());
			Symbol next = retrieve(edge.getDestination());
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
			return Alt.from(outer);
		}
	}
	
	@Override
	public Iterable<Rule> transform(Iterable<Rule> rules) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<Rule> transform(Rule rule) {
		// TODO Auto-generated method stub
		return null;
	}
}
