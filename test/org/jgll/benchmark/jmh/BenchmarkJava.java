package org.jgll.benchmark.jmh;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.JavaCharacterLevel;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.util.BenchmarkUtil;
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
 * java -Xms14g -Xmx14g -cp target/benchmark.jar org.jgll.util.benchmark.BenchmarkJava
 * 
 * @author Ali Afroozeh
 *
 */

@State(Scope.Benchmark)
@Warmup(iterations=1)
@Measurement(iterations=1)
@Fork(0)
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class BenchmarkJava extends AbstractBenchmark {
	
	private static String sourceDir = "/Users/aliafroozeh/corpus/Java/jdk1.7.0_60-b19";
	private static Grammar javaGrammar = JavaCharacterLevel.grammar;
	
	@Param({ "/Users/aliafroozeh/test.java" })
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
		grammar = javaGrammar;
		startSymbol = Nonterminal.withName("start[CompilationUnit]");
		grammarGraph = javaGrammar.toGrammarGraph(input, config);
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
		List<File> files = find(sourceDir, "java");
		String[] params = files.stream().map(f -> f.getAbsolutePath()).toArray(String[]::new);
		String[] gssParams = new String[] { GSSType.NEW.toString(), GSSType.ORIGINAL.toString() };
		Options opt = new OptionsBuilder()
				          .include(BenchmarkJava.class.getSimpleName())
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
