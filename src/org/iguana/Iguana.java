package org.iguana;

import org.iguana.generator.ide.GenerateLangFiles;
import org.iguana.generator.ide.GeneratePSIElements;
import org.iguana.generator.ide.GenerateParserFiles;
import org.iguana.generator.parser.ParseTreeVisitorGenerator;
import org.iguana.generator.parser.ParserGenerator;
import org.iguana.grammar.Grammar;
import org.iguana.iggy.IggyParseTreeToGrammarVisitor;
import org.iguana.iggy.gen.IggyParser;
import org.iguana.parser.IguanaParser;
import org.iguana.util.serialization.JsonSerializer;
import org.iguana.utils.input.Input;
import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

@Command(name = "iguana", mixinStandardHelpOptions = true, version = "0.1-SNAPSHOT",
    description = "Iguana: General Data-dependent Parser", sortOptions = false)
public class Iguana implements Callable<Integer> {

    @ArgGroup(exclusive = true, multiplicity = "1")
    Command command;

    static class Command {
        @Option(names = "--generate-grammar") boolean generateGrammar;
        @Option(names = "--generate-types") boolean generateTypes;
        @Option(names = "--generate-ide") boolean generateIDE;
    }

    @Option(names = {"--name", "-n"}, description = "The grammar name", defaultValue = "grammar")
    private String grammarName;

    @Option(names = {"--grammar", "-g"}, description = "The grammar file", required = true)
    private File grammarFile;

    @Option(names = {"--output", "-o"}, description = "The output directory for generated files", required = true)
    private File genDirectory;

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

        // Create the gen directory if it doesn't already exist
        Files.createDirectories(Paths.get(genDirectory.getAbsolutePath()));

        Grammar grammar = (Grammar) parser.getParseTree().accept(new IggyParseTreeToGrammarVisitor());

        if (command.generateGrammar || command.generateTypes) {
            JsonSerializer.serialize(grammar, new File(genDirectory, grammarName + ".json").toPath().toAbsolutePath().toString());
            ParserGenerator parserGenerator = new ParserGenerator(grammarName, packageName, genDirectory.getAbsolutePath());
            parserGenerator.generateGrammar();
        }

        if (command.generateTypes) {
            ParserGenerator parserGenerator = new ParserGenerator(grammarName, packageName, genDirectory.getAbsolutePath());
            parserGenerator.generateParser();
            ParseTreeVisitorGenerator generator = new ParseTreeVisitorGenerator(grammar.toRuntimeGrammar(), grammarName, packageName, genDirectory.getAbsolutePath());
            generator.generate();
        }

        if (command.generateIDE) {
            GenerateLangFiles generateLangFiles = new GenerateLangFiles(grammarName, genDirectory.getAbsolutePath());
            generateLangFiles.generate();
            GeneratePSIElements generatePSIElements = new GeneratePSIElements(grammar.toRuntimeGrammar(), grammarName, packageName, genDirectory.getAbsolutePath());
            generatePSIElements.generate();
            GenerateParserFiles generateParserFiles = new GenerateParserFiles(grammar.toRuntimeGrammar(), grammarName, packageName, genDirectory.getAbsolutePath());
            generateParserFiles.generate();
        }

        return 0;
    }
}
