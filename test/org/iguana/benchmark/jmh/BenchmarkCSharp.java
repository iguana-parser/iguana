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

package org.iguana.benchmark.jmh;

import static org.iguana.util.BenchmarkUtil.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.parser.GLLParser;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParserFactory;
import org.iguana.util.BenchmarkUtil;
import org.iguana.util.Configuration;
import org.iguana.util.Input;
import org.iguana.util.Configuration.GSSType;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * 
 * java -Xms14g -Xmx14g -cp target/benchmark.jar org.jgll.util.benchmark.BenchmarkCSharp
 * 
 * @author Ali Afroozeh
 *
 */

@State(Scope.Benchmark)
@Warmup(iterations=5)
@Measurement(iterations=10)
@Fork(1)
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class BenchmarkCSharp {
	
	private static Grammar CSharpGrammar = getGrammar("grammars/csharp/specification/csharp");
	
	@Param({ "/Users/aliafroozeh/test.cs" })
	String inputPath;
    
	@Param({ "NEW" })
	GSSType gssType;
	
	Input input;
	Configuration config;
	Grammar grammar;
	Nonterminal startSymbol;
	GrammarGraph grammarGraph;
	GLLParser parser;
	ParseResult result;	


	@Setup(Level.Iteration)
	public void setup() throws IOException {
		input = Input.fromPath(inputPath);
		config = Configuration.builder().setGSSType(gssType).build();
		grammar = CSharpGrammar;
		startSymbol = Nonterminal.withName("start[CompilationUnit]");
		grammarGraph = CSharpGrammar.toGrammarGraph(input, config);
		parser = ParserFactory.getParser(config, input, grammar);
	}
	
	@Benchmark
	public void test() {
		result = parser.parse(input, grammarGraph, startSymbol);
	}
	
	@TearDown(Level.Iteration)
	public void cleanUp() {
		if (result.isParseSuccess()) {
			System.out.println(BenchmarkUtil.format(input, result.asParseSuccess().getStatistics()));
		}
		result = null;
		grammarGraph = null;
		parser = null;
		input = null;
	}
	
	public static void main(String[] args) throws RunnerException, IOException {
		List<File> files = find("/Users/aliafroozeh/corpus/CSharp/output", "cs", true);
		String[] params = files.stream().map(f -> f.getAbsolutePath()).toArray(String[]::new);
		String[] gssParams = new String[] { GSSType.NEW.toString(), GSSType.ORIGINAL.toString() };
		Options opt = new OptionsBuilder()
				          .include(BenchmarkCSharp.class.getSimpleName())
				          .param("inputPath", params)
				          .param("gssType", gssParams)
				          .detectJvmArgs()
				          .resultFormat(ResultFormatType.CSV)		// -rf csv
				          .result("target/result.csv") 				// -rff target/result.csv 
//				          .output("target/output.txt")				// -o target/output.txt
				          .shouldDoGC(true) 						// -gc true
				          .build();
		new Runner(opt).run();
	}
		
}
