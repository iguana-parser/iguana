package org.iguana.generator.ide;

import org.iguana.generator.Generator;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.runtime.RuntimeRule;
import org.iguana.grammar.symbol.*;
import org.iguana.parsetree.NonterminalNode;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.iguana.generator.GeneratorUtils.writeToJavaFile;
import static org.iguana.utils.string.StringUtil.toFirstUpperCase;

public class GeneratePSIElements extends Generator {

    private final String psiGenDirectory;

    public GeneratePSIElements(RuntimeGrammar grammar, String grammarName, String packageName, String genDirectory) {
        super(grammar, grammarName, packageName, genDirectory);
        this.psiGenDirectory = new File(genDirectory, "psi").getAbsolutePath();
    }

    public void generate() {
        StringBuilder sb = new StringBuilder();
        sb.append("// This file has been generated, do not directly edit this file!\n");
        sb.append("package " + grammarName.toLowerCase() + ".ide.psi;\n");
        sb.append("\n");
        sb.append("import com.intellij.extapi.psi.ASTWrapperPsiElement;\n");
        sb.append("import com.intellij.lang.ASTNode;\n");
        sb.append("import com.intellij.psi.PsiElement;\n");
        sb.append("import org.jetbrains.annotations.NotNull;\n");
        sb.append("\n");
        sb.append("import java.util.List;\n");
        sb.append("import java.util.Optional;\n");
        sb.append("\n");
        String className = toFirstUpperCase(grammarName) + "PsiElement";
        sb.append("public class " + className + " {\n");
        sb.append("\n");
        for (Map.Entry<Nonterminal, List<RuntimeRule>> entry : grammar.getDefinitions().entrySet()) {
            String nonterminalName = entry.getKey().getName();
            List<RuntimeRule> alternatives = entry.getValue();

            if (alternatives.size() == 0) {
                sb.append(generateSymbolClass(nonterminalName, NonterminalNode.class.getSimpleName(), false, Collections.emptyList()));
            } else if (alternatives.size() == 1) {
                sb.append(generateSymbolClass(nonterminalName, NonterminalNode.class.getSimpleName(), false, alternatives.get(0).getBody()));
            } else {
                for (RuntimeRule alternative : alternatives) {
                    if (alternative.getLabel() == null)
                        throw new RuntimeException("All alternatives must have a label: " + alternative);
                    String nodeName = alternative.getLabel() + nonterminalName.substring(0, 1).toUpperCase() + nonterminalName.substring(1);
                    sb.append(generateSymbolClass(nodeName, nonterminalName, false, alternative.getBody()));
                }
            }
        }
        sb.append("}\n");
        writeToJavaFile(sb.toString(), psiGenDirectory, className);
        System.out.println(className + " has been generated.");
    }

    private String generateSymbolClass(String symbolClass, String superType, boolean isAbstract, List<Symbol> symbols) {
        return
            "    public static " + (isAbstract ? "abstract " : "") + "class " + symbolClass + " extends ASTWrapperPsiElement {\n" +
                "        public " + symbolClass + "(@NotNull ASTNode node) {\n" +
                "            super(node);\n" +
                "        }\n\n" +
                generateSymbols(symbols) +
                "    }\n\n";
    }

    private String generateSymbols(List<Symbol> symbols) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < symbols.size(); i++) {
            Symbol symbol = symbols.get(i);
            Type type = getSymbolType(symbol);
            // When type == null, it's a data-dependent construct that does not contribute to parse trees
            if (type != null) {
                String label = getLabel(symbol);
                if (label != null) {
                    sb.append("        public " + type + " " + label + "() {\n");
                    switch (type.name) {
                        case "Optional":
                            sb.append("           return Optional.of(getChildren()[" + i + "]);\n");
                            break;
                        default:
                            sb.append("           return (" + type + ") getChildren()[" + i + "];\n");
                            break;
                    }
                    sb.append("        }\n");
                }
            }
        }
        return sb.toString();
    }

    private static class Type {
        private final String name;
        private final String parameter;

        public Type(String name) {
            this(name, null);
        }
        public Type(String name, String parameter) {
            this.name = name;
            this.parameter = parameter;
        }

        @Override
        public String toString() {
            if (parameter == null) return name;
            else return String.format("%s<%s>", name, parameter);
        }
    }

    private Type getSymbolType(Symbol symbol) {
        if (symbol instanceof Nonterminal) {
            return new Type(toFirstUpperCase(grammarName) + "PsiElement." + symbol.getName());
        } else if (symbol instanceof Terminal) {
            return new Type("PsiElement");
        } else if (symbol instanceof Star) {
            return new Type("List", "PsiElement");
        } else if (symbol instanceof Plus) {
            return new Type("List", "PsiElement");
        } else if (symbol instanceof Group) {
            return new Type("List", "PsiElement");
        } else if (symbol instanceof Alt) {
            return new Type("PsiElement");
        } else if (symbol instanceof Opt) {
            return new Type("Optional", "PsiElement");
        } else if (symbol instanceof Start) {
            return new Type("PsiElement");
        } else if (symbol instanceof Code) {
            return getSymbolType(((Code) symbol).getSymbol());
        } else {
            // Data dependent symbols do not have a type
            return null;
        }
    }
}
