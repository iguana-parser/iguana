package org.jgll.util;

import java.io.FileWriter;
import java.io.Writer;

import org.jgll.sppf.SPPFNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class GraphVizUtil {
	
	private static final Logger log = LoggerFactory.getLogger(GraphVizUtil.class);
	
	public static final String SYMBOL_NODE = "[shape=box, style=rounded, height=0.1, width=0.1, color=black, fontcolor=black, label=\"%s\", fontsize=10];";
	public static final String INTERMEDIATE_NODE = "[shape=box, height=0.2, width=0.4, color=black, fontcolor=black, label=\"%s\", fontsize=10];";
	public static final String PACKED_NODE = "[shape=circle, height=0.1, width=0.1, color=black, fontcolor=black, label=\"%s\", fontsize=10];";
	public static final String EDGE = "edge [color=black, penwidth=0.5, arrowsize=0.7];";
	
	public static final String GSS_NODE = "[shape=circle, height=0.1, width=0.1, color=black, fontcolor=black, label=\"%s\", fontsize=10];";
	public static final String GSS_EDGE = "edge [color=black, penwidth=0.5, arrowsize=0.7, label=\"%s\"];";

	
	/**
	 * Generates a graph from the given SPPF.
	 * 
	 * @param sppf
	 * @param directory
	 * @param name
	 */
	public static void generateGraph(SPPFNode sppf, ToDot toDot, String directory, String name) {
		StringBuilder sb = new StringBuilder();
		String lineSeparator = System.getProperty("line.separator");
		
		sb.append("digraph sppf {").append(lineSeparator);
		sb.append("layout=dot").append(lineSeparator);
		sb.append("nodesep=.6").append(lineSeparator);
		sb.append("ranksep=.4").append(lineSeparator);
		sb.append("ordering=out").append(lineSeparator);
		
		sppf.accept(toDot, sb);

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

		generatePNGImage(fileName);
	}

	private static void generatePNGImage(String fileName) {
		String cmd = "/usr/local/bin/dot" + " -Tpdf " + "-o " + fileName + ".pdf" + " " + fileName + ".txt";
		log.debug("Running " + cmd);

		try {
			Runtime run = Runtime.getRuntime();
			Process pr = run.exec(cmd);

			pr.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
