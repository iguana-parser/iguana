package org.jgll.grammar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgll.parser.AbstractGLLParser;
import org.jgll.parser.GSSNode;
import org.jgll.parser.LevelSynchronizedGrammarInterpretter;
import org.jgll.parser.RecursiveDescentParser;
import org.jgll.recognizer.AbstractGLLRecognizer;
import org.jgll.recognizer.InterpretedGLLRecognizer;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.util.ToJavaCode;
import org.jgll.util.dot.GSSToDot;
import org.jgll.util.dot.GraphVizUtil;
import org.jgll.util.dot.SPPFToDot;
import org.jgll.util.dot.ToDotWithoutIntermediateNodes;
import org.jgll.util.dot.ToDotWithoutIntermeidateAndLists;
import org.junit.Before;

public abstract class AbstractGrammarTest {

	protected Grammar grammar;
	protected AbstractGLLParser rdParser;
	protected AbstractGLLParser levelParser;
	protected AbstractGLLRecognizer recognizer;
	protected String outputDir;
	
	@Before
	public void init() {
		grammar = initGrammar();
		rdParser = new RecursiveDescentParser();
		levelParser = new LevelSynchronizedGrammarInterpretter();
		recognizer = new InterpretedGLLRecognizer();
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
	
	protected static String getJavaCode(NonterminalSymbolNode node) {
		ToJavaCode toJavaCode = new ToJavaCode();
		toJavaCode.visit(node);
		return toJavaCode.getString();
	}
	 
	protected void generateSPPFGraphWithoutIntermeiateNodes(SPPFNode sppf) {
		SPPFToDot toDot = new ToDotWithoutIntermediateNodes();
		sppf.accept(toDot);
		GraphVizUtil.generateGraph(toDot.getString(), outputDir, "graph");
	}
	
	protected void generateSPPFGraph(SPPFNode sppf) {
		SPPFToDot toDot = new SPPFToDot();
		sppf.accept(toDot);
		GraphVizUtil.generateGraph(toDot.getString(), outputDir, "graph");
	}
	
	protected void generateSPPFGraphWithIntermeiateAndListNodes(SPPFNode sppf) {
		SPPFToDot toDot = new ToDotWithoutIntermeidateAndLists();
		sppf.accept(toDot);
		GraphVizUtil.generateGraph(toDot.getString(), outputDir, "graph");
	}
	
	protected void generateGSSGraph(Iterable<GSSNode> nodes) {
		GSSToDot toDot = new GSSToDot();
		toDot.execute(nodes);
		toDot.execute(levelParser.getLookupTable().getGSSNodes());
		GraphVizUtil.generateGraph(toDot.getString(), "/Users/ali/output", "gss");
	}
	
}
