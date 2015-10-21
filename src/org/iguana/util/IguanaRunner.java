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

import static java.util.stream.Stream.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import iguana.parsetrees.tree.TreeBuilder;
import iguana.parsetrees.tree.TreeBuilderFactory;
import iguana.utils.input.Input;
import iguana.utils.logging.IguanaLogger;
import iguana.utils.logging.JavaUtilIguanaLogger;
import iguana.utils.logging.LogLevel;
import org.apache.commons.io.FileUtils;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.parser.Iguana;
import org.iguana.parser.ParseResult;

import com.google.common.testing.GcFinalization;

public class IguanaRunner {
	
	private final Stream<Input> inputs;
	private final Grammar grammar;
	private final Configuration config;
	private final int warmupCount;
	private final int runCount;
	private final Nonterminal start;
	private final boolean runGCInBetween;
	private final int timeout;
    private final boolean buildTrees;
    private final TreeBuilder<?> treeBuilder;

    private final IguanaLogger logger;

	public IguanaRunner(Builder builder) {
		this.inputs = builder.inputs;
		this.grammar = builder.grammar;
		this.config = builder.config;
		this.start = builder.start;
		this.warmupCount = builder.warmupCount;
		this.runCount = builder.runCount;
		this.runGCInBetween = builder.runGCInBetween;
		this.timeout = builder.timeout;
        this.buildTrees = builder.buildTrees;
        this.treeBuilder = builder.treeBuilder;
        if (builder.log) {
            logger = new JavaUtilIguanaLogger("Iguana", builder.logLevel);
        } else {
            logger = IguanaLogger.DEFAULT;
        }
	}

	public List<RunResult> run() {

		final GrammarGraph grammarGraph = GrammarGraph.from(grammar, Input.empty(), config);
		
		List<RunResult> results = new ArrayList<>();

		Iterator<Input> it = inputs.iterator();
		
		while (it.hasNext()) {
			
			Input input = it.next();

            logger.log(input.getURI());

			logger.log("Warming up: ");
			for (int i = 0; i < warmupCount; i++) {
				try {
                    ParseResult result = run(grammarGraph, input, start);
                    if (result.isParseError()) {
						logger.log(result.asParseError());
					} else {
                        if (buildTrees) {
                            result.asParseSuccess().getTree(TreeBuilderFactory.getDefault(input));
                        }
                    }
					logger.log((i + 1) + " ");
				} catch (Exception e) {
					continue;
				}
			}
            logger.log("\n");
            logger.log("Running: ");
			for (int i = 0; i < runCount; i++) {			
				try {
					ParseResult result = run(grammarGraph, input, start);
					if (result.isParseSuccess()) {
						results.add(new SuccessResult(input.length(), input.getURI(), result.asParseSuccess().getStatistics()));
                        logger.log("  : Success");
                        if (buildTrees) {
                            result.asParseSuccess().getTree(TreeBuilderFactory.getDefault(input));
//                            TreeVisualization.generate(result.asParseSuccess().getTree(), "/Users/afroozeh/output", "tree", input);
                        }
					} else {
						results.add(new FailureResult(input.getURI(), result.asParseError().toString()));
                        logger.log("  : Error");
					}

                    logger.log((i + 1) + " ");
				} catch (Exception e) {
					e.printStackTrace();
					results.add(new FailureResult(input.getURI(), "Time out"));
                    logger.log("Time out");
					continue;
				}
			}
            logger.log("\n");
		}
		
		return results;
            
	}
	
	private ParseResult run(GrammarGraph grammarGraph, Input input, Nonterminal start) throws Exception {
		ParseResult result = Iguana.parse(input, grammarGraph, start);
		if (runGCInBetween)
			GcFinalization.awaitFullGc();
		
		return result;
		
//		ExecutorService executor = Executors.newSingleThreadExecutor();
//        Future<ParseResult> future = executor.submit(() -> {
//			return parser.parse(input, grammarGraph, start);
//        });
//        
//        ParseResult result;
//        if (timeout > 0)
//        	result = future.get(timeout, TimeUnit.SECONDS);
//        else 
//        	result = future.get();
//        
//        executor.shutdownNow();
//
//		if (runGCInBetween)
//			GcFinalization.awaitFullGc();
//		
//		return result;

	}
	
	public static Builder builder(Grammar grammar, Nonterminal start) {
		return new Builder(grammar, start);
	}
	
	public static class Builder {

		private final Grammar grammar;
		private final Nonterminal start;
		private Stream<Input> inputs = empty();
		private Configuration config = Configuration.DEFAULT;
		private int warmupCount = 0;
		private int runCount = 1;
		private boolean runGCInBetween = false;
		private int timeout = 30;
		private int limit = Integer.MAX_VALUE;
        private boolean buildTrees;
        private TreeBuilder<?> treeBuilder;
        private boolean log;
        private LogLevel logLevel= LogLevel.INFO;

		private Set<String> ignoreSet = new HashSet<>();
		
		public Builder(Grammar grammar, Nonterminal start) {
			this.grammar = grammar;
			this.start = start;
		}
		
		public Builder addDirectory(String dir, String ext, boolean recursive) {
			inputs = concat(inputs, find(dir, ext, recursive, ignoreSet).stream().map(Input::fromFile));
			return this;
		}
		
		public Builder addFile(String f) {
			inputs = concat(inputs, Stream.of(Input.fromFile(new File(f))));
			return this;
		}
		
		public Builder ignore(String f) {
			ignoreSet.add(f);
			return this;
		}
		
		public Builder addString(String s) {
			inputs = concat(inputs, Stream.of(Input.fromString(s)));
			return this;
		}
		
		public Builder addStrings(Iterable<String> strings) {
			inputs = concat(inputs, StreamSupport.stream(strings.spliterator(), false).map(Input::fromString));
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

        public Builder setBuildTrees(boolean buildTrees) {
            this.buildTrees = buildTrees;
            return this;
        }

        public Builder setTreeBuilder(TreeBuilder<?> treeBuilder) {
            this.treeBuilder = treeBuilder;
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
			inputs = inputs.limit(limit);
			return new IguanaRunner(this);
		}
		
		public static List<File> find(String dir, String ext, boolean recursive, Set<String> ignoreSet) {
			List<File> inputs = new ArrayList<>();
			Collection<?> files = FileUtils.listFiles(new File(dir), new String[] {ext}, recursive);
			Iterator<?> it = files.iterator();
			while(it.hasNext()) {
				File f = (File) it.next();
				if (!ignoreSet.contains(f.getAbsolutePath()))
					inputs.add(f);							
			}
			return inputs;
		}	

	}
	
}
