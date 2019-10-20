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

package org.iguana.datadependent.traversal;

import org.eclipse.imp.pdb.facts.util.ImmutableSet;
import org.eclipse.imp.pdb.facts.util.TrieSet;
import org.iguana.datadependent.ast.Expression.Boolean;
import org.iguana.datadependent.ast.Expression.Integer;
import org.iguana.datadependent.ast.Expression.String;
import org.iguana.datadependent.ast.Expression.*;
import org.iguana.datadependent.ast.Statement;
import org.iguana.datadependent.ast.Statement.Expression;
import org.iguana.datadependent.ast.VariableDeclaration;
import org.iguana.grammar.condition.Condition;
import org.iguana.grammar.condition.DataDependentCondition;
import org.iguana.grammar.condition.PositionalCondition;
import org.iguana.grammar.condition.RegularExpressionCondition;
import org.iguana.grammar.runtime.RuntimeRule;
import org.iguana.grammar.symbol.IfThenElse;
import org.iguana.grammar.symbol.*;
import org.iguana.traversal.IConditionVisitor;
import org.iguana.traversal.ISymbolVisitor;

import java.util.*;


public class FreeVariableVisitor implements IAbstractASTVisitor<Void>, ISymbolVisitor<Void>, IConditionVisitor<Void> {
	
	/*
	 * Free variables of a symbol and updates to free variables
	 */
	private final Set<java.lang.String> freeVariables;
	private final Set<java.lang.String> updates;
	
	public FreeVariableVisitor(Set<java.lang.String> freeVariables) {
		this(freeVariables, null);
	}
	
	public FreeVariableVisitor(Set<java.lang.String> freeVariables, Set<java.lang.String> updates) {
		this.freeVariables = freeVariables;
		this.updates = updates;
		this.nonterminal_uses = null;
		this.nonterminal_updates = null;
		this.nonterminal_returns = null;
		this.nonterminal_bindings = null;
	}
	
	public void compute(RuntimeRule rule) {
		
		ImmutableSet<java.lang.String> env = TrieSet.of();
		
		List<java.lang.String> parameters = rule.getHead().getParameters();
		if (parameters != null) {
			for (java.lang.String parameter : parameters)
				env = env.__insert(parameter);
		}
		
		ImmutableSet<java.lang.String> _env = env;
		
		for (Symbol symbol : rule.getBody()) {
			
			symbol.setEnv(_env);
			this.visitSymbol(symbol);
			_env = symbol.getEnv();
			
			symbol.setEmpty();
		}
		
	}
	
	/*
	 * State variable bindings of nonterminals in the rule
	 */
	private final Map<Nonterminal, Set<java.lang.String>> nonterminal_uses;
	private final Map<Nonterminal, Set<java.lang.String>> nonterminal_updates; // Given assignments to state variables within a nonterminal
	private final Map<Nonterminal, Set<java.lang.String>> nonterminal_returns; // Given uses of updated state variables of a nonterminal after a use of a nonterminal in all the rules
	
	private Map<Nonterminal, Set<java.lang.String>> nonterminal_bindings; // Given uses of updated state variables of a nonterminal after a use of the nonterminal in the current rule
	
	public FreeVariableVisitor(Map<Nonterminal, Set<java.lang.String>> nonterminal_uses, Map<Nonterminal, Set<java.lang.String>> nonterminal_updates, Map<Nonterminal, Set<java.lang.String>> nonterminal_returns) {
		this.freeVariables = null;
		this.updates = null;
		this.nonterminal_uses = nonterminal_uses;
		this.nonterminal_updates = nonterminal_updates;
		this.nonterminal_returns = nonterminal_returns;
		this.nonterminal_bindings = new HashMap<>();
	}
	
	public Map<Nonterminal, Set<java.lang.String>> computeBindings(RuntimeRule rule) {
		initBindings();
		compute(rule);
		return nonterminal_bindings;
	}
	
	private void initBindings() {
		this.nonterminal_bindings = new HashMap<>();
	}
	
	@Override
	public Void visit(Boolean expression) {
		return null;
	}

	@Override
	public Void visit(Integer expression) {
		return null;
	}

	@Override
	public Void visit(Real expression) {
		return null;
	}

	@Override
	public Void visit(String expression) {
		return null;
	}
	
	@Override
	public Void visit(Tuple expression) {
		
		for (org.iguana.datadependent.ast.Expression element : expression.getElements()) {
			element.setEnv(expression.getEnv());
			element.accept(this);
		}
		
		return null;
	}

	@Override
	public Void visit(Name expression) {
		java.lang.String name = expression.getName();
		
		if (!expression.getEnv().contains(name))
			use(name);
		
		return null;
	}
	
	private void use(java.lang.String name) {
		if (freeVariables != null)
			freeVariables.add(name);
		
		if (nonterminal_bindings != null && !nonterminal_bindings.isEmpty()) {
			for (Nonterminal nonterminal : nonterminal_bindings.keySet()) {
				if (nonterminal_updates.get(nonterminal).contains(name)) {
					nonterminal_returns.get(nonterminal).add(name);
					nonterminal_bindings.get(nonterminal).add(name);
				}
			}
		}
	}

	@Override
	public Void visit(Call expression) {
		
		for (org.iguana.datadependent.ast.Expression argument : expression.getArguments()) {
			argument.setEnv(expression.getEnv());
			argument.accept(this);
		}
		
		return null;
	}

	@Override
	public Void visit(Assignment expression) {
		
		java.lang.String id = expression.getId();
		org.iguana.datadependent.ast.Expression exp = expression.getExpression();
		
		if (!expression.getEnv().contains(id)) {
			use(id);
			update(id);
		}
		
		exp.setEnv(expression.getEnv());
		exp.accept(this);
		
		return null;
	}
	
	private void update(java.lang.String name) {
		if (updates != null)
			updates.add(name);
	}
	
	@Override
	public Void visit(LShiftANDEqZero expression) {
		
		org.iguana.datadependent.ast.Expression lhs = expression.getLhs();
		
		lhs.setEnv(expression.getEnv());
		lhs.accept(this);
		
		org.iguana.datadependent.ast.Expression rhs = expression.getRhs();
		
		rhs.setEnv(expression.getEnv());
		rhs.accept(this);
		
		return null;
	}
	
	@Override
	public Void visit(OrIndent expression) {
		
		org.iguana.datadependent.ast.Expression index = expression.getIndex();
		
		index.setEnv(expression.getEnv());
		index.accept(this);
		
		org.iguana.datadependent.ast.Expression ind = expression.getIndent();
		
		ind.setEnv(expression.getEnv());
		ind.accept(this);
		
		org.iguana.datadependent.ast.Expression first = expression.getFirst();
		
		first.setEnv(expression.getEnv());
		first.accept(this);
		
		org.iguana.datadependent.ast.Expression lExt = expression.getLExt();
		
		lExt.setEnv(expression.getEnv());
		lExt.accept(this);
		
		return null;
	}
	
	@Override
	public Void visit(AndIndent expression) {
		
		org.iguana.datadependent.ast.Expression index = expression.getIndex();
		
		index.setEnv(expression.getEnv());
		index.accept(this);
		
		org.iguana.datadependent.ast.Expression first = expression.getFirst();
		
		first.setEnv(expression.getEnv());
		first.accept(this);
		
		org.iguana.datadependent.ast.Expression lExt = expression.getLExt();
		
		lExt.setEnv(expression.getEnv());
		lExt.accept(this);
		
		return null;
	}
	
	@Override
	public Void visit(Or expression) {
		
		org.iguana.datadependent.ast.Expression lhs = expression.getLhs();
		
		lhs.setEnv(expression.getEnv());
		lhs.accept(this);
		
		org.iguana.datadependent.ast.Expression rhs = expression.getRhs();
		
		rhs.setEnv(expression.getEnv());
		rhs.accept(this);
		
		return null;
	}
	
	@Override
	public Void visit(And expression) {
		org.iguana.datadependent.ast.Expression lhs = expression.getLhs();
		
		lhs.setEnv(expression.getEnv());
		lhs.accept(this);
		
		org.iguana.datadependent.ast.Expression rhs = expression.getRhs();
		
		rhs.setEnv(lhs.getEnv());
		rhs.accept(this);
		
		return null;
	}
	
	@Override
	public Void visit(Less expression) {
		
		org.iguana.datadependent.ast.Expression lhs = expression.getLhs();
		
		lhs.setEnv(expression.getEnv());
		lhs.accept(this);
		
		org.iguana.datadependent.ast.Expression rhs = expression.getRhs();
		
		rhs.setEnv(expression.getEnv());
		rhs.accept(this);
		
		return null;
	}
	
	@Override
	public Void visit(LessThanEqual expression) {
		
		org.iguana.datadependent.ast.Expression lhs = expression.getLhs();
		
		lhs.setEnv(expression.getEnv());
		lhs.accept(this);
		
		org.iguana.datadependent.ast.Expression rhs = expression.getRhs();
		
		rhs.setEnv(expression.getEnv());
		rhs.accept(this);
		
		return null;
	}

	@Override
	public Void visit(Greater expression) {
		
		org.iguana.datadependent.ast.Expression lhs = expression.getLhs();
		
		lhs.setEnv(expression.getEnv());
		lhs.accept(this);
		
		org.iguana.datadependent.ast.Expression rhs = expression.getRhs();
		
		rhs.setEnv(expression.getEnv());
		rhs.accept(this);
		
		return null;
	}

	@Override
	public Void visit(GreaterThanEqual expression) {	
		
		org.iguana.datadependent.ast.Expression lhs = expression.getLhs();
		
		lhs.setEnv(expression.getEnv());
		lhs.accept(this);
		
		org.iguana.datadependent.ast.Expression rhs = expression.getRhs();
		
		rhs.setEnv(expression.getEnv());
		rhs.accept(this);
		
		return null;
	}

	@Override
	public Void visit(Equal expression) {
		
		org.iguana.datadependent.ast.Expression lhs = expression.getLhs();
		
		lhs.setEnv(expression.getEnv());
		lhs.accept(this);
		
		org.iguana.datadependent.ast.Expression rhs = expression.getRhs();
		
		rhs.setEnv(expression.getEnv());
		rhs.accept(this);
		
		return null;
	}
	
	@Override
	public Void visit(NotEqual expression) {
		
		org.iguana.datadependent.ast.Expression lhs = expression.getLhs();
		
		lhs.setEnv(expression.getEnv());
		lhs.accept(this);
		
		org.iguana.datadependent.ast.Expression rhs = expression.getRhs();
		
		rhs.setEnv(expression.getEnv());
		rhs.accept(this);
		
		return null;
	}

	@Override
	public Void visit(LeftExtent expression) {
		
		java.lang.String name = java.lang.String.format(org.iguana.datadependent.ast.Expression.LeftExtent.format, expression.getLabel());
		
		if (!expression.getEnv().contains(name)) 
			use(name);
		
		return null;
	}

	@Override
	public Void visit(RightExtent expression) {
		
		java.lang.String label = expression.getLabel();
		
		if (!expression.getEnv().contains(label))
			use(label);
		
		return null;
	}
	
	@Override
	public Void visit(Yield expression) {
		
		java.lang.String label = expression.getLabel();
		
		if (!expression.getEnv().contains(label))
			use(label);
		
		return null;
	}
	
	@Override
	public Void visit(Val expression) {
		
		java.lang.String label = expression.getLabel();
		
		if (!expression.getEnv().contains(label))
			use(label);
		
		return null;
	}
	
	@Override
	public Void visit(EndOfFile expression) {
		
		org.iguana.datadependent.ast.Expression index = expression.getIndex();
		
		index.setEnv(expression.getEnv());
		index.accept(this);
		
		return null;
	}
	
	@Override
	public Void visit(org.iguana.datadependent.ast.Expression.IfThenElse expression) {
		
		org.iguana.datadependent.ast.Expression condition = expression.getCondition();
		condition.setEnv(expression.getEnv());
		condition.accept(this);
		
		org.iguana.datadependent.ast.Expression thenPart = expression.getThenPart();
		thenPart.setEnv(expression.getEnv());
		thenPart.accept(this);
		
		org.iguana.datadependent.ast.Expression elsePart = expression.getElsePart();
		elsePart.setEnv(expression.getEnv());
		elsePart.accept(this);
		
		return null;
	}

	@Override
	public Void visit(VariableDeclaration declaration) {
		
		org.iguana.datadependent.ast.Expression expression = declaration.getExpression();
		
		if (expression != null) {
			expression.setEnv(declaration.getEnv());
			expression.accept(this);
		}
		
		declaration.setEnv(declaration.getEnv().__insert(declaration.getName()));
		
		return null;
	}

	@Override
	public Void visit(org.iguana.datadependent.ast.Statement.VariableDeclaration statement) {
		
		VariableDeclaration declaration = statement.getDeclaration();
		
		declaration.setEnv(statement.getEnv());
		declaration.accept(this);
		
		statement.setEnv(declaration.getEnv());
		
		return null;
	}

	@Override
	public Void visit(Expression statement) {
		
		org.iguana.datadependent.ast.Expression expression = statement.getExpression();
		
		expression.setEnv(statement.getEnv());
		expression.accept(this);
		
		return null;
	}
	
	@Override
	public Void visit(Align symbol) {
		Symbol sym = symbol.getSymbol();
		
		sym.setEnv(symbol.getEnv());
		visitSymbol(sym);
		
		symbol.setEnv(sym.getEnv());
		
		return null;
	}

	@Override
	public Void visit(Block symbol) {
		
		ImmutableSet<java.lang.String> env = symbol.getEnv();
		
		for (Symbol sym : symbol.getSymbols()) {
			sym.setEnv(env);
			visitSymbol(sym);
			env = sym.getEnv();
		}
		
		return null;
	}

	@Override
	public Void visit(Code symbol) {
		
		ImmutableSet<java.lang.String> env = symbol.getEnv();
		
		Symbol sym = symbol.getSymbol();
		
		sym.setEnv(env);
		visitSymbol(sym);
		env = sym.getEnv();
		
		for (Statement statement : symbol.getStatements()) {
			statement.setEnv(env);
			statement.accept(this);
			env = statement.getEnv();
		}
		
		symbol.setEnv(env);
		
		return null;
	}
	
	@Override
	public Void visit(Conditional symbol) {
		
		Symbol sym = symbol.getSymbol();
		org.iguana.datadependent.ast.Expression expression = symbol.getExpression();
		
		ImmutableSet<java.lang.String> env = symbol.getEnv();
		
		sym.setEnv(env);
		visitSymbol(sym);
		
		env = sym.getEnv();
		
		expression.setEnv(env);
		expression.accept(this);
		
		return null;
	}

	@Override
	public Void visit(IfThen symbol) {
		
		org.iguana.datadependent.ast.Expression expression = symbol.getExpression();
		Symbol thenPart = symbol.getThenPart();
		
		expression.setEnv(symbol.getEnv());
		expression.accept(this);
		
		thenPart.setEnv(symbol.getEnv());
		visitSymbol(thenPart);
		
		return null;
	}

	@Override
	public Void visit(IfThenElse symbol) {
		
		org.iguana.datadependent.ast.Expression expression = symbol.getExpression();
		Symbol thenPart = symbol.getThenPart();
		Symbol elsePart = symbol.getElsePart();
		
		expression.setEnv(symbol.getEnv());
		expression.accept(this);
		
		thenPart.setEnv(symbol.getEnv());
		visitSymbol(thenPart);
		
		elsePart.setEnv(symbol.getEnv());
		visitSymbol(thenPart);
		
		return null;
	}
	
	@Override
	public Void visit(Ignore symbol) {
		Symbol sym = symbol.getSymbol();
		
		sym.setEnv(symbol.getEnv());
		visitSymbol(sym);
		
		symbol.setEnv(sym.getEnv());
		
		return null;
	}

	@Override
	public Void visit(Nonterminal symbol) {
		
		ImmutableSet<java.lang.String> env = symbol.getEnv();
		
		if (symbol.getVariable() != null)
			env = env.__insert(symbol.getVariable());
		
		if (symbol.getArguments() != null) {
			for (org.iguana.datadependent.ast.Expression argument : symbol.getArguments()) {
				argument.setEnv(env);
				argument.accept(this);
				env = argument.getEnv();
			}
		}
		
		if (nonterminal_uses != null && nonterminal_uses.containsKey(symbol)) {
			for (java.lang.String parameter : nonterminal_uses.get(symbol))
				use(parameter);
		}
		
		if (nonterminal_bindings != null 
				&& !nonterminal_bindings.containsKey(symbol) 
				&& nonterminal_updates.containsKey(symbol) )
			nonterminal_bindings.put(symbol, new HashSet<>());
		
		symbol.setEnv(env);
		
		return null;
	}

	@Override
	public Void visit(Offside symbol) {
		
		Symbol sym = symbol.getSymbol();
		
		sym.setEnv(symbol.getEnv());
		visitSymbol(sym);
		
		symbol.setEnv(sym.getEnv());
		
		return null;
	}
	
	@Override
	public Void visit(Terminal symbol) {
		return null;
	}
	
	@Override
	public Void visit(While symbol) {
		
		org.iguana.datadependent.ast.Expression expression = symbol.getExpression();
		Symbol body = symbol.getBody();
		
		expression.setEnv(symbol.getEnv());
		expression.accept(this);
		
		body.setEnv(symbol.getEnv());
		visitSymbol(body);
		
		return null;
	}
	
	@Override
	public Void visit(Return symbol) {
		org.iguana.datadependent.ast.Expression expression = symbol.getExpression();
		
		expression.setEnv(symbol.getEnv());
		expression.accept(this);
		
		symbol.setEnv(expression.getEnv());
		
		return null;
	}

	@Override
	public Void visit(Alt symbol) {
		
		ImmutableSet<java.lang.String> env = symbol.getEnv();
		
		for (Symbol sym : symbol.getSymbols()) {
			sym.setEnv(env);
			visitSymbol(sym);
		}
		
		return null;
	}

	@Override
	public Void visit(Opt symbol) {
		
		Symbol sym = symbol.getSymbol();
		
		sym.setEnv(symbol.getEnv());
		visitSymbol(sym);
		
		return null;
	}

	@Override
	public Void visit(Plus symbol) {
		
		Symbol sym = symbol.getSymbol();
		
		sym.setEnv(symbol.getEnv());
		visitSymbol(sym);
		
		return null;
	}

	@Override
	public Void visit(Group symbol) {
		
		ImmutableSet<java.lang.String> env = symbol.getEnv();
		
		for (Symbol sym : symbol.getSymbols()) {
			sym.setEnv(env);
			visitSymbol(sym);
			env = sym.getEnv();
		}
		
		return null;
	}

	@Override
	public Void visit(Star symbol) {
		
		Symbol sym = symbol.getSymbol();
		
		sym.setEnv(symbol.getEnv());
		visitSymbol(sym);
		
		return null;
	}

    @Override
    public Void visit(Start start) {
        Symbol sym = start.getNonterminal();

        sym.setEnv(start.getEnv());
        visitSymbol(sym);

        return null;
    }

    // Accounts for optional label and optional preconditions and postconditions
	public Void visitSymbol(Symbol symbol) {
		
		ImmutableSet<java.lang.String> env = symbol.getEnv();
		
		if (symbol.getLabel() != null)
			env = env.__insert(java.lang.String.format(LeftExtent.format, symbol.getLabel()));
		
		for (Condition condition : symbol.getPreConditions()) {
			condition.setEnv(env);
			condition.accept(this);
		}
		
		symbol.setEnv(env);
		symbol.accept(this);
		
		env = symbol.getEnv();
		
		if (symbol.getLabel() != null)
			env = env.__insert(symbol.getLabel());
		
		for (Condition condition : symbol.getPostConditions()) {
			condition.setEnv(env);
			condition.accept(this);
		}
		
		symbol.setEnv(env);
		
		return null;
	}

	@Override
	public Void visit(DataDependentCondition condition) {
		
		org.iguana.datadependent.ast.Expression expression = condition.getExpression();
		
		expression.setEnv(condition.getEnv());
		expression.accept(this);
		
		return null;
	}

	@Override
	public Void visit(PositionalCondition condition) {
		return null;
	}

	@Override
	public Void visit(RegularExpressionCondition condition) {
		return null;
	}
	
}
