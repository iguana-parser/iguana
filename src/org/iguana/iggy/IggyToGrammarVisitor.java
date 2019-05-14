package org.iguana.iggy;

import iguana.regex.RegularExpression;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.symbol.Symbol;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.parsetree.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IggyToGrammarVisitor implements ParseTreeVisitor {

    private Map<String, Terminal> terminalsMap = new HashMap<>();

    @Override
    public Object visitNonterminalNode(NonterminalNode node) {
        switch (node.getName()) {
            case "Definition":
                return visitDefinition(node);

            case "Rule":
                return visitRule(node);
        }

        return visitChildren(node);
    }

    // Definition: Rule+;
    private Grammar visitDefinition(NonterminalNode node) {
        Grammar.Builder builder = Grammar.builder();
        builder.addRules((Iterable<Rule>) node.childAt(0).accept(this));
        return builder.build();
    }

    /*
     * Rule : Identifier Parameters? ":" Body                   %Syntax
     *      | "layout"? "terminal" Identifier ":" RegexBody     %Lexical
     *      ;
     */
    private Rule visitRule(NonterminalNode node) {
        switch (node.getGrammarDefinition().getLabel()) {
            case "Syntax":
                String nonterminalName = getIdentifier(node.getChildWithName("Identifier"));
                List<String> parameters = (List<String>) node.childAt(1).accept(this);
                Nonterminal head = Nonterminal.builder(nonterminalName).addParameters(parameters).build();

                node.childAt(1).accept(this);
                List<Symbol> body = (List<Symbol>) node.getChildWithName("Body").accept(this);
                return Rule.withHead(head).addSymbols(body).build();

            case "Lexical":
                RegularExpression regex = (RegularExpression) node.getChildWithName("RegexBody").accept(this);
                Terminal terminal = Terminal.builder(regex).build();
                String name = getIdentifier(node.getChildWithName("Identifier"));
                terminalsMap.put(name, terminal);
                return null;

            default:
                throw new RuntimeException("Unexpected label");
        }
    }

    private String getIdentifier(ParseTreeNode node) {
        return node.getText();
    }
}
