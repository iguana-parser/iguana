package org.iguana.regex;

import org.iguana.regex.visitor.RegularExpressionVisitor;

import java.util.Set;

/**
 * Represents an error while matching. This token type is used by the recognizer
 * when matching fails.
 */
public class Error extends AbstractRegularExpression {

    private static Error instance;

    private static final RegexBuilder<Error> builder = new RegexBuilder<>() {
        @Override
        public Error build() {
            return instance;
        }
    };

    private Error() {
        super(builder);
    }

    public static Error getInstance() {
        if (instance == null) {
            instance = new Error();
        }
        return instance;
    }

    @Override
    public boolean isNullable() {
        return false;
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
    public <T> T accept(RegularExpressionVisitor<T> visitor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RegexBuilder<? extends RegularExpression> copy() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "error";
    }
}
