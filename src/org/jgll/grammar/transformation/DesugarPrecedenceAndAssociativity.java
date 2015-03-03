package org.jgll.grammar.transformation;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.jgll.grammar.symbol.Associativity.*;

import org.jgll.datadependent.ast.AST;
import org.jgll.datadependent.ast.Expression;
import org.jgll.grammar.Grammar;
import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.condition.DataDependentCondition;
import org.jgll.grammar.symbol.Align;
import org.jgll.grammar.symbol.Associativity;
import org.jgll.grammar.symbol.AssociativityGroup;
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
import org.jgll.grammar.symbol.PrecedenceLevel;
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
	
	private Set<String> leftOrRightRecursiveNonterminals;
	
	@Override
	public Grammar transform(Grammar grammar) {
		
		for (Map.Entry<Nonterminal, Rule> entry : grammar.getDefinitions().entries())
			if (entry.getValue().isLeftOrRightRecursive())
				leftOrRightRecursiveNonterminals.add(entry.getKey().getName());
		
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
		
		private final Set<String> leftOrRightRecursiveNonterminals;
		
		private Expression l1;
		private Expression r2;
		
		private Expression l2;
		private Expression r1;
		
		private Set<Condition> preconditions;
		
		public Visitor(Rule rule, Set<String> leftOrRightRecursiveNonterminals) {
			this.rule = rule;
			this.leftOrRightRecursiveNonterminals = leftOrRightRecursiveNonterminals;
			
			if (this.rule.getPrecedence() == -1)
				return;
			
			AssociativityGroup associativityGroup = rule.getAssociativityGroup();
			PrecedenceLevel precedenceLevel = rule.getPrecedenceLevel();
			
			// Expressions for the left and right recursive uses
			int precedence = rule.getPrecedence();
			Associativity associativity = rule.getAssociativity();
			
			if (precedenceLevel.getLhs() == precedenceLevel.getRhs()) {
				
				switch(associativity) {
					case LEFT:
						l1 = AST.integer(precedence + 1);
						r2 = AST.integer(precedence);
						break;
						
					case RIGHT:
						l1 = AST.integer(precedence);
						r2 = AST.integer(precedence + 1);
						break;
						
					case NON_ASSOC:
						l1 = AST.integer(precedence + 1);
						r2 = AST.integer(precedence + 1);
						break;
						
					case UNDEFINED:
						l1 = AST.integer(precedence);
						r2 = AST.integer(precedence);
						break;
						
					default: throw new RuntimeException("Unexpected associativity: " + associativity);
				}
			} else {
				l1 = AST.integer(precedence);
				r2 = AST.integer(precedence);
			}
			
			// Rule for propagation of a precedence level
			
			if (precedenceLevel.hasUnaryBelow()) {
				r1 = AST.var("r");
				l2 = AST.var("l");
			} else {
				r1 = AST.integer(0);
				l2 = AST.integer(0);
			}
			
			// Constraints
			
			if (rule.isLeftRecursive())
				preconditions.add(DataDependentCondition.predicate(AST.greaterEq(AST.integer(precedenceLevel.getRhs()), AST.var("r"))));
			
			if (rule.isRightRecursive())
				preconditions.add(DataDependentCondition.predicate(AST.greaterEq(AST.integer(precedenceLevel.getRhs()), AST.var("l"))));
			
			if (precedenceLevel.getLhs() != precedenceLevel.getRhs()) {
				if (associativityGroup != null) {
					
					if (precedence == associativityGroup.getPrecedence())
						switch(associativityGroup.getAssociativity()) {
							case LEFT:
								preconditions.add(DataDependentCondition.predicate(AST.equal(AST.integer(associativityGroup.getPrecedence()), AST.var("r"))));
								break;						
							case RIGHT:
								preconditions.add(DataDependentCondition.predicate(AST.equal(AST.integer(associativityGroup.getPrecedence()), AST.var("l"))));
								break;
							case NON_ASSOC:
								preconditions.add(DataDependentCondition.predicate(AST.equal(AST.integer(associativityGroup.getPrecedence()), AST.var("l"))));
								preconditions.add(DataDependentCondition.predicate(AST.equal(AST.integer(associativityGroup.getPrecedence()), AST.var("r"))));
								break;
							default: throw new RuntimeException("Unexpected associativity: " + associativity);
						}
					
					if(!associativityGroup.getAssocMap().isEmpty()) {
						// TODO:
						throw new RuntimeException("Unsupported!");
					}
					
				} else {	
					switch(associativity) {
						case LEFT:
							preconditions.add(DataDependentCondition.predicate(AST.equal(AST.integer(precedence), AST.var("r"))));
							break;						
						case RIGHT:
							preconditions.add(DataDependentCondition.predicate(AST.equal(AST.integer(precedence), AST.var("l"))));
							break;
						case NON_ASSOC:
							preconditions.add(DataDependentCondition.predicate(AST.equal(AST.integer(precedence), AST.var("l"))));
							preconditions.add(DataDependentCondition.predicate(AST.equal(AST.integer(precedence), AST.var("r"))));
							break;
						case UNDEFINED:
							break;
						default: throw new RuntimeException("Unexpected associativity: " + associativity);
					}
				}
				
			}
			
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
			
			boolean isFirst = this.isFirst;
			boolean isLast = this.isLast;
			
			int j = 0;
			boolean modified = false;
			for (Symbol sym : symbols) {
				
				if (isFirst && j == 0) this.isFirst = true;
				else this.isFirst = false;
				
				if (isLast && j == symbols.length - 1) this.isLast = true;
				else this.isLast = false;
				
				syms[j] = sym.accept(this);
				if (sym != syms[j])
					modified = true;
				j++;
			}
			
			this.isFirst = isFirst;
			this.isLast = isLast;
			
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
			
			boolean isUseOfLeftOrRight = leftOrRightRecursiveNonterminals.contains(symbol.getName());
			
			if (!isUseOfLeftOrRight) return symbol;
			
			boolean isRecursiveUseOfLeftOrRight = isUseOfLeftOrRight && symbol.getName().equals(rule.getHead().getName());
			
			Expression[] arguments = symbol.getArguments();
			
			if (arguments == null)
				arguments = new Expression[2];
			else {
				Expression[] args = new Expression[arguments.length + 2];
				
				int i = 0;
				for (Expression argument : arguments) {
					args[i] = arguments[i];
					i++;
				}
				
				arguments = args;
			}
			
			if (rule.getPrecedence() == -1 && isUseOfLeftOrRight) {
				arguments[arguments.length] = AST.integer(0);
				arguments[arguments.length + 1] = AST.integer(0);
				return symbol.copyBuilder().apply(arguments).build();
			}
			
			if (isRecursiveUseOfLeftOrRight && isFirst) {
				arguments[arguments.length] = l1;
				arguments[arguments.length + 1] = r1;
				return symbol.copyBuilder().apply(arguments).addPreConditions(preconditions).build();
			} else if (isRecursiveUseOfLeftOrRight && isLast) {
				arguments[arguments.length] = l2;
				arguments[arguments.length + 1] = r2;
				return symbol.copyBuilder().apply(arguments).build();
			} else {
				arguments[arguments.length] = AST.integer(0);
				arguments[arguments.length + 1] = AST.integer(0);
				return symbol.copyBuilder().apply(arguments).build();
			}
			
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
