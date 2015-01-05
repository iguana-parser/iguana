package org.jgll.grammar.symbol;

import java.util.HashSet;
import java.util.Set;

import org.jgll.grammar.GrammarRegistry;
import org.jgll.grammar.condition.Condition;

public class CodeBlock implements Symbol {

	private static final long serialVersionUID = 1L;
	
	private final org.jgll.datadependent.ast.Statement[] statements;
	
	CodeBlock(org.jgll.datadependent.ast.Statement[] statements) {
		this.statements = statements;
	}
	
	public static CodeBlock code(org.jgll.datadependent.ast.Statement... statements) {
		return new CodeBlock(statements);
	}
	
	public  org.jgll.datadependent.ast.Statement[] getStatements() {
		return statements;
	}

	@Override
	public String getConstructorCode(GrammarRegistry registry) {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public Set<Condition> getPreConditions() {
		return new HashSet<>();
	}

	@Override
	public Set<Condition> getPostConditions() {
		return new HashSet<>();
	}

	@Override
	public Object getObject() {
		return null;
	}

	@Override
	public String getLabel() {
		return null;
	}

}
