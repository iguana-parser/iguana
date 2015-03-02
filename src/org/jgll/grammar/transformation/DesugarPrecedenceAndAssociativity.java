package org.jgll.grammar.transformation;

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

/**
 * 
 * @author Anastasia Izmaylova
 * 
 * Description:
 * 
 * 		- Two parameters, l and r, are introduced to pass a precedence level,
 *        and each rule gets a precedence level. 
 * 		  We need two parameters to account for left (l) and right (r) recursion.
 *      		- for left recursive rules, symbols on the left should pass as the 1st argument either 
 *              (a) the precedence level of the rule if the rule is left-associative; or 
 *              (b) the precedence level plus one if it is right-associative or non-associative, or
 *                  the first rule in the current group starting from the top.
 *          - for right recursive rules, symbols on the right should pass as the 2nd argument either
 *              (a) the precedence level of the rule if the rule is left-associative; or
 *              (b) the precedence level of the rule if it is right-associative, or
 *                  the first rule in the current group starting from the top.
 *          - The 2nd argument to left recursive symbols or the 1st argument to right recursive symbols
 *            is defined as follows:
 *            (a) the precedence level is propagated if there are rules with lower precedence that
 *                can be affected (this is to account for deep cases), i.e.,
 *                l if right, or left and right associative rules are below, otherwise 0
 *                r if left, or left and right associative rules are below, otherwise 0
 *      - Conditions are added to the rules as follows:
 *          (a) if left recursive, the precedence level (or the highest in the current group of rules) >= r
 *          (b) if right recursive, the precedence level (or the highest in the current group of rules) >= l
 *          (c) if the rule is part of a group but not the 1st in the group:
 *                - if left associative, the precedence level != r
 *                - if right associative, the precedence level != l
 *          (d) if the rule is part of a group which is a left associative with respect to each other,
 *                - if left associative, { the precedence level of a rule in the group != r}
 *                  (the lowest precedence level minus 1???)
 *                - if right associative, { the precedence level of a rule in the group != l}
 *                  (the lowest precedence level minus 1???)
 *        
 */

public class DesugarPrecedenceAndAssociativity implements GrammarTransformation {
	
	@Override
	public Grammar transform(Grammar grammar) {
		return grammar;
	}
	
	public Rule transform(Rule rule) {
		
		return rule;
	}

	@SuppressWarnings("unused")
	private static class Visitor implements ISymbolVisitor<Symbol> {
		
		private boolean isFirst;
		private boolean isLast;
		
		private final Rule rule;
		
		public Visitor(Rule rule) {
			this.rule = rule;
		}
		
		public Rule transform() {
			
			if (rule.getBody() == null)
				return rule;
			
			Rule.Builder builder = new Rule.Builder(rule.getHead());
			
			int i = 0;
			for (Symbol symbol : rule.getBody()) {
				
				if (i == 0) isFirst = true;
				else isFirst = false;
				
				if (i == rule.getBody().size() - 1) isLast = true;
				else isLast = false;
				
				builder.addSymbol(symbol.accept(this));
				
				i++;
			}
			
			return builder.setLayout(rule.getLayout()).setLayoutStrategy(rule.getLayoutStrategy()).build();
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

		@Override
		public <E extends Symbol> Symbol visit(Alt<E> symbol) {
			// TODO: EBNF
			return symbol;
		}

		@Override
		public Symbol visit(Opt symbol) {
			// TODO: EBNF
			return symbol;
		}

		@Override
		public Symbol visit(Plus symbol) {
			// TODO: EBNF
			return symbol;
		}

		@Override
		public <E extends Symbol> Symbol visit(Sequence<E> symbol) {
			// TODO: EBNF
			return symbol;
		}

		@Override
		public Symbol visit(Star symbol) {
			// TODO: EBNF
			return symbol;
		}
		
	}
}
