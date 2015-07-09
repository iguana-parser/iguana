package org.iguana.util;

import static org.iguana.util.generator.GeneratorUtil.NewLine;

import java.net.URI;
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

public class RunResults {

	public static String format(Iterable<RunResult> results) {
		StringBuilder sb = new StringBuilder();
		sb.append(BenchmarkUtil.header()).append(NewLine);
		
		for (RunResult r : results) {
			sb.append(r.getInput()).append(NewLine);
			if (r.isSuccess())
				sb.append(BenchmarkUtil.format(r.asSuccess())).append(NewLine);
			else
				sb.append("Parse error " + r.asFailure());
		}
		return sb.toString();
	}

	public static String format(Map<URI, SuccessResult> results) {
		StringBuilder sb = new StringBuilder();
		sb.append(BenchmarkUtil.header()).append(NewLine);
		
		for (Entry<URI, SuccessResult> e : results.entrySet()) {
			URI input = e.getKey();
			SuccessResult result = e.getValue();
			sb.append(input).append(NewLine);
			sb.append(String.format("%-20d %-20d %-20d %-20d %-20d %-20d %-20d %-20d %-20d %-20d %-20d %-15d %-15d", 
	    			result.inputSize - 1, 
	    			result.statistics.userTime / 1000_000,
	    			result.statistics.systemTime / 1000_000, 
	    			result.statistics.nanoTime / 1000_000,
	    			result.statistics.memoryUsed,
	    			result.statistics.descriptorsCount,
	    			result.statistics.gssNodesCount,
	    			result.statistics.gssEdgesCount,
	    			result.statistics.nonterminalNodesCount,
	    			result.statistics.intermediateNodesCount,
	    			result.statistics.terminalNodesCount, 
	    			result.statistics.packedNodesCount, 
	    			result.statistics.ambiguousNodesCount)).append(NewLine);
		}
		return sb.toString();
	}	
	
	public static Map<URI, SuccessResult> groupByInput(Iterable<RunResult> results) {
		return StreamSupport.stream(results.spliterator(), false)
		                    .filter(RunResult::isSuccess)
		                    .map(RunResult::asSuccess)
		                    .collect(Collectors.groupingBy(s -> s.getInput(), LinkedHashMap::new, new AverageResults()));
	}
	
	private static class AverageResults implements Collector<SuccessResult, SuccessResult, SuccessResult> {

		@Override
		public Supplier<SuccessResult> supplier() {
			return () -> new SuccessResult();
		}

		@Override
		public BiConsumer<SuccessResult, SuccessResult> accumulator() {
			return (r, s) -> {
				r.runCount              += 1;
				r.statistics.nanoTime   += s.statistics.nanoTime;
				r.statistics.userTime   += s.statistics.userTime;
				r.statistics.systemTime += s.statistics.systemTime;
				r.statistics.memoryUsed += s.statistics.memoryUsed;
				
				r.inputSize                         = assertEquals(r.inputSize, s.inputSize);
				r.statistics.descriptorsCount       = assertEquals(r.statistics.descriptorsCount, s.statistics.descriptorsCount);
				r.statistics.gssNodesCount          = assertEquals(r.statistics.gssNodesCount, s.statistics.gssEdgesCount);
				r.statistics.gssEdgesCount          = assertEquals(r.statistics.gssEdgesCount, s.statistics.gssEdgesCount);
				r.statistics.nonterminalNodesCount  = assertEquals(r.statistics.nonterminalNodesCount, s.statistics.nonterminalNodesCount);
				r.statistics.terminalNodesCount     = assertEquals(r.statistics.terminalNodesCount, s.statistics.terminalNodesCount);
				r.statistics.intermediateNodesCount = assertEquals(r.statistics.intermediateNodesCount, s.statistics.intermediateNodesCount);
				r.statistics.packedNodesCount       = assertEquals(r.statistics.packedNodesCount, s.statistics.packedNodesCount);
				r.statistics.ambiguousNodesCount    = assertEquals(r.statistics.ambiguousNodesCount, s.statistics.ambiguousNodesCount);
			};
		}
		
		private static int assertEquals(int a, int b) {
			if (a == -1) return b;
			if (a == b) return b;
			throw new RuntimeException("Results are not the same across runs.");
		}
		
		@Override
		public BinaryOperator<SuccessResult> combiner() {
			return (left, right) -> {
				left.runCount      += 1;
				left.statistics.nanoTime   += right.statistics.nanoTime;
				left.statistics.userTime   += right.statistics.nanoTime;
				left.statistics.systemTime += right.statistics.systemTime;
				left.statistics.memoryUsed += right.statistics.memoryUsed;
				return left;
			};
		}

		@Override
		public Function<SuccessResult, SuccessResult> finisher() {
			return r -> new SuccessResult(r.runCount, r.inputSize, r.inputURI, new ParseStatistics(
					                  r.statistics.nanoTime / r.runCount,
					                  r.statistics.systemTime / r.runCount,
								      r.statistics.userTime / r.runCount,
								      r.statistics.memoryUsed / r.runCount,
								      r.statistics.descriptorsCount,
								      r.statistics.gssNodesCount,
								      r.statistics.gssEdgesCount,
								      r.statistics.nonterminalNodesCount,
								      r.statistics.terminalNodesCount,
								      r.statistics.intermediateNodesCount,
								      r.statistics.packedNodesCount,
					                  r.statistics.ambiguousNodesCount));
		}

		@Override
		public Set<java.util.stream.Collector.Characteristics> characteristics() {
			return EnumSet.of(Characteristics.UNORDERED);
		}
	}
}
