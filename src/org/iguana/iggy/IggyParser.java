package org.iguana.iggy;

import iguana.utils.input.Input;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.transformation.*;
import org.iguana.parser.IguanaParser;
import org.iguana.parsetree.*;
import org.iguana.util.serialization.JsonSerializer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static iguana.utils.io.FileUtils.readFile;

public class IggyParser {

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

    public static Grammar getGrammar(String path) throws IOException {
        IguanaParser parser = new IguanaParser(transform(iggyGrammar().toRuntimeGrammar()));

        Input input = Input.fromFile(new File(path));
        ParseTreeNode parseTree = parser.getParserTree(input);
        if (parseTree == null) {
            System.out.println(parser.getParseError());
            throw new RuntimeException("Parse error: " + path);
        }

        return (Grammar) parseTree.accept(new IggyToGrammarVisitor());
    }

    public static RuntimeGrammar transform(RuntimeGrammar runtimeGrammar) {
        RuntimeGrammar grammar = new ResolveIdentifiers().transform(runtimeGrammar);
        grammar = new EBNFToBNF().transform(grammar);
        grammar = new DesugarStartSymbol().transform(grammar);
        grammar = new DesugarState().transform(grammar);
        DesugarPrecedenceAndAssociativity precedenceAndAssociativity = new DesugarPrecedenceAndAssociativity();
        precedenceAndAssociativity.setOP2();
        grammar = precedenceAndAssociativity.transform(grammar);
        grammar = new LayoutWeaver().transform(grammar);
        return grammar;
    }

    public static RuntimeGrammar getRuntimeGrammar(String path) throws IOException {
        return transform(getGrammar(path).toRuntimeGrammar());
    }

}
