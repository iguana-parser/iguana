// This file has been generated, do not directly edit this file!
package org.iguana.iggy.gen;

import org.iguana.grammar.Grammar;
import org.iguana.iggy.IggyToGrammarVisitor;
import org.iguana.parser.IguanaParser;
import org.iguana.parsetree.ParseTreeBuilder;
import org.iguana.parsetree.ParseTreeNode;
import org.iguana.util.serialization.JsonSerializer;
import org.iguana.utils.input.Input;

import java.io.File;
import java.io.IOException;

import static org.iguana.utils.io.FileUtils.readFile;

public class IggyParser extends IguanaParser {

    private IggyParser(Grammar grammar) {
        super(grammar);
    }

    private static final String grammarName = "iggy";

    private static IggyParser parser;

    private static Grammar grammar;

    public static Grammar getGrammar() {
        if (grammar == null) {
            grammar = loadGrammar();
        }
        return grammar;
     }

    // Creates a Grammar form the provided .iggy file
    public static Grammar fromIggyGrammarPath(String path) {
        Input input;
        try {
            input = Input.fromFile(new File(path));
        } catch (IOException e) {
            throw new RuntimeException(e);        }
        return createGrammar(input);
    }

    // Creates a Grammar form the provided grammar in string form
    public static Grammar fromIggyGrammar(String content) {
        Input input = Input.fromString(content);
        return createGrammar(input);
    }

    public static IggyParser getInstance() {
        if (parser == null) {
            parser = new IggyParser(getGrammar());
        }
        return parser;
    }

    @Override
    protected ParseTreeBuilder<ParseTreeNode> getParseTreeBuilder(Input input) {
        return new IggyParseTreeBuilder(input);
    }

    private static Grammar loadGrammar() {
        try {
            String content = readFile(IggyParser.class.getResourceAsStream("./" + grammarName + ".json"));
             return JsonSerializer.deserialize(content, Grammar.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Grammar createGrammar(Input input) {
            IguanaParser parser = IggyParser.getInstance();
            parser.parse(input);
        if (parser.hasParseError()) {
            System.out.println(parser.getParseError());
            throw new RuntimeException(parser.getParseError().toString());
        }
        ParseTreeNode parseTree = parser.getParseTree();
        return (Grammar) parseTree.accept(new IggyToGrammarVisitor());
    }
}
