package org.jgll.util;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;

public class CLIguanaInterpreter {

	private static final int DEFAULT_WARMUP_COUNT = 0;
	private static final int DEFAULT_RUN_COUNT = 1;

	private int warmupCount;

	private int runCount;

	private final Input input;

	private final String startSymbol;

	private final Grammar grammar;

	public CLIguanaInterpreter(Grammar grammar, Input input, String startSymbol, int warmupCount, int runCount) {

		if (warmupCount < DEFAULT_WARMUP_COUNT) throw new IllegalArgumentException("Warmup count should be >= " + DEFAULT_WARMUP_COUNT);
		if (runCount < DEFAULT_WARMUP_COUNT) throw new IllegalArgumentException("Run count should be >= " + DEFAULT_RUN_COUNT);

		this.grammar = grammar;
		this.input = input;
		this.startSymbol = startSymbol;
		this.warmupCount = warmupCount;
		this.runCount = runCount;
	}

	public List<ParseResult> run() {
		
		List<ParseResult> results = new ArrayList<>();

		GrammarGraph grammarGraph = grammar.toGrammarGraph();
		GLLParser parser = null;

		for (int i = 0; i < warmupCount; i++) {
			parser = ParserFactory.newParser(grammar, input);
			parser.parse(input, grammarGraph, startSymbol);
//			waitForGC();
		}
		
		if (runCount == 1) {
			parser = ParserFactory.newParser(grammar, input);
			results.add(parser.parse(input, grammarGraph, startSymbol));
//			Visualization.generateSPPFGraphWithoutIntermeiateNodes("/Users/aliafroozeh/output", results.get(0).asParseSuccess().getSPPFNode(), grammarGraph, input);
		} else {
			for (int i = 0; i < runCount; i++) {
				parser = ParserFactory.newParser(grammar, input);
				results.add(parser.parse(input, grammarGraph, startSymbol));
//				waitForGC();
			}			
		}

		return results;
	}

	private void waitForGC() {
		System.gc();
		System.gc();
		System.runFinalization();

		final CountDownLatch latch = new CountDownLatch(1);
		new Object() {
			@Override
			protected void finalize() {
				latch.countDown();
			}
		};
		System.gc();
		System.gc();
		System.runFinalization();
		try {
			latch.await(2, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	public void printResult(List<ParseResult> results) {

		long sumNanoTime = 0;
		long sumUserTime = 0;
		int sumMemory = 0;

		for (ParseResult result : results) {
			// TODO: Later add a check to ensure all results are parse errors
			if (result.isParseError()) {
				System.out.println(result.asParseError());
				return;
			}

			ParseStatistics parseStatistics = result.asParseSuccess().getParseStatistics();
			sumNanoTime += parseStatistics.getNanoTime();
			sumUserTime += parseStatistics.getUserTime();
			sumMemory += parseStatistics.getMemoryUsed();
		}

		System.out.println(String.format("Nano time: %d ms, User time: %d ms, Memory: %d mb, Ambiguities: %d",
						sumNanoTime / (1000_000 * runCount),
						sumUserTime / (1000_000 * runCount),
						sumMemory / runCount,
 						results.get(0).asParseSuccess().getParseStatistics().getAmbiguitiesCount()));
	}

	public static void main(String[] args) {
		String grammarPath = "file:///Users/aliafroozeh/java";
		String inputPath = "/Users/aliafroozeh/test.java";
		String startSymbol = "start[CompilationUnit]";
		int runCount = 1;
		int warmupCount = 0;
		try {
			System.out.println("Parsing " + inputPath + "...");
			Grammar grammar = GrammarUtil.load(URI.create(grammarPath));
			CLIguanaInterpreter test = new CLIguanaInterpreter(grammar, Input.fromPath(inputPath), startSymbol, warmupCount, runCount);
			test.printResult(test.run());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
