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

import iguana.utils.benchmark.BenchmarkUtil;
import iguana.utils.benchmark.Timer;
import iguana.utils.input.Input;
import iguana.utils.logging.LogLevel;
import iguana.utils.visualization.GraphVizUtil;
import org.apache.commons.io.FileUtils;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Start;
import org.iguana.parser.Iguana;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParserRuntime;
import org.iguana.parsetree.DefaultParseTreeBuilder;
import org.iguana.parsetree.ParseTreeNode;
import org.iguana.parsetree.SPPFToParseTree;
import org.iguana.sppf.NonPackedNode;
import org.iguana.sppf.NonterminalNode;
import org.iguana.util.serialization.JsonSerializer;
import org.iguana.util.visualization.ParseTreeToDot;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.sql.Time;
import java.util.*;

public class IguanaRunner {
	
	private final List<File> inputs;
	private final GrammarGraph grammarGraph;
	private final Configuration config;
	private final int warmupCount;
	private final int runCount;
	private final Start start;
	private final boolean runGCInBetween;
	private final int timeout;

	public IguanaRunner(Builder builder) {
		this.inputs = builder.inputFiles;
		this.grammarGraph = builder.grammarGraph;
		this.config = builder.config;
		this.start = builder.start;
		this.warmupCount = builder.warmupCount;
		this.runCount = builder.runCount;
		this.runGCInBetween = builder.runGCInBetween;
		this.timeout = builder.timeout;
	}

	public Map<URI, List<ParseStatistics>> run() {
		Map<URI, List<ParseStatistics>> resultsMap = new HashMap<>();

		System.out.printf("%-10s%20s%20s%20s%20s%20s%20s%20s%n", "#", "length", "nano time", "user time", "descriptors", "gss nodes", "ambiguities", "memory");

		for (File inputFile : inputs) {

			Input input;
			try {
				Timer timer = new Timer();
				timer.start();
				input = Input.fromFile(inputFile);
				timer.stop();
				System.out.println("Input created in " + timer.getUserTime() / 1000_000 + "ms");
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}

			for (int i = 0; i < warmupCount; i++) {
//				Iguana.run(input, new ParserRuntime(config), grammarGraph, Nonterminal.withName(grammarGraph.getStartSymbol().getName()), Collections.emptyMap(), true);
//				Iguana.run(input, new RecognizerRuntime(config), grammarGraph, Nonterminal.withName(grammarGraph.getStartSymbol().getName()), Collections.emptyMap(), true);
			}

			System.out.println("Running " + input.getURI());
			for (int i = 0; i < runCount; i++) {
				try {
					ParseResult<NonPackedNode> result = Iguana.run(input, new ParserRuntime(config), grammarGraph, Nonterminal.withName(start.getName()), Collections.emptyMap(), true);
//					ParseResult<RecognizerResult> result = Iguana.run(input, new RecognizerRuntime(config), grammarGraph, Nonterminal.withName(start.getName()), Collections.emptyMap(), true);
					if (result.isParseSuccess()) {
						ParseStatistics statistics = result.asParseSuccess().getStatistics();
						resultsMap.computeIfAbsent(input.getURI(), key -> new ArrayList<>()).add(statistics);
						Timer timer = new Timer();
//						timer.start();
//						SPPFToParseTree.toParseTree((NonterminalNode) result.asParseSuccess().getResult(), new DefaultParseTreeBuilder());
//						timer.stop();
//						System.out.println(timer.getSystemTime() / 1000_000 + ", " + timer.getNanoTime() / 1000_000);
						System.out.printf("%-10d%20d%20d%20d%20d%20d%20d%20d%n", i + 1, input.length(), statistics.getNanoTime() / 1000_000, statistics.getUserTime() / 1000_000, statistics.getDescriptorsCount(), statistics.getGssNodesCount(), statistics.getAmbiguousNodesCount(), statistics.getMemoryUsed());
					} else {
						System.out.printf("%-10d%20s%n", i + 1, result.asParseError());
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.printf("%-10d%20s%n", i + 1, "Unexpected Error");
				}
				if (runGCInBetween)
					BenchmarkUtil.awaitFullGC();
			}
		}
		
		return resultsMap;
	}
	
	public static Builder builder(Grammar grammar) {
		return new Builder(grammar);
	}
	
	public static class Builder {

		private GrammarGraph grammarGraph;
		private Start start;
		private List<File> inputFiles = new ArrayList<>();
		private Configuration config = Configuration.load();
		private int warmupCount = 0;
		private int runCount = 1;
		private boolean runGCInBetween = false;
		private int timeout = 30;
		private int limit = -1;
        private boolean log;
        private LogLevel logLevel= LogLevel.INFO;

		private Set<String> ignoreSet = new HashSet<>();
		
		public Builder(Grammar grammar) {
			Timer timer = new Timer();
			timer.start();
			this.grammarGraph = GrammarGraph.from(grammar, config);
			timer.stop();
			System.out.println("Grammar graph conversion: " + timer.getUserTime() / 1000_000 + "ms");
		}
		
		public Builder addDirectory(String dir, String ext, boolean recursive) {
			List<File> files = find(dir, ext, recursive, ignoreSet);
			inputFiles.addAll(files);
			return this;
		}
		
		public Builder addFile(String filePath) {
			inputFiles.add(new File(filePath));
			return this;
		}

		public Builder setStart(Start start) {
			this.start = start;
			return this;
		}
		
		public Builder ignore(String f) {
			ignoreSet.add(f);
			return this;
		}

		public Builder setConfiguration(Configuration config) {
			this.config = config;
			return this;
		}

		public Builder setWarmupCount(int warmupCount) {
			this.warmupCount = warmupCount;
			return this;
		}
		
		public Builder setRunCount(int runCount) {
			this.runCount = runCount;
			return this;
		}
		
		public Builder setRunGCInBetween(boolean runGCInBetween) {
			this.runGCInBetween = runGCInBetween;
			return this;
		}
				
		public Builder setTimeout(int timeout) {
			this.timeout = timeout;
			return this;
		}
		
		public Builder setLimit(int limit) {
			this.limit = limit;
			return this;
		}

        public Builder log() {
            this.log = true;
            return this;
        }

        public Builder setLogLevel(LogLevel logLevel) {
            this.logLevel = logLevel;
            return this;
        }

        public IguanaRunner build() {
			if (limit > 0)
				inputFiles = inputFiles.subList(0, limit);
			return new IguanaRunner(this);
		}
		
		private static List<File> find(String dir, String ext, boolean recursive, Set<String> ignoreSet) {
			List<File> inputs = new ArrayList<>();
			Collection<?> files = FileUtils.listFiles(new File(dir), new String[] {ext}, recursive);
			for (Object file : files) {
				File f = (File) file;
				if (!ignoreSet.contains(f.getAbsolutePath()))
					inputs.add(f);
			}
			return inputs;
		}	

	}
	
}
