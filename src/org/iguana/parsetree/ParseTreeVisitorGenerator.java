package org.iguana.parsetree;

import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.runtime.RuntimeRule;
import org.iguana.grammar.symbol.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ParseTreeVisitorGenerator {

    private final String grammarName;
    private final String packageName;

    public ParseTreeVisitorGenerator(RuntimeGrammar grammar, String grammarName, String packageName, String genDirectory) {
        this.grammarName = grammarName;
        this.packageName = packageName;

        for (Map.Entry<Nonterminal, List<RuntimeRule>> entry : grammar.getDefinitions().entrySet()) {
            String nonterminalName = entry.getKey().getName();
            List<RuntimeRule> alternatives = entry.getValue();

            if (alternatives.size() == 0) {
                generateNode(nonterminalName, NonterminalNode.class.getSimpleName(), Collections.emptyList());
            }
            else if (alternatives.size() == 1) {
                generateNode(nonterminalName, NonterminalNode.class.getSimpleName(), alternatives.get(0).getBody());
            } else {
                generateNode(nonterminalName, NonterminalNode.class.getSimpleName(), Collections.emptyList());
                for (RuntimeRule alternative : alternatives) {
                    if (alternative.getLabel() == null) throw new RuntimeException("All alternatives must have a label: " + alternative);
                    String nodeName = alternative.getLabel() + nonterminalName.substring(0, 1).toUpperCase() + nonterminalName.substring(1);
                    generateNode(nodeName, nonterminalName, alternative.getBody());
                }
            }
        }
    }

    private void generateNode(String nodeName, String superType, List<Symbol> symbols) {
        String content =
            "// This file has been generated, do not directly edit this file!\n" +
            "package " + packageName + ";\n\n" +
            "import org.iguana.parsetree.*;\n\n" +
            "public class " + nodeName + " extends " + superType + " {\n" +
            generateSymbols(symbols) +
            "    @override\n" +
            "    public void accept(" + grammarName + "ParseTreeVisitor visitor) {\n" +
            "    }\n" +
            "}\n";
        System.out.println(content);
    }

    private String generateSymbols(List<Symbol> symbols) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < symbols.size(); i++) {
            Symbol symbol = symbols.get(i);
            String type = getSymbolType(symbol);
            // When type == null, it's a data-dependent construct that does not contribute to parse trees
            if (type != null) {
                sb.append("    " + type + " child" + i + "() {\n");
                sb.append("       return (" + type + ") childAt(" + i + ");\n");
                sb.append("    }\n");
            }

            if (symbol.getLabel() != null) {
                sb.append("    " + type + " " + symbol.getLabel() + "() {\n");
                sb.append("       return (" + type + ") childAt(" + i + ");\n");
                sb.append("    }\n");
            }
        }
        return sb.toString();
    }

    private String getSymbolType(Symbol symbol) {
        if (symbol instanceof Nonterminal) {
            return symbol.getName();
        } else if (symbol instanceof Terminal) {
            return TerminalNode.class.getSimpleName();
        } else if (symbol instanceof Star || symbol instanceof Plus || symbol instanceof Opt || symbol instanceof Group) {
            return MetaSymbolNode.class.getSimpleName();
        } else {
            return null;
        }
    }
}
