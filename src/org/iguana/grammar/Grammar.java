/*
 * Copyright (c) 2015, Ali Afroozeh and Anastasia Izmaylova, Centrum Wiskunde & Informatica (CWI)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this 
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this 
 *    list of conditions and the following disclaimer in the documentation and/or 
 *    other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT 
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
 * OF SUCH DAMAGE.
 *
 */

package org.iguana.grammar;

import iguana.regex.RegularExpression;
import iguana.utils.collections.CollectionsUtil;
import org.iguana.grammar.exception.GrammarValidationException;
import org.iguana.grammar.exception.NonterminalNotDefinedException;
import org.iguana.grammar.patterns.ExceptPattern;
import org.iguana.grammar.patterns.PrecedencePattern;
import org.iguana.grammar.slot.NonterminalNodeType;
import org.iguana.grammar.symbol.*;
import org.iguana.traversal.idea.IdeaIDEGenerator;
import org.iguana.util.serialization.JsonSerializer;

import java.io.*;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static iguana.utils.string.StringUtil.listToString;


/**
 * 
 * @author Ali Afroozeh
 * @author Anastasia Izmaylova
 *
 */
public class Grammar implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final Map<Nonterminal, List<Rule>> definitions;
	
	private final List<PrecedencePattern> precedencePatterns;
	
	private final List<ExceptPattern> exceptPatterns;
	
	private final Symbol layout;
	
	private final List<Rule> rules;
	
	private final Map<String, Set<String>> ebnfLefts;
	private final Map<String, Set<String>> ebnfRights;

	private final Start startSymbol;
		
	public Grammar(Builder builder) {
		this.definitions = builder.definitions;
		this.precedencePatterns = builder.precedencePatterns;
		this.exceptPatterns = builder.exceptPatterns;
		this.layout = builder.layout;
		this.startSymbol = builder.startSymbol;
		this.rules = builder.rules;
		this.ebnfLefts = builder.ebnfLefts;
		this.ebnfRights = builder.ebnfRights;
	}
	
	public Map<Nonterminal, List<Rule>> getDefinitions() {
		return definitions;
	}
	
	public List<Rule> getAlternatives(Nonterminal nonterminal) {
		return definitions.get(nonterminal);
	}
	
	public Set<Nonterminal> getNonterminals() {
		return definitions.keySet();
	}
	
	public List<Rule> getRules() {
		return rules;
	}
	
	public Map<String, Set<String>> getEBNFLefts() {
		return this.ebnfLefts;
	}
	
	public Map<String, Set<String>> getEBNFRights() {
		return this.ebnfRights;
	}

    public Start getStartSymbol() {
        return startSymbol;
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
	
	private static Set<RuntimeException> validate(List<Rule> rules, Map<Nonterminal, List<Rule>> definitions) {
	    Set<RuntimeException> exceptions = new HashSet<>();
        for (Rule rule : rules) {
            if (rule.getBody() != null) {
                for (Symbol s : rule.getBody()) {
                    if (s instanceof Nonterminal && !definitions.containsKey(s)) {
                        exceptions.add(new NonterminalNotDefinedException((Nonterminal) s));
                    }
                }
            }
        }
        return exceptions;
	}
	
	public Symbol getLayout() {
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

	public static Builder builder() {
		return new Builder();
	}
 	
	public static class Builder {
		
		private final Map<Nonterminal, List<Rule>> definitions = new HashMap<>();
		private final List<PrecedencePattern> precedencePatterns = new ArrayList<>();
		private final List<ExceptPattern> exceptPatterns = new ArrayList<>();
		private List<Rule> rules = new ArrayList<>();
		private Symbol layout;
		private Start startSymbol;
		
		private Map<String, Set<String>> ebnfLefts = new HashMap<>();
		private Map<String, Set<String>> ebnfRights = new HashMap<>();

        public Builder() { }

        public Builder(Grammar grammar) {
            definitions.putAll(grammar.definitions);
            precedencePatterns.addAll(grammar.precedencePatterns);
            exceptPatterns.addAll(grammar.exceptPatterns);
            rules.addAll(grammar.rules);
            layout = grammar.layout;
            ebnfLefts.putAll(grammar.ebnfLefts);
            ebnfRights.putAll(grammar.ebnfRights);
            startSymbol = grammar.startSymbol;
        }
		
		public Grammar build() {
			Set<RuntimeException> exceptions = validate(rules, definitions);
			
			if (!exceptions.isEmpty()) {
				throw new GrammarValidationException(exceptions);
			}

            return new Grammar(this);
		}

        public Builder addRule(Rule rule) {
			List<Rule> rules = definitions.get(rule.getHead());
			if (rules == null) {
				rules = new ArrayList<>();
				definitions.put(rule.getHead(), rules);
			}
			rules.add(rule);
			this.rules.add(rule);
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
		
		public Builder setLayout(Symbol layout) {
			this.layout = layout;
			return this;
		}

		public Builder setStartSymbol(Start startSymbol) {
		    this.startSymbol = startSymbol;
		    return this;
        }
		
		public Builder addPrecedencePattern(PrecedencePattern pattern) {
			precedencePatterns.add(pattern);
			return this;
		}
		
		public Builder addPrecedencePatterns(Collection<PrecedencePattern> patterns) {
			precedencePatterns.addAll(patterns);
			return this;
		}		
		
		public Builder addExceptPattern(ExceptPattern pattern) {
			exceptPatterns.add(pattern);
			return this;
		}
		
		public Builder addExceptPatterns(Collection<ExceptPattern> patterns) {
			exceptPatterns.addAll(patterns);
			return this;
		}
		
		public Builder addEBNFl(Map<String, Set<String>> ebnfLefts) {
			this.ebnfLefts.putAll(ebnfLefts);
			return this;
		}
		
		public Builder addEBNFl(String ebnf, Set<String> lefts) {
			this.ebnfLefts.put(ebnf, lefts);
			return this;
		}
		
		public Builder addEBNFr(Map<String, Set<String>> ebnfRights) {
			this.ebnfRights.putAll(ebnfRights);
			return this;
		}
		
		public Builder addEBNFr(String ebnf, Set<String> rights) {
			this.ebnfRights.put(ebnf, rights);
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

	public static Grammar load(URI uri, String format) throws FileNotFoundException {
		return load(new File(uri), format);
	}

	public static Grammar load(String path, String format) throws FileNotFoundException {
		return load(new File(path), format);
	}

	public static Grammar load(File file) throws FileNotFoundException {
		return load(new FileInputStream(file), "binary");
	}

    public static Grammar load(File file, String format) throws FileNotFoundException {
		return load(new FileInputStream(file), format);
    }

	public static Grammar load(InputStream inputStream) {
		return load(inputStream, "binary");
	}

	public static Grammar load(InputStream inputStream, String format) {
		Grammar grammar;
		switch (format) {
			case "binary":
				try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(inputStream))) {
					grammar = (Grammar) in.readObject();
				} catch (IOException | ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
				break;

			case "json":
				try {
					grammar = JsonSerializer.deserialize(inputStream, Grammar.class);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				break;

			default:
				throw new RuntimeException("Unsupported format exception");
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

	@Override
	public int hashCode() {
		return definitions.hashCode();
	}

	/*
	 * Returns the size of this grammar, which is equal to the number of nonterminals +
	 * number of terminals + grammar slots.
	 */
	public int size() {
		int heads = definitions.size();
		int bodySymbols = definitions.values().stream()
											  .flatMap(l -> l.stream())
											  .filter(r -> r.getBody() != null)
											  .mapToInt(r -> r.size())
											  .sum();
		return heads + bodySymbols;
	}

	private static String rulesToString(Iterable<Rule> rules) {
		return StreamSupport.stream(rules.spliterator(), false)
				.map(r -> "\n// " + r.toString() + "\n.addRule(" + r + ")")
				.collect(Collectors.joining());
	}
	
	private static String leftsToString(Map<String, Set<String>> lefts) {
		return lefts.entrySet().stream()
				.map(entry -> ".addEBNFl(" + "\"" + entry.getKey() + "\"," 
							  + "new HashSet<String>(Arrays.asList(" 
							  		+ listToString(entry.getValue().stream().map(elem -> "\"" + elem + "\"").collect(Collectors.toList()), ",") 
							  + ")))")
				.collect(Collectors.joining());
	}
	
	private static String rightsToString(Map<String, Set<String>> rights) {
		return rights.entrySet().stream()
				.map(entry -> ".addEBNFr(" + "\"" + entry.getKey() + "\"," 
							  + "new HashSet<String>(Arrays.asList(" 
							  		+ listToString(entry.getValue().stream().map(elem -> "\"" + elem + "\"").collect(Collectors.toList()), ",") 
							  + ")))")
				.collect(Collectors.joining());
	}

	public void generate_idea_ide(String language, String extendsion, String path) {
        new IdeaIDEGenerator().generate(this, language, "iggy", path);
    }

}
