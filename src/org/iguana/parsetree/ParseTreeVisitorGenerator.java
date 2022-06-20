package org.iguana.parsetree;

import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.runtime.RuntimeRule;
import org.iguana.grammar.symbol.*;
import org.iguana.utils.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ParseTreeVisitorGenerator {

    private final String grammarName;
    private final String packageName;
    private final File genDirectory;

    public ParseTreeVisitorGenerator(RuntimeGrammar grammar, String grammarName, String packageName, File genDirectory) {
        this.grammarName = grammarName;
        this.packageName = packageName;
        this.genDirectory = genDirectory;

        for (Map.Entry<Nonterminal, List<RuntimeRule>> entry : grammar.getDefinitions().entrySet()) {
            String nonterminalName = entry.getKey().getName();
            List<RuntimeRule> alternatives = entry.getValue();

            if (alternatives.size() == 0) {
                String content = generateNode(nonterminalName, NonterminalNode.class.getSimpleName(), Collections.emptyList());
                writeClassToFile(content, nonterminalName);
            }
            else if (alternatives.size() == 1) {
                String content = generateNode(nonterminalName, NonterminalNode.class.getSimpleName(), alternatives.get(0).getBody());
                writeClassToFile(content, nonterminalName);
            } else {
                writeClassToFile(
                    generateNode(nonterminalName, NonterminalNode.class.getSimpleName(), Collections.emptyList()),
                    nonterminalName);
                for (RuntimeRule alternative : alternatives) {
                    if (alternative.getLabel() == null) throw new RuntimeException("All alternatives must have a label: " + alternative);
                    String nodeName = alternative.getLabel() + nonterminalName.substring(0, 1).toUpperCase() + nonterminalName.substring(1);
                    String content = generateNode(nodeName, nonterminalName, alternative.getBody());
                    writeClassToFile(content, nodeName);
                }
            }
        }
    }

    private void writeClassToFile(String content, String className) {
        try {
            FileUtils.writeFile(content, new File(genDirectory, className + ".java").getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateNode(String nodeName, String superType, List<Symbol> symbols) {
        return
            "// This file has been generated, do not directly edit this file!\n" +
            "package " + packageName + ";\n\n" +
            "import org.iguana.grammar.runtime.RuntimeRule;\n\n" +
            "import org.iguana.parsetree.*;\n\n" +
            "import java.util.List;\n\n\n" +
            "public class " + nodeName + " extends " + superType + " {\n" +
            "    public " + nodeName + "(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {\n" +
            "        super(rule, children, start, end);\n" +
            "    }\n\n" +
            generateSymbols(symbols) +
            "    // @Override\n" +
            "    public void accept(" + grammarName + "ParseTreeVisitor visitor) {\n" +
            "    }\n" +
            "}\n";
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
                sb.append("    }\n\n");
            }

            if (symbol.getLabel() != null) {
                sb.append("    " + type + " " + symbol.getLabel() + "() {\n");
                sb.append("       return (" + type + ") childAt(" + i + ");\n");
                sb.append("    }\n\n");
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
