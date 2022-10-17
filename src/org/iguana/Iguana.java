package org.iguana;

import org.iguana.generator.ide.GenerateLangFiles;
import org.iguana.generator.ide.GenerateParserFiles;
import org.iguana.generator.ide.GeneratePsiElements;
import org.iguana.generator.parser.ParseTreeVisitorGenerator;
import org.iguana.generator.parser.ParserGenerator;
import org.iguana.grammar.Grammar;
import org.iguana.iggy.IggyParserUtils;
import org.iguana.util.serialization.JsonSerializer;
import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
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

    @Option(names = {"--output", "-o"}, description = "The output project for generated files", required = true)
    private Path outputDirectory;

    @Option(names = "--package", description = "package name for the generated code")
    private String packageName;

    @Option(names = {"--grammar-output"}, description = "The location where the grammar.json file will be generated.", required = true)
    private Path grammarOutputDirectory;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Iguana()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        // Create the gen directory if it doesn't already exist
        Files.createDirectories(outputDirectory.toAbsolutePath());

        Grammar grammar = IggyParserUtils.fromIggyGrammarPath(grammarFile.getAbsolutePath());

        if (command.generateGrammar || command.generateTypes) {
            String jsonPath = grammarOutputDirectory.resolve(grammarName + ".json").toAbsolutePath().toString();
            JsonSerializer.serialize(grammar, jsonPath);
            System.out.println("grammar.json file has been generated in " + jsonPath);
            Path typesOutputDirectory = outputDirectory.resolve(packageName.replace(".", "/"));
            ParserGenerator parserGenerator = new ParserGenerator(grammarName, packageName, typesOutputDirectory.toAbsolutePath().toString());
            parserGenerator.generateGrammar();
        }

        if (command.generateTypes) {
            Path typesOutputDirectory = outputDirectory.resolve(packageName.replace(".", "/"));
            ParserGenerator parserGenerator = new ParserGenerator(grammarName, packageName, typesOutputDirectory.toAbsolutePath().toString());
            parserGenerator.generateParser();
            ParseTreeVisitorGenerator generator = new ParseTreeVisitorGenerator(grammar.toRuntimeGrammar(), grammarName, packageName, typesOutputDirectory.toAbsolutePath().toString());
            generator.generate();
        }

        if (command.generateIDE) {
            GenerateLangFiles generateLangFiles = new GenerateLangFiles(grammarName, outputDirectory.toAbsolutePath().toString());
            generateLangFiles.generate();
            GeneratePsiElements generatePSIElements = new GeneratePsiElements(grammar.toRuntimeGrammar(), grammarName, packageName, outputDirectory.toAbsolutePath().toString());
            generatePSIElements.generate();
            GenerateParserFiles generateParserFiles = new GenerateParserFiles(grammar.toRuntimeGrammar(), grammarName, packageName, outputDirectory.toAbsolutePath().toString());
            generateParserFiles.generate();
        }

        return 0;
    }
}
