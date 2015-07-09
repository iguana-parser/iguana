/*
 * Copyright (c) 2015, Ali Afroozeh and Anastasia Izmaylova, Centrum Wiskunde & Informatica (CWI)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this 
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this 
 *    list of conditions and the following disclaimer in the documentation and/or 
 *    other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT 
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
 * OF SUCH DAMAGE.
 *
 */

package org.iguana.util;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.iguana.grammar.Grammar;

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
	   return String.format("%-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-20s %-15s %-15s",
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
		
	public static String format(SuccessResult result) {
    	return String.format("%-20d %-20d %-20d %-20d %-20d %-20d %-20d %-20d %-20d %-20d %-20d %-15d %-15d", 
    			result.inputSize - 1, 
    			result.statistics.getUserTime() / 1000_000,
    			result.statistics.getSystemTime() / 1000_000, 
    			result.statistics.getNanoTime() / 1000_000,
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

}
