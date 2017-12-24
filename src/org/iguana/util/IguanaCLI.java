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

package org.iguana.util;

import iguana.utils.input.Input;
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.parser.Iguana;
import org.iguana.parser.ParseResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class IguanaCLI {

    public static void main(String[] args) throws Exception {

        Configuration config = Configuration.DEFAULT;
        Grammar grammar = null;

        Set<String> inputPaths = new LinkedHashSet<>();
        Set<String> ignorePaths = new LinkedHashSet<>();

        Nonterminal startSymbol = null;


        CommandLineParser commandLineParser = new DefaultParser();

        try {
            CommandLine line = commandLineParser.parse(getOptions(), args);

            // Grammar
            if (line.hasOption("g")) {
                String grammarPath = line.getOptionValue("g");
                grammar = Grammar.load(new File(grammarPath).toURI());
            }

            // Start symbol
            if (line.hasOption("s")) {
                startSymbol = Nonterminal.withName(line.getOptionValue("s"));
            }

            // Input
            if (line.hasOption("i")) {
                inputPaths.add(line.getOptionValue("i"));
            } else if (line.hasOption("d")) {
                String inputDir = null;

                inputDir = line.getOptionValue("d");

                if (!line.hasOption("e")) {
                    System.out.println("File extension is not specified.");
                    System.exit(1);
                } else {
                    String ext = line.getOptionValue("e");
                    @SuppressWarnings("rawtypes")
                    Collection files = FileUtils.listFiles(new File(inputDir), new String[]{ext}, true);
                    @SuppressWarnings("rawtypes")
                    Iterator it = files.iterator();
                    while (it.hasNext()) {
                        String path = ((File) it.next()).getPath();
                        if (!ignorePaths.contains(path)) {
                            inputPaths.add(path);
                        }
                    }
                }

                if (line.hasOption("ignore")) {
                    for (String option : line.getOptionValues("ignore")) {
                        ignorePaths.add(option);
                    }
                }

                try {
                    for (String inputPath : inputPaths) {
                        if (!ignore(ignorePaths, inputPath)) {
                            parse(startSymbol, grammar, Input.fromPath(inputPath), config);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                try {
                    String commandLineInput = in.readLine();
                    System.out.println(commandLineInput);
                    parse(startSymbol, grammar, Input.fromString(commandLineInput), config);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void parse(Nonterminal startSymbol, Grammar grammar, Input input, Configuration config) {
        ParseResult result = Iguana.parse(input, grammar, Configuration.DEFAULT, startSymbol);
        if (result.isParseSuccess()) {
            // TODO: fix it!
//			System.out.println(BenchmarkUtil.format(input, result.asParseSuccess().getStatistics()));
        } else {
            System.out.println("Parse error.");
        }
    }

    @SuppressWarnings("static-access")
    public static Options getOptions() {

        Options options = new Options();

        options.addOption("v", "verbose", false, "Verbose.");

        options.addOption(Option.builder("g").longOpt("grammar")
                .desc("The grammar file")
                .hasArg()
                .required()
                .build());

        options.addOption(Option.builder("n").longOpt("no-first-follow")
                .desc("Enable first/follow checks")
                .build());

        options.addOption(Option.builder("s").longOpt("start")
                .desc("The start symbol")
                .hasArg()
                .required()
                .build());

        options.addOption(Option.builder("i").longOpt("input")
                .desc("The input file")
                .hasArg()
                .build());

        options.addOption(Option.builder("d").longOpt("dir")
                .desc("The directory to search for files")
                .hasArg()
                .build());

        options.addOption(Option.builder("e").longOpt("ext")
                .desc("The file extension name to search")
                .hasArg()
                .build());

        options.addOption(Option.builder().longOpt("ignore")
                .desc("The files to be ignored")
                .hasArgs()
                .build());

        options.addOption(Option.builder("w").longOpt("warmup")
                .desc("The warmup count")
                .hasArg()
                .build());

        options.addOption(Option.builder("r").longOpt("run")
                .desc("The run count")
                .hasArg()
                .build());

        return options;
    }

    private static boolean ignore(Set<String> ignorePaths, String inputPath) {
        if (ignorePaths.isEmpty()) {
            return false;
        }
        for (String s : ignorePaths) {
            if (!inputPath.endsWith(s)) {
                return true;
            }
        }
        return false;
    }

}
