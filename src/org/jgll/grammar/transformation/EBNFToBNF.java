package org.jgll.grammar.transformation;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jgll.datadependent.ast.AST;
import org.jgll.datadependent.ast.Expression;
import org.jgll.datadependent.traversal.FreeVariableVisitor;
import org.jgll.grammar.Grammar;
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
import org.jgll.grammar.symbol.LayoutStrategy;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Nonterminal.Builder;
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

/**
 * 
 * 
 * @authors Ali Afroozeh, Anastasia Izmaylova
 *
 */
public class EBNFToBNF implements GrammarTransformation {
	

	@Override
	public Grammar transform(Grammar grammar) {
		Set<Rule> newRules = new LinkedHashSet<>();
		grammar.getDefinitions().values().forEach(r -> newRules.addAll(transform(r)));
		return Grammar.builder().addRules(newRules).setLayout(grammar.getLayout()).build();
	}
	
	private Set<Rule> transform(Rule rule) {
		Set<Rule> newRules = new LinkedHashSet<>();
		newRules.add(rewrite(rule, newRules));
		return newRules;
	}
	
	public static boolean isEBNF(Symbol s) {
		return s instanceof Star ||
			   s instanceof Plus ||
			   s instanceof Opt ||
			   s instanceof Sequence ||
			   s instanceof Alt;
	}

	
	public Rule rewrite(Rule rule, Set<Rule> newRules) {

		if (rule.getBody() == null) {
			return rule;
		}
		
		Rule.Builder builder = new Rule.Builder(rule.getHead());
		
		EBNFVisitor visitor = new EBNFVisitor(newRules, rule.getLayout(), rule.getLayoutStrategy());
		
		for(Symbol s : rule.getBody()) {
			builder.addSymbol(s.accept(visitor));
		}
		
		return builder.setLayout(rule.getLayout()).setLayoutStrategy(rule.getLayoutStrategy()).build();
	}
	
	public static List<Symbol> rewrite(List<Symbol> list, Nonterminal layout) {
		return list; // TODO: fix it later!
	}

	
	private static String getName(Symbol symbol, List<Symbol> separators, Nonterminal layout) {
		if (separators.isEmpty() && layout == null) {
			return symbol.getName();
		} else {
			return "{" + symbol.getName() +
					  ", " + separators.stream().map(s -> s.getName()).collect(Collectors.joining(", ")) + 
					  ", " + layout + 
				   "}";	
		}
	}
		
	private static class EBNFVisitor implements ISymbolVisitor<Symbol> {
		
		private final Set<Rule> addedRules;
		private final Nonterminal layout;
		private final LayoutStrategy strategy;
		
		public EBNFVisitor(Set<Rule> addedRules, Nonterminal layout, LayoutStrategy strategy) {
			this.addedRules = addedRules;
			this.layout = layout;
			this.strategy = strategy;
		}
		
		/**
		 *  Target cases:
		 */
		
		private Set<String> freeVars;
		private FreeVariableVisitor visitor;
		
		private void init() {
			freeVars = new LinkedHashSet<>();
			visitor = new FreeVariableVisitor(freeVars);			
		}
		
		/**
		 * (A | B) ::= A 
		 *           | B
		 */
		@Override
		public <E extends Symbol> Symbol visit(Alt<E> symbol) {	
			List<? extends Symbol> symbols = symbol.getSymbols();
			
			init();
			String[] parameters = null;
			Expression[] arguments = null;
			
			Alt.builder(symbols).build().accept(visitor);
			
			if (!freeVars.isEmpty()) {
				parameters = freeVars.stream().toArray(String[]::new);
				arguments = freeVars.stream().map(v -> AST.var(v)).toArray(Expression[]::new);
			}
			
			symbols = symbols.stream().map(x -> x.accept(this)).collect(Collectors.toList());
			
			Nonterminal newNt = parameters == null? Nonterminal.withName(symbol.getName()) 
					            		: Nonterminal.builder(symbol.getName()).addParameters(parameters).build();
			
			symbols.forEach(x -> addedRules.add(Rule.withHead(newNt).addSymbol(x).setLayout(layout).setLayoutStrategy(strategy).build()));
			
			Builder copyBuilder = arguments == null? newNt.copyBuilder() : newNt.copyBuilder().apply(arguments);
			return copyBuilder.addConditions(symbol).setLabel(symbol.getLabel()).build();
		}

		/**
		 * S? ::= S 
		 *      | epsilon
		 */
		@Override
		public Symbol visit(Opt symbol) {
			Symbol in = symbol.getSymbol();
			
			init();
			String[] parameters = null;
			Expression[] arguments = null;
			
			in.setEmpty();
			in.accept(visitor);
			
			if (!freeVars.isEmpty()) {
				parameters = freeVars.stream().toArray(String[]::new);
				arguments = freeVars.stream().map(v -> AST.var(v)).toArray(Expression[]::new);
			}
			
			Nonterminal newNt = parameters == null? Nonterminal.withName(symbol.getName())
									: Nonterminal.builder(symbol.getName()).addParameters(parameters).build();
			
			addedRules.add(Rule.withHead(newNt).addSymbol(in.accept(this)).setLayout(layout).setLayoutStrategy(strategy).build());
			addedRules.add(Rule.withHead(newNt).build());
			
			Builder copyBuilder = arguments == null? newNt.copyBuilder() : newNt.copyBuilder().apply(arguments);
			return copyBuilder.addConditions(symbol).setLabel(symbol.getLabel()).build();
		}

		/**
		 * S+ ::= S+ S
		 *      | S
		 *      
		 * in case of separators:
		 * 
		 * (S sep)+ ::= (S sep)+ sep S
		 *            | S
		 *      
		 */
		@Override
		public Symbol visit(Plus symbol) {
			Symbol S = symbol.getSymbol();
			
			init();
			String[] parameters = null;
			Expression[] arguments = null;
			
			S.setEmpty();
			S.accept(visitor);
			
			if (!freeVars.isEmpty()) {
				parameters = freeVars.stream().toArray(String[]::new);
				arguments = freeVars.stream().map(v -> AST.var(v)).toArray(Expression[]::new);
			}
			
			List<Symbol> seperators = symbol.getSeparators().stream().map(sep -> sep.accept(this)).collect(Collectors.toList());
			
			Nonterminal newNt = parameters == null? Nonterminal.withName(getName(S, symbol.getSeparators(), layout) + "+")
									: Nonterminal.builder(getName(S, symbol.getSeparators(), layout) + "+").addParameters(parameters).build();
			
			S = S.accept(this);
			
			addedRules.add(Rule.withHead(newNt)
							.addSymbol(arguments != null? Nonterminal.builder(newNt).apply(arguments).build() : newNt)
							.addSymbols(seperators)
							.addSymbols(S).setLayout(layout).setLayoutStrategy(strategy).build());
			addedRules.add(Rule.withHead(newNt).addSymbol(S).build());
			
			Builder copyBuilder = arguments == null? newNt.copyBuilder() : newNt.copyBuilder().apply(arguments);
			return copyBuilder.addConditions(symbol).setLabel(symbol.getLabel()).build();
		}

		/**
		 * (S) ::= S
		 */
		@Override
		public <E extends Symbol> Symbol visit(Sequence<E> symbol) {
			List<? extends Symbol> symbols = symbol.getSymbols();
			
			init();
			String[] parameters = null;
			Expression[] arguments = null;
			
			Sequence.builder(symbols).build().accept(visitor);
			
			if (!freeVars.isEmpty()) {
				parameters = freeVars.stream().toArray(String[]::new);
				arguments = freeVars.stream().map(v -> AST.var(v)).toArray(Expression[]::new);
			}
			
			List<Symbol> syms = symbols.stream().map(x -> x.accept(this)).collect(Collectors.toList());
			
			Nonterminal newNt = parameters == null? Nonterminal.withName(symbol.getName())
									: Nonterminal.builder(symbol.getName()).addParameters(parameters).build();
			
			addedRules.add(Rule.withHead(newNt).addSymbols(syms).setLayout(layout).setLayoutStrategy(strategy).build());
			
			Builder copyBuilder = arguments == null? newNt.copyBuilder() : newNt.copyBuilder().apply(arguments);
			return copyBuilder.addConditions(symbol).setLabel(symbol.getLabel()).build();
		}

		/**
		 * S* ::= S+ 
		 *      | epsilon
		 *        
		 */
		@Override
		public Symbol visit(Star symbol) {
			Symbol S = symbol.getSymbol();
			
			init();
			String[] parameters = null;
			Expression[] arguments = null;
			
			S.setEmpty();
			S.accept(visitor);
			
			if (!freeVars.isEmpty()) {
				parameters = freeVars.stream().toArray(String[]::new);
				arguments = freeVars.stream().map(v -> AST.var(v)).toArray(Expression[]::new);
			}
			
			Nonterminal newNt = parameters != null? Nonterminal.builder(getName(S, symbol.getSeparators(), layout) + "*").addParameters(parameters).build()
						              : Nonterminal.withName(getName(S, symbol.getSeparators(), layout) + "*");
			
			addedRules.add(Rule.withHead(newNt).addSymbols(Plus.builder(S).addSeparators(symbol.getSeparators()).build().accept(this)).setLayout(layout).setLayoutStrategy(strategy).build());
			addedRules.add(Rule.withHead(newNt).build());
			
			Builder copyBuilder = arguments == null? newNt.copyBuilder() : newNt.copyBuilder().apply(arguments);
			return copyBuilder.addConditions(symbol).setLabel(symbol.getLabel()).build();
		}
		
		/**
		 *  The rest:
		 */

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
			return symbol;
		}

		@Override
		public Symbol visit(CharacterRange symbol) {
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
			return symbol;
		}

		@Override
		public Symbol visit(Offside symbol) {
			Symbol sym = symbol.getSymbol().accept(this);
			
			return sym == symbol.getSymbol()? symbol 
					: Offside.builder(sym).setLabel(symbol.getLabel()).addConditions(symbol).build();
		}

		@Override
		public Symbol visit(Terminal symbol) {
			return symbol;
		}

		@Override
		public Symbol visit(While symbol) {
			Symbol body = symbol.getBody().accept(this);
			if (body == symbol.getBody()) 
				return symbol;
			
			return While.builder(symbol.getExpression(), body).setLabel(symbol.getLabel()).addConditions(symbol).build();
		}
		
	}

}
