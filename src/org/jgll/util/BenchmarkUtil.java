package org.jgll.util;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jgll.grammar.Grammar;

public class BenchmarkUtil {
	
	public static int getMemoryUsed() {
		int mb = 1024 * 1024;
		Runtime runtime = Runtime.getRuntime();
		int memoryUsed = (int) ((runtime.totalMemory() - runtime.freeMemory()) / mb);
		return memoryUsed;
	}

	public static long getUserTime() {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		return bean.isCurrentThreadCpuTimeSupported() ? bean.getCurrentThreadUserTime() : 0L;
	}

	public static long getSystemTime() {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		return bean.isCurrentThreadCpuTimeSupported() ? 
				(bean.getCurrentThreadCpuTime() - bean.getCurrentThreadUserTime()): 0L;
	}
	
	public static List<File> find(String dir, String ext, boolean recursive) {
		List<File> inputs = new ArrayList<>();
		Collection<?> files = FileUtils.listFiles(new File(dir), new String[] {ext}, recursive);
		Iterator<?> it = files.iterator();
		while(it.hasNext()) {
			inputs.add((File) it.next());							
		}
		return inputs;
	}
	
	public static Grammar getGrammar(String path) {
		try {
			return GrammarUtil.load(new File(path).toURI());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Returns a string of c's with the given size 
	 * 
	 */
	public static String getChars(char c, int size) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < size; i++) {
			sb.append(c);
		}
		return sb.toString();
	}

	public static String header() {
	   return String.format("%-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-15s %-15s",
	    		   				"size", 
	    		   				"user_time", 
	    		   				"system_time", 
	    		   				"nano_time", 
	    		   				"descriptors",
	    		   				"gss_nodes",
	    		   				"gss_edges",
	    		   				"nonterminal_nodes", 
	    		   				"intermediate_nodes", 
	    		   				"terminal_nodes",
	    		   				"packed_nodes", 
	    		   				"ambiguous_nodes");
		}
		
	public static String format(Input input, ParseStatistics statistics) {
    	return String.format("%-20d %-20d %-20d %-20d %-20d %-20d %-20d %-20d %-20d %-20d %-15d %-15d", 
    			input.length() - 1, 
    			statistics.getUserTime() / 1000_000,
    			statistics.getSystemTime() / 1000_000, 
    			statistics.getNanoTime() / 1000_000,
    			statistics.getDescriptorsCount(),
    			statistics.getGssNodesCount(),
    			statistics.getGssEdgesCount(),
    			statistics.getNonterminalNodesCount(),
    			statistics.getIntermediateNodesCount(),
    			statistics.getTerminalNodesCount(), 
    			statistics.getPackedNodesCount(), 
    			statistics.getCountAmbiguousNodes());
	}

}
