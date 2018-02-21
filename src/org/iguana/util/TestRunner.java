package org.iguana.util;

import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.parser.Iguana;
import org.iguana.parser.ParseResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class TestRunner {

    public static void main(String[] args) {
        run("/Users/afroozeh/workspace/iguana/test/grammars/basic/Test1");
    }

    public static void run(String testPath) {
        Path path = Paths.get(testPath);
        Path testName = path.getFileName();

        String grammarPath = testPath + "/grammar.json";

        Grammar grammar;
        try {
            grammar = Grammar.load(grammarPath, "json");
        } catch (FileNotFoundException e) {
            System.out.println("No grammar.json file is present");
            return;
        }

        File testDir = new File(testPath);
        String[] inputNames = testDir.list((dir, name) -> name.matches("input\\d*.txt"));

        for (String inputName : inputNames) {
            String inputPath = testPath + "/" + inputName;
            Input input;
            try {
                input = Input.fromPath(inputPath);
            } catch (FileNotFoundException e) {
                System.out.println();
                return;
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            ParseResult result = Iguana.parse(input, grammar, Nonterminal.withName("A"));
            System.out.println(result);
        }
    }
}
