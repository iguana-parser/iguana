package org.jgll.util;

import java.util.Stack;

import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.ListSymbolNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.traversal.SPPFVisitor;

public class ToJavaCode implements SPPFVisitor {
	
	private int count;
	private StringBuilder sb = new StringBuilder();
	Stack<String> stack = new Stack<>();

	@Override
	public void visit(TerminalSymbolNode node) {
		sb.append("TerminalSymbolNode node" + count + " = new TerminalSymbolNode(" + node.getMatchedChar() + ", " + node.getLeftExtent() + ");\n");
		stack.push("node" + count);
		count++;
	}

	@Override
	public void visit(NonterminalSymbolNode node) {
		visitChildren(node);
		sb.append("NonterminalSymbolNode node" + count + " = new NonterminalSymbolNode(" +
					"grammar.getNonterminal(" + node.getGrammarSlot().getId()  + "), " + 
					node.getLeftExtent() + ", " + 
					node.getRightExtent() + ");\n");
		
		addChildren(node);
		
		stack.push("node" + count);
		count++;
	}

	@Override
	public void visit(IntermediateNode node) {
		visitChildren(node);
		sb.append("Intermediate node" + count + " = new IntermediateNode(" +
					"grammar.getGrammarSlot(" + node.getGrammarSlot().getId()  + "), " + 
					node.getLeftExtent() + ", " + 
					node.getRightExtent() + ");\n");
		
		addChildren(node);
		
		stack.push("node" + count);
		count++;
	}

	@Override
	public void visit(PackedNode node) {
		visitChildren(node);
		sb.append("PackedNode node" + count + " = new PackedNode(" +
					"grammar.getGrammarSlot(" + node.getGrammarSlot().getId()  + "), " + 
					node.getPivot() + ");\n");
		
		addChildren(node);
		
		stack.push("node" + count);
		count++;
	}

	@Override
	public void visit(ListSymbolNode node) {
		visitChildren(node);
		sb.append("ListSymbolNode node" + count + " = new ListSymbolNode(" +
					"grammar.getGrammarSlot(" + node.getGrammarSlot().getId()  + "), " + 
					node.getLeftExtent() + ", " + 
					node.getRightExtent() + ");\n");
		
		addChildren(node);
		
		stack.push("node" + count);
		count++;
		
	}
	
	private void visitChildren(SPPFNode node) {
		for(SPPFNode child : node.getChildren()) {
			child.accept(this);
		}
	}
	
	private void addChildren(SPPFNode node) {
		for(@SuppressWarnings("unused") SPPFNode child : node.getChildren()) {
			sb.append("node" + count + ".addChild(" + stack.pop() + ");\n");
		}
	}
	
	public String getString() {
		return sb.toString();
	}

}
