package org.iguana;

import iguana.utils.input.Input;
import iguana.utils.io.FileUtils;
import iguana.utils.visualization.DotGraph;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.transformation.GrammarTransformer;
import org.iguana.iggy.IggyParser;
import org.iguana.parser.*;
import org.iguana.parsetree.ParseTreeNode;
import org.iguana.traversal.exception.AmbiguityException;
import org.iguana.traversal.exception.CyclicGrammarException;
import org.iguana.util.serialization.JsonSerializer;
import org.iguana.util.serialization.ParseStatisticsSerializer;
import org.iguana.util.serialization.RecognizerStatisticsSerializer;
import org.iguana.util.visualization.ParseTreeToDot;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static iguana.utils.io.FileUtils.readFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class GrammarTest {

    private static boolean REGENERATE_FILES = false;

    static {
        String regenerate = System.getenv("REGENERATE");
        if (regenerate != null && regenerate.equals("true")) {
            REGENERATE_FILES = true;
        }
    }

    @TestFactory
    Collection<DynamicTest> grammarTests() throws IOException {
        String path = Paths.get("test/resources/grammars").toAbsolutePath().toString();

        List<Path> testPaths = getTests(path).stream().map(test -> Paths.get(test)).collect(Collectors.toList());

        List<DynamicTest> grammarTests = new ArrayList<>();

        for (Path test : testPaths) {
            test(grammarTests, test);
        }

        return grammarTests;
    }

    private void test(List<DynamicTest> grammarTests, Path test) throws IOException {
        Path testName = test.getFileName();
        Path category = test.getName(test.getNameCount() - 2);

        String testPath = test.toAbsolutePath().toString();

        String grammarPath = test + "/grammar.iggy";
        Grammar grammar = IggyParser.getGrammar(grammarPath);

        String jsonGrammarPath = testPath + "/grammar.json";

        if (REGENERATE_FILES || !Files.exists(Paths.get(jsonGrammarPath))) {
            record(grammar, jsonGrammarPath);
        } else {
            Grammar jsonGrammar;
            try {
                jsonGrammar = Grammar.load(jsonGrammarPath, "json");
            } catch (FileNotFoundException e) {
                throw new RuntimeException("No grammar.json file is present");
            }
            assertEquals(grammar, jsonGrammar);
        }

        RuntimeGrammar runtimeGrammar = GrammarTransformer.transform(grammar.toRuntimeGrammar(), grammar.getStartSymbol().getStartSymbol());

        File testDir = new File(testPath);
        int size = testDir.list((dir, name) -> name.matches("input\\d*.txt")).length;

        for (int i = 1; i <= size; i++) {
            String inputPath = testPath + "/input" + i + ".txt";

            Input input;
            try {
                input = Input.fromFile(new File(inputPath));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            String parserTestName = "Parser test " + category + " " + testName;
            IguanaParser parser = new IguanaParser(runtimeGrammar);
            DynamicTest dynamicParserTest = DynamicTest.dynamicTest(parserTestName, getParserTest(testPath, parser, i, input));

            String recognizerTestName = "Recognizer test " + category + " " + testName;
            IguanaRecognizer recognizer = new IguanaRecognizer(runtimeGrammar);
            DynamicTest dynamicRecognizerTest = DynamicTest.dynamicTest(recognizerTestName, getRecognizerTest(testPath, recognizer, i, input));

            grammarTests.add(dynamicParserTest);
            grammarTests.add(dynamicRecognizerTest);
        }
    }

    private Executable getRecognizerTest(String testPath, IguanaRecognizer recognizer, int j, Input input) {
        return () -> {
            if (recognizer.recognize(input)) {
                String statisticsPath = testPath + "/statistics" + j + ".json";
                RecognizerStatistics expectedStatistics = RecognizerStatisticsSerializer.deserialize(FileUtils.readFile(statisticsPath));

                assertEquals(expectedStatistics, recognizer.getStatistics());
            }
        };
    }

    private Executable getParserTest(String testPath, IguanaParser parser, int j, Input input) {
        return () -> {

            ParseTreeNode actualParseTree = null;
            boolean isCyclic = false;

            ParseError parseError = null;
            try {
                parser.parse(input);
            } catch (ParseError error) {
                parseError = error;
            }

            String statisticsPath = testPath + "/statistics" + j + ".json";
            if (REGENERATE_FILES || !Files.exists(Paths.get(statisticsPath))) {
                record(parser.getStatistics(), statisticsPath);
            } else {
                ParseStatistics expectedStatistics = ParseStatisticsSerializer.deserialize(FileUtils.readFile(statisticsPath));
                assertEquals(expectedStatistics, parser.getStatistics());
            }

            try {
                actualParseTree = parser.getParseTree();
                // No parse error
                if (actualParseTree != null) {
                    DotGraph dotGraph = ParseTreeToDot.getDotGraph(actualParseTree, input);
                    dotGraph.generate(testPath + "/tree" + j + ".pdf");
                }
            } catch (AmbiguityException e) {
                try {
                    actualParseTree = parser.getParseTree(true, true);
                    DotGraph dotGraph = ParseTreeToDot.getDotGraph(actualParseTree, input);
                    dotGraph.generate(testPath + "/tree" + j + ".pdf");
                } catch (CyclicGrammarException ee) {
                    isCyclic = true;
                }
            } catch (CyclicGrammarException e) {
                isCyclic = true;
            }

            if (!isCyclic) {
                String resultPath = testPath + "/result" + j + ".json";

                if (actualParseTree == null) { // Parse error
                    assertNotNull(parseError);
                    if (REGENERATE_FILES || !Files.exists(Paths.get(resultPath))) {
                        record(parser.getParseError(), resultPath);
                    } else {
                        ParseError expectedParseError = JsonSerializer.deserialize(readFile(resultPath), ParseError.class);
                        assertEquals(expectedParseError, parser.getParseError());
                    }
                } else {
                    if (REGENERATE_FILES || !Files.exists(Paths.get(resultPath))) {
                        record(actualParseTree, resultPath);
                    } else {
                        ParseTreeNode expectedParseTree = JsonSerializer.deserialize(readFile(resultPath), ParseTreeNode.class);
                        assertEquals(expectedParseTree, actualParseTree);
                    }
                }
            }
        };
    }

    private static void record(Object obj, String path) throws IOException {
        try {
            String json = JsonSerializer.serialize(obj);
            FileUtils.writeFile(json, path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<String> getTests(String rootPath) {
        List<String> tests = new ArrayList<>();
        getTests(rootPath, tests);
        return sort(tests);
    }

    private static List<String> sort(List<String> filePaths) {
        Pattern pattern = Pattern.compile("(.*?)(\\d*)");
        filePaths.sort((path1, path2) -> {
            Matcher matcher1 = pattern.matcher(path1);
            Matcher matcher2 = pattern.matcher(path2);
            if (matcher1.matches() && matcher2.matches()) {
                String path1NamePart = matcher1.group(1);
                int path1NumericPart = matcher1.group(2).equals("") ? 0 : Integer.parseInt(matcher1.group(2));

                String path2NamePart = matcher2.group(1);
                int path2NumericPart = matcher2.group(2).equals("") ? 0 : Integer.parseInt(matcher2.group(2));

                int diff = path1NamePart.compareTo(path2NamePart);
                if (diff != 0) return diff;
                return path1NumericPart - path2NumericPart;
            }
            throw new RuntimeException("Should not reach here");
        });
        return filePaths;
    }

    private static void getTests(String currentDirPath, List<String> tests) {
        File currentDir = new File(currentDirPath);
        if (!currentDir.isDirectory()) return;
        if (currentDir.getName().matches("Test.*\\d*")) {
            tests.add(currentDirPath);
        } else {
            for (String childDir : currentDir.list()) {
                getTests(currentDir + "/" + childDir, tests);
            }
        }
    }

}
