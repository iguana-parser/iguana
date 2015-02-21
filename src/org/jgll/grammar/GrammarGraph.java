package org.jgll.grammar;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.jgll.datadependent.ast.Expression;
import org.jgll.datadependent.ast.Statement;
import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.exception.IncorrectNumberOfArgumentsException;
import org.jgll.grammar.slot.AbstractTerminalTransition;
import org.jgll.grammar.slot.BeforeLastTerminalTransition;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.CodeTransition;
import org.jgll.grammar.slot.ConditionalTransition;
import org.jgll.grammar.slot.EndGrammarSlot;
import org.jgll.grammar.slot.EpsilonGrammarSlot;
import org.jgll.grammar.slot.EpsilonTransition;
import org.jgll.grammar.slot.FirstAndLastTerminalTransition;
import org.jgll.grammar.slot.FirstTerminalTransition;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.grammar.slot.NonterminalTransition;
import org.jgll.grammar.slot.TerminalGrammarSlot;
import org.jgll.grammar.slot.TerminalTransition;
import org.jgll.grammar.symbol.Block;
import org.jgll.grammar.symbol.Code;
import org.jgll.grammar.symbol.Conditional;
import org.jgll.grammar.symbol.Epsilon;
import org.jgll.grammar.symbol.IfThen;
import org.jgll.grammar.symbol.IfThenElse;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Position;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Start;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.grammar.symbol.While;
import org.jgll.parser.gss.lookup.ArrayNodeLookup;
import org.jgll.parser.gss.lookup.DummyNodeLookup;
import org.jgll.parser.gss.lookup.GSSNodeLookup;
import org.jgll.parser.gss.lookup.HashMapNodeLookup;
import org.jgll.regex.RegularExpression;
import org.jgll.util.Configuration;
import org.jgll.util.Configuration.GSSType;
import org.jgll.util.Configuration.LookupImpl;
import org.jgll.util.Input;

public class GrammarGraph implements Serializable {

	private static final long serialVersionUID = 1L;

	Map<Nonterminal, NonterminalGrammarSlot> nonterminalsMap;
	
	Map<RegularExpression, TerminalGrammarSlot> terminalsMap;
	
	private Map<String, GrammarSlot> names;
	
	Grammar grammar;

	private Configuration config;

	private Input input;
	
	private final Nonterminal layout;
	
	private int id = 1;
	
	private TerminalGrammarSlot epsilon = new TerminalGrammarSlot(0, Epsilon.getInstance());
	
	public GrammarGraph(Grammar grammar, Input input, Configuration config) {
		this.grammar = grammar;
		this.input = input;
		this.config = config;
		this.layout = grammar.getLayout();
		this.nonterminalsMap = new LinkedHashMap<>();
		this.terminalsMap = new LinkedHashMap<>();
		this.names = new HashMap<>();
		
		terminalsMap.put(Epsilon.getInstance(), epsilon);
		names.put(Epsilon.getInstance().getName(), epsilon);
		
		for (Nonterminal nonterminal : grammar.getNonterminals()) {
			getNonterminalGrammarSlot(nonterminal);
		}
		
		for (Nonterminal nonterminal : grammar.getNonterminals()) {
			convert(nonterminal, grammar);
		}
	}
	
	public NonterminalGrammarSlot getHead(Nonterminal start) {
		if (start instanceof Start) {
			Nonterminal nt = ((Start)start).getNonterminal();

			if (layout == null) {
				return nonterminalsMap.get(nt);
			}
			
			Rule startRule = Rule.withHead(start)
								 .addSymbol(layout).addSymbol(nt).addSymbol(layout).build();
			NonterminalGrammarSlot nonterminalGrammarSlot = getNonterminalGrammarSlot(start);
			addRule(nonterminalGrammarSlot, startRule);
			return nonterminalGrammarSlot;
		}
		
		return nonterminalsMap.get(start);
	}	
	
	public TerminalGrammarSlot getTerminal(RegularExpression regex) {
		return terminalsMap.get(regex);
	}

	public GrammarSlot getGrammarSlot(String s) {
		return names.get(s);
	}
	
	public Collection<NonterminalGrammarSlot> getNonterminals() {
		return nonterminalsMap.values();
	}
	
	public RegularExpression getRegularExpression(String s) {
		GrammarSlot slot = names.get(s);
		if (!(slot instanceof TerminalGrammarSlot))
			throw new RuntimeException("No regular expression for " + s + " found.");
		return ((TerminalGrammarSlot) names.get(s)).getRegularExpression();
	}
		
	private void convert(Nonterminal nonterminal, Grammar grammar) {
		List<Rule> rules = grammar.getAlternatives(nonterminal);
		NonterminalGrammarSlot nonterminalSlot = getNonterminalGrammarSlot(nonterminal);
		rules.forEach(r -> addRule(nonterminalSlot, r));
	}
	
	private void addRule(NonterminalGrammarSlot head, Rule rule) {
		
		BodyGrammarSlot firstSlot = getFirstGrammarSlot(rule, head);
		head.addFirstSlot(firstSlot);	
		BodyGrammarSlot currentSlot = firstSlot;
		
		GrammarGraphSymbolVisitor rule2graph = new GrammarGraphSymbolVisitor(head, rule, currentSlot);
		
		while (rule2graph.hasNext()) rule2graph.nextSymbol();
	}
	
	private class GrammarGraphSymbolVisitor extends  AbstractGrammarGraphSymbolVisitor {
		
		private final NonterminalGrammarSlot head;
		private final Rule rule;
		
		private BodyGrammarSlot currentSlot;
		private int i = 0;
		
		private int j;
		private final Stack<BodyGrammarSlot> elseSlots;
		
		public GrammarGraphSymbolVisitor(NonterminalGrammarSlot head, Rule rule, BodyGrammarSlot currentSlot) {
			this.head = head;
			this.rule = rule;
			this.currentSlot = currentSlot;
			this.elseSlots = new Stack<>();
		}
		
		public boolean hasNext() {
			return i < rule.size();
		}
		
		public void nextSymbol() {
			j = 0;
			rule.symbolAt(i).accept(this);
			i++;
		}
		
		@Override
		public Void visit(While symbol) {
			
			Expression expression = symbol.getExpression();
			Symbol body = symbol.getBody();
			
			BodyGrammarSlot thenSlot = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1, j + 1), head, null, null);
			j++;
			BodyGrammarSlot elseSlot = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1, j + 1), head, null, null);
			j++;
			
			if (currentSlot != null) {
				currentSlot.addTransition(new ConditionalTransition(expression, currentSlot, thenSlot, elseSlot));
			} else {
				while (!elseSlots.isEmpty()) {
					BodyGrammarSlot currentSlot = elseSlots.pop();
					currentSlot.addTransition(new ConditionalTransition(expression, currentSlot, thenSlot, elseSlot));
				}
			}
			
			BodyGrammarSlot currentSlot = this.currentSlot;
			
			this.currentSlot = thenSlot;
			body.accept(this);
			
			this.elseSlots.add(this.currentSlot);
			this.elseSlots.add(elseSlot);
			
			
			return null;
		}
		
		@Override
		public Void visit(Nonterminal symbol) {
			
			NonterminalGrammarSlot nonterminalSlot = getNonterminalGrammarSlot(symbol);
			BodyGrammarSlot slot = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1, j + 1), head, symbol.getLabel(), symbol.getVariable());
			
			Expression[] arguments = symbol.getArguments();
			
			// FIXME: Uncomment validateNumberOfArguments(nonterminal, arguments);
			
			Set<Condition> preConditions = symbol.getPreConditions();
			currentSlot.addTransition(new NonterminalTransition(nonterminalSlot, currentSlot, slot, arguments, preConditions));
			
			currentSlot = slot;
			j++;
			
			return null;
		}
		
		@Override
		public Void visit(IfThenElse symbol) {
			
			Expression expression = symbol.getExpression();
			
			BodyGrammarSlot thenSlot = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null);
			BodyGrammarSlot elseSlot = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null);
			
			currentSlot.addTransition(new ConditionalTransition(expression, currentSlot, thenSlot, elseSlot));
			
			Symbol thenPart = symbol.getThenPart();
			Symbol elsePart = symbol.getElsePart();
			
			currentSlot = thenSlot;
			thenPart.accept(this);
			
			this.elseSlots.add(currentSlot);
			
			currentSlot = elseSlot;
			elsePart.accept(this);
			
			this.elseSlots.add(currentSlot);
			
			currentSlot = null;
			
			return null;
		}
		
		@Override
		public Void visit(IfThen symbol) {
			
			Expression expression = symbol.getExpression();
			
			BodyGrammarSlot thenSlot = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null);
			BodyGrammarSlot elseSlot = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null);
			
			currentSlot.addTransition(new ConditionalTransition(expression, currentSlot, thenSlot, elseSlot));
			
			Symbol thenPart = symbol.getThenPart();
			
			currentSlot = thenSlot;
			thenPart.accept(this);
			
			this.elseSlots.add(currentSlot);
			this.elseSlots.add(elseSlot);
			
			currentSlot = null;
			
			return null;
			
		}
		
		@Override
		public Void visit(Conditional symbol) {
			
			Symbol sym = symbol.getSymbol();
			Expression expression = symbol.getExpression();
			
			sym.accept(this);
			
			BodyGrammarSlot thenSlot = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null);
			
			if (currentSlot != null) {
				currentSlot.addTransition(new ConditionalTransition(expression, currentSlot, thenSlot, null));
			} else {
				while (!elseSlots.isEmpty()) {
					BodyGrammarSlot currentSlot = elseSlots.pop();
					currentSlot.addTransition(new ConditionalTransition(expression, currentSlot, thenSlot, null));
				}
			}
			
			currentSlot = thenSlot;
			
			return null;
		}
		
		@Override
		public Void visit(Code symbol) {
			
			Symbol sym = symbol.getSymbol();
			Statement[] statements = symbol.getStatements();
			
			sym.accept(this);
			
			BodyGrammarSlot slot = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1, j), head, null, null);
			j++;
			
			if (currentSlot != null) {
				currentSlot.addTransition(new CodeTransition(statements, currentSlot, slot));
			} else {
				while (!elseSlots.isEmpty()) {
					BodyGrammarSlot currentSlot = elseSlots.pop();
					currentSlot.addTransition(new CodeTransition(statements, currentSlot, slot));
				}
			}
			
			currentSlot = slot;
			
			return null;
		}
		
		@Override
		public Void visit(Block symbol) {
			
			Symbol[] symbols = symbol.getSymbols();
			
			for (Symbol sym : symbols) {
				sym.accept(this);
			}
			
			return null;
		}
		
		@Override
		public Void visit(RegularExpression symbol) {
			TerminalGrammarSlot terminalSlot = getTerminalGrammarSlot(symbol);
			
			BodyGrammarSlot slot = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1, j + 1), head, symbol.getLabel(), null);
			Set<Condition> preConditions = symbol.getPreConditions();
			Set<Condition> postConditions = symbol.getPostConditions();
			currentSlot.addTransition(getTerminalTransition(rule, i + 1, terminalSlot, currentSlot, slot, preConditions, postConditions));
			
			currentSlot = slot;
			j++;
			
			return null;
		}
		
		private void merge() {
			if (currentSlot == null && !elseSlots.isEmpty()) {
				
				currentSlot = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null);
				
				while (!elseSlots.isEmpty()) {
					BodyGrammarSlot slot = elseSlots.pop();
					// FIXME:
					// slot.addTransition(new EpsilonTransition(slot, currentSlot));
				}
			} else {
				throw new RuntimeException("Something wrong with the logic of the grammar graph builder.");
			}
		}
	}; 
	
	private AbstractTerminalTransition getTerminalTransition(Rule rule, int i, TerminalGrammarSlot slot, 
															 BodyGrammarSlot origin, BodyGrammarSlot dest,
															 Set<Condition> preConditions, Set<Condition> postConditions) {
		
		if (i == 1 && rule.size() > 1) {
			return new FirstTerminalTransition(slot, origin, dest, preConditions, postConditions);
		} 
		else if (i == 1 && rule.size() == 1) {
			return new FirstAndLastTerminalTransition(slot, origin, dest, preConditions, postConditions);
		} 
		else if (i == rule.size())  {
			return new BeforeLastTerminalTransition(slot, origin, dest, preConditions, postConditions);
		} 
		else {
			return new TerminalTransition(slot, origin, dest, preConditions, postConditions);
		}
	}
	
	private TerminalGrammarSlot getTerminalGrammarSlot(RegularExpression regex) {
		TerminalGrammarSlot terminalSlot = new TerminalGrammarSlot(id++, regex);
		names.put(terminalSlot.toString(), terminalSlot);
		return terminalsMap.computeIfAbsent(regex, k -> terminalSlot);
	}
	
	private NonterminalGrammarSlot getNonterminalGrammarSlot(Nonterminal nonterminal) {
		return nonterminalsMap.computeIfAbsent(nonterminal, k -> {
			NonterminalGrammarSlot ntSlot;
			if (config.getGSSType() == GSSType.NEW) {
				ntSlot = new NonterminalGrammarSlot(id++, nonterminal, getNodeLookup());			
			} else {
				ntSlot = new NonterminalGrammarSlot(id++, nonterminal, DummyNodeLookup.getInstance());
			}
			names.put(ntSlot.toString(), ntSlot);
			return ntSlot;
		});
	}
	
	private BodyGrammarSlot getFirstGrammarSlot(Rule rule,  NonterminalGrammarSlot nonterminal) {
		BodyGrammarSlot slot;
		
		if (rule.size() == 0) {
			slot = new EpsilonGrammarSlot(id++, rule.getPosition(0), nonterminal, epsilon, DummyNodeLookup.getInstance(), Collections.emptySet());
		} else {
			slot = new BodyGrammarSlot(id++, rule.getPosition(0), DummyNodeLookup.getInstance(), null, null, rule.symbolAt(0).getPostConditions());
		}
		
		names.put(slot.toString(), slot);
		return slot;
	}
	
	private BodyGrammarSlot getBodyGrammarSlot(Rule rule, int i, Position position, NonterminalGrammarSlot nonterminal, String label, String variable) {
		BodyGrammarSlot slot;
		if (i == rule.size()) {
			if (config.getGSSType() == GSSType.NEW) {
				slot = new EndGrammarSlot(id++, position, nonterminal, DummyNodeLookup.getInstance(), label, variable, rule.symbolAt(i - 1).getPostConditions());				
			} else {
				slot = new EndGrammarSlot(id++, position, nonterminal, getNodeLookup(), label, variable, rule.symbolAt(i - 1).getPostConditions());
			}
		} else {
			if (config.getGSSType() == GSSType.NEW) {
				// With new GSS we don't lookup in body grammarSlots
				slot = new BodyGrammarSlot(id++, position, DummyNodeLookup.getInstance(), label, variable, rule.symbolAt(i - 1).getPostConditions());
			} else {
				slot = new BodyGrammarSlot(id++, position, getNodeLookup(), label, variable, rule.symbolAt(i - 1).getPostConditions());				
			}
		}
		names.put(slot.toString(), slot);
		return slot;
	}
	
	private GSSNodeLookup getNodeLookup() {
		if (config.getGSSLookupImpl() == LookupImpl.HASH_MAP) {
			return new HashMapNodeLookup();
		} else {
			return new ArrayNodeLookup(input);
		}
	}
	
	@SuppressWarnings("unused")
	static private void validateNumberOfArguments(Nonterminal nonterminal, Expression[] arguments) {
		String[] parameters = nonterminal.getParameters();
		if ((parameters == null && arguments == null) 
				|| (parameters.length == arguments.length)) return;
		
		throw new IncorrectNumberOfArgumentsException(nonterminal, arguments);
	}
	
}