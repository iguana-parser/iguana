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
import iguana.utils.input.Input;
import iguana.utils.logging.LogLevel;
import org.apache.commons.io.FileUtils;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Start;
import org.iguana.parser.Iguana;
import org.iguana.parser.ParseResult;
import org.iguana.result.ParserResultOps;
import org.iguana.result.RecognizerResult;
import org.iguana.result.RecognizerResultOps;
import org.iguana.sppf.NonPackedNode;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class IguanaRunner {
	
	private final List<File> inputFiles;
	private final Grammar grammar;
	private final Configuration config;
	private final int warmupCount;
	private final int runCount;
	private final Start start;
	private final boolean runGCInBetween;
	private final int timeout;

	public IguanaRunner(Builder builder) {
		this.inputFiles = builder.inputFiles;
		this.grammar = builder.grammar;
		this.config = builder.config;
		this.start = builder.start;
		this.warmupCount = builder.warmupCount;
		this.runCount = builder.runCount;
		this.runGCInBetween = builder.runGCInBetween;
		this.timeout = builder.timeout;
	}

	public Map<Input, List<ParseStatistics>> run() {
		Map<Input, List<ParseStatistics>> resultsMap = new HashMap<>();

		for (File file : inputFiles) {

			Input input;
			try {
				input = Input.fromFile(file);
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}

//			GrammarGraph<RecognizerResult> grammarGraph = GrammarGraph.from(grammar, input, config, new RecognizerResultOps());
			GrammarGraph<NonPackedNode> grammarGraph = GrammarGraph.from(grammar, input, config, new ParserResultOps());

			for (int i = 0; i < warmupCount; i++) {
				Iguana.parse(input, grammarGraph, Nonterminal.withName(grammar.getStartSymbol().getName()), Configuration.DEFAULT, Collections.emptyMap(), true);
			}

			System.out.println("Running " + file.getPath());
			System.out.printf("%-10s%20s%20s%20s%20s%20s%n", "#", "length", "nano time", "user time", "descriptors", "memory");
			for (int i = 0; i < runCount; i++) {
				try {
					ParseResult<NonPackedNode> result = Iguana.parse(input, grammarGraph, Nonterminal.withName(grammar.getStartSymbol().getName()), Configuration.DEFAULT, Collections.emptyMap(), true);
//					ParseResult<RecognizerResult> result = Iguana.parse(input, grammarGraph, Nonterminal.withName(grammar.getStartSymbol().getName()), Configuration.DEFAULT, Collections.emptyMap(), true);
					if (result.isParseSuccess()) {
						ParseStatistics statistics = result.asParseSuccess().getStatistics();
						resultsMap.computeIfAbsent(input, key -> new ArrayList<>()).add(statistics);
						System.out.printf("%-10d%20d%20d%20d%20d%20d%n", i + 1, input.length(), statistics.getNanoTime() / 1000_000, statistics.getUserTime() / 1000_000, statistics.getDescriptorsCount(), statistics.getMemoryUsed());
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

		private final Grammar grammar;
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
			this.grammar = grammar;
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
