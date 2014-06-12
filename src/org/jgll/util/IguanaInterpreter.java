package org.jgll.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.SPPFNode;

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
			
			for (SPPFNode ambiguousNode : parseStatistics.getAmbiguousNodes()) {
				Visualization.generateSPPFGraphWithoutIntermeiateNodes("/Users/aliafroozeh/output", ambiguousNode, grammar.toGrammarGraph(), input);
			}
		}

		System.out.println(String.format("Input size: %s, Nano time: %d ms, User time: %d ms, Memory: %d mb, Ambiguities: %d",
				        results.get(0).asParseSuccess().getParseStatistics().getInput().length(),
						sumNanoTime / (1000_000 * runCount),
						sumUserTime / (1000_000 * runCount),
						sumMemory / runCount,
 						results.get(0).asParseSuccess().getParseStatistics().getCountAmbiguousNodes()));
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
		
		options.addOption(OptionBuilder.withLongOpt("dir")
                .withDescription("The directory to search for files")
                .hasArg()
                .create("d"));
		
		options.addOption(OptionBuilder.withLongOpt("ext")
                .withDescription("The file extension name to search")
                .hasArg()
                .create("e"));
		
		options.addOption(OptionBuilder.withLongOpt("warmup")
                .withDescription("The warmup count")
                .hasArg()
                .create("w"));
		
		options.addOption(OptionBuilder.withLongOpt("run")
                .withDescription("The run count")
                .hasArg()
                .create("r"));

		String grammarPath = null;
		String startSymbol = null;
		String inputDir = null;
		
		List<String> inputPaths = new ArrayList<>();
		
		int runCount = 1;
		int warmupCount = 0;
		
		CommandLineParser parser = new BasicParser();
	    try {
	        CommandLine line = parser.parse(options, args);
	        if (line.hasOption("g")) {
	        	grammarPath = line.getOptionValue("g");
	        }
	        if (line.hasOption("s")) {
	        	startSymbol = line.getOptionValue("s");
	        }

	        if (line.hasOption("i")) {
	        	String inputPath = line.getOptionValue("i");
	        	inputPaths.add(inputPath);
	        } else if (line.hasOption("d")) {
	        	inputDir = line.getOptionValue("d");
	        	
	        	if (!line.hasOption("e")) {
	        		System.out.println("File extension is not specified.");
	        		System.exit(1);
	        	} else {
	        		String ext = line.getOptionValue("e");
	        		@SuppressWarnings("rawtypes")
					Collection files = FileUtils.listFiles(new File(inputDir), new String[] {ext}, true);
	        		@SuppressWarnings("rawtypes")
					Iterator it = files.iterator();
	        		while(it.hasNext()) {
	        			inputPaths.add(((File) it.next()).getPath());
	        		}
	        	}
	        }
	    }
		catch (ParseException e) {
			System.out.println(e.getMessage());
		}
		
		try {
			Grammar grammar = GrammarUtil.load(new File(grammarPath).toURI());
			for (String inputPath : inputPaths) {
				Input input = Input.fromPath(inputPath);
				System.out.println("Parsing " + input.getURI() + "...");
				IguanaInterpreter test = new IguanaInterpreter(grammar, input, startSymbol, warmupCount, runCount);
				test.printResult(test.run());				
			}		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
