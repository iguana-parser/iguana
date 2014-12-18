package org.jgll.util;

import static org.jgll.util.generator.GeneratorUtil.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.jgll.grammar.symbol.Epsilon;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.ListSymbolNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalNode;
import org.jgll.traversal.SPPFVisitor;

public class ToJavaCode implements SPPFVisitor {
	
	private AtomicInteger id = new AtomicInteger(1);
	
	private Map<SPPFNode, Integer> idsMap = new HashMap<>();
	
	private StringBuilder sb = new StringBuilder();

	public ToJavaCode() {
		sb.append("SPPFNodeFactory factory = new SPPFNodeFactory(registry);\n");
	}
	
	public static String toJavaCode(NonterminalNode node) {
		ToJavaCode toJavaCode = new ToJavaCode();
		toJavaCode.visit(node);
		return toJavaCode.toString();
	}
	
	@Override
	public void visit(TerminalNode node) {
		
		if (idsMap.putIfAbsent(node, id.getAndIncrement()) != null)
			return;
		
		if (node.getGrammarSlot() == Epsilon.TOKEN_ID) {
			sb.append("TerminalNode node" + idsMap.get(node) + " = factory.createEpsilonNode(" + node.getLeftExtent() + ");\n");
		} else {
			sb.append("TerminalNode node" + idsMap.get(node) + " = factory.createTerminalNode(" +
					  "\"" + escape(node.getGrammarSlot().toString()) + "\", " +
					  node.getLeftExtent() + ", " + node.getRightExtent() + ");\n");			
		}
	}

	@Override
	public void visit(NonterminalNode node) {
		
		if (idsMap.putIfAbsent(node, id.getAndIncrement()) != null)
			return;
		
		sb.append("NonterminalNode node" + idsMap.get(node) + " = factory.createNonterminalNode(" +
				"\"" + node.getGrammarSlot().getNonterminal() + "\", " + 
				node.getGrammarSlot().getNonterminal().getIndex() + ", " +
				node.getLeftExtent() + ", " + 
				node.getRightExtent() + ").init();\n");
		
		visitChildren(node);
		
		addChildren(node);
	}

	@Override
	public void visit(IntermediateNode node) {
		
		if (idsMap.put(node, id.getAndIncrement()) != null) 
			return;
		
		sb.append("IntermediateNode node" + idsMap.get(node) + " = factory.createIntermediateNode(" +
				  "\"" + escape(node.getGrammarSlot().toString()) + "\", " + 
				  node.getLeftExtent() + ", " + 
				  node.getRightExtent() + ").init();\n");
		
		visitChildren(node);
		
		addChildren(node);
	}

	@Override
	public void visit(PackedNode node) {
		
		sb.append("PackedNode node" + id.getAndIncrement() + " = factory.createPackedNode(" +
				  "\"" + escape(node.getGrammarSlot().toString()) + "\", " + 
				  node.getPivot() + ", " + "node" + idsMap.get(node.getParent()) + ");\n");				
		
		visitChildren(node);
		
		addChildren(node);			
	}

	@Override
	public void visit(ListSymbolNode node) {

		if (idsMap.put(node, id.getAndIncrement()) != null) return;
		
		sb.append("ListSymbolNode node" + idsMap.get(node) + " = factory.createListNode(" +
				  "\"" + node.getGrammarSlot().toString() + "\", " +
				  idsMap.get(node) + ", " +
				  node.getLeftExtent() + ", " + 
				  node.getRightExtent() + ").init();\n");
		
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
