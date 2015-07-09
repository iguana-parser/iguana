package org.iguana.util;

import static org.iguana.util.generator.GeneratorUtil.*;

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

import org.iguana.parser.ParseResult;
import org.iguana.parser.ParseSuccess;

public class RunResults {

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

	public static String format(Map<Input, SuccessResult> results) {
		StringBuilder sb = new StringBuilder();
		sb.append(BenchmarkUtil.header()).append(NewLine);
		
		for (Entry<Input, SuccessResult> e : results.entrySet()) {
			Input input = e.getKey();
			SuccessResult result = e.getValue();
			sb.append(input.getURI()).append(NewLine);
			sb.append(String.format("%-20d %-20d %-20d %-20d %-20d %-20d %-20d %-20d %-20d %-20d %-15d %-15d", 
	    			input.length() - 1, 
	    			result.statistics.userTime / 1000_000,
	    			result.statistics.systemTime / 1000_000, 
	    			result.statistics.nanoTime / 1000_000,
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
	
	public static Map<Input, SuccessResult> groupByInput(Iterable<RunResult> results) {
		return StreamSupport.stream(results.spliterator(), false)
		                    .filter(r -> r instanceof SuccessResult)
		                    .map(r -> (SuccessResult) r)
		                    .collect(Collectors.groupingBy(s -> s, LinkedHashMap::new, new AverageResults()));
	}
	
	private static class AverageResults implements Collector<SuccessResult, SuccessResult, SuccessResult> {

		@Override
		public Supplier<SuccessResult> supplier() {
			return () -> new SuccessResult();
		}

		@Override
		public BiConsumer<SuccessResult, SuccessResult> accumulator() {
			return (r, s) -> {
				r.count                 += 1;
				r.statistics.nanoTime   += s.getStatistics().nanoTime;
				r.statistics.userTime   += s.getStatistics().userTime;
				r.statistics.systemTime += s.getStatistics().systemTime;
				r.statistics.memoryUsed += s.getStatistics().memoryUsed;
				
				r.statistics.descriptorsCount       = assertEquals(r.statistics.descriptorsCount, s.getStatistics().descriptorsCount);
				r.statistics.gssNodesCount          = assertEquals(r.statistics.gssNodesCount, s.getStatistics().gssEdgesCount);
				r.statistics.gssEdgesCount          = assertEquals(r.statistics.gssEdgesCount, s.getStatistics().gssEdgesCount);
				r.statistics.nonterminalNodesCount  = assertEquals(r.statistics.nonterminalNodesCount, s.getStatistics().nonterminalNodesCount);
				r.statistics.terminalNodesCount     = assertEquals(r.statistics.terminalNodesCount, s.getStatistics().terminalNodesCount);
				r.statistics.intermediateNodesCount = assertEquals(r.statistics.intermediateNodesCount, s.getStatistics().intermediateNodesCount);
				r.statistics.packedNodesCount       = assertEquals(r.statistics.packedNodesCount, s.getStatistics().packedNodesCount);
				r.statistics.ambiguousNodesCount    = assertEquals(r.statistics.ambiguousNodesCount, s.getStatistics().ambiguousNodesCount);
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
				left.count      += 1;
				left.statistics.nanoTime   += right.statistics.nanoTime;
				left.statistics.userTime   += right.statistics.nanoTime;
				left.statistics.systemTime += right.statistics.systemTime;
				left.statistics.memoryUsed += right.statistics.memoryUsed;
				return left;
			};
		}

		@Override
		public Function<SuccessResult, SuccessResult> finisher() {
			return r -> new SuccessResult(r.count, new ParseStatistics(
					                  r.statistics.nanoTime / r.count,
					                  r.statistics.systemTime / r.count,
								      r.statistics.userTime / r.count,
								      r.statistics.memoryUsed / r.count,
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
