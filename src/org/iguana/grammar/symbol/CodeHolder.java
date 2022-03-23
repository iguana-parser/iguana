package org.iguana.grammar.symbol;

import org.eclipse.imp.pdb.facts.util.ImmutableSet;
import org.iguana.datadependent.ast.Statement;
import org.iguana.grammar.condition.Condition;
import org.iguana.traversal.ISymbolVisitor;

import java.util.Objects;
import java.util.Set;

public class CodeHolder implements org.iguana.grammar.symbol.Symbol {

    public final Statement statement;

    public CodeHolder(Builder builder) {
        this.statement = builder.statement;
    }

    public CodeHolder(Statement statement) {
        this.statement = statement;
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
    public Builder copy() {
        return new Builder(this);
    }

    @Override
    public String toString(int j) {
        return null;
    }

    @Override
    public <T> T accept(ISymbolVisitor<T> visitor) {
        return visitor.visit(this);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CodeHolder)) return false;
        CodeHolder that = (CodeHolder) o;
        return Objects.equals(statement, that.statement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statement);
    }

    public static class Builder extends SymbolBuilder<CodeHolder> {

        public Statement statement;

        public Builder() {}

        public Builder(CodeHolder codeHolder) {
            this.statement = codeHolder.statement;
        }

        public void setStatement(Statement statement) {
            this.statement = statement;
        }

        @Override
        public CodeHolder build() {
            return new CodeHolder(this);
        }
    }
}
