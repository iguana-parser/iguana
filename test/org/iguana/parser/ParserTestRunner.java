package org.iguana.parser;

import org.iguana.ParserTest;
import org.iguana.grammar.Grammar;
import org.iguana.iggy.IggyParserUtils;
import org.iguana.parsetree.ParseTreeNode;
import org.iguana.util.serialization.JsonSerializer;
import org.iguana.util.visualization.ParseTreeToDot;
import org.iguana.utils.input.Input;
import org.iguana.utils.visualization.DotGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class ParserTestRunner {

    private static boolean REGENERATE_FILES = false;

    static {
        String regenerate = System.getenv("REGENERATE");
        if (regenerate != null && regenerate.equals("true")) {
            REGENERATE_FILES = true;
        }
    }

    protected String testName;

    @BeforeEach
    void setUp(TestInfo testInfo) {
        testName = testInfo.getTestMethod().get().getName();
    }

    private Path getTestDirectory() {
        Path path = Path.of("test/resources/tests")
            .resolve(this.getClass().getSimpleName())
            .resolve(testName);
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return path;
    }

    protected void run(ParserTest parserTest) {
        Grammar grammar = IggyParserUtils.fromIggyGrammar(parserTest.getGrammar());
        Input input = Input.fromString(parserTest.getInput());
        IguanaParser parser = new IguanaParser(grammar);
        try {
            parser.parse(input, parserTest.getStartSymbol(), parserTest.getParseOptions());
        } catch (ParseErrorException e) {
            throw e;
        }
        if (parserTest.verifyParseTree()) {
            verifyParseTree(parser.getParseTree(), input);
        }
    }

    private void verifyParseTree(ParseTreeNode actualParseTree, Input input) {
        Path parseTreePath = getTestDirectory().resolve("parse_tree.json");
        if (REGENERATE_FILES || !Files.exists(parseTreePath)) {
            DotGraph dotGraph = ParseTreeToDot.getDotGraph(actualParseTree, input);
            Path pdfPath = getTestDirectory().resolve("parse_tree.pdf");
            try {
                dotGraph.generate(pdfPath.toAbsolutePath().toString());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            record(actualParseTree, parseTreePath);
        } else {
            ParseTreeNode expectedParseTree;
            try {
                expectedParseTree = JsonSerializer.deserialize(Files.readString(parseTreePath), ParseTreeNode.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            assertEquals(expectedParseTree, actualParseTree);
        }
    }

    private static void record(Object obj, Path path) {
        String json = JsonSerializer.serialize(obj);
        try {
            Files.writeString(path, json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
