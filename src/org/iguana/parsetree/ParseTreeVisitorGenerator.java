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
import java.util.stream.Collectors;

import static org.iguana.regex.utils.RegexUtils.isLiteral;
import static org.iguana.utils.string.StringUtil.listToString;
import static org.iguana.utils.string.StringUtil.toFirstUpperCase;

public class ParseTreeVisitorGenerator {

    private final String grammarName;
    private final String packageName;
    private final File genDirectory;

    public ParseTreeVisitorGenerator(RuntimeGrammar grammar, String grammarName, String packageName, File genDirectory) {
        this.grammarName = grammarName;
        this.packageName = packageName;
        this.genDirectory = genDirectory;

        generateParseTreeTypes(grammar);
        generateVisitor(grammar);
        generateParseTreeBuilder(grammar);
        generateIggyParser();
    }

    private void generateIggyParser() {
        String className = toFirstUpperCase(grammarName);
        String content =
            "// This file has been generated, do not directly edit this file!\n" +
            "package " + packageName + ";\n" +
            "\n" +
            "import org.iguana.grammar.Grammar;\n" +
            "import org.iguana.parser.IguanaParser;\n" +
            "import org.iguana.parsetree.ParseTreeBuilder;\n" +
            "import org.iguana.parsetree.ParseTreeNode;\n" +
            "import org.iguana.utils.input.Input;\n" +
            "\n" +
            "public class " + className + "Parser extends IguanaParser {\n" +
            "\n" +
            "    public IggyParser(Grammar grammar) {\n" +
            "        super(grammar);\n" +
            "    }\n" +
            "\n" +
            "    @Override\n" +
            "    protected ParseTreeBuilder<ParseTreeNode> getParseTreeBuilder(Input input) {\n" +
            "        return new " + className + "ParseTreeBuilder(input);\n" +
            "    }\n" +
            "}\n";
        writeToFile(content, className + "Parser");
    }

    private void generateParseTreeBuilder(RuntimeGrammar grammar) {
        StringBuilder sb = new StringBuilder();
        sb.append("// This file has been generated, do not directly edit this file!\n");
        sb.append("package " + packageName + ";\n\n");
        sb.append("import org.iguana.grammar.runtime.RuntimeRule;\n");
        sb.append("import org.iguana.parsetree.DefaultParseTreeBuilder;\n");
        sb.append("import org.iguana.parsetree.NonterminalNode;\n");
        sb.append("import org.iguana.parsetree.ParseTreeNode;\n");
        sb.append("import org.iguana.utils.input.Input;\n\n");
        sb.append("import java.util.List;\n\n");
        sb.append("import static " + packageName + ".IggyParseTree.*;\n\n");

        String className = toFirstUpperCase(grammarName) + "ParseTreeBuilder";
        sb.append("public class " + className + " extends DefaultParseTreeBuilder {\n\n");
        sb.append("    public IggyParseTreeBuilder(Input input) {\n");
        sb.append("        super(input);\n");
        sb.append("    }\n\n");

        sb.append("    @Override\n");
        sb.append("    public NonterminalNode nonterminalNode(RuntimeRule rule, List<ParseTreeNode> children, int leftExtent, int rightExtent) {\n");
        sb.append("        String name = rule.getHead().getName();\n");
        sb.append("        String label = rule.getLabel();\n\n");
        sb.append("        switch (name) {\n");
        generateBuilderCases(grammar, sb);
        sb.append("            default:\n");
        sb.append("                throw new RuntimeException(\"Unexpected nonterminal:\" + name);\n");
        sb.append("        }\n");
        sb.append("    }\n");
        sb.append("}\n");

        writeToFile(sb.toString(), className);
    }

    private void generateBuilderCases(RuntimeGrammar grammar, StringBuilder sb) {
        for (Map.Entry<Nonterminal, List<RuntimeRule>> entry : grammar.getDefinitions().entrySet()) {
            String nonterminalName = entry.getKey().getName();
            List<RuntimeRule> alternatives = entry.getValue();

            sb.append("            case \"" + nonterminalName + "\":\n");
            if (alternatives.size() == 0) {
                sb.append("                return new " + nonterminalName + "(rule, children, leftExtent, rightExtent);\n");
            } else if (alternatives.size() == 1) {
                sb.append("                return new " + nonterminalName + "(rule, children, leftExtent, rightExtent);\n");
            } else {
                sb.append("                switch (label) {\n");
                for (RuntimeRule alternative : alternatives) {
                    if (alternative.getLabel() == null) throw new RuntimeException("All alternatives must have a label: " + alternative);
                    sb.append("                    case \"" + alternative.getLabel() + "\":\n");
                    String className = alternative.getLabel() + nonterminalName.substring(0, 1).toUpperCase() + nonterminalName.substring(1);
                    sb.append("                        return new " + className + "(rule, children, leftExtent, rightExtent);\n");
                }
                sb.append("                    default:\n");
                sb.append("                        throw new RuntimeException(\"Unexpected label:\" + label);\n");
                sb.append("                }\n");
            }
        }
    }

    private void generateParseTreeTypes(RuntimeGrammar grammar) {
        StringBuilder sb = new StringBuilder();
        sb.append("// This file has been generated, do not directly edit this file!\n");
        sb.append("package " + packageName + ";\n\n");
        sb.append("import org.iguana.grammar.runtime.RuntimeRule;\n");
        sb.append("import org.iguana.parsetree.*;\n\n");
        sb.append("import java.util.List;\n\n");
        String className = toFirstUpperCase(grammarName) + "ParseTree";
        sb.append("public class " + className + " {\n");
        for (Map.Entry<Nonterminal, List<RuntimeRule>> entry : grammar.getDefinitions().entrySet()) {
            String nonterminalName = entry.getKey().getName();
            List<RuntimeRule> alternatives = entry.getValue();

            if (alternatives.size() == 0) {
                sb.append("    // " + nonterminalName + " = \n");
                sb.append(generateSymbolClass(nonterminalName, NonterminalNode.class.getSimpleName(), false, Collections.emptyList()));
            } else if (alternatives.size() == 1) {
                sb.append("    // " + nonterminalName + " = " + listToString(alternatives.get(0).getBody().stream().map(this::symbolToString).collect(Collectors.toList())) + "\n");
                sb.append(generateSymbolClass(nonterminalName, NonterminalNode.class.getSimpleName(), false, alternatives.get(0).getBody()));
            } else {
                sb.append(generateSymbolClass(nonterminalName, NonterminalNode.class.getSimpleName(), true, Collections.emptyList()));
                for (RuntimeRule alternative : alternatives) {
                    if (alternative.getLabel() == null) throw new RuntimeException("All alternatives must have a label: " + alternative);
                    String nodeName = alternative.getLabel() + nonterminalName.substring(0, 1).toUpperCase() + nonterminalName.substring(1);
                    sb.append("    // " + nonterminalName + " = " + listToString(alternative.getBody().stream().map(this::symbolToString).collect(Collectors.toList())) + "\n");
                    sb.append(generateSymbolClass(nodeName, nonterminalName, false, alternative.getBody()));
                }
            }
        }
        sb.append("}\n");
        writeToFile(sb.toString(), className);
    }

    private void generateVisitor(RuntimeGrammar grammar) {
        StringBuilder sb = new StringBuilder();
        sb.append("// This file has been generated, do not directly edit this file!\n");
        sb.append("package " + packageName + ";\n\n");
        sb.append("import org.iguana.parsetree.ParseTreeVisitor;\n");
        sb.append("import org.iguana.parsetree.NonterminalNode;\n\n");
        sb.append("import static " + packageName + ".IggyParseTree.*;\n\n");

        String className = toFirstUpperCase(grammarName) + "ParseTreeVisitor";
        sb.append("public abstract class " +  className + "<T> implements ParseTreeVisitor<T> {\n\n");
        sb.append("    @Override\n");
        sb.append("    public T visitNonterminalNode(NonterminalNode node) {\n");
        sb.append("        throw new UnsupportedOperationException();\n");
        sb.append("    }\n\n");
        generateVisitMethods(grammar, sb);
        sb.append("}\n");
        writeToFile(sb.toString(), className);
    }

    private void generateVisitMethods(RuntimeGrammar grammar, StringBuilder sb) {
        for (Map.Entry<Nonterminal, List<RuntimeRule>> entry : grammar.getDefinitions().entrySet()) {
            String nonterminalName = entry.getKey().getName();
            List<RuntimeRule> alternatives = entry.getValue();

            if (alternatives.size() == 0) {
                sb.append(generateVisitorMethod(nonterminalName));
            }
            else if (alternatives.size() == 1) {
                sb.append(generateVisitorMethod(nonterminalName));
            } else {
                for (RuntimeRule alternative : alternatives) {
                    if (alternative.getLabel() == null) throw new RuntimeException("All alternatives must have a label: " + alternative);
                    String nodeName = alternative.getLabel() + nonterminalName.substring(0, 1).toUpperCase() + nonterminalName.substring(1);
                    sb.append(generateVisitorMethod(nodeName));
                }
            }
        }
    }

    private String generateVisitorMethod(String name) {
        return "    public abstract T visit" + name + "(" + name + " node);\n\n";
    }

    private void writeToFile(String content, String className) {
        try {
            FileUtils.writeFile(content, new File(genDirectory, className + ".java").getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateSymbolClass(String symbolClass, String superType, boolean isAbstract, List<Symbol> symbols) {
        return
            "    public static " + (isAbstract ? "abstract " : "") + "class " + symbolClass + " extends " + superType + " {\n" +
            "        public " + symbolClass + "(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {\n" +
            "            super(rule, children, start, end);\n" +
            "        }\n\n" +
            generateSymbols(symbols) +
            (isAbstract ? "" : generateAcceptMethod(symbolClass)) +
            "    }\n\n";
    }

    private String generateAcceptMethod(String symbolClass) {
        String visitorName = toFirstUpperCase(grammarName) + "ParseTreeVisitor";
        return "        @Override\n" +
               "        public <T> T accept(ParseTreeVisitor<T> visitor) {\n" +
               "            if (visitor instanceof " + visitorName + ") {\n" +
               "                return ((" + visitorName + "<T>) visitor).visit" + symbolClass + "(this);\n" +
               "            }\n" +
               "            return visitor.visitNonterminalNode(this);\n" +
               "        }\n";
    }

    private String generateSymbols(List<Symbol> symbols) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < symbols.size(); i++) {
            Symbol symbol = symbols.get(i);
            String type = getSymbolType(symbol);
            // When type == null, it's a data-dependent construct that does not contribute to parse trees
            if (type != null) {
                sb.append("        public " + type + " child" + i + "() {\n");
                sb.append("           return (" + type + ") childAt(" + i + ");\n");
                sb.append("        }\n\n");
            }

            if (symbol.getLabel() != null) {
                sb.append("        public " + type + " " + symbol.getLabel() + "() {\n");
                sb.append("           return (" + type + ") childAt(" + i + ");\n");
                sb.append("        }\n\n");
            }
        }
        return sb.toString();
    }

    private String symbolToString(Symbol symbol) {
        if (symbol instanceof Terminal) {
            if (isLiteral(((Terminal) symbol).getRegularExpression())) {
                return "'" + symbol.getName() + "'";
            }
        }
        return symbol.getName();
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