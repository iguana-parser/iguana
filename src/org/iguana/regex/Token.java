package org.iguana.regex;

import org.iguana.utils.input.Input;

import java.util.Objects;

import static org.iguana.utils.string.StringUtil.escapeNewLine;

public class Token {

    /**
     * The regular expression used to match this token
     */
    private final RegularExpression regularExpression;
    /**
     * A string representing the type of this token, e.g., identifier, keyword, string literal, etc.
     */
    private final String type;
    private final Input input;
    private final int start;
    private final int end;

    public Token(RegularExpression regularExpression, String type, Input input, int start, int end) {
        this.regularExpression = regularExpression;
        this.type = type;
        this.input = input;
        this.start = start;
        this.end = end;
    }

    public String getLexeme() {
        return input.subString(start, end);
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public RegularExpression getRegularExpression() {
        return regularExpression;
    }

    public String getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Token)) return false;
        Token token = (Token) o;
        return input == token.input
               && start == token.start
               && end == token.end
               && Objects.equals(regularExpression, token.regularExpression)
               && Objects.equals(type, token.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(regularExpression, type, input, start, end);
    }

    @Override
    public String toString() {
        return String.format("(%s, %d, %d, \"%s\")", type, start, end, escapeNewLine(getLexeme()));
    }
}
