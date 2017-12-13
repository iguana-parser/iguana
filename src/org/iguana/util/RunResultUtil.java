package org.iguana.util;

import java.net.URI;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static iguana.utils.string.StringUtil.NewLine;


public class RunResultUtil {
	
	public static String format(Iterable<RunResult> results) {
		return format(results, TimeUnit.NANOSECONDS);
	}
	
	public static String format(Iterable<RunResult> results, TimeUnit unit) {
		StringBuilder sb = new StringBuilder();
		sb.append(header).append(NewLine);
		
		for (RunResult r : results) {
			sb.append(r.getInput()).append(NewLine);
			if (r.isSuccess())
				sb.append(format(r.asSuccess(), unit)).append(NewLine);
			else
				sb.append(r.asFailure()).append(NewLine);
		}
		return sb.toString();
	}

	public static String format(Map<URI, SuccessResult> results) {
		return format(results, TimeUnit.NANOSECONDS);
	}
	
	public static String format(Map<URI, SuccessResult> results, TimeUnit unit) {
		StringBuilder sb = new StringBuilder();
		sb.append(header).append(NewLine);
		for (Entry<URI, SuccessResult> e : results.entrySet()) {
			URI input = e.getKey();
			SuccessResult result = e.getValue();
			sb.append(input).append(NewLine);
			sb.append(format(result, unit)).append(NewLine);
		}
		return sb.toString();
	}	
	
	public static Map<URI, SuccessResult> groupByInput(Iterable<RunResult> results) {
		return StreamSupport.stream(results.spliterator(), false)
		                    .filter(RunResult::isSuccess)
		                    .map(RunResult::asSuccess)
		                    .collect(Collectors.groupingBy(s -> s.getInput(), LinkedHashMap::new, new AverageResults()));
	}
	
	public static String summary(Map<URI, SuccessResult> results) {
		int inputSum = results.values().stream().mapToInt(r -> r.inputSize).sum();
		long systemTimeSum = results.values().stream().mapToLong(r -> r.statistics.userTime).sum();
		return inputSum + ", " + systemTimeSum;
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
				r.statistics.gssNodesCount          = assertEquals(r.statistics.gssNodesCount, s.statistics.gssNodesCount);
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
	
	public static String format(SuccessResult result, TimeUnit unit) {
    	return String.format("%-20d %-20d %-20d %-20d %-20d %-20d %-20d %-20d %-20d %-20d %-20d %-15d %-15d", 
    			result.inputSize - 1, 
    			unit.convert(result.statistics.getUserTime(), TimeUnit.NANOSECONDS),
    			unit.convert(result.statistics.getSystemTime(), TimeUnit.NANOSECONDS),
    			unit.convert(result.statistics.getNanoTime(), TimeUnit.NANOSECONDS),
    			result.statistics.getMemoryUsed(),
    			result.statistics.getDescriptorsCount(),
    			result.statistics.getGssNodesCount(),
    			result.statistics.getGssEdgesCount(),
    			result.statistics.getNonterminalNodesCount(),
    			result.statistics.getIntermediateNodesCount(),
    			result.statistics.getTerminalNodesCount(), 
    			result.statistics.getPackedNodesCount(), 
    			result.statistics.getCountAmbiguousNodes());
	}
	
	private static String header = String.format("%-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-15s %-15s",
					    		   				 "size", 
					    		   				 "user_time", 
					    		   				 "system_time", 
					    		   				 "nano_time", 
					    		   				 "memory",
					    		   				 "descriptors",
					    		   				 "gss_nodes",
					    		   				 "gss_edges",
					    		   				 "nonterminal_nodes", 
					    		   				 "intermediate_nodes", 
					    		   				 "terminal_nodes",
					    		   				 "packed_nodes", 
					    		   				 "ambiguous_nodes");
}
