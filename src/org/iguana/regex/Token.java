package org.iguana.regex;

public class Token {

    private final String lexeme;
    private final RegularExpression regularExpression;
    private final String name;

    public Token(String lexeme, RegularExpression regularExpression, String name) {
        this.lexeme = lexeme;
        this.regularExpression = regularExpression;
        this.name = name;
    }

    public String getLexeme() {
        return lexeme;
    }

    public RegularExpression getRegularExpression() {
        return regularExpression;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", name, lexeme);
    }
}
