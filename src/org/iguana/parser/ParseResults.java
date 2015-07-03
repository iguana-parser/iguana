package org.iguana.parser;

import org.iguana.util.BenchmarkUtil;
import org.iguana.util.Input;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.iguana.util.generator.GeneratorUtil.*;

public class ParseResults {

	public static String format(Iterable<ParseResult> results) {
		StringBuilder sb = new StringBuilder();
		sb.append(BenchmarkUtil.header()).append(NewLine);
		
		for (ParseResult r : results) {
			sb.append(r.getInput().getURI()).append(NewLine);
			if (r.isParseSuccess())
				sb.append(BenchmarkUtil.format(r.getInput(), r.asParseSuccess().getStatistics())).append(NewLine);
			else
				sb.append("Parse error " + r.asParseError());
		}
		return sb.toString();
	}

	public static String format(Map<Input, RunResult> results) {
		StringBuilder sb = new StringBuilder();
		sb.append(BenchmarkUtil.header()).append(NewLine);
		
		for (Entry<Input, RunResult> e : results.entrySet()) {
			Input input = e.getKey();
			RunResult result = e.getValue();
			sb.append(input.getURI()).append(NewLine);
			sb.append(String.format("%-20d %-20d %-20d %-20d %-20d %-20d %-20d %-20d %-20d %-20d %-15d %-15d", 
	    			input.length() - 1, 
	    			result.userTime / 1000_000,
	    			result.systemTime / 1000_000, 
	    			result.nanoTime / 1000_000,
	    			result.descriptorsCount,
	    			result.gssNodesCount,
	    			result.gssEdgesCount,
	    			result.nonterminalNodesCount,
	    			result.intermediateNodesCount,
	    			result.terminalNodesCount, 
	    			result.packedNodesCount, 
	    			result.ambiguousNodesCount)).append(NewLine);
		}
		return sb.toString();
	}	
	
	public static Map<Input, RunResult> groupByInput(Iterable<ParseResult> results) {
		return StreamSupport.stream(results.spliterator(), false)
		                    .filter(ParseResult::isParseSuccess)
		                    .map(ParseResult::asParseSuccess)
		                    .collect(Collectors.groupingBy(ParseResult::getInput, LinkedHashMap::new, new AverageResults()));
	}
	
	private static class AverageResults implements Collector<ParseSuccess, RunResult, RunResult> {

		@Override
		public Supplier<RunResult> supplier() {
			return () -> new RunResult();
		}

		@Override
		public BiConsumer<RunResult, ParseSuccess> accumulator() {
			return (r, s) -> {
				r.count      += 1;
				r.nanoTime   += s.getStatistics().getNanoTime();
				r.userTime   += s.getStatistics().getUserTime();
				r.systemTime += s.getStatistics().getSystemTime();
				r.memoryUsed += s.getStatistics().getMemoryUsed();
				
				r.descriptorsCount       = assertEquals(r.descriptorsCount, s.getStatistics().getDescriptorsCount());
				r.gssNodesCount          = assertEquals(r.gssNodesCount, s.getStatistics().getGssEdgesCount());
				r.gssEdgesCount          = assertEquals(r.gssEdgesCount, s.getStatistics().getGssEdgesCount());
				r.nonterminalNodesCount  = assertEquals(r.nonterminalNodesCount, s.getStatistics().getNonterminalNodesCount());
				r.terminalNodesCount     = assertEquals(r.terminalNodesCount, s.getStatistics().getTerminalNodesCount());
				r.intermediateNodesCount = assertEquals(r.intermediateNodesCount, s.getStatistics().getIntermediateNodesCount());
				r.packedNodesCount       = assertEquals(r.packedNodesCount, s.getStatistics().getPackedNodesCount());
				r.ambiguousNodesCount    = assertEquals(r.ambiguousNodesCount, s.getStatistics().getCountAmbiguousNodes());
			};
		}
		
		private static int assertEquals(int a, int b) {
			if (a == -1) return b;
			if (a == b) return b;
			throw new RuntimeException("Results are not the same across runs.");
		}
		
		@Override
		public BinaryOperator<RunResult> combiner() {
			return (left, right) -> {
				left.count      += 1;
				left.nanoTime   += right.nanoTime;
				left.userTime   += right.nanoTime;
				left.systemTime += right.systemTime;
				left.memoryUsed += right.memoryUsed;
				return left;
			};
		}

		@Override
		public Function<RunResult, RunResult> finisher() {
			return r -> new RunResult(r.count,
					                  r.nanoTime / r.count,
					                  r.systemTime / r.count,
								      r.userTime / r.count,
								      r.memoryUsed / r.count,
								      r.descriptorsCount,
								      r.gssNodesCount,
								      r.gssEdgesCount,
								      r.nonterminalNodesCount,
								      r.terminalNodesCount,
								      r.intermediateNodesCount,
								      r.packedNodesCount,
					                  r.ambiguousNodesCount);
		}

		@Override
		public Set<java.util.stream.Collector.Characteristics> characteristics() {
			return EnumSet.of(Characteristics.UNORDERED);
		}
	}
	
	public static class RunResult {
		int count;
		
		long nanoTime;
		long systemTime;
		long userTime;
		int memoryUsed;
		
		int descriptorsCount = -1;
		int gssNodesCount = -1;
		int gssEdgesCount = -1;
		int nonterminalNodesCount = -1;
		int terminalNodesCount = -1;
		int intermediateNodesCount = -1;
		int packedNodesCount = -1;
		int ambiguousNodesCount = -1;
		
		public RunResult() {} 
		
		public RunResult(int count, long nanoTime, long systemTime, long userTime, int memoryUsed, int descriptorsCount,
				int gssNodesCount, int gssEdgesCount, int nonterminalNodesCount, int terminalNodesCount,
				int intermediateNodesCount, int packedNodesCount, int ambiguousNodesCount) {
			this.count = count;
			this.nanoTime = nanoTime;
			this.systemTime = systemTime;
			this.userTime = userTime;
			this.memoryUsed = memoryUsed;
			this.descriptorsCount = descriptorsCount;
			this.gssNodesCount = gssNodesCount;
			this.gssEdgesCount = gssEdgesCount;
			this.nonterminalNodesCount = nonterminalNodesCount;
			this.terminalNodesCount = terminalNodesCount;
			this.intermediateNodesCount = intermediateNodesCount;
			this.packedNodesCount = packedNodesCount;
			this.ambiguousNodesCount = ambiguousNodesCount;
		}
	}
}
