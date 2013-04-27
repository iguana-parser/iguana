package org.jgll.grammar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgll.parser.GLLParser;
import org.jgll.parser.LevelSynchronizedGrammarInterpretter;
import org.jgll.parser.RecursiveDescentParser;
import org.jgll.sppf.SPPFNode;
import org.jgll.util.GraphVizUtil;
import org.jgll.util.ToDot;
import org.jgll.util.ToDotWithoutIntermediateNodes;
import org.jgll.util.ToDotWithoutIntermeidateAndLists;
import org.junit.Before;

public abstract class AbstractGrammarTest {

	protected Grammar grammar;
	protected GLLParser rdParser;
	protected GLLParser levelParser;
	protected String outputDir;
	
	@Before
	public void init() {
		grammar = initGrammar();
		rdParser = new RecursiveDescentParser();
		levelParser = new LevelSynchronizedGrammarInterpretter();
		outputDir = System.getProperty("user.home") + "/output";
	}
	
	protected abstract Grammar initGrammar();

	@SafeVarargs
	protected static <T> Set<T> set(T...objects) {
		Set<T>  set = new HashSet<>();
		for(T t : objects) {
			set.add(t);
		}
		return set;
	}
	
	@SafeVarargs
	protected static <T> List<T> list(T...objects){
		List<T> list = new ArrayList<>();
		for(T t : objects) {
			list.add(t);
		}
		return list;
	}
	
	protected static List<Symbol> emptyList() {
		return Collections.emptyList();
	}
	 
	protected void generateGraphWithoutIntermeiateNodes(SPPFNode sppf) {
		GraphVizUtil.generateGraph(sppf, new ToDotWithoutIntermediateNodes(), outputDir, "graph");
	}
	
	protected void generateGraph(SPPFNode sppf) {
		GraphVizUtil.generateGraph(sppf, new ToDot(), outputDir, "graph");
	}
	
	protected void generateGraphWithPackedNodeNames(SPPFNode sppf) {
		GraphVizUtil.generateGraph(sppf, new ToDot(true), outputDir, "graph");
	}
	
	protected void generateGraphWithIntermeiateAndListNodes(SPPFNode sppf) {
		GraphVizUtil.generateGraph(sppf, new ToDotWithoutIntermeidateAndLists(), outputDir, "graph");
	}
	
}
