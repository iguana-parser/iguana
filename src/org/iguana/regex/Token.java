package org.iguana.regex;

import org.iguana.utils.input.Input;

public class Token {

    private final RegularExpression regularExpression;
    private final String name;
    private final Input input;
    private final int start;
    private final int end;

    public Token(RegularExpression regularExpression, String name, Input input, int start, int end) {
        this.regularExpression = regularExpression;
        this.name = name;
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

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", name, getLexeme());
    }
}
