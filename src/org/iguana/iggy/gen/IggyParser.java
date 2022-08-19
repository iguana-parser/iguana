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
import java.io.InputStream;

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
        String grammarJsonFile = grammarName + ".json";
        try (InputStream in = IggyParser.class.getResourceAsStream(grammarJsonFile)) {
            if (in == null) throw new RuntimeException("Grammar json file " + grammarJsonFile + " is not found.");
            String content = readFile(in);
            return JsonSerializer.deserialize(content, Grammar.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
