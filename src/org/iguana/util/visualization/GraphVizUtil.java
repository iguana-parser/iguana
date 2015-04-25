/*
 * Copyright (c) 2015, CWI
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

package org.iguana.util.visualization;

import java.io.FileWriter;
import java.io.Writer;

import org.iguana.util.logging.LoggerWrapper;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class GraphVizUtil {
	
	private static final LoggerWrapper log = LoggerWrapper.getLogger(GraphVizUtil.class);
	
	public static final String SYMBOL_NODE = "[shape=box, style=rounded, height=0.1, width=0.1, color=black, fontcolor=black, label=\"%s\", fontsize=10];";
	public static final String AMBIGUOUS_SYMBOL_NODE = "[shape=box, style=rounded, height=0.1, width=0.1, color=red, fontcolor=black, label=\"%s\", fontsize=10];";
	public static final String INTERMEDIATE_NODE = "[shape=box, height=0.2, width=0.4, color=black, fontcolor=black, label=\"%s\", fontsize=10];";
	public static final String AMBIGUOUS_INTERMEDIATE_NODE = "[shape=box, height=0.2, width=0.4, color=red, fontcolor=black, label=\"%s\", fontsize=10];";
	public static final String PACKED_NODE = "[shape=circle, height=0.1, width=0.1, color=black, fontcolor=black, label=\"%s\", fontsize=10];";
	public static final String EDGE = "edge [color=black, style=solid, penwidth=0.5, arrowsize=0.7];";
	
	public static final String GSS_NODE = "[shape=circle, height=0.1, width=0.1, color=black, fontcolor=black, label=\"%s\", fontsize=10];";
	public static final String GSS_EDGE = "edge [color=black, style=solid, penwidth=0.5, arrowsize=0.7, label=\"%s\"];";
	
	public static final String NONTERMINAL_SLOT = "[shape=circle, height=0.1, width=0.1, color=black, fontcolor=black, label=\"%s\", fontsize=10];";
	public static final String BODY_SLOT = "[shape=circle, height=0.1, width=0.1, color=black, fontcolor=black, fontsize=10, label=\"\"];";
	public static final String LAST_SYMBOL_SLOT = "[shape=circle, height=0.1, width=0.1, color=red, fontcolor=black, fontsize=10, label=\"%s\"];";
	public static final String LAST_SYMBOL_AND_END_SLOT = "[shape=doublecircle, height=0.1, width=0.1, color=red, fontcolor=black, fontsize=10, label=\"%s\"];";
	public static final String END_SLOT = "[shape=doublecircle, height=0.1, width=0.1, color=black, fontcolor=black, label=\"%s\", fontsize=10];";
	public static final String TRANSITION = "edge [color=black, style=solid, penwidth=0.5, arrowsize=0.7, label=\"%s\"];";
	public static final String SPECIAL_TERMINAL_TRANSITION = "edge [color=black, style=dotted, penwidth=1.5, arrowsize=0.7, label=\"%s\"];";
	public static final String EPSILON_TRANSITION = "edge [color=black, style=dashed, penwidth=0.5, arrowsize=0.7, label=\"\"];";
	
	public static final String NODE = "[shape=circle, height=0.1, width=0.1, color=black, fontcolor=black, label=\"%s\", fontsize=10];";

	
	public static final int TOP_DOWN = 0;
	public static final int LEFT_TO_RIGHT = 1;
	
	public static void generateGraph(String dot, String directory, String fileName) {
		generateGraph(dot, directory, fileName, TOP_DOWN);
	}
	
	/**
	 * Generates a graph from the given SPPF.
	 * 
	 * @param sppf
	 * @param directory
	 * @param name
	 */
	public static void generateGraph(String dot, String directory, String name, int layout) {
		StringBuilder sb = new StringBuilder();
		String lineSeparator = System.getProperty("line.separator");
		
		sb.append("digraph sppf {").append(lineSeparator);
		sb.append("layout=dot").append(lineSeparator);
		sb.append("nodesep=.6").append(lineSeparator);
		sb.append("ranksep=.4").append(lineSeparator);		
		sb.append("ordering=out").append(lineSeparator);
		
		if (layout == LEFT_TO_RIGHT) {
			sb.append("rankdir=LR").append(lineSeparator);
		}
		
		sb.append(dot);

		sb.append(lineSeparator);
		sb.append("}");
		
		String fileName = directory + "/" + name;
		try {
			Writer out = new FileWriter(fileName + ".txt");
			out.write(sb.toString());
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		generateImage(fileName);
	}

	private static void generateImage(String fileName) {
		String cmd = "/usr/local/bin/dot" + " -Tpdf " + "-o " + fileName + ".pdf" + " " + fileName + ".txt";
		log.info("Running " + cmd);

		try {
			Runtime run = Runtime.getRuntime();
			Process pr = run.exec(cmd);
			pr.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
