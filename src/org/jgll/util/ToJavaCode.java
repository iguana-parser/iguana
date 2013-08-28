package org.jgll.util;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.slot.HeadGrammarSlot;
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
	private Grammar grammar;

	public ToJavaCode(Grammar grammar) {
		this.grammar = grammar;
	}
	
	public static String toJavaCode(NonterminalSymbolNode node, Grammar grammar) {
		ToJavaCode toJavaCode = new ToJavaCode(grammar);
		toJavaCode.visit(node);
		return toJavaCode.toString();
	}
	
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
			
			if(grammar.isNewNonterminal((HeadGrammarSlot) node.getGrammarSlot())) {
				int index = grammar.getIndex((HeadGrammarSlot) node.getGrammarSlot());
				sb.append("NonterminalSymbolNode node" + count + " = new NonterminalSymbolNode(" +
						"grammar.getNonterminalByNameAndIndex(\"" + node.getGrammarSlot()  + "\", " + index + "), " + 
						node.getLeftExtent() + ", " + 
						node.getRightExtent() + ");\n");
			} else {
				sb.append("NonterminalSymbolNode node" + count + " = new NonterminalSymbolNode(" +
						"grammar.getNonterminalByName(\"" + node.getGrammarSlot()  + "\"), " + 
						node.getLeftExtent() + ", " + 
						node.getRightExtent() + ");\n");
				
			}
			
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
						"grammar.getGrammarSlotByName(\"" + node.getGrammarSlot()  + "\"), " + 
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
					  "grammar.getGrammarSlotByName(\"" + node.getGrammarSlot() + "\"), " + 
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
					  "grammar.getNonterminalByName(\"" + node.getGrammarSlot()  + "\"), " + 
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

	@Override
	public String toString() {
		return sb.toString();
	}

}
