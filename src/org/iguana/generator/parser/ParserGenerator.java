package org.iguana.generator.parser;

import org.iguana.generator.Generator;

import static org.iguana.generator.GeneratorUtils.writeToJavaFile;
import static org.iguana.utils.string.StringUtil.toFirstUpperCase;

public class ParserGenerator extends Generator {

    public ParserGenerator(String grammarName, String packageName, String genDirectory) {
        super(null, grammarName, packageName, genDirectory);
    }

    // CHECKSTYLE:OFF: LineLength
    public void generateGrammar() {
        String className = toFirstUpperCase(grammarName);
        String content =
            "// This file has been generated, do not directly edit this file!\n"
            + "\n"
            + "package " + packageName + ";\n"
            + "\n"
            + "import org.iguana.grammar.Grammar;\n"
            + "import org.iguana.util.serialization.JsonSerializer;\n"
            + "\n"
            + "import java.io.IOException;\n"
            + "import java.io.InputStream;\n"
            + "\n"
            + "public class " + className + "Grammar {\n"
            + "\n"
            + "    private static final String grammarName = \"" + grammarName + "\";\n"
            + "\n"
            + "    private static Grammar grammar;\n"
            + "\n"
            + "    public static Grammar getGrammar() {\n"
            + "        if (grammar == null) {\n"
            + "            grammar = loadGrammar();\n"
            + "        }\n"
            + "        return grammar;\n"
            + "     }\n"
            + "\n"
            + "    private static Grammar loadGrammar() {\n"
            + "        String grammarJsonFile = grammarName + \".json\";\n"
            + "        try (InputStream in = " + className + "Parser.class.getResourceAsStream(\"/\" + " + "grammarJsonFile)) {\n"
            + "            if (in == null) throw new RuntimeException(\"Grammar json file \" + grammarJsonFile + \" is not found.\");\n"
            + "            return JsonSerializer.deserialize(in, Grammar.class);\n"
            + "        } catch (IOException e) {\n"
            + "            throw new RuntimeException(e);\n"
            + "        }\n"
            + "    }\n"
            + "}\n";
        writeToJavaFile(content, genDirectory, className + "Grammar");
        System.out.println(className + "Grammar" + " has been generated in " + genDirectory + "/" + className + "Grammar.java");
    }
    // CHECKSTYLE:ON: LineLength

    public void generateParser() {
        String className = toFirstUpperCase(grammarName);
        String content =
            "// This file has been generated, do not directly edit this file!\n"
            + "\n"
            + "package " + packageName + ";\n"
            + "\n"
            + "import org.iguana.grammar.Grammar;\n"
            + "import org.iguana.parser.IguanaParser;\n"
            + "import org.iguana.parsetree.ParseTreeBuilder;\n"
            + "import org.iguana.parsetree.ParseTreeNode;\n"
            + "import org.iguana.utils.input.Input;\n"
            + "\n"
            + "public class " + className + "Parser extends IguanaParser {\n"
            + "\n"
            + "    private " + className + "Parser(Grammar grammar) {\n"
            + "        super(grammar);\n"
            + "    }\n"
            + "\n"
            + "    private static " + className + "Parser parser;\n"
            + "\n"
            + "    public static " + className + "Parser getInstance() {\n"
            + "        if (parser == null) {\n"
            + "            parser = new " + className + "Parser(" + className + "Grammar.getGrammar());\n"
            + "        }\n"
            + "        return parser;\n"
            + "    }\n"
            + "\n"
            + "    @Override\n"
            + "    protected ParseTreeBuilder<ParseTreeNode> getParseTreeBuilder(Input input) {\n"
            + "        return new " + className + "ParseTreeBuilder(input);\n"
            + "    }\n"
            + "}\n";
        writeToJavaFile(content, genDirectory, className + "Parser");
        System.out.println(
            className + "Parser" + " has been generated in " + genDirectory + "/" + className + "Parser.java");
    }
}
