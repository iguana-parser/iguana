package org.jgll.grammar;

import static org.jgll.util.generator.GeneratorUtil.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.jgll.grammar.exception.GrammarValidationException;
import org.jgll.grammar.exception.NonterminalNotDefinedException;
import org.jgll.grammar.symbol.Epsilon;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.regex.RegularExpression;
import org.jgll.util.Configuration;
import org.jgll.util.Input;
import org.jgll.util.generator.ConstructorCode;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;


/**
 * 
 * @author Ali Afroozeh
 *
 */
public class Grammar implements ConstructorCode, Serializable {

	private static final long serialVersionUID = 1L;
	
	private final ListMultimap<Nonterminal, Rule> definitions;

	private final SetMultimap<Nonterminal, RegularExpression> firstSets;
	
	private final SetMultimap<Nonterminal, RegularExpression> followSets;
	
	private final Nonterminal layout;
	
	public Grammar(Builder builder) {
		this.definitions = builder.definitions;
		this.layout = builder.layout;
		// TODO: replace them with calculation
		this.firstSets = HashMultimap.create();
		this.followSets = HashMultimap.create();
	}
	
	public Multimap<Nonterminal, Rule> getDefinitions() {
		return definitions;
	}
	
	public List<Rule> getAlternatives(Nonterminal nonterminal) {
		return definitions.get(nonterminal);
	}
	
	public Set<Nonterminal> getNonterminals() {
		return definitions.keySet();
	}
	
	public List<Rule> getRules() {
		return new ArrayList<>(definitions.values());
	}
	
	public int sizeRules() {
		int num = 0;
		for(Nonterminal head : definitions.keySet()) {
			num += definitions.get(head).size();
		}
		return num;
	}
	
	public Set<RegularExpression> getFirstSet(Nonterminal nonterminal) {
		return firstSets.get(nonterminal);
	}
	
	public Set<RegularExpression> getFollowSet(Nonterminal nonterminal) {
		return followSets.get(nonterminal);
	}
	
	public Set<RegularExpression> getPredictionSet(Nonterminal nonterminal) {
		Set<RegularExpression> set = getFirstSet(nonterminal);
		if (set.contains(Epsilon.getInstance())) {
			set.addAll(getFollowSet(nonterminal));
		}
		return set;
	}
	
	public Set<RegularExpression> getPredictionSet(Rule rule, int index) {
		return null;
	}
	
	public boolean isNullable(Nonterminal nonterminal) {
		return firstSets.get(nonterminal).contains(Epsilon.getInstance());
	}
	
	public GrammarGraph toGrammarGraph(Input input, Configuration config) {
		return new GrammarGraph(this, input, config);
	}
	
	private static Set<RuntimeException> validate(ListMultimap<Nonterminal, Rule> definitions) {
		
		Set<RuntimeException> validationExceptions = new HashSet<>();
		
		for (Rule rule : definitions.values()) {
			if (rule.getBody() == null) continue; // Rewritten priority alternatives
			for (Symbol s : rule.getBody()) {
				if (s instanceof Nonterminal) {
					if (!definitions.containsKey(s)) {
						validationExceptions.add(new NonterminalNotDefinedException((Nonterminal) s));
					}						
				}
			}
		}
		
		return validationExceptions;
	}
	
	public Nonterminal getLayout() {
		return layout;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		for (Nonterminal nonterminal : definitions.keySet()) {
			sb.append(nonterminal).append(" ::= ");
			for (List<Symbol> alternatives : definitions.get(nonterminal).stream().map(r -> r.getBody()).collect(Collectors.toList())) {
				if (alternatives == null) continue;
				sb.append(listToString(alternatives)).append("\n");
			}
		}
		
		return sb.toString();
	}
	
	public String toStringWithOrderByPrecedence() {
		StringBuilder sb = new StringBuilder();
		
		for (Nonterminal nonterminal : definitions.keySet()) {
			sb.append(nonterminal).append(" ::= ");
			
			int precedence = -1;
			List<Rule> rules = definitions.get(nonterminal);
			
			boolean found = true;
			while(found) {
				found = false;
				for (Rule rule : rules) {
					if (rule.getPrecedence() == precedence) {
						found = true;
						sb.append(listToString(rule.getBody())).append(" {" + rule.getPrecedence() + "}" + "\n");
					}
				}
				precedence++;
			}
			
		}
		
		return sb.toString();
	}
	
	public Object getObject(Nonterminal nonterminal, int alternateIndex) {
		return definitions.get(nonterminal).get(alternateIndex).getObject();
	}
	
	public static Builder builder() {
		return new Builder();
	}
 	
	public static class Builder {
		
		private final ListMultimap<Nonterminal, Rule> definitions = ArrayListMultimap.create();
		private Nonterminal layout;
		
		public Grammar build() {
			Set<RuntimeException> exceptions = validate(definitions);
			
			if (!exceptions.isEmpty()) {
				throw new GrammarValidationException(exceptions);
			}
			
//			GrammarOperations op = new GrammarOperations(definitions);
//			return new Grammar(definitions, op.getFirstSets(), op.getFollowSets());
			
			return new Grammar(this);
		}
		
		public Builder addRule(Rule rule) {
			definitions.put(rule.getHead(), rule);
			return this;
		}
		
		public Builder addRules(Iterable<Rule> rules) {
			rules.forEach(r -> addRule(r));
			return this;
		}
		
		public Builder addRules(Rule...rules) {
			addRules(Arrays.asList(rules));
			return this;
		}
		
		public Builder setLayout(Nonterminal layout) {
			this.layout = layout;
			return this;
		}
		
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		
		if (!(obj instanceof Grammar))
			return false;
		
		Grammar other = (Grammar) obj;
		
		return definitions.equals(other.definitions);
	}
	
	/**
	 * Returns the size of this grammar, which is equal to the number of nonterminals +
	 * number of terminals + grammar slots.
	 * 
	 */
	public int size() {
		return  definitions.size() +
				(int) definitions.values().stream().filter(r -> r.getBody() != null).flatMap(r -> r.getBody().stream()).filter(s -> s instanceof RegularExpression).count() +
				definitions.values().stream().map(r -> r.size() + 1).reduce(0, (a, b) -> a + b);
	}

	@Override
	public String getConstructorCode() {
		return "Grammar.builder()\n" +
			   (layout == null ? "" : ".setLayout(" + layout.getConstructorCode() + ")") +
			   rulesToString(definitions.values()) + "\n.build()";
	}
	
	private static String rulesToString(Iterable<Rule> rules) {
		return StreamSupport.stream(rules.spliterator(), false)
				.map(r -> "\n// " + r.toString() + "\n.addRule(" + r.getConstructorCode() + ")")
				.collect(Collectors.joining());
	}
	
}
