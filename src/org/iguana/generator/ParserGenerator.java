package org.iguana.generator;

import static org.iguana.generator.Utils.writeToFile;
import static org.iguana.utils.string.StringUtil.toFirstUpperCase;

public class ParserGenerator {

    private final String grammarName;
    private final String packageName;
    private final String genDirectory;

    public ParserGenerator(String grammarName, String packageName, String genDirectory) {
        this.grammarName = grammarName;
        this.packageName = packageName;
        this.genDirectory = genDirectory;
    }

    public void generate() {
        String className = toFirstUpperCase(grammarName);
        String content =
            "// This file has been generated, do not directly edit this file!\n" +
                "package " + packageName + ";\n" +
                "\n" +
                "import org.iguana.grammar.Grammar;\n" +
                "import org.iguana.iggy.IggyToGrammarVisitor;\n" +
                "import org.iguana.parser.IguanaParser;\n" +
                "import org.iguana.parsetree.ParseTreeBuilder;\n" +
                "import org.iguana.parsetree.ParseTreeNode;\n" +
                "import org.iguana.util.serialization.JsonSerializer;\n" +
                "import org.iguana.utils.input.Input;\n" +
                "\n" +
                "import java.io.File;\n" +
                "import java.io.IOException;\n" +
                "import java.io.InputStream;\n" +
                "\n" +
                "import static org.iguana.utils.io.FileUtils.readFile;\n" +
                "\n" +
                "public class " + className + "Parser extends IguanaParser {\n" +
                "\n" +
                "    private IggyParser(Grammar grammar) {\n" +
                "        super(grammar);\n" +
                "    }\n" +
                "\n" +
                "    private static final String grammarName = \"" + grammarName + "\";\n" +
                "\n" +
                "    private static IggyParser parser;\n" +
                "\n" +
                "    private static Grammar grammar;\n" +
                "\n" +
                "    public static Grammar getGrammar() {\n"+
                "        if (grammar == null) {\n"+
                "            grammar = loadGrammar();\n"+
                "        }\n" +
                "        return grammar;\n" +
                "     }\n" +
                "\n" +
                "    // Creates a Grammar form the provided .iggy file\n" +
                "    public static Grammar fromIggyGrammarPath(String path) {\n" +
                "        Input input;\n" +
                "        try {\n" +
                "            input = Input.fromFile(new File(path));\n" +
                "        } catch (IOException e) {\n" +
                "            throw new RuntimeException(e);" +
                "        }\n" +
                "        return createGrammar(input);\n" +
                "    }\n" +
                "\n" +
                "    // Creates a Grammar form the provided grammar in string form\n" +
                "    public static Grammar fromIggyGrammar(String content) {\n" +
                "        Input input = Input.fromString(content);\n" +
                "        return createGrammar(input);\n" +
                "    }\n" +
                "\n" +
                "    public static IggyParser getInstance() {\n" +
                "        if (parser == null) {\n" +
                "            parser = new IggyParser(getGrammar());\n" +
                "        }\n" +
                "        return parser;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    protected ParseTreeBuilder<ParseTreeNode> getParseTreeBuilder(Input input) {\n" +
                "        return new " + className + "ParseTreeBuilder(input);\n" +
                "    }\n" +
                "\n" +
                "    private static Grammar loadGrammar() {\n" +
                "        String grammarJsonFile = grammarName + \".json\";\n" +
                "        try (InputStream in = IggyParser.class.getResourceAsStream(grammarJsonFile)) {\n" +
                "            if (in == null) throw new RuntimeException(\"Grammar json file \" + grammarJsonFile + \" is not found.\");\n" +
                "            String content = readFile(in);\n" +
                "            return JsonSerializer.deserialize(content, Grammar.class);\n" +
                "        } catch (IOException e) {\n" +
                "            throw new RuntimeException(e);\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    private static Grammar createGrammar(Input input) {\n" +
                "            IguanaParser parser = IggyParser.getInstance();\n" +
                "            parser.parse(input);\n" +
                "        if (parser.hasParseError()) {\n" +
                "            System.out.println(parser.getParseError());\n" +
                "            throw new RuntimeException(parser.getParseError().toString());\n" +
                "        }\n" +
                "        ParseTreeNode parseTree = parser.getParseTree();\n" +
                "        return (Grammar) parseTree.accept(new IggyToGrammarVisitor());\n" +
                "    }\n" +
                "}\n";
        writeToFile(content, genDirectory, className + "Parser");
    }
}
