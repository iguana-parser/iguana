package org.jgll.grammar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.jgll.grammar.slot.EpsilonTransition.Type;
import org.jgll.grammar.slot.FirstAndLastTerminalTransition;
import org.jgll.grammar.slot.FirstTerminalTransition;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.LastSymbolAndEndGrammarSlot;
import org.jgll.grammar.slot.LastSymbolGrammarSlot;
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
	
	private final Map<String, GrammarSlot> names;
	
	private List<GrammarSlot> slots;
	
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
		this.slots = new ArrayList<>();
		
		terminalsMap.put(Epsilon.getInstance(), epsilon);

		add(epsilon);
		
		for (Nonterminal nonterminal : grammar.getNonterminals()) {
			getNonterminalGrammarSlot(nonterminal);
		}
		
		for (Nonterminal nonterminal : grammar.getNonterminals()) {
			convert(nonterminal, grammar);
		}
	}
	
	public NonterminalGrammarSlot getHead(Nonterminal start) {
		if (start instanceof Start) {
			
			NonterminalGrammarSlot s = nonterminalsMap.get(start);
			if (s != null) {
				return s;
			}
			
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
		
		private boolean isLast = false;
		private boolean isFirst = false;
		
		public GrammarGraphSymbolVisitor(NonterminalGrammarSlot head, Rule rule, BodyGrammarSlot currentSlot) {
			this.head = head;
			this.rule = rule;
			this.currentSlot = currentSlot;
		}
		
		public boolean hasNext() {
			return i < rule.size();
		}
		
		public void nextSymbol() {
			j = 0;
			isFirst = false;
			isLast = false;
			if (i == 0) isFirst = true;
			if (i == rule.size() - 1) isLast = true;
			visitSymbol(rule.symbolAt(i));
			i++;
		}
		
		@Override
		public Void visit(While symbol) {
			
			Expression expression = symbol.getExpression();
			Symbol body = symbol.getBody();
			
			BodyGrammarSlot thenSlot = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null);
			BodyGrammarSlot elseSlot = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null);
			
			BodyGrammarSlot loop = currentSlot;
			
			currentSlot.addTransition(new ConditionalTransition(expression, currentSlot, thenSlot, elseSlot));
			currentSlot = thenSlot;
			
			BodyGrammarSlot opened = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null);
			currentSlot.addTransition(new EpsilonTransition(Type.OPEN, new HashSet<>(), currentSlot, opened));
			currentSlot = opened;
			
			visitSymbol(body);
			
			BodyGrammarSlot closed = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null);
			currentSlot.addTransition(new EpsilonTransition(Type.CLOSE, new HashSet<>(), currentSlot, closed));
			currentSlot = closed;
			
			currentSlot.addTransition(new EpsilonTransition(new HashSet<>(), currentSlot, loop));
			
			currentSlot = elseSlot;
			
			visitSymbol(Epsilon.getInstance());
			
			return null;
		}
		
		@Override
		public Void visit(Nonterminal symbol) {
			
			NonterminalGrammarSlot nonterminalSlot = getNonterminalGrammarSlot(symbol);
			
			BodyGrammarSlot slot;
			
			if (isLast && symbol == rule.symbolAt(i)) {
				slot = getLastSymbolAndEndGrammarSlot(rule, i + 1, rule.getPosition(i + 1, j + 1), head, symbol.getLabel(), symbol.getVariable());
			} else if (isLast) {
				slot = getLastSymbolGrammarSlot(rule, i + 1, rule.getPosition(i + 1, j + 1), head, symbol.getLabel(), symbol.getVariable());
			} else {
				slot = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1, j + 1), head, symbol.getLabel(), symbol.getVariable());
			}
			
			Expression[] arguments = symbol.getArguments();
			
			validateNumberOfArguments(nonterminalSlot.getNonterminal(), arguments);
			
			Set<Condition> preConditions = symbol.getPreConditions();
			currentSlot.addTransition(new NonterminalTransition(nonterminalSlot, currentSlot, slot, arguments, preConditions));
			
			currentSlot = slot;
			j++;
			
			return null;
		}
		
		@Override
		public Void visit(IfThenElse symbol) {
			
			Expression expression = symbol.getExpression();
			Symbol thenPart = symbol.getThenPart();
			Symbol elsePart = symbol.getElsePart();
			
			BodyGrammarSlot thenSlot = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null);
			BodyGrammarSlot elseSlot = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null);
			
			currentSlot.addTransition(new ConditionalTransition(expression, currentSlot, thenSlot, elseSlot));
			currentSlot = thenSlot;
			
			BodyGrammarSlot opened = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null);
			currentSlot.addTransition(new EpsilonTransition(Type.OPEN, new HashSet<>(), currentSlot, opened));
			currentSlot = opened;
			
			visitSymbol(thenPart);
			
			BodyGrammarSlot closed = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null);
			currentSlot.addTransition(new EpsilonTransition(Type.CLOSE, new HashSet<>(), currentSlot, closed));
			currentSlot = closed;
			
			currentSlot = elseSlot;
			
			opened = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null);
			currentSlot.addTransition(new EpsilonTransition(Type.OPEN, new HashSet<>(), currentSlot, opened));
			currentSlot = opened;
			
			visitSymbol(elsePart);
			
			currentSlot.addTransition(new EpsilonTransition(Type.CLOSE, new HashSet<>(), currentSlot, closed));
			currentSlot = closed;
			
			return null;
		}
		
		@Override
		public Void visit(IfThen symbol) {
			
			Expression expression = symbol.getExpression();
			Symbol thenPart = symbol.getThenPart();
			
			BodyGrammarSlot thenSlot = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null);
			BodyGrammarSlot elseSlot = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null);
			
			currentSlot.addTransition(new ConditionalTransition(expression, currentSlot, thenSlot, elseSlot));
			currentSlot = thenSlot;
			
			BodyGrammarSlot opened = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null);
			currentSlot.addTransition(new EpsilonTransition(Type.OPEN, new HashSet<>(), currentSlot, opened));
			currentSlot = opened;
			
			visitSymbol(thenPart);
			
			BodyGrammarSlot closed = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null);
			currentSlot.addTransition(new EpsilonTransition(Type.CLOSE, new HashSet<>(), currentSlot, closed));
			
			currentSlot = elseSlot;
			
			visitSymbol(Epsilon.getInstance());
			
			currentSlot.addTransition(new EpsilonTransition(new HashSet<>(), currentSlot, closed));
			currentSlot = closed;
			
			return null;
			
		}
		
		@Override
		public Void visit(Conditional symbol) {
			
			Symbol sym = symbol.getSymbol();
			Expression expression = symbol.getExpression();
			
			visitSymbol(sym);
			
			BodyGrammarSlot thenSlot = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null);
			currentSlot.addTransition(new ConditionalTransition(expression, currentSlot, thenSlot));
			currentSlot = thenSlot;
			
			return null;
		}
		
		@Override
		public Void visit(Code symbol) {
			
			Symbol sym = symbol.getSymbol();
			Statement[] statements = symbol.getStatements();
			
			visitSymbol(sym);
			
			BodyGrammarSlot done = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null);
			currentSlot.addTransition(new CodeTransition(statements, currentSlot, done));
			currentSlot = done;
			
			return null;
		}
		
		@Override
		public Void visit(Block symbol) {
			
			boolean isFirst = (i == 0) && this.isFirst;
			this.isFirst = false;
			
			boolean isLast = (i == rule.size() - 1) && this.isLast;
			this.isLast = false;
			
			Symbol[] symbols = symbol.getSymbols();
			
			BodyGrammarSlot opened = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null);
			currentSlot.addTransition(new EpsilonTransition(Type.OPEN, new HashSet<>(), currentSlot, opened));
			currentSlot = opened;
			
			int n = 0;
			for (Symbol sym : symbols) {
				if (n == 0 && isFirst) this.isFirst = true;
				if (n == symbols.length - 1 && isLast) this.isLast = true;
				visitSymbol(sym);
				n++;
			}
			
			BodyGrammarSlot closed = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null);
			currentSlot.addTransition(new EpsilonTransition(Type.CLOSE, new HashSet<>(), currentSlot, closed));
			currentSlot = closed;
				
			this.isFirst = isFirst;
			this.isLast = isLast;
			
			return null;
		}
		
		@Override
		public Void visit(RegularExpression symbol) {
			TerminalGrammarSlot terminalSlot = getTerminalGrammarSlot(symbol);
			
			BodyGrammarSlot slot;
			
			if (isLast && symbol == rule.symbolAt(i)) {
				slot = getLastSymbolAndEndGrammarSlot(rule, i + 1, rule.getPosition(i + 1, j + 1), head, symbol.getLabel(), null);
			} else if (isLast) {
				slot = getLastSymbolGrammarSlot(rule, i + 1, rule.getPosition(i + 1, j + 1), head, symbol.getLabel(), null);
			} else {
				slot = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1, j + 1), head, symbol.getLabel(), null);
			}
			
			Set<Condition> preConditions = symbol.getPreConditions();
			Set<Condition> postConditions = symbol.getPostConditions();
			currentSlot.addTransition(getTerminalTransition(rule, i + 1, terminalSlot, currentSlot, slot, preConditions, postConditions, isFirst, isLast));
			
			currentSlot = slot;
			j++;
			
			return null;
		}
		
		/**
		 *  Introduces epsilon transitions to handle labels and preconditions/postconditions
		 */
		private void visitSymbol(Symbol symbol) {
			
			if (symbol instanceof Nonterminal || symbol instanceof RegularExpression) { // TODO: I think this can be unified
				symbol.accept(this);
				return;
			}
			
			if (symbol.getLabel() != null) {
				BodyGrammarSlot declared = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null);
				currentSlot.addTransition(new EpsilonTransition(Type.DECLARE_LABEL, symbol.getLabel(), symbol.getPreConditions(), currentSlot, declared));
				currentSlot = declared;
			} else {
				BodyGrammarSlot checked = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null);
				currentSlot.addTransition(new EpsilonTransition(symbol.getPreConditions(), currentSlot, checked));
				currentSlot = checked;
			}
			
			symbol.accept(this);
			
			if (symbol.getLabel() != null) {
				BodyGrammarSlot stored;
				
				if (isLast && symbol == rule.symbolAt(i)) {
					stored = getEndGrammarSlot(rule, i + 1, rule.getPosition(i + 1));
				} else {
					stored = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null);
				}
				
				currentSlot.addTransition(new EpsilonTransition(Type.STORE_LABEL, symbol.getLabel(), symbol.getPostConditions(), currentSlot, stored));
				currentSlot = stored;
			} else {
				BodyGrammarSlot checked;
				
				if (isLast && symbol == rule.symbolAt(i)) {
					checked = getEndGrammarSlot(rule, i + 1, rule.getPosition(i + 1));
				} else {
					checked = getBodyGrammarSlot(rule, i + 1, rule.getPosition(i + 1), head, null, null);
				}
				
				currentSlot.addTransition(new EpsilonTransition(symbol.getPostConditions(), currentSlot, checked));
				currentSlot = checked;
			}
		}
		
	}
	
	private AbstractTerminalTransition getTerminalTransition(Rule rule, int i, TerminalGrammarSlot slot, 
															 BodyGrammarSlot origin, BodyGrammarSlot dest,
															 Set<Condition> preConditions, Set<Condition> postConditions, boolean isFirst, boolean isLast) {
		
		if (isFirst && !isLast) {
			return new FirstTerminalTransition(slot, origin, dest, preConditions, postConditions);
		} 
		else if (isFirst && isLast) {
			return new FirstAndLastTerminalTransition(slot, origin, dest, preConditions, postConditions);
		} 
		else if (isLast)  {
			return new BeforeLastTerminalTransition(slot, origin, dest, preConditions, postConditions);
		} 
		else {
			return new TerminalTransition(slot, origin, dest, preConditions, postConditions);
		}
	}
	
	private TerminalGrammarSlot getTerminalGrammarSlot(RegularExpression regex) {
		TerminalGrammarSlot terminalSlot = new TerminalGrammarSlot(id++, regex);
		add(terminalSlot);
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
			add(ntSlot);
			return ntSlot;
		});
	}
	
	private BodyGrammarSlot getFirstGrammarSlot(Rule rule,  NonterminalGrammarSlot nonterminal) {
		BodyGrammarSlot slot;
		
		if (rule.size() == 0) {
			slot = new EpsilonGrammarSlot(id++, rule.getPosition(0,0), nonterminal, epsilon, DummyNodeLookup.getInstance(), Collections.emptySet());
		} else {
			// TODO: this is a temporarily solution, which should be re-thought; 
			//       in particular, not any precondition of the first symbol can be moved to the first slot.  
			Set<Condition> preConditions = new HashSet<>();
			preConditions.addAll(rule.symbolAt(0).getPreConditions());
			 
			rule.symbolAt(0).getPreConditions().clear();
			
			slot = new BodyGrammarSlot(id++, rule.getPosition(0,0), DummyNodeLookup.getInstance(), null, null, preConditions);
		}
		add(slot);
		return slot;
	}
	
	private BodyGrammarSlot getBodyGrammarSlot(Rule rule, int i, Position position, NonterminalGrammarSlot nonterminal, String label, String variable) {
		assert i < rule.size();
		
		BodyGrammarSlot slot;
		if (config.getGSSType() == GSSType.NEW) {
			// With new GSS we don't lookup in body grammarSlots
			slot = new BodyGrammarSlot(id++, position, DummyNodeLookup.getInstance(), label, variable, rule.symbolAt(i - 1).getPostConditions());
		} else {
			slot = new BodyGrammarSlot(id++, position, getNodeLookup(), label, variable, rule.symbolAt(i - 1).getPostConditions());				
		}
		
		add(slot);
		return slot;
	}
	
	private BodyGrammarSlot getLastSymbolGrammarSlot(Rule rule, int i, Position position, NonterminalGrammarSlot nonterminal, String label, String variable) {
		assert i == rule.size();
		
		BodyGrammarSlot slot;
		if (config.getGSSType() == GSSType.NEW) {
			slot = new LastSymbolGrammarSlot(id++, position, nonterminal, DummyNodeLookup.getInstance(), label, variable, rule.symbolAt(i - 1).getPostConditions());				
		} else {
			slot = new LastSymbolGrammarSlot(id++, position, nonterminal, getNodeLookup(), label, variable, rule.symbolAt(i - 1).getPostConditions());
		}
		
		add(slot);
		return slot;
	}
	
	private BodyGrammarSlot getLastSymbolAndEndGrammarSlot(Rule rule, int i, Position position, NonterminalGrammarSlot nonterminal, String label, String variable) {
		assert i == rule.size();
		
		BodyGrammarSlot slot;
		if (config.getGSSType() == GSSType.NEW) {
			slot = new LastSymbolAndEndGrammarSlot(id++, position, nonterminal, DummyNodeLookup.getInstance(), label, variable, rule.symbolAt(i - 1).getPostConditions());				
		} else {
			slot = new LastSymbolAndEndGrammarSlot(id++, position, nonterminal, getNodeLookup(), label, variable, rule.symbolAt(i - 1).getPostConditions());
		}
		
		add(slot);
		return slot;
	}
	
	private BodyGrammarSlot getEndGrammarSlot(Rule rule, int i, Position position) {
		assert i == rule.size();
		
		BodyGrammarSlot slot;
		if (config.getGSSType() == GSSType.NEW) {
			slot = new EndGrammarSlot(id++, position, DummyNodeLookup.getInstance(), null, null, new HashSet<>());				
		} else {
			slot = new EndGrammarSlot(id++, position, getNodeLookup(), null, null, new HashSet<>());
		}
		
		add(slot);
		return slot;
	}

	private void add(GrammarSlot slot) {
		names.put(slot.toString(), slot);
		slots.add(slot);
	}
	
	private GSSNodeLookup getNodeLookup() {
		if (config.getGSSLookupImpl() == LookupImpl.HASH_MAP) {
			return new HashMapNodeLookup();
		} else {
			return new ArrayNodeLookup(input);
		}
	}
	
	static private void validateNumberOfArguments(Nonterminal nonterminal, Expression[] arguments) {
		String[] parameters = nonterminal.getParameters();
		if ((parameters == null && arguments == null) 
				|| (parameters.length == arguments.length)) return;
		
		throw new IncorrectNumberOfArgumentsException(nonterminal, arguments);
	}

	public void reset(Input input) {
		slots.forEach(s -> s.reset(input));
	}

}
