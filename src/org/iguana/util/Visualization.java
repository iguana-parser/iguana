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

import org.iguana.grammar.GrammarGraph;
import org.iguana.parser.gss.GSSNode;
import org.iguana.regex.automaton.Automaton;
import org.iguana.regex.automaton.State;
import org.iguana.sppf.SPPFNode;
import org.iguana.util.collections.IntRangeTree;
import org.iguana.util.collections.RangeTree;
import org.iguana.util.trie.Trie;
import org.iguana.util.visualization.AutomatonToDot;
import org.iguana.util.visualization.GSSToDot;
import org.iguana.util.visualization.GrammarGraphToDot;
import org.iguana.util.visualization.GraphVizUtil;
import org.iguana.util.visualization.RangeTreeToDot;
import org.iguana.util.visualization.SPPFToDot;
import org.iguana.util.visualization.SPPFToDotUnpacked;
import org.iguana.util.visualization.ToDotWithoutIntermediateNodes;
import org.iguana.util.visualization.ToDotWithoutIntermeidateAndLists;
import org.iguana.util.visualization.TrieToDot;


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
	
	public static void generateGSSGraph(String outputDir, Iterable<GSSNode> gssNodes) {
		GSSToDot toDot = new GSSToDot();
		toDot.execute(gssNodes);
		GraphVizUtil.generateGraph(toDot.getString(), outputDir, "gss");
	}
	
	public static void generateSPPFNodesUnPacked(String outputDir, SPPFNode node, Input input) {
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
		GraphVizUtil.generateGraph(dot, outputDir, "automaton", GraphVizUtil.LEFT_TO_RIGHT);
	}
	
	public static <T> void generateRangeTree(String outputDir, RangeTree<T> t) {
		GraphVizUtil.generateGraph(RangeTreeToDot.toDot(t), outputDir, "rangeTree");
	}
	
	public static <T> void generateRangeTree(String outputDir, IntRangeTree t) {
		GraphVizUtil.generateGraph(RangeTreeToDot.toDot(t), outputDir, "rangeTree");
	}
	
	public static void generateGrammarGraph(String outputDir, GrammarGraph graph) {
		String dot = GrammarGraphToDot.toDot(graph);
		GraphVizUtil.generateGraph(dot, outputDir, "grammar", GraphVizUtil.LEFT_TO_RIGHT);
	}	
	
	public static <T> void generateTrieGraph(String outputDir, Trie<T> trie) {
		String dot = new TrieToDot<>(trie).toString();
		GraphVizUtil.generateGraph(dot, outputDir, "trie");
	}

}
