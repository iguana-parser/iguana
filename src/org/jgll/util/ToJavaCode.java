package org.jgll.util;

import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.ListSymbolNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.traversal.SPPFVisitor;

public class ToJavaCode implements SPPFVisitor {
	
	private int count = 1;
	private StringBuilder sb = new StringBuilder();

	@Override
	public void visit(TerminalSymbolNode node) {
		if(!node.isVisited()) {
			node.setVisited(true);
			sb.append("TerminalSymbolNode node" + count + " = new TerminalSymbolNode(" + node.getMatchedChar() + ", " + node.getLeftExtent() + ");\n");
			node.setObject("node" + count++);
		}
	}

	@Override
	public void visit(NonterminalSymbolNode node) {
		if(!node.isVisited()) {
			node.setVisited(true);
			node.setObject("node" + count);
			
			sb.append("NonterminalSymbolNode node" + count + " = new NonterminalSymbolNode(" +
					"grammar.getNonterminalByName(\"" + node.getGrammarSlot().getName()  + "\"), " + 
					node.getLeftExtent() + ", " + 
					node.getRightExtent() + ");\n");
			
			count++;
			
			visitChildren(node);
			
			addChildren(node);
		}
	}

	@Override
	public void visit(IntermediateNode node) {
		if(!node.isVisited()) {
			node.setVisited(true);
			node.setObject("node" + count);

			sb.append("IntermediateNode node" + count + " = new IntermediateNode(" +
						"grammar.getGrammarSlotByName(\"" + node.getGrammarSlot().toString()  + "\"), " + 
						node.getLeftExtent() + ", " + 
						node.getRightExtent() + ");\n");
			
			count++;
			
			visitChildren(node);
			
			addChildren(node);
		}
	}

	@Override
	public void visit(PackedNode node) {
		if(!node.isVisited()) {
			node.setVisited(true);
			node.setObject("node" + count);
			
			sb.append("PackedNode node" + count + " = new PackedNode(" +
					  "grammar.getGrammarSlotByName(\"" + node.getGrammarSlot().toString()  + "\"), " + 
					  node.getPivot() + ", " + node.getParent().getObject() + ");\n");
			
			count++;
			
			visitChildren(node);
			
			addChildren(node);			
		}
	}

	@Override
	public void visit(ListSymbolNode node) {
		if(!node.isVisited()) {
			node.setVisited(true);
			node.setObject("node" + count);

			sb.append("ListSymbolNode node" + count + " = new ListSymbolNode(" +
					  "grammar.getNonterminalByName(\"" + node.getGrammarSlot().getName()  + "\"), " + 
					  node.getLeftExtent() + ", " + 
					  node.getRightExtent() + ");\n");
			
			count++;
			
			visitChildren(node);
			
			addChildren(node);
		}
	}
	
	private void visitChildren(SPPFNode node) {
		for(SPPFNode child : node.getChildren()) {
			child.accept(this);
		}
	}
	
	private void addChildren(SPPFNode node) {
		for(SPPFNode child : node.getChildren()) {
			String childName = (String) child.getObject();
			assert childName != null;
			sb.append(node.getObject() + ".addChild(" + childName + ");\n");
		}
	}
	
	public String getString() {
		return sb.toString();
	}

}
