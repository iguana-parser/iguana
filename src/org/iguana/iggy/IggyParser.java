package org.iguana.iggy;

import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.HighLevelGrammar;
import org.iguana.grammar.transformation.DesugarPrecedenceAndAssociativity;
import org.iguana.grammar.transformation.DesugarStartSymbol;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.grammar.transformation.LayoutWeaver;
import org.iguana.parser.IguanaParser;
import org.iguana.parsetree.*;
import org.iguana.util.serialization.JsonSerializer;

import java.io.File;
import java.io.IOException;

import static iguana.utils.io.FileUtils.readFile;

public class IggyParser {

    private static HighLevelGrammar highLevelIggyGrammar() {
        try {
            String content = readFile(IggyParser.class.getResourceAsStream("/iggy.json"));
            return JsonSerializer.deserialize(content, HighLevelGrammar.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        HighLevelGrammar g = HighLevelGrammar.load(new File("/Users/afroozeh/iggy"));
        System.out.println(JsonSerializer.toJSON(g));
    }

    public static HighLevelGrammar getHighLevelGrammar(String path) throws IOException {
        Grammar iggyGrammar = transform(highLevelIggyGrammar());
        IguanaParser parser = new IguanaParser(iggyGrammar);

        Input input = Input.fromFile(new File(path));
        ParseTreeNode parseTree = parser.getParserTree(input);
        if (parseTree == null) {
            throw new RuntimeException("Parse error");
        }

        return (HighLevelGrammar) parseTree.accept(new IggyToGrammarVisitor());
    }

    public static Grammar transform(HighLevelGrammar highLevelGrammar) {
        Grammar grammar = new EBNFToBNF().transform(highLevelGrammar.toGrammar());

        DesugarPrecedenceAndAssociativity precedenceAndAssociativity = new DesugarPrecedenceAndAssociativity();
        precedenceAndAssociativity.setOP2();
        grammar = precedenceAndAssociativity.transform(grammar);

        grammar = new LayoutWeaver().transform(grammar);
        grammar = new DesugarStartSymbol().transform(grammar);
        return grammar;
    }

    public static Grammar getGrammar(String path) throws IOException {
        return transform(getHighLevelGrammar(path));
    }

}
