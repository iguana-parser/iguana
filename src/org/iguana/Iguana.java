package org.iguana;

import org.iguana.grammar.Grammar;
import org.iguana.iggy.IggyParser;
import org.iguana.iggy.IggyToGrammarVisitor;
import org.iguana.parser.IguanaParser;
import org.iguana.parsetree.ParseTreeVisitorGenerator;
import org.iguana.utils.input.Input;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.concurrent.Callable;

@Command(name = "iguana", mixinStandardHelpOptions = true, version = "0.1-SNAPSHOT",
    description = "Iguana: General Data-dependent Parser")
public class Iguana implements Callable<Integer> {

    @Parameters(index = "0", description = "The grammar file")
    private File grammarFile;

    @Option(names = { "--visitor"}, description = "generate visitors")
    private File genDirectory;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Iguana()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        IguanaParser parser = IggyParser.iggyParser();
        parser.parse(Input.fromFile(grammarFile));
        if (parser.hasParseError()) {
            System.out.println(parser.getParseError());
            return 1;
        }
        Grammar grammar = (Grammar) parser.getParseTree().accept(new IggyToGrammarVisitor());
        ParseTreeVisitorGenerator visitor = new ParseTreeVisitorGenerator(grammar.toRuntimeGrammar(), "Iggy", "org.iguana.iggy.gen.parsetree", genDirectory);
        return 0;
    }
}
