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

import iguana.regex.CharRange;
import iguana.regex.Epsilon;
import iguana.regex.RegularExpression;
import iguana.regex.matcher.DFAMatcherFactory;
import iguana.regex.matcher.MatcherFactory;
import iguana.utils.input.Input;
import org.iguana.datadependent.ast.Expression;
import org.iguana.datadependent.ast.Statement;
import org.iguana.datadependent.env.GLLEvaluator;
import org.iguana.datadependent.env.IEvaluatorContext;
import org.iguana.grammar.condition.Condition;
import org.iguana.grammar.condition.Conditions;
import org.iguana.grammar.condition.ConditionsFactory;
import org.iguana.grammar.exception.IncorrectNumberOfArgumentsException;
import org.iguana.grammar.operations.FirstFollowSets;
import org.iguana.grammar.slot.*;
import org.iguana.grammar.slot.EpsilonTransition.Type;
import org.iguana.grammar.slot.lookahead.FollowTest;
import org.iguana.grammar.slot.lookahead.LookAheadTest;
import org.iguana.grammar.slot.lookahead.RangeTreeFollowTest;
import org.iguana.grammar.slot.lookahead.RangeTreeLookaheadTest;
import org.iguana.grammar.symbol.*;
import org.iguana.grammar.symbol.Terminal.Category;
import org.iguana.grammar.transformation.VarToInt;
import org.iguana.parser.ParserRuntime;
import org.iguana.parser.ParserRuntimeImpl;
import org.iguana.parser.gss.lookup.ArrayNodeLookup;
import org.iguana.parser.gss.lookup.GSSNodeLookup;
import org.iguana.parser.gss.lookup.IntOpenAddressingMap;
import org.iguana.parser.gss.lookup.JavaHashMapNodeLookup;
import org.iguana.util.Configuration;
import org.iguana.util.Configuration.EnvironmentImpl;
import org.iguana.util.Configuration.HashMapImpl;
import org.iguana.util.Configuration.LookupImpl;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static iguana.regex.CharacterRanges.toNonOverlapping2;
import static iguana.regex.CharacterRanges.toNonOverlappingSet;

public class GrammarGraph implements Serializable {

	private static final long serialVersionUID = 1L;

	Map<Nonterminal, NonterminalGrammarSlot> nonterminalsMap;
	
	Map<Terminal, TerminalGrammarSlot> terminalsMap;
	
	private final Map<String, GrammarSlot> names;
	
	private List<GrammarSlot> slots;
	
	private FirstFollowSets firstFollow;
	
	Grammar grammar;

	private Configuration config;

	private Input input;
	
	private final Map<Integer, Map<String, Integer>> mapping;

	private Map<String, Integer> current;
	
	private MatcherFactory matcherFactory;
	
	private TerminalGrammarSlot epsilonSlot;

    private ParserRuntime runtime;

    public static GrammarGraph from(Grammar grammar, Input input) {
        return from(grammar, input, Configuration.load());
    }

    public static GrammarGraph from(Grammar grammar, Input input, Configuration config) {
        IEvaluatorContext ctx = GLLEvaluator.getEvaluatorContext(config, input);
        GrammarGraph grammarGraph = new GrammarGraph(grammar, input, config);
        ParserRuntime runtime = new ParserRuntimeImpl(grammarGraph, config, ctx);
        grammarGraph.convert(runtime);
        return grammarGraph;
    }

    private void convert(ParserRuntime runtime) {
        matcherFactory = new DFAMatcherFactory();

        this.runtime = runtime;
        this.firstFollow = new FirstFollowSets(this.grammar);

        epsilonSlot = new TerminalGrammarSlot(Terminal.from(Epsilon.getInstance()), matcherFactory, runtime);

        terminalsMap.put(Terminal.from(Epsilon.getInstance()), epsilonSlot);

        add(epsilonSlot);

        Set<Nonterminal> nonterminals = this.grammar.getNonterminals();
        nonterminals.forEach(n -> getNonterminalSlot(n));

        int i = 0;
        for (Rule r : this.grammar.getRules()) {
            current = mapping.get(i);
            convert(r);
            i++;
        }

        nonterminals.forEach(n -> setFirstFollowTests(n));
    }
	
	private GrammarGraph(Grammar grammar, Input input, Configuration config) {
		if (config.getEnvImpl() == EnvironmentImpl.ARRAY) {
			VarToInt transformer = new VarToInt();
			this.grammar = transformer.transform(grammar);
			// System.out.println(this.grammar);
			this.mapping = transformer.getMapping();
		} else {
			this.grammar = grammar;
			this.mapping = new HashMap<>();
		}
		this.input = input;
		this.config = config;
		this.nonterminalsMap = new LinkedHashMap<>();
		this.terminalsMap = new LinkedHashMap<>();
		this.names = new HashMap<>();
		this.slots = new ArrayList<>();
	}
	
	public NonterminalGrammarSlot getHead(Nonterminal start) {
		return nonterminalsMap.get(start);
	}	
	
	public TerminalGrammarSlot getTerminalSlot(RegularExpression regex) {
		return terminalsMap.get(regex);
	}

	public GrammarSlot getSlot(String s) {
		return names.get(s);
	}
	
	public Collection<NonterminalGrammarSlot> getNonterminals() {
		return nonterminalsMap.values();
	}
	
	private void convert(Rule rule) {
		Nonterminal nonterminal = rule.getHead();
		NonterminalGrammarSlot nonterminalSlot = getNonterminalSlot(nonterminal);
		addRule(nonterminalSlot, rule);
	}
	
	private void setFirstFollowTests(Nonterminal nonterminal) {
		NonterminalGrammarSlot nonterminalSlot = getNonterminalSlot(nonterminal);
		nonterminalSlot.setLookAheadTest(getLookAheadTest(nonterminal, nonterminalSlot));
		nonterminalSlot.setFollowTest(getFollowTest(nonterminal));
	}

	private LookAheadTest getLookAheadTest(Nonterminal nonterminal, NonterminalGrammarSlot nonterminalSlot) {
		if (config.getLookAheadCount() == 0)
			return i -> nonterminalSlot.getFirstSlots();
		
		Map<CharRange, List<BodyGrammarSlot>> map = new HashMap<>();
		
		List<Rule> alternatives = grammar.getAlternatives(nonterminal);
		
		for (int i = 0; i < alternatives.size(); i++) {
			Rule rule = alternatives.get(i);
			BodyGrammarSlot firstSlot = nonterminalSlot.getFirstSlots().get(i);
			Set<CharRange> set = firstFollow.getPredictionSet(rule, 0);
			set.forEach(cr -> map.computeIfAbsent(cr, k -> new ArrayList<>()).add(firstSlot));			
		}
		
		// A map from non-overlapping ranges to a list of original ranges
		Map<CharRange, List<CharRange>> rangeMap = toNonOverlapping2(map.keySet());
		
		// A map from non-overlapping ranges to a list associated body grammar slots
		Map<CharRange, List<BodyGrammarSlot>> nonOverlappingMap = new HashMap<>();
		
		// compute a list of body grammar slots from a non-overlapping range
		Function<CharRange, Set<BodyGrammarSlot>> f = r -> rangeMap.get(r).stream().flatMap(range -> map.get(range).stream()).collect(Collectors.toCollection(LinkedHashSet::new));
		
		rangeMap.keySet().forEach(r -> nonOverlappingMap.computeIfAbsent(r, range -> new ArrayList<>()).addAll(f.apply(r))); 
		
		return new RangeTreeLookaheadTest(nonOverlappingMap);
	}
	
	private FollowTest getFollowTest(Nonterminal nonterminal) {
		
		if (config.getLookAheadCount() == 0)
			return FollowTest.DEFAULT;
		
		// TODO: move toNonOverlapping to first follow itself
		Set<CharRange> followSet = toNonOverlappingSet(firstFollow.getFollowSet(nonterminal));
		
		return new RangeTreeFollowTest(followSet);
	}
	
	private FollowTest getFollowTest(Rule rule, int i) {
		if (config.getLookAheadCount() == 0)
			return FollowTest.DEFAULT;

		Set<CharRange> set = toNonOverlappingSet(firstFollow.getPredictionSet(rule, i));
		
		return new RangeTreeFollowTest(set);
	}
	
	private void addRule(NonterminalGrammarSlot head, Rule rule) {
		
		BodyGrammarSlot firstSlot = getFirstGrammarSlot(rule, head);
		head.addFirstSlot(firstSlot);	
		BodyGrammarSlot currentSlot = firstSlot;
		
		GrammarGraphSymbolVisitor rule2graph = new GrammarGraphSymbolVisitor(head, rule, currentSlot);
		
		while (rule2graph.hasNext()) 
			rule2graph.nextSymbol();
	}

    public ParserRuntime getRuntime() {
        return runtime;
    }

    private class GrammarGraphSymbolVisitor extends  AbstractGrammarGraphSymbolVisitor<Void> {
		
		private final NonterminalGrammarSlot head;
		private final Rule rule;
		
		private BodyGrammarSlot currentSlot;
		private int i = 0;
		
		private int j = -1;
		
		public GrammarGraphSymbolVisitor(NonterminalGrammarSlot head, Rule rule, BodyGrammarSlot currentSlot) {
			this.head = head;
			this.rule = rule;
			this.currentSlot = currentSlot;
		}
		
		public boolean hasNext() {
			return i < rule.size();
		}
		
		public void nextSymbol() {
			j = -1;
			visitSymbol(rule.symbolAt(i));
			i++;
		}
		
		public Void visit(Nonterminal symbol) {
			
			NonterminalGrammarSlot nonterminalSlot = getNonterminalSlot(symbol);
			
			BodyGrammarSlot slot;
			if (i == rule.size() - 1 && j == -1)
				slot = getEndSlot(rule, i + 1, rule.getPosition(i + 1), head, symbol.getLabel(), symbol.getVariable(), symbol.getState());
			else
				slot = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, symbol.getLabel(), symbol.getVariable(), symbol.getState());
			
			Expression[] arguments = symbol.getArguments();
			
			validateNumberOfArguments(nonterminalSlot.getNonterminal(), arguments);
			
			Set<Condition> preConditions = (i == 0 && j == -1)? new HashSet<>() : symbol.getPreConditions();
			currentSlot.addTransition(new NonterminalTransition(nonterminalSlot, currentSlot, slot, arguments, getConditions(preConditions), runtime));
			currentSlot = slot;
			
			return null;
		}
		
		@Override
		public Void visit(Conditional symbol) {
			
			Symbol sym = symbol.getSymbol();
			Expression expression = symbol.getExpression();
			
			visitSymbol(sym);
			
			BodyGrammarSlot thenSlot = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null, null);
			currentSlot.addTransition(new ConditionalTransition(expression, currentSlot, thenSlot, runtime));
			currentSlot = thenSlot;
			
			return null;
		}
		
		@Override
		public Void visit(Code symbol) {
			
			Symbol sym = symbol.getSymbol();
			Statement[] statements = symbol.getStatements();
			
			visitSymbol(sym);
			
			BodyGrammarSlot done = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null, null);
			currentSlot.addTransition(new CodeTransition(statements, currentSlot, done, runtime));
			currentSlot = done;
			
			return null;
		}
				
		public Void visit(Return symbol) {
			BodyGrammarSlot done;
			if (i != rule.size() - 1)
				throw new RuntimeException("Return symbol can only be used at the end of a grammar rule!");
			else {
				if (rule.size() == 1)
					done = new EpsilonGrammarSlot(rule.getPosition(i + 1), head, epsilonSlot, ConditionsFactory.DEFAULT, runtime);
				else
					done = getEndSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null, null);
			}
			
			currentSlot.addTransition(new ReturnTransition(symbol.getExpression(), currentSlot, done, runtime));
			currentSlot = done;
			
			return null;
		}

        @Override
        public Void visit(Terminal symbol) {
            TerminalGrammarSlot terminalSlot;

            if (symbol.getCategory() == Category.Regex) {
                terminalSlot = getTerminalGrammarSlot(symbol, symbol.getName());
            } else {
                terminalSlot = getTerminalGrammarSlot(symbol, null);
            }

            BodyGrammarSlot slot;

            if (i == rule.size() - 1 && j == -1)
                slot = getEndSlot(rule, i + 1, rule.getPosition(i + 1), head, symbol.getLabel(), null, null);
            else
                slot = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, symbol.getLabel(), null, null);

            Set<Condition> preConditions = (i == 0 && j == -1)? new HashSet<>() : symbol.getPreConditions();
            currentSlot.addTransition(getTerminalTransition(rule, i + 1, terminalSlot, currentSlot, slot, preConditions, symbol.getPostConditions()));
            currentSlot = slot;

            return null;
        }
		
		/**
		 *  Introduces epsilon transitions to handle labels and preconditions/postconditions
		 */
		private void visitSymbol(Symbol symbol) {
			
			if (symbol instanceof Nonterminal || symbol instanceof Terminal || symbol instanceof Return) { // TODO: I think this can be unified
				symbol.accept(this);
				return;
			}
			
			Conditions preconditions = i == 0? ConditionsFactory.DEFAULT : getConditions(symbol.getPreConditions());
			
			if (symbol.getLabel() != null) {
				BodyGrammarSlot declared = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null, null);
				currentSlot.addTransition(new EpsilonTransition(Type.DECLARE_LABEL, symbol.getLabel(), preconditions, currentSlot, declared, runtime));
				currentSlot = declared;
			} else {
				BodyGrammarSlot checked = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null, null);
				currentSlot.addTransition(new EpsilonTransition(preconditions, currentSlot, checked, runtime));
				currentSlot = checked;
			}
			
			j += 1;
			
			symbol.accept(this);
			
			j -= 1;
			
			if (symbol.getLabel() != null) {
				
				BodyGrammarSlot stored;
				if (i == rule.size() - 1 && j == -1)
					stored = getEndSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null, null);
				else
					stored = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null, null);
				
				currentSlot.addTransition(new EpsilonTransition(Type.STORE_LABEL, symbol.getLabel(), getConditions(symbol.getPostConditions()), currentSlot, stored, runtime));
				currentSlot = stored;
			} else {
				
				BodyGrammarSlot checked;
				if (i == rule.size() - 1 && j == -1)
					checked = getEndSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null, null);
				else
					checked = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null, null);
				
				currentSlot.addTransition(new EpsilonTransition(getConditions(symbol.getPostConditions()), currentSlot, checked, runtime));
				currentSlot = checked;
			}
		}
		
	}
	
	private TerminalTransition getTerminalTransition(Rule rule, int i, TerminalGrammarSlot slot, 
															 BodyGrammarSlot origin, BodyGrammarSlot dest,
															 Set<Condition> preConditions, Set<Condition> postConditions) {
		
		return new TerminalTransition(slot, origin, dest, getConditions(preConditions), getConditions(postConditions), runtime);
	}
	
	private TerminalGrammarSlot getTerminalGrammarSlot(Terminal t, String name) {
        return terminalsMap.computeIfAbsent(t, k -> {
            TerminalGrammarSlot terminalSlot = new TerminalGrammarSlot(t, matcherFactory, name, runtime);
            add(terminalSlot);
            return terminalSlot;
        });
	}
	
	private NonterminalGrammarSlot getNonterminalSlot(Nonterminal nonterminal) {
		return nonterminalsMap.computeIfAbsent(nonterminal, k -> {
			NonterminalGrammarSlot ntSlot;
			ntSlot = new NonterminalGrammarSlot(nonterminal, getNodeLookup(), nonterminal.getNodeType(), runtime);
			add(ntSlot);
			return ntSlot;
		});
	}
	
	private BodyGrammarSlot getFirstGrammarSlot(Rule rule,  NonterminalGrammarSlot nonterminal) {
		BodyGrammarSlot slot;
		
		if (rule.size() == 0) {
			slot = new EpsilonGrammarSlot(rule.getPosition(0,0), nonterminal, epsilonSlot, ConditionsFactory.DEFAULT, runtime);
		} else {
			// TODO: This is not a final solution; in particular, 
			//       not any precondition of the first symbol (due to labels) can currently be moved to the first slot.  
			Set<Condition> preConditions = new HashSet<>();
			preConditions.addAll(rule.symbolAt(0).getPreConditions());
			 
			slot = new BodyGrammarSlot(rule.getPosition(0,0), rule.symbolAt(0).getLabel(), null, null, getConditions(preConditions), runtime);
		}
		add(slot);
		return slot;
	}
	
	private BodyGrammarSlot getBodyGrammarSlot(Rule rule, int i, Position position, NonterminalGrammarSlot nonterminal, String label, String variable, Set<String> state) {
		assert i < rule.size();
		
		BodyGrammarSlot slot;
		if (current != null)
			slot = new BodyGrammarSlot(position, label, (label != null && !label.isEmpty())? current.get(label) : -1,
									   variable, (variable != null && !variable.isEmpty())? current.get(variable) : -1, state, getConditions(rule.symbolAt(i - 1).getPostConditions()), runtime);
		else
			slot = new BodyGrammarSlot(position, label, variable, state, getConditions(rule.symbolAt(i - 1).getPostConditions()), runtime);
		
		add(slot);
		slot.setFollowTest(getFollowTest(rule, i));
		return slot;
	}
	
	private BodyGrammarSlot getEndSlot(Rule rule, int i, Position position, NonterminalGrammarSlot nonterminal, String label, String variable, Set<String> state) {
		assert i == rule.size();
		
		BodyGrammarSlot slot;
		if (current != null)
			slot = new EndGrammarSlot(position, nonterminal, label, (label != null && !label.isEmpty())? current.get(label) : -1,
									  variable, (variable != null && !variable.isEmpty())? current.get(variable) : -1, state, getConditions(rule.symbolAt(i - 1).getPostConditions()), runtime);
		else
			slot = new EndGrammarSlot(position, nonterminal, label, variable, state, getConditions(rule.symbolAt(i - 1).getPostConditions()), runtime);
		
		add(slot);
		slot.setFollowTest(getFollowTest(rule, i));
		return slot;
	}

	private void add(GrammarSlot slot) {
        names.put(slot.toString(), slot);
		slots.add(slot);
	}
	
	private GSSNodeLookup getNodeLookup() {
		if (config.getGSSLookupImpl() == LookupImpl.HASH_MAP) {
			if (config.getHashmapImpl() == HashMapImpl.JAVA)
				return new JavaHashMapNodeLookup();
			else if (config.getHashmapImpl() == HashMapImpl.INT_OPEN_ADDRESSING)
				return new IntOpenAddressingMap(); 
			else
				throw new RuntimeException();
		} else {
			return new ArrayNodeLookup(input);
		}
	}
	
	static private void validateNumberOfArguments(Nonterminal nonterminal, Expression[] arguments) {
		String[] parameters = nonterminal.getParameters();
		if ((parameters == null && arguments == null) || (parameters.length == arguments.length)) return;
		
		throw new IncorrectNumberOfArgumentsException(nonterminal, arguments);
	}

	public void reset(Input input) {
		slots.forEach(s -> s.reset(input));
	}

	private Conditions getConditions(Set<Condition> conditions) {
		if (conditions.isEmpty())
			return ConditionsFactory.DEFAULT;
		return ConditionsFactory.getConditions(conditions, matcherFactory);
	}
	
}
