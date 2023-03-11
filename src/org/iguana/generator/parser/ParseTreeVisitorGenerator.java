package org.iguana.generator.parser;

import org.iguana.generator.Generator;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.runtime.RuntimeRule;
import org.iguana.grammar.symbol.*;
import org.iguana.parsetree.NonterminalNode;
import org.iguana.parsetree.TerminalNode;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.iguana.generator.GeneratorUtils.writeToJavaFile;
import static org.iguana.parsetree.MetaSymbolNode.*;
import static org.iguana.utils.string.StringUtil.listToString;
import static org.iguana.utils.string.StringUtil.toFirstUpperCase;

public class ParseTreeVisitorGenerator extends Generator {

    public ParseTreeVisitorGenerator(
            RuntimeGrammar grammar,
            String grammarName,
            String packageName,
            String genDirectory) {
        super(grammar, grammarName, packageName, genDirectory);
    }

    public void generate() {
        generateParseTreeTypes();
        generateVisitor();
        generateParseTreeBuilder();
    }

    private void generateParseTreeBuilder() {
        StringBuilder sb = new StringBuilder();
        sb.append("// This file has been generated, do not directly edit this file!\n");
        sb.append("package " + packageName + ";\n\n");
        sb.append("import org.iguana.grammar.runtime.RuntimeRule;\n");
        sb.append("import org.iguana.parsetree.DefaultParseTreeBuilder;\n");
        sb.append("import org.iguana.parsetree.NonterminalNode;\n");
        sb.append("import org.iguana.parsetree.ParseTreeNode;\n");
        sb.append("import org.iguana.utils.input.Input;\n\n");
        sb.append("import java.util.List;\n\n");

        // CHECKSTYLE:OFF: LineLength
        String className = toFirstUpperCase(grammarName) + "ParseTreeBuilder";
        sb.append("public class " + className + " extends DefaultParseTreeBuilder {\n\n");
        sb.append("    public " + className + "(Input input) {\n");
        sb.append("        super(input);\n");
        sb.append("    }\n\n");

        sb.append("    @Override\n");
        sb.append(
            "    public NonterminalNode nonterminalNode(RuntimeRule rule, List<ParseTreeNode> children, int leftExtent, int rightExtent) {\n");
        sb.append("        java.lang.String name = rule.getHead().getName();\n");
        sb.append("        java.lang.String label = rule.getLabel();\n\n");
        sb.append("        switch (name) {\n");
        generateBuilderCases(grammar, sb);
        sb.append("            default:\n");
        sb.append("                throw new RuntimeException(\"Unexpected nonterminal:\" + name);\n");
        sb.append("        }\n");
        sb.append("    }\n");
        sb.append("}\n");
        // CHECKSTYLE:ON: LineLength

        writeToJavaFile(sb.toString(), genDirectory, className);
        System.out.println(className + " has been generated in " + genDirectory + "/" + className + ".java");
    }

    private void generateBuilderCases(RuntimeGrammar grammar, StringBuilder sb) {
        for (Map.Entry<Nonterminal, List<RuntimeRule>> entry : grammar.getDefinitions().entrySet()) {
            String nonterminalName = entry.getKey().getName();
            List<RuntimeRule> alternatives = entry.getValue();

            sb.append("            case \"" + nonterminalName + "\":\n");
            if (alternatives.size() == 0) {
                sb.append("                return new " + toFirstUpperCase(grammarName) + "ParseTree" + "." +
                          toFirstUpperCase(nonterminalName) + "(rule, children, leftExtent, rightExtent);\n");
            } else if (alternatives.size() == 1) {
                sb.append("                return new " + toFirstUpperCase(grammarName) + "ParseTree" + "." +
                          toFirstUpperCase(nonterminalName) + "(rule, children, leftExtent, rightExtent);\n");
            } else {
                sb.append("                switch (label) {\n");
                for (RuntimeRule alternative : alternatives) {
                    if (alternative.getLabel() == null)
                        throw new RuntimeException("All alternatives must have a label: " + alternative);
                    sb.append("                    case \"" + alternative.getLabel() + "\":\n");
                    String className = toFirstUpperCase(grammarName) + "ParseTree" + "." +
                                       toFirstUpperCase(alternative.getLabel()) + toFirstUpperCase(nonterminalName);
                    sb.append("                        return new " + toFirstUpperCase(className) +
                              "(rule, children, leftExtent, rightExtent);\n");
                }
                sb.append("                    default:\n");
                sb.append("                        throw new RuntimeException(\"Unexpected label:\" + label);\n");
                sb.append("                }\n");
            }
        }
    }

    private void generateParseTreeTypes() {
        StringBuilder sb = new StringBuilder();
        sb.append("// This file has been generated, do not directly edit this file!\n");
        sb.append("package " + packageName + ";\n\n");
        sb.append("import org.iguana.grammar.runtime.RuntimeRule;\n");
        sb.append("import org.iguana.parsetree.*;\n\n");
        sb.append("import java.util.List;\n\n");
        sb.append("import static org.iguana.parsetree.MetaSymbolNode.*;\n\n");
        String className = toFirstUpperCase(grammarName) + "ParseTree";
        sb.append("public class " + className + " {\n");
        for (Map.Entry<Nonterminal, List<RuntimeRule>> entry : grammar.getDefinitions().entrySet()) {
            String nonterminalName = entry.getKey().getName();
            List<RuntimeRule> alternatives = entry.getValue();

            if (alternatives.size() == 0) {
                sb.append("    // " + nonterminalName + " = \n");
                sb.append(generateSymbolClass(nonterminalName, NonterminalNode.class.getSimpleName(), false,
                    Collections.emptyList()));
            } else if (alternatives.size() == 1) {
                sb.append("    // " + nonterminalName + " = " + listToString(alternatives.get(0).getBody().stream()
                    .map(this::symbolToString).collect(Collectors.toList())) + "\n");
                sb.append(generateSymbolClass(nonterminalName, NonterminalNode.class.getSimpleName(), false,
                    alternatives.get(0).getBody()));
            } else {
                sb.append(generateSymbolClass(nonterminalName, NonterminalNode.class.getSimpleName(), true,
                    Collections.emptyList()));
                for (RuntimeRule alternative : alternatives) {
                    if (alternative.getLabel() == null)
                        throw new RuntimeException("All alternatives must have a label: " + alternative);
                    String nodeName = alternative.getLabel() + nonterminalName.substring(0, 1).toUpperCase() +
                                      nonterminalName.substring(1);
                    sb.append("    // " + nonterminalName + " = " + listToString(alternative.getBody().stream()
                        .map(this::symbolToString).collect(Collectors.toList())) + "\n");
                    sb.append(generateSymbolClass(nodeName, nonterminalName, false, alternative.getBody()));
                }
            }
        }
        sb.append("}\n");
        writeToJavaFile(sb.toString(), genDirectory, className);
        System.out.println(className + " has been generated in " + genDirectory + "/" + className + ".java");
    }

    private void generateVisitor() {
        StringBuilder sb = new StringBuilder();
        sb.append("// This file has been generated, do not directly edit this file!\n");
        sb.append("package " + packageName + ";\n\n");
        sb.append("import org.iguana.parsetree.ParseTreeVisitor;\n");
        sb.append("import org.iguana.parsetree.NonterminalNode;\n\n");

        String className = toFirstUpperCase(grammarName) + "ParseTreeVisitor";
        sb.append("public interface " + className + "<T> extends ParseTreeVisitor<T> {\n\n");
        sb.append("    @Override\n");
        sb.append("    default T visitNonterminalNode(NonterminalNode node) {\n");
        sb.append("        throw new UnsupportedOperationException();\n");
        sb.append("    }\n\n");
        generateVisitMethods(grammar, sb);
        sb.append("}\n");
        writeToJavaFile(sb.toString(), genDirectory, className);
        System.out.println(className + " has been generated in " + genDirectory + "/" + className + ".java");
    }

    private void generateVisitMethods(RuntimeGrammar grammar, StringBuilder sb) {
        for (Map.Entry<Nonterminal, List<RuntimeRule>> entry : grammar.getDefinitions().entrySet()) {
            String nonterminalName = entry.getKey().getName();
            List<RuntimeRule> alternatives = entry.getValue();

            if (alternatives.size() == 0) {
                sb.append(generateVisitorMethod(nonterminalName));
            } else if (alternatives.size() == 1) {
                sb.append(generateVisitorMethod(nonterminalName));
            } else {
                for (RuntimeRule alternative : alternatives) {
                    if (alternative.getLabel() == null)
                        throw new RuntimeException("All alternatives must have a label: " + alternative);
                    String nodeName = alternative.getLabel() + nonterminalName.substring(0, 1).toUpperCase() +
                                      nonterminalName.substring(1);
                    sb.append(generateVisitorMethod(nodeName));
                }
            }
        }
    }

    private String generateVisitorMethod(String name) {
        String className = toFirstUpperCase(grammarName) + "ParseTree";
        // CHECKSTYLE:DISABLE:LineLength
        if (name.startsWith("$_")) {
            return "    default T visit" + toFirstUpperCase(name) + "(" + className + "." + toFirstUpperCase(name) +
                   " node) {\n" +
                   "        return node.child().accept(this);\n" +
                   "    }\n\n";
        } else {
            return "    T visit" + toFirstUpperCase(name) + "(" + className + "." + toFirstUpperCase(name) +
                   " node);\n\n";
        }
        // CHECKSTYLE:ENABLE:LineLength
    }

    private String generateSymbolClass(String symbolClass, String superType, boolean isAbstract, List<Symbol> symbols) {
        // CHECKSTYLE:DISABLE:LineLength
        return
            "    public static " + (isAbstract ? "abstract " : "") + "class " + toFirstUpperCase(symbolClass) +
            " extends " + toFirstUpperCase(superType) + " {\n" +
            "        public " + toFirstUpperCase(symbolClass) +
            "(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {\n" +
            "            super(rule, children, start, end);\n" +
            "        }\n\n" +
            generateSymbols(symbols) +
            (isAbstract ? "" : generateAcceptMethod(symbolClass)) +
            "    }\n\n";
        // CHECKSTYLE:ENABLE:LineLength
    }

    private String generateAcceptMethod(String symbolClass) {
        // CHECKSTYLE:DISABLE:LineLength
        String visitorName = toFirstUpperCase(grammarName) + "ParseTreeVisitor";
        return
            "        @Override\n" +
            "        public <T> T accept(ParseTreeVisitor<T> visitor) {\n" +
            "            if (visitor instanceof " + visitorName + ") {\n" +
            "                return ((" + visitorName + "<T>) visitor).visit" + toFirstUpperCase(symbolClass) +
            "(this);\n" +
            "            }\n" +
            "            return visitor.visitNonterminalNode(this);\n" +
            "        }\n";
        // CHECKSTYLE:ENABLE:LineLength
    }

    private String generateSymbols(List<Symbol> symbols) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < symbols.size(); i++) {
            Symbol symbol = symbols.get(i);
            String type = getSymbolType(symbol);
            // When type == null, it's a data-dependent construct that does not contribute to parse trees
            if (type != null) {
                String label = getLabel(symbol);
                if (label != null) {
                    sb.append("        public " + toFirstUpperCase(type) + " " + label + "() {\n");
                    sb.append("           return (" + toFirstUpperCase(type) + ") childAt(" + i + ");\n");
                    sb.append("        }\n\n");
                }
            }
        }
        return sb.toString();
    }

    private String getSymbolType(Symbol symbol) {
        if (symbol instanceof Nonterminal) {
            return symbol.getName();
        } else if (symbol instanceof Terminal) {
            return TerminalNode.class.getSimpleName();
        } else if (symbol instanceof Star) {
            return StarNode.class.getSimpleName();
        } else if (symbol instanceof Plus) {
            return PlusNode.class.getSimpleName();
        } else if (symbol instanceof Group) {
            return GroupNode.class.getSimpleName();
        } else if (symbol instanceof Alt) {
            return AltNode.class.getSimpleName();
        } else if (symbol instanceof Opt) {
            return OptionNode.class.getSimpleName();
        } else if (symbol instanceof Start) {
            return StartNode.class.getSimpleName();
        } else if (symbol instanceof Code) {
            return getSymbolType(((Code) symbol).getSymbol());
        } else {
            // Data dependent symbols do not have a type
            return null;
        }
    }
}
