package org.jgll.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;

public class IguanaInterpreter {

	private static final int DEFAULT_WARMUP_COUNT = 0;
	private static final int DEFAULT_RUN_COUNT = 1;

	private int warmupCount;

	private int runCount;

	private final Input input;

	private final String startSymbol;

	private final Grammar grammar;

	public IguanaInterpreter(Grammar grammar, Input input, String startSymbol, int warmupCount, int runCount) {

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

	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		
		Options options = new Options();
		
		options.addOption( "v", "verbose", false, "run verbosely." );
		
		options.addOption(OptionBuilder.withLongOpt("grammar")
		                  .withDescription("The grammar file")
		                  .hasArg()
		                  .isRequired()
		                  .create("g"));
		
		options.addOption(OptionBuilder.withLongOpt("start")
                .withDescription("The start symbol")
                .hasArg()
                .isRequired()
                .create("s"));

		options.addOption(OptionBuilder.withLongOpt("input")
                .withDescription("The input file")
                .hasArg()
                .create("i"));

		String grammarPath = null;
		String inputPath = null;
		String startSymbol = null;
		int runCount = 1;
		int warmupCount = 0;
		
		CommandLineParser parser = new BasicParser();
	    try {
	        CommandLine line = parser.parse(options, args);
	        if (line.hasOption("g")) {
	        	grammarPath = line.getOptionValue("g");
	        }
	        if (line.hasOption("s")) {
	        	inputPath = line.getOptionValue("s");
	        }
	        if (line.hasOption("i")) {
	        	inputPath = line.getOptionValue("i");
	        }
	    }
		catch (ParseException e) {
			System.out.println(e.getMessage());
		}
		
		try {
			System.out.println("Parsing " + inputPath + "...");
			Grammar grammar = GrammarUtil.load(new File(grammarPath).toURI());
			IguanaInterpreter test = new IguanaInterpreter(grammar, Input.fromPath(inputPath), startSymbol, warmupCount, runCount);
			test.printResult(test.run());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
