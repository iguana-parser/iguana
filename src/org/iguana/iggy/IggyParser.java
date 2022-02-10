package org.iguana.iggy;

import iguana.regex.RegularExpression;
import iguana.utils.input.Input;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.slot.TerminalNodeType;
import org.iguana.grammar.symbol.Identifier;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.grammar.transformation.*;
import org.iguana.parser.IguanaParser;
import org.iguana.parsetree.*;
import org.iguana.util.serialization.JsonSerializer;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
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

    public static Grammar transform(Grammar grammar) {
        Set<String> nonterminals = new HashSet<>();
        for (Rule rule : grammar.getRules()) {
            nonterminals.add(rule.getHead().getName());
        }
        return GrammarTransformer.transform(grammar, new SymbolTransformation(
                s -> s instanceof Identifier,
                s -> {
                    Identifier id = (Identifier) s;
                    if (nonterminals.contains(id.getName())) {
                        return new Nonterminal.Builder(id.getName())
                                .addPostConditions(id.getPreConditions())
                                .addPostConditions(id.getPostConditions())
                                .setLabel(id.getLabel())
                                .build();
                    } else if (grammar.getTerminals().containsKey(id.getName())) {
                        RegularExpression regularExpression = grammar.getTerminals().get(id.getName());
                        return new Terminal.Builder(regularExpression)
                                .setNodeType(TerminalNodeType.Regex)
                                .setTerminalPostConditions(id.getPostConditions())
                                .setTerminalPreConditions(id.getPreConditions())
                                .setLabel(id.getLabel())
                                .build();
                    } else {
                        throw new RuntimeException("Id " + id + " cannot be resolved.");
                    }
                }
        ));
    }

    public static void main(String[] args) throws IOException {
        Grammar g = Grammar.load(new File("/Users/afroozeh/iggy"));
        System.out.println(JsonSerializer.toJSON(transform(g)));
    }

    public static Grammar getGrammar(String path) throws IOException {
        IguanaParser parser = new IguanaParser(transform(transform(iggyGrammar()).toRuntimeGrammar()));

        Input input = Input.fromFile(new File(path));
        ParseTreeNode parseTree = parser.getParserTree(input);
        if (parseTree == null) {
            System.out.println(parser.getParseError());
            throw new RuntimeException("Parse error");
        }

        return (Grammar) parseTree.accept(new IggyToGrammarVisitor());
    }

    public static RuntimeGrammar transform(RuntimeGrammar runtimeGrammar) {
        RuntimeGrammar grammar = new EBNFToBNF().transform(runtimeGrammar);

        grammar = new DesugarStartSymbol().transform(grammar);
        DesugarPrecedenceAndAssociativity precedenceAndAssociativity = new DesugarPrecedenceAndAssociativity();
        precedenceAndAssociativity.setOP2();
        grammar = precedenceAndAssociativity.transform(grammar);

        grammar = new LayoutWeaver().transform(grammar);
        return grammar;
    }

    public static RuntimeGrammar getRuntimeGrammar(String path) throws IOException {
        return transform(transform(getGrammar(path)).toRuntimeGrammar());
    }

}
