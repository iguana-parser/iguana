package org.iguana;

import org.iguana.grammar.symbol.Symbol;
import org.iguana.parser.options.ParseOptions;

public class ParserTest {

    private final String grammar;
    private final String input;
    private final Symbol startSymbol;
    private final boolean verifyParseTree;
    private final ParseOptions parseOptions;

    private ParserTest(ParserTestBuilder builder) {
        this.grammar = builder.grammar;
        this.input = builder.input;
        this.startSymbol = builder.startSymbol;
        this.verifyParseTree = builder.verifyParseTree;
        this.parseOptions = builder.parseOptions;
    }

    public String getGrammar() {
        return grammar;
    }

    public String getInput() {
        return input;
    }

    public boolean verifyParseTree() {
        return verifyParseTree;
    }

    public Symbol getStartSymbol() {
        return startSymbol;
    }

    public ParseOptions getParseOptions() {
        return parseOptions;
    }

    public static ParserTestBuilder newTest() {
        return new ParserTestBuilder();
    }

    public static class ParserTestBuilder {
        private String grammar;
        private String input;
        private Symbol startSymbol;
        private boolean verifyParseTree;
        private ParseOptions parseOptions;

        public ParserTestBuilder setGrammar(String grammar) {
            this.grammar = grammar;
            return this;
        }

        public ParserTestBuilder setInput(String input) {
            this.input = input;
            return this;
        }

        public ParserTestBuilder setStartSymbol(Symbol startSymbol) {
            this.startSymbol = startSymbol;
            return this;
        }

        public ParserTestBuilder verifyParseTree() {
            verifyParseTree = true;
            return this;
        }

        public ParserTestBuilder setParseOptions(ParseOptions parseOptions) {
            this.parseOptions = parseOptions;
            return this;
        }

        public ParserTest build() {
            if (startSymbol == null) throw new IllegalStateException("startSymbol cannot be null");
            return new ParserTest(this);
        }
    }
}
