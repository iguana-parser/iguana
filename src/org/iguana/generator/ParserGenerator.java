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
        writeToFile(content, genDirectory, className + "Parser");
    }
}
