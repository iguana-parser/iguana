package org.iguana.iggy;

import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.symbol.Symbol;
import org.iguana.grammar.transformation.DesugarPrecedenceAndAssociativity;
import org.iguana.grammar.transformation.DesugarStartSymbol;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.grammar.transformation.LayoutWeaver;
import org.iguana.parser.IguanaParser;
import org.iguana.parsetree.*;
import org.iguana.util.serialization.JsonSerializer;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        Grammar g = Grammar.load(new java.io.File("/Users/afroozeh/iggy"));
        DesugarPrecedenceAndAssociativity precedenceAndAssociativity = new DesugarPrecedenceAndAssociativity();
        precedenceAndAssociativity.setOP2();

        g = new EBNFToBNF().transform(g);
        g = precedenceAndAssociativity.transform(g);
        g = new LayoutWeaver().transform(g);
        g = new DesugarStartSymbol().transform(g);
        System.out.println(JsonSerializer.toJSON(g));
    }

    public static Grammar getGrammar(String path) throws IOException {
        Input input = Input.fromFile(new File(path));
        Grammar iggyGrammar = iggyGrammar();
        IguanaParser parser = new IguanaParser(iggyGrammar);

        ParseTreeNode parseTree = parser.parse(input);
        if (parseTree == null) {
            throw new RuntimeException("Parse error");
        }

        ParseTreeVisitor<Object> parseTreeVisitor = new ParseTreeVisitor<Object>() {

            @Override
            public Object visitNonterminalNode(org.iguana.parsetree.NonterminalNode node) {
                switch (node.getName()) {
                    case "Definition": {
                        Grammar.Builder builder = Grammar.builder();
                        List<Rule> rules = (List<Rule>) node.children().get(0);
                        rules.forEach(r -> builder.addRule(r));
                        return builder.build();
                    }

                    case "Rule":
                        switch (node.getGrammarDefinition().getLabel()) {

                            case "Syntax":
                                Nonterminal head = (Nonterminal) node.getChildWithName("NontName");
                                Object tag = node.getChildWithName("Tag?").accept(this);
                                if (tag != null) {
                                }
                                List<Symbol> body = (List<Symbol>) node.getChildWithName("Body").accept(this);
                                return new Rule.Builder(head).addSymbols(body).build();
                        }
                        break;

                    case "Identifier":
                        return input.subString(node.getStart(), node.getEnd());
                }

                throw new RuntimeException("Should not reach here");
            }

            @Override
            public Object visitAmbiguityNode(AmbiguityNode node) {
                return null;
            }

            @Override
            public Object visitTerminalNode(TerminalNode node) {
                return null;
            }

            @Override
            public Object visitMetaSymbolNode(MetaSymbolNode node) {
                return null;
            }
        };


        Grammar grammar = (Grammar) parseTree.accept(parseTreeVisitor);

        DesugarPrecedenceAndAssociativity precedenceAndAssociativity = new DesugarPrecedenceAndAssociativity();
        precedenceAndAssociativity.setOP2();
//        grammar = precedenceAndAssociativity.transform(grammar);

        grammar = new EBNFToBNF().transform(grammar);
        grammar = new LayoutWeaver().transform(grammar);
        grammar = new DesugarStartSymbol().transform(grammar);
        return grammar;
    }

}
