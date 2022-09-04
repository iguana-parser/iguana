package org.iguana.generator.ide;

import org.iguana.generator.Generator;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.runtime.RuntimeRule;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Symbol;
import org.iguana.parsetree.NonterminalNode;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.iguana.generator.GeneratorUtils.writeToJavaFile;
import static org.iguana.utils.string.StringUtil.listToString;
import static org.iguana.utils.string.StringUtil.toFirstUpperCase;

public class GeneratePSIElements extends Generator {

    public GeneratePSIElements(RuntimeGrammar grammar, String grammarName, String packageName, String genDirectory) {
        super(grammar, grammarName, packageName, genDirectory);
    }

    public void generate() {
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
                sb.append(generateSymbolClass(nonterminalName, NonterminalNode.class.getSimpleName(), false, Collections.emptyList()));
            } else if (alternatives.size() == 1) {
                sb.append("    // " + nonterminalName + " = " + listToString(alternatives.get(0).getBody().stream().map(this::symbolToString).collect(Collectors.toList())) + "\n");
                sb.append(generateSymbolClass(nonterminalName, NonterminalNode.class.getSimpleName(), false, alternatives.get(0).getBody()));
            } else {
                sb.append(generateSymbolClass(nonterminalName, NonterminalNode.class.getSimpleName(), true, Collections.emptyList()));
                for (RuntimeRule alternative : alternatives) {
                    if (alternative.getLabel() == null)
                        throw new RuntimeException("All alternatives must have a label: " + alternative);
                    String nodeName = alternative.getLabel() + nonterminalName.substring(0, 1).toUpperCase() + nonterminalName.substring(1);
                    sb.append("    // " + nonterminalName + " = " + listToString(alternative.getBody().stream().map(this::symbolToString).collect(Collectors.toList())) + "\n");
                    sb.append(generateSymbolClass(nodeName, nonterminalName, false, alternative.getBody()));
                }
            }
        }
        sb.append("}\n");
        writeToJavaFile(sb.toString(), genDirectory, className);
        System.out.println(className + " has been generated.");
    }

    private String generateSymbolClass(String symbolClass, String superType, boolean isAbstract, List<Symbol> symbols) {
        return
            "";
    }
}
