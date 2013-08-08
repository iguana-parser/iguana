package org.jgll.util;

import org.jgll.parser.GSSNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.util.dot.GSSToDot;
import org.jgll.util.dot.GraphVizUtil;
import org.jgll.util.dot.SPPFToDot;
import org.jgll.util.dot.ToDotWithoutIntermediateNodes;
import org.jgll.util.dot.ToDotWithoutIntermeidateAndLists;


public class Visualization {
	
	public static void generateSPPFGraphWithoutIntermeiateNodes(String outputDir, SPPFNode sppf) {
		SPPFToDot toDot = new ToDotWithoutIntermediateNodes();
		sppf.accept(toDot);
		GraphVizUtil.generateGraph(toDot.getString(), outputDir, "graph");
	}
	
	public static void generateSPPFGraph(String outputDir, SPPFNode sppf) {
		SPPFToDot toDot = new SPPFToDot();
		sppf.accept(toDot);
		GraphVizUtil.generateGraph(toDot.getString(), outputDir, "graph");
	}
	
	protected void generateSPPFGraphWithIntermeiateAndListNodes(String outputDir, SPPFNode sppf) {
		SPPFToDot toDot = new ToDotWithoutIntermeidateAndLists();
		sppf.accept(toDot);
		GraphVizUtil.generateGraph(toDot.getString(), outputDir, "graph");
	}
	
	protected void generateGSSGraph(String outputDir, Iterable<GSSNode> nodes) {
		GSSToDot toDot = new GSSToDot();
		toDot.execute(nodes);
		toDot.execute(nodes);
		GraphVizUtil.generateGraph(toDot.getString(), outputDir, "gss");
	}

}
