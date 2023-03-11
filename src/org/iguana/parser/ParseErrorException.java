package org.iguana.parser;

@SuppressWarnings("serial")
public class ParseErrorException extends RuntimeException {

    private final ParseError<?> parseError;

    public ParseErrorException(ParseError<?> parseError) {
        this.parseError = parseError;
    }

    public ParseError<?> getParseError() {
        return parseError;
    }

    @Override
    public String toString() {
        return parseError.toString();
    }
}
