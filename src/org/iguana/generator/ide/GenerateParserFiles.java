package org.iguana.generator.ide;

import org.iguana.generator.Generator;
import org.iguana.grammar.runtime.RuntimeGrammar;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.iguana.generator.GeneratorUtils.writeToJavaFile;
import static org.iguana.utils.string.StringUtil.toFirstUpperCase;

public class GenerateParserFiles extends Generator {

    private final String parserGenDirectory;
    private final String className;
    private final List<String> metaSymbolNodes = Arrays.asList("Opt", "Star", "Plus", "Group", "Alt", "Start");

    public GenerateParserFiles(RuntimeGrammar grammar, String grammarName, String packageName, String genDirectory) {
        super(grammar, grammarName, packageName, genDirectory);
        this.parserGenDirectory = new File(genDirectory, "parser").getAbsolutePath();
        this.className = toFirstUpperCase(grammarName);
    }

    public void generate() {
        generateIggyElementTypes();
    }

    private void generateIggyElementTypes() {
        StringBuilder sb = new StringBuilder();
        sb.append("// This file has been generated, do not directly edit this file!\n");
        sb.append("package " + grammarName.toLowerCase() + ".ide.parser;\n");
        sb.append("\n");
        sb.append("import com.intellij.psi.tree.IElementType;\n");
        sb.append("import iggy.ide.lang.IggyLang;\n");
        sb.append("\n");
        sb.append("import java.util.HashMap;\n");
        sb.append("import java.util.Map;\n");
        sb.append("\n");
        sb.append("public class " + className + "ElementTypes {\n");
        metaSymbolNodes.forEach(s -> sb.append(generateElementType(s)));
        sb.append("\n");
        generateTypes(sb, this::generateElementType);
        sb.append("\n");
        sb.append("    private static final Map<String, IElementType> stringToElementTypeMap = new HashMap<>();\n");
        sb.append("\n");
        sb.append("    static {\n");
        metaSymbolNodes.forEach(s -> sb.append(generateStringToElementTypetMapEntry(s)));
        sb.append("\n");
        generateTypes(sb, this::generateStringToElementTypetMapEntry);
        sb.append("    }\n");
        sb.append("\n");
        sb.append("    public static IElementType getElementType(String name) {\n");
        sb.append("        return stringToElementTypeMap.get(name);\n");
        sb.append("    }\n");
        sb.append("}\n");

        writeToJavaFile(sb.toString(), parserGenDirectory, className + "ElementTypes");
    }

    private String generateElementType(String type) {
        return "    public static IElementType " + type + " = new IggyElementType(\"" + type
               + "\", IggyLang.instance);\n";
    }

    private String generateStringToElementTypetMapEntry(String type) {
        return "        stringToElementTypeMap.put(\"" + type + "\"" + ", " + type + ");\n";
    }
}
