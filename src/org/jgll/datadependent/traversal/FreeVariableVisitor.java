package org.jgll.datadependent.traversal;

import java.util.ArrayList;
import java.util.List;

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
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.grammar.symbol.CodeBlock;
import org.jgll.grammar.symbol.EOF;
import org.jgll.grammar.symbol.Epsilon;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.grammar.symbol.Terminal;
import org.jgll.regex.Alt;
import org.jgll.regex.Opt;
import org.jgll.regex.Plus;
import org.jgll.regex.Sequence;
import org.jgll.regex.Star;
import org.jgll.traversal.IConditionVisitor;
import org.jgll.traversal.ISymbolVisitor;

public class FreeVariableVisitor implements IAbstractASTVisitor<Void>, ISymbolVisitor<Void>, IConditionVisitor<Void> {
	
	private final List<java.lang.String> freeVariables = new ArrayList<>();

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
		if (!expression.getEnv().contains(expression.getName())) {
			freeVariables.add(expression.getName());
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
		if (!expression.getEnv().contains(expression.getId())) {
			freeVariables.add(expression.getId());
		}
		
		org.jgll.datadependent.ast.Expression exp = expression.getExpression();
		
		exp.setEnv(expression.getEnv());
		exp.accept(this);
		
		return null;
	}

	@Override
	public Void visit(Less expression) {
		expression.getLhs().setEnv(expression.getEnv());
		expression.getLhs().accept(this);
		
		expression.getRhs().setEnv(expression.getEnv());
		expression.getRhs().accept(this);
		
		return null;
	}

	@Override
	public Void visit(Greater expression) {
		expression.getLhs().setEnv(expression.getEnv());
		expression.getLhs().accept(this);
		
		expression.getRhs().setEnv(expression.getEnv());
		expression.getRhs().accept(this);
		
		return null;
	}

	@Override
	public Void visit(GreaterThanEqual expression) {		
		expression.getLhs().setEnv(expression.getEnv());
		expression.getLhs().accept(this);
		
		expression.getRhs().setEnv(expression.getEnv());
		expression.getRhs().accept(this);
		
		return null;
	}

	@Override
	public Void visit(Equal expression) {	
		expression.getLhs().setEnv(expression.getEnv());
		expression.getLhs().accept(this);
		
		expression.getRhs().setEnv(expression.getEnv());
		expression.getRhs().accept(this);
		
		return null;
	}

	@Override
	public Void visit(LeftExtent expression) {
		if (!expression.getEnv().contains(expression.getLabel())) {
			freeVariables.add(expression.getLabel());
		}
		return null;
	}

	@Override
	public Void visit(RightExtent expression) {
		if (!expression.getEnv().contains(expression.getLabel())) {
			freeVariables.add(expression.getLabel());
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
		statement.getExpression().setEnv(statement.getEnv());
		statement.getExpression().accept(this);
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
	public Void visit(CodeBlock symbol) {
		
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
	public Void visit(EOF symbol) {
		return null;
	}

	@Override
	public Void visit(Epsilon symbol) {
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
