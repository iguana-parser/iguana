package org.iguana;

import iguana.utils.input.Input;
import iguana.utils.io.FileUtils;
import iguana.utils.visualization.DotGraph;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.parser.Iguana;
import org.iguana.parser.ParseResult;
import org.iguana.parsetree.DefaultParseTreeBuilder;
import org.iguana.parsetree.ParseTreeNode;
import org.iguana.parsetree.SPPFToParseTree;
import org.iguana.result.RecognizerResult;
import org.iguana.sppf.CyclicGrammarException;
import org.iguana.sppf.NonPackedNode;
import org.iguana.sppf.NonterminalNode;
import org.iguana.util.ParseStatistics;
import org.iguana.util.serialization.JsonSerializer;
import org.iguana.util.serialization.ParseStatisticsSerializer;
import org.iguana.util.serialization.SPPFJsonSerializer;
import org.iguana.util.visualization.SPPFToDot;
import org.junit.Assert;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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

public class GrammarTest {

    @TestFactory
    Collection<DynamicTest> grammarTests() {
        String path = Paths.get("test/resources/grammars").toAbsolutePath().toString();

        List<Path> tests = getTests(path).stream().map(test -> Paths.get(test)).collect(Collectors.toList());

        List<DynamicTest> grammarTests = new ArrayList<>();

        for (Path test : tests) {
            Path testName = test.getFileName();
            Path category = test.getName(test.getNameCount() - 2);

            String testPath = test.toAbsolutePath().toString();

            String grammarPath = testPath + "/grammar.json";

            Grammar grammar;
            try {
                grammar = Grammar.load(grammarPath, "json");
            } catch (FileNotFoundException e) {
                throw new RuntimeException("No grammar.json file is present");
            }

            File testDir = new File(testPath);
            int size = testDir.list((dir, name) -> name.matches("input\\d*.txt")).length;

            for (int i = 1; i <= size; i++) {
                final int j = i;
                String inputPath = testPath + "/input" + i + ".txt";

                Input input;
                try {
                    input = Input.fromPath(inputPath);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                DynamicTest dynamicParserTest = DynamicTest.dynamicTest("Parser test " + category + " " + testName,
                        () -> {
                            ParseResult<NonPackedNode> result = Iguana.parse(input, grammar);

                            Assert.assertTrue(result.isParseSuccess());

                            String statisticsPath = testPath + "/statistics" + j + ".json";
                            String sppfPath = testPath + "/sppf" + j + ".json";
                            String parseTreePath = testPath + "/parsetree" + j + ".json";

                            if (!new File(statisticsPath).exists()) {
                                record(grammar, result, grammarPath, statisticsPath, sppfPath, parseTreePath);
                                return;
                            }

                            ParseStatistics expectedStatistics = ParseStatisticsSerializer.deserialize(FileUtils.readFile(statisticsPath), ParseStatistics.class);

                            GrammarGraph grammarGraph = GrammarGraph.from(grammar);
                            NonterminalNode expectedSPPFNode = SPPFJsonSerializer.deserialize(readFile(sppfPath), grammarGraph);

                            assertEquals(SPPFJsonSerializer.serialize(expectedSPPFNode), SPPFJsonSerializer.serialize((NonterminalNode) result.asParseSuccess().getResult()));

                            try {
                                ParseTreeNode actualParseTree = SPPFToParseTree.toParseTree((NonterminalNode) result.asParseSuccess().getResult(), new DefaultParseTreeBuilder());
                                ParseTreeNode expectedParseTree = JsonSerializer.deserialize(readFile(parseTreePath), ParseTreeNode.class);

                                assertEquals(expectedParseTree, actualParseTree);
                            } catch (CyclicGrammarException e) {}

                            assertEquals(expectedStatistics, result.asParseSuccess().getStatistics());
                        });

                DynamicTest dynamicRecognizerTest = DynamicTest.dynamicTest("Recognizer test " + category + " " + testName,
                        () -> {
                            ParseResult<RecognizerResult> result = Iguana.recognize(input, grammar);

                            Assert.assertTrue(result.isParseSuccess());

                            String statisticsPath = testPath + "/statistics" + j + ".json";
                            ParseStatistics expectedStatistics = ParseStatisticsSerializer.deserialize(FileUtils.readFile(statisticsPath), ParseStatistics.class);

                            assertEquals(expectedStatistics, result.asParseSuccess().getStatistics());
                        });

                grammarTests.add(dynamicParserTest);
                grammarTests.add(dynamicRecognizerTest);
            }
        }

        return grammarTests;
    }

    public static void record(Grammar grammar, ParseResult<NonPackedNode> result, String grammarPath, String statisticsPath, String sppfPath, String parseTreePath) throws IOException {
        String jsonGrammar = JsonSerializer.toJSON(grammar);
        FileUtils.writeFile(jsonGrammar, grammarPath);

        ParseStatistics statistics = result.asParseSuccess().getStatistics();
        String jsonStatistics = ParseStatisticsSerializer.serialize(statistics);
        FileUtils.writeFile(jsonStatistics, statisticsPath);

        String jsonSPPF = SPPFJsonSerializer.serialize((NonterminalNode) result.asParseSuccess().getResult());
        FileUtils.writeFile(jsonSPPF, sppfPath);

        try {
            ParseTreeNode parseTree = SPPFToParseTree.toParseTree((NonterminalNode) result.asParseSuccess().getResult(), new DefaultParseTreeBuilder());
            String jsonTree = JsonSerializer.toJSON(parseTree);
            FileUtils.writeFile(jsonTree, parseTreePath);
        } catch (Exception e) { }
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
