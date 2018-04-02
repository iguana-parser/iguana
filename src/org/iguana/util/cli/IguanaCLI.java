/*
 * Copyright (c) 2015, Ali Afroozeh and Anastasia Izmaylova, Centrum Wiskunde & Informatica (CWI)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 */

package org.iguana.util.cli;

import iguana.utils.input.Input;
import iguana.utils.io.FileUtils;
import iguana.utils.visualization.GraphVizUtil;
import org.apache.commons.cli.*;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.result.ParserResultOps;
import org.iguana.parsetree.ParseTreeNode;
import org.iguana.sppf.NonterminalNode;
import org.iguana.util.serialization.JsonSerializer;
import org.iguana.util.serialization.SPPFJsonSerializer;
import org.iguana.util.TestRunner;
import org.iguana.util.visualization.ParseTreeToDot;
import org.iguana.util.visualization.SPPFToDot;

import java.io.*;

public class IguanaCLI {

    public static void main(String[] args) {

        CommandLineParser commandLineParser = new DefaultParser();
        Input input = null;
        Grammar grammar = null;

        try {
            CommandLine line = commandLineParser.parse(getOptions(), args);

            if (line.hasOption("printHelp")) {
                printHelp();
                return;
            }

            if (line.hasOption("test")) {
                String testPath = line.getOptionValue("test");
                TestRunner.run(testPath);
                return;
            }

            if (line.hasOption("input")) {
                try {
                    input = Input.fromPath(line.getOptionValue("input"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (line.hasOption("grammar")) {
                String grammarPath = line.getOptionValue("grammar");
                try {
                    String jsonContent = FileUtils.readFile(grammarPath);
                    grammar = JsonSerializer.deserialize(jsonContent, Grammar.class);
                } catch (IOException e) {
                    System.out.println("Could not load the grammar from " + grammarPath);
                    e.printStackTrace();
                    return;
                }
            }

            if (line.hasOption("visTree")) {
                if (input == null) throw new RuntimeException("Input is not specified");

                String[] values = line.getOptionValues("visTree");
                try {
                    String contentPath = values[0];
                    String outputFile = values[1];

                    ParseTreeNode node = JsonSerializer.deserialize(FileUtils.readFile(contentPath), ParseTreeNode.class);
                    String dot = new ParseTreeToDot().toDot(node, input);
                    GraphVizUtil.generateGraph(dot, outputFile);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }

            if (line.hasOption("visSPPF")) {
                if (input == null) throw new RuntimeException("Input is not specified");
                if (grammar == null) throw new RuntimeException("Grammar is not specified");

                String[] values = line.getOptionValues("visSPPF");
                try {
                    String contentPath = values[0];
                    String outputFile = values[1];

                    NonterminalNode sppf = SPPFJsonSerializer.deserialize(new FileInputStream(contentPath), GrammarGraph.from(grammar, input, new ParserResultOps()));
                    SPPFToDot toDot = new SPPFToDot(input);
                    toDot.visit(sppf);
                    GraphVizUtil.generateGraph(toDot.getString(), outputFile);
                    System.out.println("SPPF is successfully visualized in " + outputFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }

//            // Start symbol
//            if (line.hasOption("s")) {
//                startSymbol = Nonterminal.withName(line.getOptionValue("s"));
//            }
//
//            // Input
//            if (line.hasOption("i")) {
//                inputPaths.add(line.getOptionValue("i"));
//            } else if (line.hasOption("d")) {
//                String inputDir = null;
//
//                inputDir = line.getOptionValue("d");
//
//                if (!line.hasOption("e")) {
//                    System.out.println("File extension is not specified.");
//                    System.exit(1);
//                } else {
//                    String ext = line.getOptionValue("e");
//                    @SuppressWarnings("rawtypes")
//                    Collection files = FileUtils.listFiles(new File(inputDir), new String[]{ext}, true);
//                    @SuppressWarnings("rawtypes")
//                    Iterator it = files.iterator();
//                    while (it.hasNext()) {
//                        String path = ((File) it.next()).getPath();
//                        if (!ignorePaths.contains(path)) {
//                            inputPaths.add(path);
//                        }
//                    }
//                }
//
//                try {
//                    for (String inputPath : inputPaths) {
//                        parse(startSymbol, grammar, Input.fromPath(inputPath), config);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            } else {
//                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//                try {
//                    String commandLineInput = in.readLine();
//                    System.out.println(commandLineInput);
//                    parse(startSymbol, grammar, Input.fromString(commandLineInput), config);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }

            printHelp();
        } catch (ParseException e) {
            System.err.println("Invalid argument: " + e.getMessage());
        }
    }

    private static void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(80);
        formatter.printHelp("iguana", getOptions());
        System.exit(0);
    }

    private static Options getOptions() {

        Option help = Option.builder("h").longOpt("printHelp")
                .desc("print this message")
                .build();

        Option version = Option.builder("v").longOpt("version")
                .desc("print the version information")
                .build();

        Option grammar = Option.builder("g").longOpt("grammar")
                .desc("the input grammar (in .iggy or .json format)")
                .argName("grammar")
                .hasArg()
                .build();

        Option startSymbol = Option.builder("s").longOpt("start")
                .desc("The start symbol")
                .argName("start")
                .hasArg()
                .build();

        Option convert = Option.builder().longOpt("convert")
                .desc("convert the .iggy grammar to json format")
                .numberOfArgs(2)
                .build();

        Option input = Option.builder("i").longOpt("input")
                .desc("specify the input file")
                .hasArg()
                .build();

        Option visualizeTree = Option.builder("visTree")
                .desc("visualizes the parse tree")
                .numberOfArgs(2)
                .argName("content> <output")
                .build();

        Option visualizeSPPF = Option.builder("visSPPF")
                .desc("visualizes the SPPF")
                .numberOfArgs(2)
                .argName("content> <output")
                .build();

        Option benchmark = Option.builder().longOpt("benchmark")
                .desc("benchmark the iguana parser")
                .hasArg()
                .build();

        Option test = Option.builder().longOpt("test")
                .desc("run tests")
                .hasArg()
                .build();

        Options options = new Options();
        options.addOption(help);
        options.addOption(version);
        options.addOption(grammar);
        options.addOption(startSymbol);
        options.addOption(convert);
        options.addOption(input);
        options.addOption(visualizeTree);
        options.addOption(visualizeSPPF);
        options.addOption(benchmark);
        options.addOption(test);

        return options;
    }
}
