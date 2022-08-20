package org.iguana.iggy;

import org.iguana.grammar.Grammar;
import org.iguana.iggy.gen.IggyParser;
import org.iguana.parser.IguanaParser;
import org.iguana.parsetree.ParseTreeNode;
import org.iguana.utils.input.Input;

import java.io.File;
import java.io.IOException;

public class IggyParserUtils {

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
