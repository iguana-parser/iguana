package org.jgll.datadependent.traversal;

import java.util.Set;

import org.eclipse.imp.pdb.facts.util.ImmutableSet;
import org.jgll.datadependent.ast.Expression.Assignment;
import org.jgll.datadependent.ast.Expression.Boolean;
import org.jgll.datadependent.ast.Expression.Call;
import org.jgll.datadependent.ast.Expression.Equal;
import org.jgll.datadependent.ast.Expression.Greater;
import org.jgll.datadependent.ast.Expression.GreaterThanEqual;
import org.jgll.datadependent.ast.Expression.Integer;
import org.jgll.datadependent.ast.Expression.LeftExtent;
import org.jgll.datadependent.ast.Expression.Less;
import org.jgll.datadependent.ast.Expression.Name;
import org.jgll.datadependent.ast.Expression.Real;
import org.jgll.datadependent.ast.Expression.RightExtent;
import org.jgll.datadependent.ast.Expression.String;
import org.jgll.datadependent.ast.Statement;
import org.jgll.datadependent.ast.Statement.Expression;
import org.jgll.datadependent.ast.VariableDeclaration;
import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.condition.ContextFreeCondition;
import org.jgll.grammar.condition.DataDependentCondition;
import org.jgll.grammar.condition.PositionalCondition;
import org.jgll.grammar.condition.RegularExpressionCondition;
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
import org.jgll.grammar.symbol.Symbol;
import org.jgll.grammar.symbol.Terminal;
import org.jgll.grammar.symbol.While;
import org.jgll.regex.Alt;
import org.jgll.regex.Opt;
import org.jgll.regex.Plus;
import org.jgll.regex.Sequence;
import org.jgll.regex.Star;
import org.jgll.traversal.IConditionVisitor;
import org.jgll.traversal.ISymbolVisitor;

public class FreeVariableVisitor implements IAbstractASTVisitor<Void>, ISymbolVisitor<Void>, IConditionVisitor<Void> {
	
	private final Set<java.lang.String> freeVariables;
	
	public FreeVariableVisitor(Set<java.lang.String> freeVariables) {
		this.freeVariables = freeVariables;
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
	public Void visit(Name expression) {
		java.lang.String name = expression.getName();
		
		if (!expression.getEnv().contains(name)) {
			freeVariables.add(name);
		}
		
		return null;
	}

	@Override
	public Void visit(Call expression) {
		
		for (org.jgll.datadependent.ast.Expression argument : expression.getArguments()) {
			argument.setEnv(expression.getEnv());
			argument.accept(this);
		}
		
		return null;
	}

	@Override
	public Void visit(Assignment expression) {
		
		java.lang.String id = expression.getId();
		org.jgll.datadependent.ast.Expression exp = expression.getExpression();
		
		if (!expression.getEnv().contains(id)) {
			freeVariables.add(id);
		}
		
		exp.setEnv(expression.getEnv());
		exp.accept(this);
		
		return null;
	}

	@Override
	public Void visit(Less expression) {
		
		org.jgll.datadependent.ast.Expression lhs = expression.getLhs();
		
		lhs.setEnv(expression.getEnv());
		lhs.accept(this);
		
		org.jgll.datadependent.ast.Expression rhs = expression.getRhs();
		
		rhs.setEnv(expression.getEnv());
		rhs.accept(this);
		
		return null;
	}

	@Override
	public Void visit(Greater expression) {
		
		org.jgll.datadependent.ast.Expression lhs = expression.getLhs();
		
		lhs.setEnv(expression.getEnv());
		lhs.accept(this);
		
		org.jgll.datadependent.ast.Expression rhs = expression.getRhs();
		
		rhs.setEnv(expression.getEnv());
		rhs.accept(this);
		
		return null;
	}

	@Override
	public Void visit(GreaterThanEqual expression) {	
		
		org.jgll.datadependent.ast.Expression lhs = expression.getLhs();
		
		lhs.setEnv(expression.getEnv());
		lhs.accept(this);
		
		org.jgll.datadependent.ast.Expression rhs = expression.getRhs();
		
		rhs.setEnv(expression.getEnv());
		rhs.accept(this);
		
		return null;
	}

	@Override
	public Void visit(Equal expression) {
		
		org.jgll.datadependent.ast.Expression lhs = expression.getLhs();
		
		lhs.setEnv(expression.getEnv());
		lhs.accept(this);
		
		org.jgll.datadependent.ast.Expression rhs = expression.getRhs();
		
		rhs.setEnv(expression.getEnv());
		rhs.accept(this);
		
		return null;
	}

	@Override
	public Void visit(LeftExtent expression) {
		
		java.lang.String name = java.lang.String.format(org.jgll.datadependent.ast.Expression.LeftExtent.format, expression.getLabel());
		
		if (!expression.getEnv().contains(name)) {
			freeVariables.add(name);
		}
		return null;
	}

	@Override
	public Void visit(RightExtent expression) {
		
		java.lang.String label = expression.getLabel();
		
		if (!expression.getEnv().contains(label)) {
			freeVariables.add(label);
		}
		return null;
	}

	@Override
	public Void visit(VariableDeclaration declaration) {
		
		org.jgll.datadependent.ast.Expression expression = declaration.getExpression();
		
		if (expression != null) {
			expression.setEnv(declaration.getEnv());
			expression.accept(this);
		}
		
		declaration.setEnv(declaration.getEnv().__insert(declaration.getName()));
		
		return null;
	}

	@Override
	public Void visit(org.jgll.datadependent.ast.Statement.VariableDeclaration statement) {
		
		VariableDeclaration declaration = statement.getDeclaration();
		
		declaration.setEnv(statement.getEnv());
		declaration.accept(this);
		
		statement.setEnv(declaration.getEnv());
		
		return null;
	}

	@Override
	public Void visit(Expression statement) {
		
		org.jgll.datadependent.ast.Expression expression = statement.getExpression();
		
		expression.setEnv(statement.getEnv());
		expression.accept(this);
		
		return null;
	}
	
	@Override
	public Void visit(Align symbol) {
		// FIXME: EBNF
		return null;
	}

	@Override
	public Void visit(Block symbol) {
		// FIXME: EBNF
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
		
		// FIXME: EBNF
		assert symbol.getPreConditions().isEmpty();
		assert symbol.getPostConditions().isEmpty();
		
		ImmutableSet<java.lang.String> env = symbol.getEnv();
		
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
		// FIXME: EBNF
		return null;
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
		// FIXME: EBNF
		return null;
	}

	@Override
	public Void visit(IfThenElse symbol) {
		// FIXME: EBNF
		return null;
	}

	@Override
	public Void visit(Nonterminal symbol) {
		
		ImmutableSet<java.lang.String> env = symbol.getEnv();
		
		if (symbol.getLabel() != null) {
			env = env.__insert(java.lang.String.format(LeftExtent.format, symbol.getLabel()));
		}
		
		for (Condition condition : symbol.getPreConditions()) {
			condition.setEnv(env);
			condition.accept(this);
		}
		
		if (symbol.getLabel() != null) {
			env = env.__insert(symbol.getLabel());
		}
		
		if (symbol.getVariable() != null) {
			env = env.__insert(symbol.getVariable());
		}
		
		for (Condition condition : symbol.getPostConditions()) {
			condition.setEnv(env);
			condition.accept(this);
		}
		
		symbol.setEnv(env);
		
		return null;
	}

	@Override
	public Void visit(Offside symbol) {
		// FIXME: EBNF
		return null;
	}
	
	@Override
	public Void visit(Terminal symbol) {
		
		ImmutableSet<java.lang.String> env = symbol.getEnv();
		
		if (symbol.getLabel() != null) {
			env = env.__insert(java.lang.String.format(LeftExtent.format, symbol.getLabel()));
		}
		
		for (Condition condition : symbol.getPreConditions()) {
			condition.setEnv(env);
			condition.accept(this);
		}
		
		if (symbol.getLabel() != null) {
			env = env.__insert(symbol.getLabel());
		}
		
		for (Condition condition : symbol.getPostConditions()) {
			condition.setEnv(env);
			condition.accept(this);
		}
		
		symbol.setEnv(env);
		
		return null;
	}
	
	@Override
	public Void visit(While symbol) {
		// FIXME: EBNF
		return null;
	}

	@Override
	public <E extends Symbol> Void visit(Alt<E> symbol) {
		
		ImmutableSet<java.lang.String> env = symbol.getEnv();
		
		if (symbol.getLabel() != null) {
			env = env.__insert(java.lang.String.format(LeftExtent.format, symbol.getLabel()));
		}
		
		for (Condition condition : symbol.getPreConditions()) {
			condition.setEnv(env);
			condition.accept(this);
		}
		
		for (Symbol sym : symbol.getSymbols()) {
			sym.setEnv(env);
			sym.accept(this);
		}
		
		if (symbol.getLabel() != null) {
			env = env.__insert(symbol.getLabel());
		}
		
		// TODO: Return values
		
		for (Condition condition : symbol.getPostConditions()) {
			condition.setEnv(env);
			condition.accept(this);
		}
		
		symbol.setEnv(env);
		
		return null;
	}

	@Override
	public Void visit(Opt symbol) {
		
		ImmutableSet<java.lang.String> env = symbol.getEnv();
		
		if (symbol.getLabel() != null) {
			env = env.__insert(java.lang.String.format(LeftExtent.format, symbol.getLabel()));
		}
		
		for (Condition condition : symbol.getPreConditions()) {
			condition.setEnv(env);
			condition.accept(this);
		}
		
		symbol.getSymbol().setEnv(env);
		symbol.getSymbol().accept(this);
		
		if (symbol.getLabel() != null) {
			env = env.__insert(symbol.getLabel());
		}
		
		// TODO: Return values
		
		for (Condition condition : symbol.getPostConditions()) {
			condition.setEnv(env);
			condition.accept(this);
		}
		
		symbol.setEnv(env);
		
		return null;
	}

	@Override
	public Void visit(Plus symbol) {
		
		ImmutableSet<java.lang.String> env = symbol.getEnv();
		
		if (symbol.getLabel() != null) {
			env = env.__insert(java.lang.String.format(LeftExtent.format, symbol.getLabel()));
		}
		
		for (Condition condition : symbol.getPreConditions()) {
			condition.setEnv(env);
			condition.accept(this);
		}
		
		symbol.getSymbol().setEnv(env);
		symbol.getSymbol().accept(this);
		
		if (symbol.getLabel() != null) {
			env = env.__insert(symbol.getLabel());
		}
		
		// TODO: Return values
		
		for (Condition condition : symbol.getPostConditions()) {
			condition.setEnv(env);
			condition.accept(this);
		}
		
		symbol.setEnv(env);
		
		return null;
	}

	@Override
	public <E extends Symbol> Void visit(Sequence<E> symbol) {
		
		ImmutableSet<java.lang.String> env = symbol.getEnv();
		
		if (symbol.getLabel() != null) {
			env = env.__insert(java.lang.String.format(LeftExtent.format, symbol.getLabel()));
		}
		
		for (Condition condition : symbol.getPreConditions()) {
			condition.setEnv(env);
			condition.accept(this);
		}
		
		for (E sym : symbol.getSymbols()) {
			sym.setEnv(env);
			sym.accept(this);
			env = sym.getEnv();
		}
		
		if (symbol.getLabel() != null) {
			env = env.__insert(symbol.getLabel());
		}
		
		// TODO: Return values
		
		for (Condition condition : symbol.getPostConditions()) {
			condition.setEnv(env);
			condition.accept(this);
		}
		
		symbol.setEnv(env);
		
		return null;
	}

	@Override
	public Void visit(Star symbol) {
		
		ImmutableSet<java.lang.String> env = symbol.getEnv();
		
		if (symbol.getLabel() != null) {
			env = env.__insert(java.lang.String.format(LeftExtent.format, symbol.getLabel()));
		}
		
		symbol.getSymbol().setEnv(env);
		symbol.getSymbol().accept(this);
		
		if (symbol.getLabel() != null) {
			env = env.__insert(symbol.getLabel());
		}
		
		// TODO: Return values
		
		for (Condition condition : symbol.getPostConditions()) {
			condition.setEnv(env);
			condition.accept(this);
		}
		
		symbol.setEnv(env);
		
		return null;
	}

	@Override
	public Void visit(ContextFreeCondition condition) {
		throw new UnsupportedOperationException("Context-free condition");
	}

	@Override
	public Void visit(DataDependentCondition condition) {
		condition.getExpression().setEnv(condition.getEnv());
		condition.getExpression().accept(this);
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
