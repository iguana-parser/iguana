//package org.iguana;
//
//import iguana.utils.input.Input;
//import iguana.utils.io.FileUtils;
//import org.iguana.grammar.Grammar;
//import org.iguana.parser.*;
//import org.iguana.parsetree.ParseTreeNode;
//import org.iguana.traversal.exception.AmbiguityException;
//import org.iguana.traversal.exception.CyclicGrammarException;
//import org.iguana.util.serialization.JsonSerializer;
//import org.iguana.util.serialization.ParseStatisticsSerializer;
//import org.iguana.util.serialization.RecognizerStatisticsSerializer;
//import org.junit.Assert;
//import org.junit.jupiter.api.DynamicTest;
//import org.junit.jupiter.api.TestFactory;
//import org.junit.jupiter.api.function.Executable;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//import java.util.stream.Collectors;
//
//import static iguana.utils.io.FileUtils.readFile;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//
//public class GrammarTest {
//
//    @TestFactory
//    Collection<DynamicTest> grammarTests() {
//        String path = Paths.get("test/resources/grammars").toAbsolutePath().toString();
//
//        List<Path> tests = getTests(path).stream().map(test -> Paths.get(test)).collect(Collectors.toList());
//
//        List<DynamicTest> grammarTests = new ArrayList<>();
//
//        for (Path test : tests) {
//            Path testName = test.getFileName();
//            Path category = test.getName(test.getNameCount() - 2);
//
//            String testPath = test.toAbsolutePath().toString();
//
//            String grammarPath = testPath + "/grammar.json";
//
//            Grammar grammar;
//            try {
//                grammar = Grammar.load(grammarPath, "json");
//            } catch (FileNotFoundException e) {
//                throw new RuntimeException("No grammar.json file is present");
//            }
//
//            File testDir = new File(testPath);
//            int size = testDir.list((dir, name) -> name.matches("input\\d*.txt")).length;
//
//            for (int i = 1; i <= size; i++) {
//                String inputPath = testPath + "/input" + i + ".txt";
//
//                Input input;
//                try {
//                    input = Input.fromFile(new File(inputPath));
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//
//                String parserTestName = "Parser test " + category + " " + testName;
//                IguanaParser parser = new IguanaParser(grammar);
//                DynamicTest dynamicParserTest = DynamicTest.dynamicTest(parserTestName, getParserTest(testPath, grammarPath, parser, i, input, grammar));
//
//                String recognizerTestName = "Recognizer test " + category + " " + testName;
//                IguanaRecognizer recognizer = new IguanaRecognizer(grammar);
//                DynamicTest dynamicRecognizerTest = DynamicTest.dynamicTest(recognizerTestName, getRecognizerTest(testPath, recognizer, i, input));
//
//                grammarTests.add(dynamicParserTest);
//                grammarTests.add(dynamicRecognizerTest);
//            }
//        }
//
//        return grammarTests;
//    }
//
//    private Executable getRecognizerTest(String testPath, IguanaRecognizer recognizer, int j, Input input) {
//        return () -> {
//            Assert.assertTrue(recognizer.recognize(input));
//
//            String statisticsPath = testPath + "/statistics" + j + ".json";
//            RecognizerStatistics expectedStatistics = RecognizerStatisticsSerializer.deserialize(FileUtils.readFile(statisticsPath));
//
//            assertEquals(expectedStatistics, recognizer.getStatistics());
//        };
//    }
//
//    private Executable getParserTest(String testPath, String grammarPath, IguanaParser parser, int j, Input input, Grammar grammar) {
//        return () -> {
//
//            ParseTreeNode actualParseTree = null;
//            boolean isCyclic = false;
//
//            try {
//                actualParseTree = parser.getParserTree(input);
//            } catch (AmbiguityException e) {
//                try {
//                    actualParseTree = parser.getParserTree(input, new ParseOptions.Builder().setAmbiguous(true).build());
//                } catch (CyclicGrammarException ee) {
//                    isCyclic = true;
//                }
//            } catch (CyclicGrammarException e) {
//                isCyclic = true;
//            }
//
//            String statisticsPath = testPath + "/statistics" + j + ".json";
//            String parseTreePath = testPath + "/parsetree" + j + ".json";
//
//            if (!new File(statisticsPath).exists()) {
//                record(parser, actualParseTree, input, grammar, grammarPath, statisticsPath, parseTreePath);
//                return;
//            }
//
//            ParseStatistics expectedStatistics = ParseStatisticsSerializer.deserialize(FileUtils.readFile(statisticsPath));
//            assertEquals(expectedStatistics, parser.getStatistics());
//
//            if (!isCyclic) {
//                assertNotNull("Parse Error: " + parser.getParseError(), actualParseTree);
//
//                ParseTreeNode expectedParseTree = JsonSerializer.deserialize(readFile(parseTreePath), ParseTreeNode.class);
//
//                assertEquals(expectedParseTree, actualParseTree);
//            }
//        };
//    }
//
//    private static void record(IguanaParser parser, ParseTreeNode parseTree, Input input, Grammar grammar, String grammarPath, String statisticsPath, String parseTreePath) throws IOException {
//        String jsonGrammar = JsonSerializer.toJSON(grammar);
//        FileUtils.writeFile(jsonGrammar, grammarPath);
//
//        ParseStatistics statistics = parser.getStatistics();
//        String jsonStatistics = ParseStatisticsSerializer.serialize(statistics);
//        FileUtils.writeFile(jsonStatistics, statisticsPath);
//
//        try {
//            String jsonTree = JsonSerializer.toJSON(parseTree);
//            FileUtils.writeFile(jsonTree, parseTreePath);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static List<String> getTests(String rootPath) {
//        List<String> tests = new ArrayList<>();
//        getTests(rootPath, tests);
//        return sort(tests);
//    }
//
//    private static List<String> sort(List<String> filePaths) {
//        Pattern pattern = Pattern.compile("(.*?)(\\d*)");
//        filePaths.sort((path1, path2) -> {
//            Matcher matcher1 = pattern.matcher(path1);
//            Matcher matcher2 = pattern.matcher(path2);
//            if (matcher1.matches() && matcher2.matches()) {
//                String path1NamePart = matcher1.group(1);
//                int path1NumericPart = matcher1.group(2).equals("") ? 0 : Integer.parseInt(matcher1.group(2));
//
//                String path2NamePart = matcher2.group(1);
//                int path2NumericPart = matcher2.group(2).equals("") ? 0 : Integer.parseInt(matcher2.group(2));
//
//                int diff = path1NamePart.compareTo(path2NamePart);
//                if (diff != 0) return diff;
//                return path1NumericPart - path2NumericPart;
//            }
//            throw new RuntimeException("Should not reach here");
//        });
//        return filePaths;
//    }
//
//    private static void getTests(String currentDirPath, List<String> tests) {
//        File currentDir = new File(currentDirPath);
//        if (!currentDir.isDirectory()) return;
//        if (currentDir.getName().matches("Test.*\\d*")) {
//            tests.add(currentDirPath);
//        } else {
//            for (String childDir : currentDir.list()) {
//                getTests(currentDir + "/" + childDir, tests);
//            }
//        }
//    }
//}
