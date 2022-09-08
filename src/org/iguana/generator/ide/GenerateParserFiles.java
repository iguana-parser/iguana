package org.iguana.generator.ide;

import org.iguana.generator.Generator;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.runtime.RuntimeRule;
import org.iguana.grammar.symbol.Nonterminal;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.iguana.generator.GeneratorUtils.writeToJavaFile;
import static org.iguana.utils.string.StringUtil.toFirstUpperCase;

public class GenerateParserFiles extends Generator {

    private final String parserGenDirectory;

    public GenerateParserFiles(RuntimeGrammar grammar, String grammarName, String packageName, String genDirectory) {
        super(grammar, grammarName, packageName, genDirectory);
        this.parserGenDirectory = new File(genDirectory, "parser").getAbsolutePath();
    }

    public void generate() {
        generateIggyElementTypes();
    }

    private void generateIggyElementTypes() {
        List<String> metaSymbolNodes = Arrays.asList("Opt", "Star", "Plus", "Group", "Alt");

        String className = toFirstUpperCase(grammarName);
        StringBuilder sb = new StringBuilder();
        sb.append("// This file has been generated, do not directly edit this file!\n");
        sb.append("package " + grammarName.toLowerCase() + ".ide.parser;\n");
        sb.append("\n");
        sb.append("import com.intellij.lang.ASTNode;\n");
        sb.append("import com.intellij.psi.PsiElement;\n");
        sb.append("import com.intellij.psi.tree.IElementType;\n");
        sb.append("import iggy.ide.lang.IggyLang;\n");
        sb.append("\n");
        sb.append("import java.util.IdentityHashMap;\n");
        sb.append("import java.util.Map;\n");
        sb.append("import java.util.function.Function;\n");
        sb.append("\n");
        sb.append("public class " + className + "ElementTypes {\n");
        metaSymbolNodes.forEach(s -> sb.append(generateElementType(s)));
        sb.append("\n");
        generateTypes(sb, this::generateElementType);
        sb.append("\n");
        sb.append("    private static final Map<IElementType, Function<ASTNode, PsiElement>> map = new IdentityHashMap<>();\n");
        sb.append("\n");
        sb.append("    static {\n");
        metaSymbolNodes.forEach(s -> sb.append(generateMapEntry(s)));
        sb.append("\n");
        generateTypes(sb, this::generateMapEntry);
        sb.append("    }\n");
        sb.append("\n");
        sb.append("    public static PsiElement createPsiElement(ASTNode node) {\n");
        sb.append("        return map.get(node.getElementType()).apply(node);\n");
        sb.append("    }\n");
        sb.append("}\n");

        writeToJavaFile(sb.toString(), parserGenDirectory, className + "ElementTypes");
    }

    private void generateTypes(StringBuilder sb, Function<String, String> f) {
        for (Map.Entry<Nonterminal, List<RuntimeRule>> entry : grammar.getDefinitions().entrySet()) {
            String nonterminalName = entry.getKey().getName();
            List<RuntimeRule> alternatives = entry.getValue();

            if (alternatives.size() == 0) {
                sb.append(f.apply(nonterminalName));
            } else if (alternatives.size() == 1) {
                sb.append(f.apply(nonterminalName));
            } else {
                for (RuntimeRule alternative : alternatives) {
                    if (alternative.getLabel() == null)
                        throw new RuntimeException("All alternatives must have a label: " + alternative);
                    String nodeName = alternative.getLabel() + nonterminalName.substring(0, 1).toUpperCase() + nonterminalName.substring(1);
                    sb.append(f.apply(nodeName));
                }
            }
        }
    }

    private String generateElementType(String type) {
        return "    public static IElementType " + type + " = new IggyElementType(\"" + type + "\", IggyLang.instance);\n";
    }

    private String generateMapEntry(String type) {
        return "        map.put(" + type + ", node -> new iggy.ide.psi.IggyPsiElement." + type + "(node));\n";
    }
}
