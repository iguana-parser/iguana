package org.iguana.grammar;

import static org.iguana.util.generator.GeneratorUtil.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.iguana.grammar.exception.GrammarValidationException;
import org.iguana.grammar.exception.NonterminalNotDefinedException;
import org.iguana.grammar.patterns.ExceptPattern;
import org.iguana.grammar.patterns.PrecedencePattern;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.symbol.Symbol;
import org.iguana.regex.RegularExpression;
import org.iguana.util.Configuration;
import org.iguana.util.Input;
import org.iguana.util.generator.ConstructorCode;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;


/**
 * 
 * @author Ali Afroozeh
 *
 */
public class Grammar implements ConstructorCode, Serializable {

	private static final long serialVersionUID = 1L;
	
	private final ListMultimap<Nonterminal, Rule> definitions;
	
	private final List<PrecedencePattern> precedencePatterns;
	
	private final List<ExceptPattern> exceptPatterns;
	
	private final Nonterminal layout;
		
	public Grammar(Builder builder) {
		this.definitions = builder.definitions;
		this.precedencePatterns = builder.precedencePatterns;
		this.exceptPatterns = builder.exceptPatterns;
		this.layout = builder.layout;
	}
	
	public ListMultimap<Nonterminal, Rule> getDefinitions() {
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
	
	public List<PrecedencePattern> getPrecedencePatterns() {
		return precedencePatterns;
	}
	
	public List<ExceptPattern> getExceptPatterns() {
		return exceptPatterns;
	}
	
	public Set<RegularExpression> getPredictionSet(Rule rule, int index) {
		return null;
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
				
				if (precedence == 0) {
					found = true;
					precedence++;
				} else precedence++;
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
		private final List<PrecedencePattern> precedencePatterns = new ArrayList<>();
		private final List<ExceptPattern> exceptPatterns = new ArrayList<>();
		private Nonterminal layout;
		
		public Grammar build() {
			Set<RuntimeException> exceptions = validate(definitions);
			
			if (!exceptions.isEmpty()) {
				throw new GrammarValidationException(exceptions);
			}
			
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
		
		public Builder addPrecedencePattern(PrecedencePattern pattern) {
			precedencePatterns.add(pattern);
			return this;
		}
		
		public Builder addExceptPattern(ExceptPattern pattern) {
			exceptPatterns.add(pattern);
			return this;
		}
	}
	
	public void save(URI uri) {
		save(new File(uri));
	}
	
	public void save(File file) {
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
		try (ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
			out.writeObject(this);			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Grammar load(URI uri) {
		return load(new File(uri));
	}
	
	public static Grammar load(File file) {
		Grammar grammar;
		try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)))) {
			grammar = (Grammar) in.readObject();
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		return grammar;
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
