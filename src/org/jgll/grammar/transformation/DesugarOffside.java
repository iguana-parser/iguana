package org.jgll.grammar.transformation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.jgll.datadependent.ast.AST.*;
import static org.jgll.grammar.condition.DataDependentCondition.predicate;

import org.jgll.datadependent.ast.AST;
import org.jgll.grammar.Grammar;
import org.jgll.grammar.condition.DataDependentCondition;
import org.jgll.grammar.symbol.Align;
import org.jgll.grammar.symbol.Block;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.grammar.symbol.Code;
import org.jgll.grammar.symbol.Conditional;
import org.jgll.grammar.symbol.EOF;
import org.jgll.grammar.symbol.Epsilon;
import org.jgll.grammar.symbol.IfThen;
import org.jgll.grammar.symbol.IfThenElse;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Offside;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.grammar.symbol.Terminal;
import org.jgll.grammar.symbol.While;
import org.jgll.regex.Alt;
import org.jgll.regex.Opt;
import org.jgll.regex.Plus;
import org.jgll.regex.Sequence;
import org.jgll.regex.Star;
import org.jgll.traversal.ISymbolVisitor;

import com.google.common.collect.SetMultimap;

public class DesugarOffside implements GrammarTransformation {
	
	private final SetMultimap<Nonterminal, Nonterminal> reachabilityGraph;
	
	private Set<String> offsided;
	
	public DesugarOffside(SetMultimap<Nonterminal, Nonterminal> reachabilityGraph) {
		this.reachabilityGraph = reachabilityGraph;
	}
	
	@Override
	public Grammar transform(Grammar grammar) {
		
		FindOffsidesVisitor findOffsides = new FindOffsidesVisitor();
		findOffsides.find(grammar);
		offsided = findOffsides.getOffsides();
		
		for (Map.Entry<Nonterminal, Nonterminal> entry : reachabilityGraph.entries())
			if (offsided.contains(entry.getKey().getName()))
				offsided.add(entry.getValue().getName());
		
		DesugarOffsideVisitor desugarOffsides = new DesugarOffsideVisitor(offsided);
		
		Set<Rule> rules = new LinkedHashSet<>();
		
		for (Rule rule : grammar.getDefinitions().values())
			rules.add(desugarOffsides.transform(rule));
		
		return Grammar.builder().addRules(rules).setLayout(grammar.getLayout()).build();
	}
	
	private static class DesugarOffsideVisitor implements ISymbolVisitor<Symbol> {
		
		private final Set<String> offsided;
		
		private Rule rule;
		private boolean isOffsided;
		
		private int i;
		
		public DesugarOffsideVisitor(Set<String> offsided) {
			this.offsided = offsided;
		}
		
		public Rule transform(Rule rule) {
			this.rule = rule;
			isOffsided = offsided.contains(this.rule.getHead());
			i = 0;
			
			Rule.Builder builder;
			
			if (isOffsided)
				builder = rule.copyBuilderButWithHead(rule.getHead().copyBuilder().addParameters("ind").build());
			else
				builder = rule.copyBuilder();
			
			List<Symbol> symbols = new ArrayList<>();
			builder = builder.setSymbols(symbols);
			
			if (this.rule.getBody() != null) {
				for (Symbol symbol : this.rule.getBody())
					symbols.add(symbol.accept(this));
			}
			
			return builder.build();
		}

		@Override
		public Symbol visit(Align symbol) {
			Symbol sym = symbol.getSymbol().accept(this);
			
			return sym == symbol.getSymbol()? symbol 
					: Align.builder(sym).setLabel(symbol.getLabel()).addConditions(symbol).build();
		}

		@Override
		public Symbol visit(Block symbol) {
			Symbol[] symbols = symbol.getSymbols();
			Symbol[] syms = new Symbol[symbols.length];
			
			int j = 0;
			boolean modified = false;
			for (Symbol sym : symbols) {
				
				syms[j] = sym.accept(this);
				if (sym != syms[j])
					modified = true;
				j++;
			}
			
			return modified? Block.builder(syms).setLabel(symbol.getLabel()).addConditions(symbol).build()
					: symbol;
		}

		@Override
		public Symbol visit(Character symbol) {
			if (isOffsided) {
				String label = symbol.getLabel() != null? symbol.getLabel() : "t" + i++;
				return symbol.copyBuilder()
								.setLabel(label)
								.addConditions(symbol)
								.addPreCondition(predicate(greater(indent(lExt(label)), var("ind"))))
								.build();
			}
			return symbol;
		}

		@Override
		public Symbol visit(CharacterRange symbol) {
			if (isOffsided) {
				String label = symbol.getLabel() != null? symbol.getLabel() : "t" + i++;
				return symbol.copyBuilder()
								.setLabel(label)
								.addConditions(symbol)
								.addPreCondition(predicate(greater(indent(lExt(label)), var("ind"))))
								.build();
			}
			return symbol;
		}

		@Override
		public Symbol visit(Code symbol) {
			Symbol sym = symbol.getSymbol().accept(this);
			if (sym == symbol.getSymbol())
				return symbol;
			
			return Code.builder(sym, symbol.getStatements()).setLabel(symbol.getLabel()).addConditions(symbol).build();
		}

		@Override
		public Symbol visit(Conditional symbol) {
			Symbol sym = symbol.getSymbol().accept(this);
			if (sym == symbol.getSymbol())
				return symbol;
			
			return Conditional.builder(sym, symbol.getExpression()).setLabel(symbol.getLabel()).addConditions(symbol).build();
		}

		@Override
		public Symbol visit(EOF symbol) {
			return symbol;
		}

		@Override
		public Symbol visit(Epsilon symbol) {
			return symbol;
		}

		@Override
		public Symbol visit(IfThen symbol) {
			Symbol sym = symbol.getThenPart().accept(this);
			if (sym == symbol.getThenPart())
				return symbol;
			
			return IfThen.builder(symbol.getExpression(), sym).setLabel(symbol.getLabel()).addConditions(symbol).build();
		}

		@Override
		public Symbol visit(IfThenElse symbol) {
			Symbol thenPart = symbol.getThenPart().accept(this);
			Symbol elsePart = symbol.getElsePart().accept(this);
			if (thenPart == symbol.getThenPart() 
					&& elsePart == symbol.getElsePart())
				return symbol;
			
			return IfThenElse.builder(symbol.getExpression(), thenPart, elsePart).setLabel(symbol.getLabel()).addConditions(symbol).build();
		}

		@Override
		public Symbol visit(Nonterminal symbol) {
			if (isOffsided) { // The rule has a parameter for indentation, and therefore, also all reachable nonterminals
				return symbol.copyBuilder().apply(var("ind")).build();
			} else {
				if (offsided.contains(symbol.getName()))
					return symbol.copyBuilder().apply(integer(0)).build();
				else
					return symbol;
			}
		}

		@Override
		public Symbol visit(Offside symbol) {
			Symbol sym = symbol.getSymbol();
			
			if (sym instanceof Nonterminal) {
				Nonterminal s = (Nonterminal) sym;
				String label = s.getLabel() != null? s.getLabel() : s.getName().toLowerCase();
				if (isOffsided) { // Offside inside a rule that has a parameter for indentation
					return s.copyBuilder()
							.apply(indent(lExt(label)))
							.setLabel(label)
							.addConditions(sym)
							.addPreCondition(predicate(greater(indent(lExt(label)), var("ind"))))
							.build();
				} else {
					return s.copyBuilder()
							.apply(indent(lExt(label)))
							.setLabel(label)
							.addConditions(sym)
							.build();
				}
			} 
			
			// Otherwise, ignore offside
			return symbol.accept(this);
		}

		@Override
		public Symbol visit(Terminal symbol) {
			if (isOffsided) {
				String label = symbol.getLabel() != null? symbol.getLabel() : "t" + i++;
				return symbol.copyBuilder()
								.setLabel(label)
								.addConditions(symbol)
								.addPreCondition(predicate(greater(indent(lExt(label)), var("ind"))))
								.build();
			}
			return symbol;
		}

		@Override
		public Symbol visit(While symbol) {
			Symbol body = symbol.getBody().accept(this);
			if (body == symbol.getBody()) 
				return symbol;
			
			return While.builder(symbol.getExpression(), body).setLabel(symbol.getLabel()).addConditions(symbol).build();
		}

		@Override
		public <E extends Symbol> Symbol visit(Alt<E> symbol) {
			
			List<? extends Symbol> symbols = symbol.getSymbols();
			List<Symbol> syms = new ArrayList<>();
			
			boolean modified = false;
			for (Symbol sym : symbols) {
				Symbol s = sym.accept(this);
				syms.add(s);
				if (sym != s)
					modified = true;
			}
			
			return modified? Alt.builder(syms).setLabel(symbol.getLabel()).addConditions(symbol).build() 
					       : symbol;
		}

		@Override
		public Symbol visit(Opt symbol) {
			Symbol sym = symbol.getSymbol().accept(this);
			if (sym == symbol.getSymbol())
				return symbol;
			return Opt.builder(sym).setLabel(symbol.getLabel()).addConditions(symbol).build();
		}

		@Override
		public Symbol visit(Plus symbol) {
			
			Symbol sym = symbol.getSymbol().accept(this);
			List<Symbol> separators = symbol.getSeparators();
			
			boolean modified = sym != symbol.getSymbol();
			
			List<Symbol> seps = new ArrayList<>();
			for (Symbol sep : separators) {
				Symbol s = sep.accept(this);
				seps.add(s);
				if (s != sep)
					modified = true;
			}
			
			return modified? Plus.builder(sym).addSeparators(seps).setLabel(symbol.getLabel()).addConditions(symbol).build()
					       : symbol;
		}

		@Override
		public <E extends Symbol> Symbol visit(Sequence<E> symbol) {
			
			List<? extends Symbol> symbols = symbol.getSymbols();
			List<Symbol> syms = new ArrayList<>();
			
			boolean modified = false;
			for (Symbol sym : symbols) {
				Symbol s = sym.accept(this);
				syms.add(s);
				if (sym != s)
					modified = true;
			}
			
			return modified? Sequence.builder(syms).setLabel(symbol.getLabel()).addConditions(symbol).build() 
					       : symbol;
		}

		@Override
		public Symbol visit(Star symbol) {
			Symbol sym = symbol.getSymbol().accept(this);
			List<Symbol> separators = symbol.getSeparators();
			
			boolean modified = sym != symbol.getSymbol();
			
			List<Symbol> seps = new ArrayList<>();
			for (Symbol sep : separators) {
				Symbol s = sep.accept(this);
				seps.add(s);
				if (s != sep)
					modified = true;
			}
			
			return modified? Star.builder(sym).addSeparators(seps).setLabel(symbol.getLabel()).addConditions(symbol).build()
					       : symbol;
		}
		
	}
	
	private static class FindOffsidesVisitor implements ISymbolVisitor<Void> {
		
		private final Set<String> offsided;
		
		public FindOffsidesVisitor() {
			this.offsided = new HashSet<>();
		}
		
		public Set<String> getOffsides() {
			return offsided;
		}
		
		public void find(Grammar grammar) {
			for (Rule rule : grammar.getDefinitions().values()) {
				
				if (rule.getBody() == null)
					continue;
				
				for (Symbol s : rule.getBody())
					s.accept(this);
			}
		}
		
		@Override
		public Void visit(Offside symbol) {
			Symbol sym = symbol.getSymbol();
			
			// Offside will only be applied to nonterminals 
			if (sym instanceof Nonterminal)
				offsided.add(((Nonterminal) sym).getName());
			
			return sym.accept(this);
		}

		@Override
		public Void visit(Align symbol) {
			return symbol.accept(this);
		}

		@Override
		public Void visit(Block symbol) {
			for (Symbol sym : symbol.getSymbols())
				sym.accept(this);
			return null;
		}

		@Override
		public Void visit(Character symbol) {
			return null;
		}

		@Override
		public Void visit(CharacterRange symbol) {
			return null;
		}

		@Override
		public Void visit(Code symbol) {
			return symbol.getSymbol().accept(this);
		}

		@Override
		public Void visit(Conditional symbol) {
			return symbol.getSymbol().accept(this);
		}

		@Override
		public Void visit(EOF symbol) {
			return null;
		}

		@Override
		public Void visit(Epsilon symbol) {
			return null;
		}

		@Override
		public Void visit(IfThen symbol) {
			return symbol.getThenPart().accept(this);
		}

		@Override
		public Void visit(IfThenElse symbol) {
			symbol.getThenPart().accept(this);
			symbol.getElsePart().accept(this);
			return null;
		}

		@Override
		public Void visit(Nonterminal symbol) {
			return null;
		}

		@Override
		public Void visit(Terminal symbol) {
			return null;
		}

		@Override
		public Void visit(While symbol) {
			return symbol.getBody().accept(this);
		}

		@Override
		public <E extends Symbol> Void visit(Alt<E> symbol) {
			for (Symbol sym : symbol.getSymbols())
				sym.accept(this);
			return null;
		}

		@Override
		public Void visit(Opt symbol) {
			return symbol.accept(this);
		}

		@Override
		public Void visit(Plus symbol) {
			for (Symbol sym : symbol.getSeparators())
				sym.accept(this);
			return symbol.getSymbol().accept(this);
		}

		@Override
		public <E extends Symbol> Void visit(Sequence<E> symbol) {
			for (Symbol sym : symbol.getSymbols())
				sym.accept(this);
			return null;
		}

		@Override
		public Void visit(Star symbol) {
			for (Symbol sym : symbol.getSeparators())
				sym.accept(this);
			return symbol.getSymbol().accept(this);
		}
		
	}

}
