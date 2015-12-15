package org.iguana.regex;

import org.iguana.traversal.RegularExpressionVisitor;

import java.util.Set;

public class Name extends AbstractRegularExpression {

    private final String regexName;

    public static Name from(String regexName) {
        return new Builder(regexName).build();
    }

    public Name(Builder builder) {
        super(builder);
        this.regexName = builder.regexName;
    }

    @Override
    public boolean isNullable() {
        throw new RuntimeException("Names should be first inlined.");
    }

    @Override
    public Set<CharacterRange> getFirstSet() {
        throw new RuntimeException("Names should be first inlined.");
    }

    @Override
    public Set<CharacterRange> getNotFollowSet() {
        throw new RuntimeException("Names should be first inlined.");
    }

    @Override
    public int length() {
        throw new RuntimeException("Names should be first inlined.");
    }

    @Override
    public <T> T accept(RegularExpressionVisitor<T> visitor) {
        throw new RuntimeException("Names should be first inlined.");
    }

    @Override
    public RegexBuilder<? extends RegularExpression> copyBuilder() {
        return new Builder(regexName);
    }

    static class Builder extends RegexBuilder<Name> {

        private String regexName;

        public Builder(String regexName) {
            this.regexName = regexName;
        }

        @Override
        public Name build() {
            return new Name(this);
        }
    }
}
