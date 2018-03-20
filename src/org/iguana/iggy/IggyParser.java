package org.iguana.iggy;

import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.*;
import org.iguana.grammar.transformation.DesugarPrecedenceAndAssociativity;
import org.iguana.grammar.transformation.DesugarStartSymbol;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.grammar.transformation.LayoutWeaver;
import org.iguana.parser.Iguana;
import org.iguana.parser.ParseResult;
import org.iguana.parsetree.ParseTreeBuilder;
import org.iguana.util.serialization.JsonSerializer;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
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
        Input input = Input.fromPath(path);
        ParseResult result = Iguana.parse(input, iggyGrammar());

        if (result.isParseError()) {
            throw new RuntimeException("Parse error");
        }

        Grammar grammar = (Grammar) result.asParseSuccess().getParseTree(new ParseTreeBuilder<Object>() {

            private Start start;

            @Override
            public Object terminalNode(Terminal terminal, int leftExtent, int rightExtent) {
                return input.subString(leftExtent, rightExtent);
            }

            @Override
            public Object nonterminalNode(Rule rule, List<Object> children, int leftExtent, int rightExtent) {
                if (rule.isLayout()) return null;

                switch (rule.getHead().getName()) {

                    case "Definition": {
                        Grammar.Builder builder = Grammar.builder();
                        List<Rule> rules = (List<Rule>) children.get(0);
                        rules.forEach(r -> builder.addRule(r));
                        return builder.setStartSymbol(start).build();
                    }

                    case "Rule":
                        switch (rule.getLabel()) {

                            case "Syntax":
                                Nonterminal head = (Nonterminal) getByName(rule, children, "NontName");
                                Optional<String> tag = getOption("Tag?", rule, children);
                                if (tag.isPresent()) {
                                    switch (tag.get()) {
                                        case "@Start":
                                            start = Start.from(head);
                                            break;
                                    }
                                }
                                List<Symbol> body = (List<Symbol>) getByName(rule, children, "Body");
                                return new Rule.Builder(head).addSymbols(body).build();
                        }
                        break;

                    case "Body":
                        return children.get(0);

                    case "NontName":
                        return Nonterminal.withName((String) children.get(0));

                    case "Identifier":
                        return input.subString(leftExtent, rightExtent);

                    case "Layout":
                    case "WhiteSpaceOrComment":
                        return null;
                }
                throw new RuntimeException("Should not reach here " + rule.getHead().getName());
            }

            @Override
            public Object ambiguityNode(Set<Object> node) {
                throw new RuntimeException("Grammar cannot be ambiguous");
            }

            @Override
            public Object metaSymbolNode(Symbol symbol, List<Object> children, int leftExtent, int rightExtent) {
                if (symbol instanceof Start) {
                    return children.get(1);
                }
                return children;
            }

            private Optional<String> getOption(String name, Rule rule, List<Object> children) {
                List<String> values = (List<String>) getByName(rule, children, name);
                if (values.size() == 0) return Optional.empty();
                if (values.size() == 1) return Optional.of(values.get(0));
                throw new RuntimeException("There are more than one values for an optional symbol");
            }
        });

        DesugarPrecedenceAndAssociativity precedenceAndAssociativity = new DesugarPrecedenceAndAssociativity();
        precedenceAndAssociativity.setOP2();
//        grammar = precedenceAndAssociativity.transform(grammar);

        grammar = new EBNFToBNF().transform(grammar);
        grammar = new LayoutWeaver().transform(grammar);
        grammar = new DesugarStartSymbol().transform(grammar);
        return grammar;
    }

}
