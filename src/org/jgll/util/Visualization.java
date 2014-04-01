package org.jgll.util;

import java.util.Map;

import org.jgll.grammar.Grammar;
import org.jgll.parser.gss.GSSEdge;
import org.jgll.parser.gss.GSSNode;
import org.jgll.regex.automaton.State;
import org.jgll.sppf.SPPFNode;
import org.jgll.util.dot.GSSToDot;
import org.jgll.util.dot.GraphVizUtil;
import org.jgll.util.dot.NFAToDot;
import org.jgll.util.dot.SPPFToDot;
import org.jgll.util.dot.SPPFToDotUnpacked;
import org.jgll.util.dot.ToDotWithoutIntermediateNodes;
import org.jgll.util.dot.ToDotWithoutIntermeidateAndLists;


public class Visualization {
	
	public static void generateSPPFGraphWithoutIntermeiateNodes(String outputDir, SPPFNode sppf, Grammar grammar, Input input) {
		SPPFToDot toDot = new ToDotWithoutIntermediateNodes(grammar, input);
		sppf.accept(toDot);
		GraphVizUtil.generateGraph(toDot.getString(), outputDir, "graph");
	}
	
	public static void generateSPPFGraph(String outputDir, SPPFNode sppf, Grammar grammar, Input input) {
		SPPFToDot toDot = new SPPFToDot(grammar, input);
		sppf.accept(toDot);
		GraphVizUtil.generateGraph(toDot.getString(), outputDir, "graph");
	}
	
	public static void generateSPPFWithNonterminalNodesOnly(String outputDir, SPPFNode sppf, Grammar grammar, Input input) {
		SPPFToDot toDot = new ToDotWithoutIntermeidateAndLists(grammar, input);
		sppf.accept(toDot);
		GraphVizUtil.generateGraph(toDot.getString(), outputDir, "graph");
	}
	
	public static void generateGSSGraph(String outputDir, Iterable<GSSNode> nodes, Map<GSSNode, Iterable<GSSEdge>> map) {
		GSSToDot toDot = new GSSToDot();
		toDot.execute(nodes, map);
		GraphVizUtil.generateGraph(toDot.getString(), outputDir, "gss");
	}
	
	public static void generateSPPFNodesUnPacked(String outputDir, SPPFNode node, Grammar grammar, Input input) {
		SPPFToDotUnpacked toDot = new SPPFToDotUnpacked(grammar, input);
		toDot.visit(node);
		int i = 0;
		for(String s : toDot.getResult()) {
			GraphVizUtil.generateGraph(s, outputDir, "sppf-" + ++i);
		}
	}
	
	public static void generateAutomatonGraph(String outputDir, State startState) {
		String dot = NFAToDot.toDot(startState);
		GraphVizUtil.generateGraph(dot, outputDir, "automaton");
	}

}
