package org.iguana.iggy;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.Start;
import org.iguana.iggy.gen.IggyGrammar;
import org.iguana.iggy.gen.IggyParser;
import org.iguana.parser.IguanaParser;
import org.iguana.parser.ParseErrorException;
import org.iguana.parsetree.ParseTreeNode;
import org.iguana.utils.input.Input;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
        Start start = IggyGrammar.getGrammar().getStartSymbols().get(0);
        IguanaParser parser = IggyParser.getInstance();
        try {
            parser.parse(input, start);
        } catch (ParseErrorException e) {
            System.out.println(parser.getParseError());
            throw new RuntimeException(parser.getParseError().toString());
        }

        ParseTreeNode parseTree = parser.getParseTree();
        Object result = parseTree.accept(new IggyParseTreeToGrammarVisitor());
        if (result instanceof List<?>) { // When there is a start symbol
            List<?> nodes = (List<?>) result;
            if (nodes.size() == 1) { // No layout definition
                return (Grammar) nodes.get(0);
            } else { // Layout Grammar Layout
                return (Grammar) nodes.get(1);
            }
        } else {
            return (Grammar) result;
        }
    }
}
