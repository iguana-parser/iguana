package org.iguana.datadependent.traversal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.iguana.datadependent.ast.AST;
import org.iguana.datadependent.ast.AbstractAST;
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
import org.iguana.grammar.condition.Condition;
import org.iguana.grammar.condition.ContextFreeCondition;
import org.iguana.grammar.condition.DataDependentCondition;
import org.iguana.grammar.condition.PositionalCondition;
import org.iguana.grammar.condition.RegularExpressionCondition;
import org.iguana.grammar.exception.UndeclaredVariableException;
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
import org.iguana.traversal.IConditionVisitor;
import org.iguana.traversal.ISymbolVisitor;

public class VarToInt implements IAbstractASTVisitor<AbstractAST>, ISymbolVisitor<Symbol>, IConditionVisitor<Condition> {
	
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
	
	private Symbol visit(Symbol symbol) {
		java.lang.String label = symbol.getLabel();
		
		if (label != null && !label.isEmpty())
			current.put(label, current.size());
		
		Symbol sym = symbol.accept(this);
		
		Set<Condition> preConditions = new HashSet<>();
		Set<Condition> postConditions = new HashSet<>();
				
		for (Condition condition : symbol.getPreConditions())
			preConditions.add(condition.accept(this));
		
		for (Condition condition : symbol.getPostConditions())
			postConditions.add(condition.accept(this));
		
		return sym.copyBuilder()
				.setLabel(symbol.getLabel())
				.addPreConditions(preConditions)
				.addPostConditions(postConditions)
				.build();
	}

	@Override
	public Symbol visit(Align symbol) {
		return Align.align(visit(symbol.getSymbol()));
	}

	@Override
	public Symbol visit(Block symbol) {
		throw new RuntimeException("Unsupported symbol: var-to-int!");
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
		Symbol sym = visit(symbol.getSymbol());
		
		Statement[] statements = new Statement[symbol.getStatements().length];
		int i = 0;
		for (Statement statement : symbol.getStatements())
			statements[i++] = (Statement) statement.accept(this);
		
		return Code.code(sym, statements);
	}

	@Override
	public Symbol visit(Conditional symbol) {
		Symbol sym = visit(symbol.getSymbol());
		return Conditional.when(sym, (org.iguana.datadependent.ast.Expression) symbol.getExpression().accept(this));
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
		throw new RuntimeException("Unsupported symbol: var-to-int!");
	}

	@Override
	public Symbol visit(IfThenElse symbol) {
		throw new RuntimeException("Unsupported symbol: var-to-int!");
	}

	@Override
	public Symbol visit(Ignore symbol) {
		return Ignore.ignore(visit(symbol.getSymbol()));
	}

	@Override
	public Symbol visit(Nonterminal symbol) {
		java.lang.String variable = symbol.getVariable();
		
		if (variable != null && !variable.isEmpty())
			current.put(variable, current.size());
		
		return symbol;
	}

	@Override
	public Symbol visit(Offside symbol) {
		return Offside.offside(visit(symbol.getSymbol()));
	}

	@Override
	public Symbol visit(Terminal symbol) {
		return symbol;
	}

	@Override
	public Symbol visit(While symbol) {
		throw new RuntimeException("Unsupported symbol: var-to-int!");
	}

	@Override
	public Symbol visit(Return symbol) {
		return Return.ret((org.iguana.datadependent.ast.Expression) symbol.getExpression().accept(this));
	}

	@Override
	public <E extends Symbol> Symbol visit(Alt<E> symbol) {
		throw new RuntimeException("Unsupported symbol: var-to-int!");
	}

	@Override
	public Symbol visit(Opt symbol) {
		throw new RuntimeException("Unsupported symbol: var-to-int!");
	}

	@Override
	public Symbol visit(Plus symbol) {
		throw new RuntimeException("Unsupported symbol: var-to-int!");
	}

	@Override
	public <E extends Symbol> Symbol visit(Sequence<E> symbol) {
		throw new RuntimeException("Unsupported symbol: var-to-int!");
	}

	@Override
	public Symbol visit(Star symbol) {
		throw new RuntimeException("Unsupported symbol: var-to-int!");
	}

	@Override
	public AbstractAST visit(Boolean expression) {
		return expression;
	}

	@Override
	public AbstractAST visit(Integer expression) {
		return expression;
	}

	@Override
	public AbstractAST visit(Real expression) {
		return expression;
	}

	@Override
	public AbstractAST visit(String expression) {
		return expression;
	}

	@Override
	public AbstractAST visit(Tuple expression) {
		org.iguana.datadependent.ast.Expression[] expressions = new org.iguana.datadependent.ast.Expression[expression.getElements().length];
		
		int i = 0;
		for (org.iguana.datadependent.ast.Expression e : expression.getElements())
			expressions[i++] = (org.iguana.datadependent.ast.Expression) e.accept(this);
		
		return AST.tuple(expressions);
	}

	@Override
	public AbstractAST visit(Name expression) {
		java.lang.String name = expression.getName();
		java.lang.Integer i = current.get(name);
		
		if (i == null)
			throw new UndeclaredVariableException(name);
		
		return AST.var(name, i);
	}

	@Override
	public AbstractAST visit(Call expression) {
		org.iguana.datadependent.ast.Expression[] arguments = new org.iguana.datadependent.ast.Expression[expression.getArguments().length];
		int i = 0;
		for (org.iguana.datadependent.ast.Expression e : expression.getArguments())
			arguments[i++] = (org.iguana.datadependent.ast.Expression) e.accept(this);
		
		java.lang.String name = expression.getFunName();
		
		switch(name) {
			case "println": 
				return AST.println(arguments);
			case "indent": 
				return AST.indent(arguments[0]);
			case "ppDeclare": 
				return AST.ppDeclare(arguments[0], arguments[1]);
			case "ppLookup": 
				return AST.ppLookup(arguments[0]);
			case "endsWith": 
				return AST.endsWith(arguments[0], arguments[1]);
			case "startsWith": 
				return AST.startsWith(arguments[0]);
			case "not": 
				return AST.not(arguments[0]);
			case "neg": 
				return AST.neg(arguments[0]);
			case "len": 
				return AST.len(arguments[0]);
			case "pr1": 
				return AST.pr1(arguments[0], arguments[1], arguments[2]);
			case "pr2":
				org.iguana.datadependent.ast.Expression[] rest = new org.iguana.datadependent.ast.Expression[arguments.length - 2];
				System.arraycopy(arguments, 2, rest, 0, arguments.length - 2);
				return AST.pr2(arguments[0], arguments[1], rest);
			case "pr3": 
				return AST.pr3(arguments[0], arguments[1]);
			case "min": 
				return AST.min(arguments[0], arguments[1]);
			case "map": 
				return AST.map();
			case "put": 
				if (arguments.length == 2)
					return AST.put(arguments[0], arguments[1]);
				else
					return AST.put(arguments[0], arguments[1], arguments[3]);
			case "contains": 
				return AST.contains(arguments[0], arguments[1]);
			case "push": 
				return AST.push(arguments[0], arguments[1]);
			case "pop": 
				return AST.pop(arguments[0]);
			case "top": 
				return AST.top(arguments[0]);
			case "find": 
				return AST.find(arguments[0], arguments[1]);
			case "get": 
				return AST.get(arguments[0], arguments[1]);
			case "undef": 
				return AST.undef();
			default:
				throw new UndeclaredVariableException(name);
		}
	}

	@Override
	public AbstractAST visit(Assignment expression) {
		
		java.lang.String id = expression.getId();
		java.lang.Integer i = current.get(id);
		
		if (i == null)
			throw new UndeclaredVariableException(id);
		
		return AST.assign(id, i, (org.iguana.datadependent.ast.Expression) expression.getExpression().accept(this));
	}

	@Override
	public AbstractAST visit(LShiftANDEqZero expression) {
		return AST.lShiftANDEqZero((org.iguana.datadependent.ast.Expression) expression.getLhs().accept(this), 
								   (org.iguana.datadependent.ast.Expression) expression.getRhs().accept(this));
	}

	@Override
	public AbstractAST visit(OrIndent expression) {
		return AST.orIndent((org.iguana.datadependent.ast.Expression) expression.getIndex().accept(this), 
							(org.iguana.datadependent.ast.Expression) expression.getIndent().accept(this), 
							(org.iguana.datadependent.ast.Expression) expression.getFirst().accept(this), 
							(org.iguana.datadependent.ast.Expression) expression.getLExt().accept(this));
	}

	@Override
	public AbstractAST visit(AndIndent expression) {
		return AST.andIndent((org.iguana.datadependent.ast.Expression) expression.getIndex().accept(this), 
							 (org.iguana.datadependent.ast.Expression) expression.getFirst().accept(this), 
							 (org.iguana.datadependent.ast.Expression) expression.getLExt().accept(this));
	}

	@Override
	public AbstractAST visit(Or expression) {
		return AST.or((org.iguana.datadependent.ast.Expression) expression.getLhs().accept(this), 
				      (org.iguana.datadependent.ast.Expression) expression.getRhs().accept(this));
	}

	@Override
	public AbstractAST visit(And expression) {
		return AST.and((org.iguana.datadependent.ast.Expression) expression.getLhs().accept(this), 
				   	   (org.iguana.datadependent.ast.Expression) expression.getRhs().accept(this));
	}

	@Override
	public AbstractAST visit(Less expression) {
		return AST.less((org.iguana.datadependent.ast.Expression) expression.getLhs().accept(this), 
				        (org.iguana.datadependent.ast.Expression) expression.getRhs().accept(this));
	}

	@Override
	public AbstractAST visit(LessThanEqual expression) {
		return AST.lessEq((org.iguana.datadependent.ast.Expression) expression.getLhs().accept(this), 
				   	      (org.iguana.datadependent.ast.Expression) expression.getRhs().accept(this));
	}

	@Override
	public AbstractAST visit(Greater expression) {
		return AST.greater((org.iguana.datadependent.ast.Expression) expression.getLhs().accept(this), 
				           (org.iguana.datadependent.ast.Expression) expression.getRhs().accept(this));
	}

	@Override
	public AbstractAST visit(GreaterThanEqual expression) {
		return AST.greaterEq((org.iguana.datadependent.ast.Expression) expression.getLhs().accept(this), 
				             (org.iguana.datadependent.ast.Expression) expression.getRhs().accept(this));
	}

	@Override
	public AbstractAST visit(Equal expression) {
		return AST.equal((org.iguana.datadependent.ast.Expression) expression.getLhs().accept(this), 
				         (org.iguana.datadependent.ast.Expression) expression.getRhs().accept(this));
	}

	@Override
	public AbstractAST visit(NotEqual expression) {
		return AST.notEqual((org.iguana.datadependent.ast.Expression) expression.getLhs().accept(this), 
				            (org.iguana.datadependent.ast.Expression) expression.getRhs().accept(this));
	}

	@Override
	public AbstractAST visit(LeftExtent expression) {
		throw new RuntimeException("Unsupported yet expression: " + expression);
	}

	@Override
	public AbstractAST visit(RightExtent expression) {
		throw new RuntimeException("Unsupported yet expression: " + expression);
	}

	@Override
	public AbstractAST visit(Yield expression) {
		throw new RuntimeException("Unsupported yet expression: " + expression);
	}

	@Override
	public AbstractAST visit(Val expression) {
		throw new RuntimeException("Unsupported yet expression: " + expression);
	}

	@Override
	public AbstractAST visit(EndOfFile expression) {
		return expression;
	}

	@Override
	public AbstractAST visit(org.iguana.datadependent.ast.Expression.IfThenElse expression) {
		return AST.ifThenElse((org.iguana.datadependent.ast.Expression) expression.getCondition().accept(this), 
				              (org.iguana.datadependent.ast.Expression) expression.getThenPart().accept(this), 
				              (org.iguana.datadependent.ast.Expression) expression.getElsePart().accept(this));
	}

	@Override
	public AbstractAST visit(VariableDeclaration declaration) {
		current.put(declaration.getName(), current.size());
		return null;
	}

	@Override
	public AbstractAST visit(org.iguana.datadependent.ast.Statement.VariableDeclaration declaration) {
		declaration.getDeclaration().accept(this);
		return null;
	}

	@Override
	public AbstractAST visit(Expression statement) {
		return null;
	}

	@Override
	public Condition visit(ContextFreeCondition condition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Condition visit(DataDependentCondition condition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Condition visit(PositionalCondition condition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Condition visit(RegularExpressionCondition condition) {
		// TODO Auto-generated method stub
		return null;
	}

}
