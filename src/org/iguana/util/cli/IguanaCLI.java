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
import iguana.utils.visualization.GraphVizUtil;
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.parser.Iguana;
import org.iguana.parser.ParseResult;
import org.iguana.parsetree.ParseTreeNode;
import org.iguana.util.JsonSerializer;
import org.iguana.util.visualization.ParseTreeToDot;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class IguanaCLI {

    public static void main(String[] args) {

        CommandLineParser commandLineParser = new DefaultParser();
        Input input = null;

        try {
            CommandLine line = commandLineParser.parse(getOptions(), args);

            if (line.hasOption("help")) {
                help();
                return;
            }

            if (line.hasOption("input")) {
                input = Input.fromPath(line.getOptionValue("input"));
            }

            if (line.hasOption("visualize")) {
                if (input == null) throw new RuntimeException("Input is not specified");

                String[] values = line.getOptionValues("visualize");
                try {
                    ParseTreeNode node = JsonSerializer.deserialize(values[0], ParseTreeNode.class);
                    System.out.println(JsonSerializer.toJSON(node));
                    String dot = new ParseTreeToDot().toDot(node, input);
                    GraphVizUtil.generateGraph(dot, values[1]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }

//            // Grammar
//            if (line.hasOption("g")) {
//                String grammarPath = line.getOptionValue("g");
//                grammar = Grammar.load(new File(grammarPath).toURI());
//            }
//
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

            help();
        } catch (ParseException e) {
            System.err.println("Invalid argument: " + e.getMessage());
        }
    }

    private static void help() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("iguana", getOptions());
        System.exit(0);
    }

    private static Options getOptions() {

        Option help = Option.builder("h").longOpt("help")
                .desc("print this message")
                .build();

        Option version = Option.builder("v").longOpt("version")
                .desc("print the version information")
                .build();

        Option grammar = Option.builder("g").longOpt("grammar")
                .desc("specify the grammar in .iggy format")
                .hasArg()
                .build();

        Option startSymbol = Option.builder("s").longOpt("start")
                .desc("The start symbol")
                .hasArg()
                .build();

        Option ddgrammar = Option.builder().longOpt("ddgrammar")
                .desc("specify the data-dependent grammar in .json format")
                .hasArg()
                .build();

        Option convert = Option.builder().longOpt("convert")
                .desc("convert the .iggy grammar to a data-dependent format")
                .hasArgs()
                .build();

        Option input = Option.builder("i").longOpt("input")
                .desc("specify the input file")
                .hasArg()
                .build();

        Option visualize = Option.builder("vis").longOpt("visualize")
                .desc("visualizes the parse tree, sppf, etc.")
                .numberOfArgs(2)
                .build();

        Option benchmark = Option.builder().longOpt("benchmark")
                .desc("benchmark the iguana parser")
                .hasArg()
                .build();

        Options options = new Options();
        options.addOption(help);
        options.addOption(version);
        options.addOption(grammar);
        options.addOption(startSymbol);
        options.addOption(ddgrammar);
        options.addOption(convert);
        options.addOption(input);
        options.addOption(visualize);
        options.addOption(benchmark);

        return options;
    }
}
