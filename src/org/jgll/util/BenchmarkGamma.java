package org.jgll.util;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
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

import com.google.common.testing.GcFinalization;

/**
 * java -Xms14g -Xmx14g -jar target/benchmark.jar -v EXTRA -gc true -rf csv -rff target/result.csv -o output.txt
 * 
 * @author Ali Afroozeh
 *
 */

@State(Scope.Benchmark)
@Warmup(iterations=10)
@Measurement(iterations=20)
@Fork(1)
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class BenchmarkGamma {
	
	/**
	 * S ::= S S S | S S | b
	 */
	private static Grammar gamma2() {
		Nonterminal S = Nonterminal.withName("S");
		Character b = Character.from('b');
		Grammar.Builder builder = new Grammar.Builder();
		builder.addRule(Rule.builder(S).addSymbols(S, S, S).build());
		builder.addRule(Rule.builder(S).addSymbols(S, S).build());
		builder.addRule(Rule.builder(S).addSymbol(b).build());
		return builder.build();
	}

//    @Param({"10",  "20",  "30",  "40",  "50",  "60",  "70",  "80",  "90",  "100", 
//    		"110", "120", "130", "140", "150", "160", "170", "180", "190", "200",
//    		"210", "220", "230", "240", "250", "260", "270", "280", "290", "300",
//    		"310", "320", "330", "340", "350", "360", "370", "380", "390", "400",
//    		"410", "420", "430", "440", "450", "460", "470", "480", "490", "500"})
	@Param({ "300" })
	int inputSize;
    
    
	Input input;
	Configuration config;
	Grammar grammar;
	Nonterminal startSymbol;
	GrammarGraph grammarGraph;
	GLLParser parser;
	ParseResult result;
	
	@Setup(Level.Iteration)
	public void setup() {
		input = Input.fromString(getBs(inputSize));
		config = Configuration.builder().build();
		grammar = gamma2();
		startSymbol = Nonterminal.withName("S");
		grammarGraph = gamma2().toGrammarGraph(input, config);
		parser = ParserFactory.getParser(config, input, grammar);
	}
	
	@Benchmark
	public void test() {
		result = parser.parse(input, grammarGraph, startSymbol, config);
	}
	
	@TearDown(Level.Iteration)
	public void cleanUp() {
		result = null;
		grammarGraph = null;
		parser = null;
		input = null;
	}
	
	public static void main(String[] args) throws InstantiationException, IllegalAccessException {
		
		int warmupCount = 0;
		int runCount = 20;
		
		Grammar grammar = gamma2();
		Nonterminal startSymbol = Nonterminal.withName("S");
		
//		StringWriter writer = new StringWriter();
//		grammarGraph.generate(new PrintWriter(writer));
//		System.out.println(writer.toString());
//		Class<?> clazz = CompilationUtil.getClass("test", "Test", writer.toString());
		
		Input input = Input.fromString(getBs(500));
		Configuration config = Configuration.builder().build();
		
		// Warmup
		for (int i = 1; i <= warmupCount; i++) {
			GLLParser parser = ParserFactory.getParser(config, input, grammar);
//			GLLParser parser = (GLLParser) clazz.newInstance();
			parser.parse(input, grammar, startSymbol);
			parser.reset();
		}
		GcFinalization.awaitFullGc();
		
		System.out.println(BenchmarkUtil.header());
		for (int i = 40; i <= 40; i++) {
			for (int j = 0; j < runCount; j++) {
				input = Input.fromString(getBs(i * 10));
				GLLParser parser = ParserFactory.getParser(config, input, grammar);
				
//				GLLParser parser = (GLLParser) clazz.newInstance();
				
				ParseResult res = parser.parse(input, grammar, startSymbol);
//				parser.getGSSNodes().forEach(n -> System.out.println(n.countDescriptors() + ", " + n.countPoppedElements() + ", " + n.countGSSEdges()));
				
				if (res.isParseSuccess()) {
					System.out.println(BenchmarkUtil.format(input, res.asParseSuccess().getStatistics()));
				} else {
					System.out.println("Parse error");
				}
				parser = null;
				res = null;
				GcFinalization.awaitFullGc();
			}
		}
	}
	
	
	private static String getBs(int size) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < size; i++) {
			sb.append("b");
		}
		return sb.toString();
	}
	
	private static String getAs(int size) {
		StringBuilder sb = new StringBuilder();
		sb.append("a");
		for(int i = 0; i < size; i++) {
			sb.append("+a");
		}
		return sb.toString();
	}

}
