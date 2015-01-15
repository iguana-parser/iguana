package org.jgll.util.benchmark;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.util.Configuration;
import org.jgll.util.Configuration.GSSType;
import org.jgll.util.Input;
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
public class BenchmarkCSharp extends AbstractBenchmark {
	
	private static Grammar CSharpGrammar = getGrammar("grammars/csharp/specification/csharp");
	
	@Param({ "/Users/aliafroozeh/test.cs" })
	String inputPath;
    
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
		config = Configuration.builder().build();
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
		result = null;
		grammarGraph = null;
		parser = null;
		input = null;
	}
	
	public static void main(String[] args) throws RunnerException, IOException {
		List<File> files = find("/Users/aliafroozeh/corpus/CSharp/output", "cs");
		String[] params = files.stream().map(f -> f.getAbsolutePath()).toArray(String[]::new);
		Options opt = new OptionsBuilder()
				          .include(BenchmarkCSharp.class.getSimpleName())
				          .param("inputPath", params)
				          .detectJvmArgs()
				          .resultFormat(ResultFormatType.CSV)		// -rf csv
				          .result("target/result.csv") 				// -rff target/result.csv 
				          .output("target/output.txt")				// -o target/output.txt
				          .shouldDoGC(true) 						// -gc true
				          .build();
		new Runner(opt).run();
	}
		
}
