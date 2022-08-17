package org.iguana;

import org.iguana.generator.ParseTreeVisitorGenerator;
import org.iguana.grammar.Grammar;
import org.iguana.iggy.IggyToGrammarVisitor;
import org.iguana.iggy.gen.IggyParser;
import org.iguana.parser.IguanaParser;
import org.iguana.util.serialization.JsonSerializer;
import org.iguana.utils.input.Input;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.util.concurrent.Callable;

@Command(name = "iguana", mixinStandardHelpOptions = true, version = "0.1-SNAPSHOT",
    description = "Iguana: General Data-dependent Parser")
public class Iguana implements Callable<Integer> {

    @Option(names = {"--name", "-n"}, description = "The grammar name", defaultValue = "grammar")
    private String grammarName;

    @Option(names = {"--grammar", "-g"}, description = "The grammar file", required = true)
    private File grammarFile;

    @Option(names = {"--output", "-o"}, description = "The output directory for generated files", required = true)
    private File genDirectory;

    @Option(names = {"--visitor"}, description = "Should generate visitors?")
    private boolean generateVisitors;

    @Option(names = "--package", description = "package name for the generated code")
    private String packageName;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Iguana()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        IguanaParser parser = IggyParser.getInstance();
        parser.parse(Input.fromFile(grammarFile));
        if (parser.hasParseError()) {
            System.out.println(parser.getParseError());
            return 1;
        }
        Grammar grammar = (Grammar) parser.getParseTree().accept(new IggyToGrammarVisitor());
        JsonSerializer.serialize(grammar, new File(genDirectory, grammarName + ".json").toPath().toAbsolutePath().toString());
        if (generateVisitors) {
            ParseTreeVisitorGenerator generator = new ParseTreeVisitorGenerator(grammar.toRuntimeGrammar(), grammar.getName(), packageName, genDirectory.getAbsolutePath());
            generator.generate();
        }
        return 0;
    }
}
