package org.iguana.util;

import iguana.utils.input.Input;
import iguana.utils.io.FileUtils;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.iggy.IggyParser;
import org.iguana.parser.Iguana;
import org.iguana.parser.ParseResult;
import org.iguana.parsetree.DefaultParseTreeBuilder;
import org.iguana.parsetree.ParseTreeNode;
import org.iguana.parsetree.SPPFToParseTree;
import org.iguana.result.RecognizerResult;
import org.iguana.sppf.CyclicGrammarException;
import org.iguana.sppf.NonPackedNode;
import org.iguana.sppf.NonterminalNode;
import org.iguana.util.serialization.JsonSerializer;
import org.iguana.util.serialization.ParseStatisticsSerializer;
import org.iguana.util.serialization.SPPFJsonSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static iguana.utils.io.FileUtils.readFile;

public class TestRunner {

    public static void main(String[] args) {
        String path = Paths.get("test", "resources", "grammars", "basic").toAbsolutePath().toString();
        run(path);
    }

    public static void run(String rootDir) {
        Iterable<String> tests = getTests(rootDir);
        for (String testDir : tests) {
            runRecognizer(testDir);
            runParser(testDir);
        }
    }

    public static void record(Grammar grammar, Input input, int number, String dir) {
        String inputPath = dir + "/input" + number + ".txt";
        if (new File(inputPath).exists()) return;

        if (!new File(dir).exists()) {
            boolean dirCreated = new File(dir).mkdir();
            if (!dirCreated) throw new RuntimeException("Could not create the directory");
        }

        ParseResult<NonPackedNode> result = null;
        try {
            result = Iguana.parse(input, grammar);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (result.isParseError()) {
            throw new RuntimeException("Parse Error");
        }

        try {
            String jsonGrammar = JsonSerializer.toJSON(grammar);
            FileUtils.writeFile(jsonGrammar, dir + "/grammar.json");

            FileUtils.writeFile(input.toString(), inputPath);

            ParseStatistics statistics = result.asParseSuccess().getStatistics();
            String jsonStatistics = ParseStatisticsSerializer.serialize(statistics);
            FileUtils.writeFile(jsonStatistics, dir + "/statistics" + number + ".json");

            String jsonSPPF = SPPFJsonSerializer.serialize((NonterminalNode) result.asParseSuccess().getResult());
            FileUtils.writeFile(jsonSPPF, dir + "/sppf" + number + ".json");

            ParseTreeNode parseTree = SPPFToParseTree.toParseTree((NonterminalNode) result.asParseSuccess().getResult(), new DefaultParseTreeBuilder());
            String jsonTree = JsonSerializer.toJSON(parseTree);
            FileUtils.writeFile(jsonTree, dir + "/parsetree" + number + ".json");
        } catch (IOException | CyclicGrammarException e) {
            e.printStackTrace();
        }
    }

    private static void runRecognizer(String testPath) {
        Path path = Paths.get(testPath);
        Path testName = path.getFileName();
        System.out.println("Running " + testName);

        String grammarPath = testPath + "/grammar.json";

        Grammar grammar;
        try {
            grammar = Grammar.load(grammarPath, "json");
        } catch (FileNotFoundException e) {
            System.out.println("No grammar.json file is present");
            return;
        }

        String iggyPath = testPath + "/grammar.iggy";
        if (new File(iggyPath).exists()) {
            try {
                Grammar iggyGrammar = IggyParser.getGrammar(iggyPath);

                if (!iggyGrammar.equals(grammar)) {
                    error("Grammars do not match");
                }
            } catch (IOException e) {
                System.out.println("Problem parsing grammar.iggy");
                return;
            }
        }

        File testDir = new File(testPath);
        int size = testDir.list((dir, name) -> name.matches("input\\d*.txt")).length;

        for (int i = 1; i <= size; i++) {
            System.out.print("Input" + i);
            String inputPath = testPath + "/input" + i + ".txt";
            Input input;
            try {
                input = Input.fromPath(inputPath);
            } catch (FileNotFoundException e) {
                System.out.println("Input file not found");
                continue;
            } catch (IOException e) {
                System.out.println("An error occurred while reading from " + inputPath);
                continue;
            }

            ParseResult<RecognizerResult> result = Iguana.recognize(input, grammar);
            if (result.isParseError()) {
                error(result.asParseError().toString());
                continue;
            }

            if (result.asParseSuccess().getResult().getIndex() != input.length() - 1) {
                error("Recognition error");
                continue;
            }

            if (!checkParseStatistics(testPath, i, result.asParseSuccess().getStatistics())) continue;

            success();
        }
        System.out.println();
    }

    public static void runParser(String testPath) {
        Path path = Paths.get(testPath);
        Path testName = path.getFileName();
        System.out.println("Running " + testName);

        String grammarPath = testPath + "/grammar.json";

        Grammar grammar;
        try {
            grammar = Grammar.load(grammarPath, "json");
        } catch (FileNotFoundException e) {
            System.out.println("No grammar.json file is present");
            return;
        }

        String iggyPath = testPath + "/grammar.iggy";
        if (new File(iggyPath).exists()) {
            try {
                Grammar iggyGrammar = IggyParser.getGrammar(iggyPath);

                if (!iggyGrammar.equals(grammar)) {
                    error("Grammars do not match");
                }
            } catch (IOException e) {
                System.out.println("Problem parsing grammar.iggy");
                return;
            }
        }

        File testDir = new File(testPath);
        int size = testDir.list((dir, name) -> name.matches("input\\d*.txt")).length;

        for (int i = 1; i <= size; i++) {
            System.out.print("Input" + i);
            String inputPath = testPath + "/input" + i + ".txt";
            Input input;
            try {
                input = Input.fromPath(inputPath);
            } catch (FileNotFoundException e) {
                System.out.println("Input file not found");
                continue;
            } catch (IOException e) {
                System.out.println("An error occurred while reading from " + inputPath);
                continue;
            }

            ParseResult result = Iguana.parse(input, grammar);
            if (result.isParseError()) {
                error(result.asParseError().toString());
                continue;
            }

            if (!checkParseStatistics(testPath, i, result.asParseSuccess().getStatistics())) continue;

            String sppfPath = testPath + "/sppf" + i + ".json";
            NonterminalNode expectedSPPFNode;
            try {
                GrammarGraph grammarGraph = GrammarGraph.from(grammar);
                expectedSPPFNode = SPPFJsonSerializer.deserialize(readFile(sppfPath), grammarGraph);
            } catch (IOException e) {
                error("Cannot deserialize SPPF");
                continue;
            }

            if (!equals(expectedSPPFNode, (NonterminalNode) result.asParseSuccess().getResult())) {
                error("SPPF nodes do not match");
                continue;
            }

            ParseTreeNode actualParseTree;
            try {
                actualParseTree = SPPFToParseTree.toParseTree((NonterminalNode) result.asParseSuccess().getResult(), new DefaultParseTreeBuilder());
            } catch (CyclicGrammarException e) {
                success();
                continue;
            }

            String parseTreePath = testPath + "/parsetree" + i + ".json";
            ParseTreeNode expectedParseTree;
            try {
                expectedParseTree = JsonSerializer.deserialize(readFile(parseTreePath), ParseTreeNode.class);
            } catch (IOException e) {
                error("Cannot deserialize parse trees");
                continue;
            }

            if (!expectedParseTree.equals(actualParseTree)) {
                error("Parse trees do not match");
                continue;
            }
            success();
        }
        System.out.println();
    }

    private static boolean checkParseStatistics(String testPath, int i, ParseStatistics statistics) {
        String statisticsPath = testPath + "/statistics" + i + ".json";
        try {
            ParseStatistics expectedStatistics = ParseStatisticsSerializer.deserialize(FileUtils.readFile(statisticsPath), ParseStatistics.class);
            if (!expectedStatistics.equals(statistics)) {
                error("Parse results do not match");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("An error occurred while reading from " + statisticsPath);
            return false;
        }
        return true;
    }

    private static void error(String message) {
        System.out.println("\t\t\tError: " + message);
    }

    private static void success() {
        System.out.println("\t\t\tSuccess");
    }

    private static boolean equals(NonterminalNode expected, NonterminalNode actual) {
        return SPPFJsonSerializer.serialize(expected).equals(SPPFJsonSerializer.serialize(actual));
    }

    private static Iterable<String> getTests(String rootPath) {
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
            for (String childDir: currentDir.list()) {
                getTests(currentDir + "/" + childDir, tests);
            }
        }
    }
}
