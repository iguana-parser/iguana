package org.iguana.datadependent.traversal;

import java.util.HashMap;
import java.util.Map;

import org.iguana.datadependent.ast.Expression.And;
import org.iguana.datadependent.ast.Expression.AndIndent;
import org.iguana.datadependent.ast.Expression.Assignment;
import org.iguana.datadependent.ast.Expression.Boolean;
import org.iguana.datadependent.ast.Expression.Call;
import org.iguana.datadependent.ast.Expression.EndOfFile;
import org.iguana.datadependent.ast.Expression.Equal;
import org.iguana.datadependent.ast.Expression.Greater;
import org.iguana.datadependent.ast.Expression.GreaterThanEqual;
import org.iguana.datadependent.ast.Expression.Integer;
import org.iguana.datadependent.ast.Expression.LShiftANDEqZero;
import org.iguana.datadependent.ast.Expression.LeftExtent;
import org.iguana.datadependent.ast.Expression.Less;
import org.iguana.datadependent.ast.Expression.LessThanEqual;
import org.iguana.datadependent.ast.Expression.Name;
import org.iguana.datadependent.ast.Expression.NotEqual;
import org.iguana.datadependent.ast.Expression.Or;
import org.iguana.datadependent.ast.Expression.OrIndent;
import org.iguana.datadependent.ast.Expression.Real;
import org.iguana.datadependent.ast.Expression.RightExtent;
import org.iguana.datadependent.ast.Expression.String;
import org.iguana.datadependent.ast.Expression.Tuple;
import org.iguana.datadependent.ast.Expression.Val;
import org.iguana.datadependent.ast.Expression.Yield;
import org.iguana.datadependent.ast.Statement;
import org.iguana.datadependent.ast.Statement.Expression;
import org.iguana.datadependent.ast.VariableDeclaration;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.Align;
import org.iguana.grammar.symbol.Block;
import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.CharacterRange;
import org.iguana.grammar.symbol.Code;
import org.iguana.grammar.symbol.Conditional;
import org.iguana.grammar.symbol.EOF;
import org.iguana.grammar.symbol.Epsilon;
import org.iguana.grammar.symbol.IfThen;
import org.iguana.grammar.symbol.IfThenElse;
import org.iguana.grammar.symbol.Ignore;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Offside;
import org.iguana.grammar.symbol.Return;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.symbol.Symbol;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.grammar.symbol.While;
import org.iguana.regex.Alt;
import org.iguana.regex.Opt;
import org.iguana.regex.Plus;
import org.iguana.regex.Sequence;
import org.iguana.regex.Star;
import org.iguana.traversal.ISymbolVisitor;

public class VarToInt implements IAbstractASTVisitor<Void>, ISymbolVisitor<Void> {
	
	private Map<java.lang.Integer, Map<java.lang.String, java.lang.Integer>> mapping;
	
	private Map<java.lang.String, java.lang.Integer> current;
	
	public Map<java.lang.Integer, Map<java.lang.String, java.lang.Integer>> visit(Grammar grammar) {
		mapping = new HashMap<>();
		int i = 0;
		for (Rule rule : grammar.getRules()) {
			mapping.put(i, current);
			visit(rule);
			i++;
		}
		return mapping;
	}
	
	public Map<java.lang.String, java.lang.Integer> visit(Rule rule) {
		current = new HashMap<>();
		
		java.lang.String[] parameters = rule.getHead().getParameters();
		
		for (java.lang.String parameter : parameters)
			current.put(parameter, current.size());
		
		for (Symbol symbol : rule.getBody())
			visit(symbol);
		
		return current;
	}
	
	private Void visit(Symbol symbol) {
		java.lang.String label = symbol.getLabel();
		if (label != null && !label.isEmpty())
			current.put(label, current.size());
		
		symbol.accept(this);
		return null;
	}

	@Override
	public Void visit(Align symbol) {
		return visit(symbol.getSymbol());
	}

	@Override
	public Void visit(Block symbol) {
		throw new RuntimeException("Unsupported symbol: var-to-int!");
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
		visit(symbol.getSymbol());
		
		for (Statement statement : symbol.getStatements())
			statement.accept(this);
		
		return null;
	}

	@Override
	public Void visit(Conditional symbol) {
		return visit(symbol.getSymbol());
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
		throw new RuntimeException("Unsupported symbol: var-to-int!");
	}

	@Override
	public Void visit(IfThenElse symbol) {
		throw new RuntimeException("Unsupported symbol: var-to-int!");
	}

	@Override
	public Void visit(Ignore symbol) {
		return visit(symbol.getSymbol());
	}

	@Override
	public Void visit(Nonterminal symbol) {
		java.lang.String variable = symbol.getVariable();
		
		if (variable != null && !variable.isEmpty())
			current.put(variable, current.size());
		
		return null;
	}

	@Override
	public Void visit(Offside symbol) {
		return visit(symbol.getSymbol());
	}

	@Override
	public Void visit(Terminal symbol) {
		return null;
	}

	@Override
	public Void visit(While symbol) {
		throw new RuntimeException("Unsupported symbol: var-to-int!");
	}

	@Override
	public Void visit(Return symbol) {
		return null;
	}

	@Override
	public <E extends Symbol> Void visit(Alt<E> symbol) {
		throw new RuntimeException("Unsupported symbol: var-to-int!");
	}

	@Override
	public Void visit(Opt symbol) {
		throw new RuntimeException("Unsupported symbol: var-to-int!");
	}

	@Override
	public Void visit(Plus symbol) {
		throw new RuntimeException("Unsupported symbol: var-to-int!");
	}

	@Override
	public <E extends Symbol> Void visit(Sequence<E> symbol) {
		throw new RuntimeException("Unsupported symbol: var-to-int!");
	}

	@Override
	public Void visit(Star symbol) {
		throw new RuntimeException("Unsupported symbol: var-to-int!");
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
		return null;
	}

	@Override
	public Void visit(Name expression) {
		return null;
	}

	@Override
	public Void visit(Call expression) {
		return null;
	}

	@Override
	public Void visit(Assignment expression) {
		return null;
	}

	@Override
	public Void visit(LShiftANDEqZero expression) {
		return null;
	}

	@Override
	public Void visit(OrIndent expression) {
		return null;
	}

	@Override
	public Void visit(AndIndent expression) {
		return null;
	}

	@Override
	public Void visit(Or expression) {
		return null;
	}

	@Override
	public Void visit(And expression) {
		return null;
	}

	@Override
	public Void visit(Less expression) {
		return null;
	}

	@Override
	public Void visit(LessThanEqual expression) {
		return null;
	}

	@Override
	public Void visit(Greater expression) {
		return null;
	}

	@Override
	public Void visit(GreaterThanEqual expression) {
		return null;
	}

	@Override
	public Void visit(Equal expression) {
		return null;
	}

	@Override
	public Void visit(NotEqual expression) {
		return null;
	}

	@Override
	public Void visit(LeftExtent expression) {
		return null;
	}

	@Override
	public Void visit(RightExtent expression) {
		return null;
	}

	@Override
	public Void visit(Yield expression) {
		return null;
	}

	@Override
	public Void visit(Val expression) {
		return null;
	}

	@Override
	public Void visit(EndOfFile expression) {
		return null;
	}

	@Override
	public Void visit(org.iguana.datadependent.ast.Expression.IfThenElse expression) {
		return null;
	}

	@Override
	public Void visit(VariableDeclaration declaration) {
		current.put(declaration.getName(), current.size());
		return null;
	}

	@Override
	public Void visit(org.iguana.datadependent.ast.Statement.VariableDeclaration declaration) {
		declaration.getDeclaration().accept(this);
		return null;
	}

	@Override
	public Void visit(Expression statement) {
		return null;
	}

}
