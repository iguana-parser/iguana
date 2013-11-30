package org.jgll.util;

import org.jgll.parser.GSSNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.util.dot.GSSToDot;
import org.jgll.util.dot.GraphVizUtil;
import org.jgll.util.dot.SPPFToDot;
import org.jgll.util.dot.SPPFToDot2;
import org.jgll.util.dot.ToDotWithoutIntermediateNodes;
import org.jgll.util.dot.ToDotWithoutIntermeidateAndLists;


public class Visualization {
	
	public static void generateSPPFGraphWithoutIntermeiateNodes(String outputDir, SPPFNode sppf, Input input) {
		SPPFToDot toDot = new ToDotWithoutIntermediateNodes(input);
		sppf.accept(toDot);
		GraphVizUtil.generateGraph(toDot.getString(), outputDir, "graph");
	}
	
	public static void generateSPPFGraph(String outputDir, SPPFNode sppf, Input input) {
		SPPFToDot toDot = new SPPFToDot(input);
		sppf.accept(toDot);
		GraphVizUtil.generateGraph(toDot.getString(), outputDir, "graph");
	}
	
	public static void generateSPPFWithNonterminalNodesOnly(String outputDir, SPPFNode sppf, Input input) {
		SPPFToDot toDot = new ToDotWithoutIntermeidateAndLists(input);
		sppf.accept(toDot);
		GraphVizUtil.generateGraph(toDot.getString(), outputDir, "graph");
	}
	
	public static void generateGSSGraph(String outputDir, Iterable<GSSNode> nodes) {
		GSSToDot toDot = new GSSToDot();
		toDot.execute(nodes);
		GraphVizUtil.generateGraph(toDot.getString(), outputDir, "gss");
	}
	
	public static void generateSPPFNodesUnPacked(String outputDir, SPPFNode node, Input input) {
		SPPFToDot2 toDot = new SPPFToDot2(input);
		toDot.visit(node);
		int i = 0;
		for(StringBuilder sb : toDot.getResult()) {
			GraphVizUtil.generateGraph(sb.toString(), outputDir, "sppf-" + ++i);
		}
	}

}
