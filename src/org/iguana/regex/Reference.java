package org.iguana.regex;

import org.iguana.regex.visitor.RegularExpressionVisitor;

import java.util.Objects;
import java.util.Set;

public class Reference extends AbstractRegularExpression {

    private final String name;

    public static Reference from(String name) {
        return new Builder(name).build();
    }

    public Reference(Builder builder) {
        super(builder);
        this.name = builder.name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean isNullable() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<CharRange> getFirstSet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<CharRange> getNotFollowSet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int length() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reference)) return false;
        Reference reference = (Reference) o;
        return Objects.equals(name, reference.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public <T> T accept(RegularExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public RegexBuilder<? extends RegularExpression> copy() {
        return null;
    }

    @Override
    public String toString() {
        return name;
    }

    public static class Builder extends RegexBuilder<Reference> {

        private String name;

        public Builder() { }

        Builder(String name) {
            this.name = name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public Reference build() {
            return new Reference(this);
        }
    }
}
