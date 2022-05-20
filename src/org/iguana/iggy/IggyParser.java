package org.iguana.iggy;

import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.parser.IguanaParser;
import org.iguana.parsetree.ParseTreeNode;
import org.iguana.util.serialization.JsonSerializer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static iguana.utils.io.FileUtils.readFile;

public class IggyParser {

    private static final IguanaParser parser = new IguanaParser(iggyGrammar());

    private static Grammar iggyGrammar() {
        try {
            String content = readFile(IggyParser.class.getResourceAsStream("/iggy.json"));
            return JsonSerializer.deserialize(content, Grammar.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) throws IOException {
//        Grammar grammar = Grammar.load(new File("/Users/afroozeh/iggy"));
//        System.out.println(JsonSerializer.toJSON(grammar));
        String path = Paths.get("src/resources/Iguana.iggy").toAbsolutePath().toString();
        Grammar grammar = getGrammar(path);
        System.out.println(JsonSerializer.toJSON(grammar));
        JsonSerializer.serialize(grammar, Paths.get("src/resources/iggy.json").toAbsolutePath().toString());
    }

    public static Grammar fromGrammar(String grammar)  {
        return getGrammar(Input.fromString(grammar));
    }

    public static Grammar getGrammar(String path) throws IOException {
        return getGrammar(Input.fromFile(new File(path)));
    }

    private static Grammar getGrammar(Input input) {
        parser.parse(input);
        if (parser.hasParseError()) {
            System.out.println(parser.getParseError());
            throw new RuntimeException(parser.getParseError().toString());
        }

        ParseTreeNode parseTree = parser.getParseTree();

        return (Grammar) parseTree.accept(new IggyToGrammarVisitor());
    }
}
