package org.jgll.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgll.grammar.GrammarGraph;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.ListSymbolNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.traversal.SPPFVisitor;

import static org.jgll.util.generator.GeneratorUtil.*;

public class ToJavaCode implements SPPFVisitor {
	
	private Map<SPPFNode, Integer> idsMap = new HashMap<>();
	
	protected Set<SPPFNode> visited = new HashSet<>();

	private int count = 1;
	private StringBuilder sb = new StringBuilder();

	public ToJavaCode() {
		sb.append("SPPFNodeFactory factory = new SPPFNodeFactory(grammar.toGrammarGraph());\n");
	}
	
	public static String toJavaCode(NonterminalNode node) {
		ToJavaCode toJavaCode = new ToJavaCode();
		toJavaCode.visit(node);
		return toJavaCode.toString();
	}
	
	@Override
	public void visit(TerminalSymbolNode node) {
		if(!visited.contains(node)) {
			visited.add(node);
			sb.append("TokenSymbolNode node" + count + " = factory.createTokenNode(" +
					  "\"" + escape(node.getGrammarSlot().toString()) + "\", " +
					  node.getLeftExtent() + ", " + node.getRightExtent() + ");\n");
		}
	}

	@Override
	public void visit(NonterminalNode node) {
		
		if (idsMap.put(node, idsMap.size()) != null) return;
		
		sb.append("NonterminalNode node" + count + " = factory.createNonterminalNode(" +
				"\"" + node.getGrammarSlot().getNonterminal() + "\", " + 
				idsMap.get(node) + ", " +
				node.getLeftExtent() + ", " + 
				node.getRightExtent() + ").init();\n");
		
		count++;
		
		visitChildren(node);
		
		addChildren(node);
	}

	@Override
	public void visit(IntermediateNode node) {
		
		if (idsMap.put(node, idsMap.size()) != null) return;
		
		sb.append("IntermediateNode node" + count + " = factory.createIntermediateNode(" +
				  "\"" + escape(node.getGrammarSlot().toString()) + "\", " + 
				  node.getLeftExtent() + ", " + 
				  node.getRightExtent() + ").init();\n");
		
		count++;
		
		visitChildren(node);
		
		addChildren(node);
	}

	@Override
	public void visit(PackedNode node) {

		sb.append("PackedNode node" + count + " = factory.createPackedNode(" +
				  "\"" + escape(node.getGrammarSlot().toString()) + "\", " + 
				  node.getPivot() + ", " + "node" + idsMap.get(node.getParent()) + ");\n");				
		
		count++;
		
		visitChildren(node);
		
		addChildren(node);			
	}

	@Override
	public void visit(ListSymbolNode node) {

		if (idsMap.put(node, idsMap.size()) != null) return;
		
		sb.append("ListSymbolNode node" + count + " = factory.createListNode(" +
				  "\"" + node.getGrammarSlot().toString() + "\", " +
				  idsMap.get(node) + ", " +
				  node.getLeftExtent() + ", " + 
				  node.getRightExtent() + ").init();\n");
		
		count++;
		
		visitChildren(node);
			
		addChildren(node);
	}
	
	private void visitChildren(SPPFNode node) {
		for(SPPFNode child : node.getChildren()) {
			child.accept(this);
		}
	}
	
	private void addChildren(SPPFNode node) {
		for(SPPFNode child : node.getChildren()) {
			sb.append("node" + idsMap.get(node) + ".addChild(" + "node" + idsMap.get(child) + ");\n");
		}
	}
	
	@Override
	public String toString() {
		return sb.toString();
	}

}
