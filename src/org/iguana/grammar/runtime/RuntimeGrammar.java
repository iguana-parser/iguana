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

package org.iguana.grammar.runtime;

import org.iguana.datadependent.ast.Expression;
import org.iguana.grammar.exception.GrammarValidationException;
import org.iguana.grammar.exception.NonterminalNotDefinedException;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Start;
import org.iguana.grammar.symbol.Symbol;
import org.iguana.regex.RegularExpression;
import org.iguana.traversal.idea.IdeaIDEGenerator;

import java.io.*;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.iguana.utils.string.StringUtil.listToString;


/**
 * 
 * @author Ali Afroozeh
 * @author Anastasia Izmaylova
 *
 */
public class RuntimeGrammar {

	private final Map<Nonterminal, List<RuntimeRule>> definitions;

	private final Symbol layout;
	
	private final List<RuntimeRule> rules;

	private final Map<String, RegularExpression> regularExpressionDefinitions;
	private final Map<String, RegularExpression> literals;

	private final Map<String, Set<String>> ebnfLefts;
	private final Map<String, Set<String>> ebnfRights;

	private final Start startSymbol;

	private final Map<String, Expression> globals;

	private final String name;

	public Builder copy() {
		return new Builder(this);
	}

	public RuntimeGrammar(Builder builder) {
		this.definitions = builder.definitions;
		this.layout = builder.layout;
		this.startSymbol = builder.startSymbol;
		this.rules = builder.rules;
		this.ebnfLefts = builder.ebnfLefts;
		this.ebnfRights = builder.ebnfRights;
		this.regularExpressionDefinitions = builder.regularExpressionDefinitions;
		this.literals = builder.literals;
		this.globals = builder.globals;
		this.name = builder.name;
	}
	
	public Map<Nonterminal, List<RuntimeRule>> getDefinitions() {
		return definitions;
	}
	
	public List<RuntimeRule> getAlternatives(Nonterminal nonterminal) {
		return definitions.get(nonterminal);
	}
	
	public Set<Nonterminal> getNonterminals() {
		return definitions.keySet();
	}
	
	public List<RuntimeRule> getRules() {
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
	
	public Map<String, RegularExpression> getRegularExpressionDefinitions() {
		return regularExpressionDefinitions;
	}

	public Map<String, RegularExpression> getLiterals() {
		return literals;
	}

	private static Set<RuntimeException> validate(List<RuntimeRule> rules, Map<Nonterminal, List<RuntimeRule>> definitions) {
	    Set<RuntimeException> exceptions = new HashSet<>();
        for (RuntimeRule rule : rules) {
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

	public Map<String, Expression> getGlobals() {
		return globals;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		for (Nonterminal nonterminal : definitions.keySet()) {
			sb.append(nonterminal).append(" = ");
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
			List<RuntimeRule> rules = definitions.get(nonterminal);
			
			boolean found = true;
			while(found) {
				found = false;
				for (RuntimeRule rule : rules) {
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
		
		private final Map<Nonterminal, List<RuntimeRule>> definitions = new LinkedHashMap<>();
		private final List<RuntimeRule> rules = new ArrayList<>();
		public String name;
		private Symbol layout;
		private Start startSymbol;
		private Map<String, RegularExpression> regularExpressionDefinitions;
		private Map<String, RegularExpression> literals;

		private Map<String, Set<String>> ebnfLefts = new HashMap<>();
		private Map<String, Set<String>> ebnfRights = new HashMap<>();
		private Map<String, Expression> globals = new HashMap<>();

        public Builder() { }

        public Builder(RuntimeGrammar grammar) {
            definitions.putAll(grammar.definitions);
            rules.addAll(grammar.rules);
            layout = grammar.layout;
            ebnfLefts.putAll(grammar.ebnfLefts);
            ebnfRights.putAll(grammar.ebnfRights);
            startSymbol = grammar.startSymbol;
			regularExpressionDefinitions = grammar.getRegularExpressionDefinitions();
			globals = grammar.globals;
			name = grammar.name;
        }
		
		public RuntimeGrammar build() {
			if (definitions.isEmpty()) {
				for (RuntimeRule rule : rules) {
					definitions.computeIfAbsent(rule.getHead(), k -> new ArrayList<>()).add(rule);
				}
			}
			Set<RuntimeException> exceptions = validate(rules, definitions);
			
			if (!exceptions.isEmpty()) {
				throw new GrammarValidationException(exceptions);
			}

            return new RuntimeGrammar(this);
		}

        public Builder addRule(RuntimeRule rule) {
			List<RuntimeRule> rules = definitions.get(rule.getHead());
			if (rules == null) {
				rules = new ArrayList<>();
				definitions.put(rule.getHead(), rules);
			}
			rules.add(rule);
			this.rules.add(rule);
			return this;
		}
		
		public Builder addRules(Iterable<RuntimeRule> rules) {
			rules.forEach(r -> addRule(r));
			return this;
		}
		
		public Builder addRules(RuntimeRule...rules) {
			addRules(Arrays.asList(rules));
			return this;
		}
		
		public Builder setLayout(Symbol layout) {
			this.layout = layout;
			return this;
		}

		public Builder setName(String name) {
			this.name = name;
			return this;
		}

		public Builder setStartSymbol(Start startSymbol) {
		    this.startSymbol = startSymbol;
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

		public Builder setEbnfLefts(Map<String, Set<String>> ebnfLefts) {
			this.ebnfLefts = ebnfLefts;
			return this;
		}

		public Builder setEbnfRights(Map<String, Set<String>> ebnfRights) {
			this.ebnfRights = ebnfRights;
			return this;
		}

		public Builder setRegularExpressionDefinitions(Map<String, RegularExpression> regularExpressionDefinitions) {
			this.regularExpressionDefinitions = regularExpressionDefinitions;
			return this;
		}

		public Builder setLiterals(Map<String, RegularExpression> literals) {
			this.literals = literals;
			return this;
		}

		public Builder setGlobals(Map<String, Expression> globals) {
			this.globals = globals;
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

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (!(obj instanceof RuntimeGrammar))
			return false;
		
		RuntimeGrammar other = (RuntimeGrammar) obj;

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

	private static String rulesToString(Iterable<RuntimeRule> rules) {
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
