package org.iguana.grammar.symbol;

import org.eclipse.imp.pdb.facts.util.ImmutableSet;
import org.iguana.datadependent.ast.Expression;
import org.iguana.datadependent.ast.Statement;
import org.iguana.grammar.condition.Condition;
import org.iguana.traversal.ISymbolVisitor;

import java.util.List;
import java.util.Set;

public class CodeHolder implements org.iguana.grammar.symbol.Symbol {

    public final List<Statement> statements;
    public final List<Expression> expressions;

    public CodeHolder(List<Statement> statements, List<Expression> expressions) {
        this.statements = statements;
        this.expressions = expressions;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Set<Condition> getPreConditions() {
        return null;
    }

    @Override
    public Set<Condition> getPostConditions() {
        return null;
    }

    @Override
    public String getLabel() {
        return null;
    }

    @Override
    public SymbolBuilder<? extends org.iguana.grammar.symbol.Symbol> copyBuilder() {
        return null;
    }

    @Override
    public String toString(int j) {
        return null;
    }

    @Override
    public <T> T accept(ISymbolVisitor<T> visitor) {
        return null;
    }

    @Override
    public ImmutableSet<String> getEnv() {
        return null;
    }

    @Override
    public void setEnv(ImmutableSet<String> env) {
    }

    @Override
    public void setEmpty() {
    }

}