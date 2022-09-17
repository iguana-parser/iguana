package org.iguana.grammar.symbol;

import org.iguana.datadependent.ast.Statement;
import org.iguana.grammar.condition.Condition;
import org.iguana.traversal.ISymbolVisitor;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    public List<Condition> getPreConditions() {
        return Collections.emptyList();
    }

    @Override
    public List<Condition> getPostConditions() {
        return Collections.emptyList();
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
    public Map<String, Object> getAttributes() {
        return Collections.emptyMap();
    }

    @Override
    public <T> T accept(ISymbolVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public io.usethesource.capsule.Set.Immutable<String> getEnv() {
        return null;
    }

    @Override
    public void setEnv(io.usethesource.capsule.Set.Immutable<String> env) {
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
