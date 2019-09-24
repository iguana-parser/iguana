package org.iguana.iggy;

import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.transformation.DesugarPrecedenceAndAssociativity;
import org.iguana.grammar.transformation.DesugarStartSymbol;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.grammar.transformation.LayoutWeaver;
import org.iguana.parser.IguanaParser;
import org.iguana.parsetree.*;
import org.iguana.util.serialization.JsonSerializer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

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
        Grammar g = Grammar.load(new File("/Users/afroozeh/iggy"));
        g = new EBNFToBNF().transform(g);
        DesugarPrecedenceAndAssociativity precedenceAndAssociativity = new DesugarPrecedenceAndAssociativity();
        precedenceAndAssociativity.setOP2();
        g = precedenceAndAssociativity.transform(g);
        g = new LayoutWeaver().transform(g);
        g = new DesugarStartSymbol().transform(g);
        System.out.println(JsonSerializer.toJSON(g));
    }

    public static Grammar getGrammar(Path path) throws IOException {
        return getGrammar(path.toAbsolutePath().toString());
    }

    public static Grammar getGrammar(String path) throws IOException {
        Input input = Input.fromFile(new File(path));
        Grammar iggyGrammar = iggyGrammar();
        IguanaParser parser = new IguanaParser(iggyGrammar);

        ParseTreeNode parseTree = parser.getParserTree(input);
        if (parseTree == null) {
            throw new RuntimeException("Parse error");
        }

        Grammar grammar = (Grammar) parseTree.accept(new IggyToGrammarVisitor());

        DesugarPrecedenceAndAssociativity precedenceAndAssociativity = new DesugarPrecedenceAndAssociativity();
        precedenceAndAssociativity.setOP2();
        grammar = precedenceAndAssociativity.transform(grammar);

        grammar = new EBNFToBNF().transform(grammar);
        grammar = new LayoutWeaver().transform(grammar);
        grammar = new DesugarStartSymbol().transform(grammar);
        return grammar;
    }

}
