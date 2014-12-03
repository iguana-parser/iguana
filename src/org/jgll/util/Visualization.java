package org.jgll.util;

import org.jgll.grammar.GrammarGraph;
import org.jgll.parser.gss.GSSNode;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.State;
import org.jgll.sppf.SPPFNode;
import org.jgll.util.trie.*;
import org.jgll.util.visualization.*;


public class Visualization {
	
	public static void generateSPPFGraphWithoutIntermeiateNodes(String outputDir, SPPFNode sppf, GrammarGraph grammar, Input input) {
		SPPFToDot toDot = new ToDotWithoutIntermediateNodes(grammar, input);
		sppf.accept(toDot);
		GraphVizUtil.generateGraph(toDot.getString(), outputDir, "graph");
	}
	
	public static void generateSPPFGraph(String outputDir, SPPFNode sppf, GrammarGraph grammar, Input input) {
		SPPFToDot toDot = new SPPFToDot(grammar, input);
		sppf.accept(toDot);
		GraphVizUtil.generateGraph(toDot.getString(), outputDir, "graph");
	}
	
	public static void generateSPPFWithNonterminalNodesOnly(String outputDir, SPPFNode sppf, GrammarGraph grammar, Input input) {
		SPPFToDot toDot = new ToDotWithoutIntermeidateAndLists(grammar, input);
		sppf.accept(toDot);
		GraphVizUtil.generateGraph(toDot.getString(), outputDir, "graph");
	}
	
	public static void generateGSSGraph(String outputDir, Iterable<GSSNode> gssNodes) {
		GSSToDot toDot = new GSSToDot();
		toDot.execute(gssNodes);
		GraphVizUtil.generateGraph(toDot.getString(), outputDir, "gss");
	}
	
	public static void generateSPPFNodesUnPacked(String outputDir, SPPFNode node, GrammarGraph grammar, Input input) {
		SPPFToDotUnpacked toDot = new SPPFToDotUnpacked(input);
		toDot.visit(node);
		int i = 0;
		for(String s : toDot.getResult()) {
			GraphVizUtil.generateGraph(s, outputDir, "sppf-" + ++i);
		}
	}
	
	public static void generateAutomatonGraph(String outputDir, Automaton automaton) {
		generateAutomatonGraph(outputDir, automaton.getStartState());
	}
	
	public static void generateAutomatonGraph(String outputDir, State startState) {
		String dot = AutomatonToDot.toDot(startState);
		GraphVizUtil.generateGraph(dot, outputDir, "automaton");
	}
	
	public static <T> void generateTrieGraph(String outputDir, Trie<T> trie) {
		String dot = new TrieToDot<>(trie).toString();
		GraphVizUtil.generateGraph(dot, outputDir, "trie");
	}

}
