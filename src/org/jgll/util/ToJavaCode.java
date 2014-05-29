package org.jgll.util;

import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.ListSymbolNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TokenSymbolNode;
import org.jgll.traversal.SPPFVisitor;

public class ToJavaCode implements SPPFVisitor {
	
	
	private int count = 1;
	private StringBuilder sb = new StringBuilder();
	private GrammarGraph grammar;

	public ToJavaCode(GrammarGraph grammar) {
		this.grammar = grammar;
		sb.append("SPPFNodeFactory factory = new SPPFNodeFactory(grammarGraph);\n");
	}
	
	public static String toJavaCode(NonterminalSymbolNode node, GrammarGraph grammar) {
		ToJavaCode toJavaCode = new ToJavaCode(grammar);
		toJavaCode.visit(node);
		return toJavaCode.toString();
	}
	
	@Override
	public void visit(TokenSymbolNode node) {
		if(!node.isVisited()) {
			node.setVisited(true);
			sb.append("TokenSymbolNode node" + count + " = factory.createTokenNode(" +
					  grammar.getRegularExpressionById(node.getTokenID()).getName() + ", " +
					  node.getLeftExtent() + ", " + node.getLength() + ");\n");
			node.setObject("node" + count++);
		}
	}

	@Override
	public void visit(NonterminalSymbolNode node) {
		if(!node.isVisited()) {
			node.setVisited(true);
			node.setObject("node" + count);
			
			sb.append("NonterminalSymbolNode node" + count + " = factory.createNonterminalNode(" +
					grammar.getNonterminalById(node.getId()).getName() + ", " +
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

			sb.append("IntermediateNode node" + count + " = factory.createIntermediateNode(" +
					  "list(" + CollectionsUtil.listToString(grammar.getIntermediateNodeLabel(node.getId()), ", ") + "), " + 
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
			
			
			if(node.getParent() instanceof NonterminalSymbolNode) {
				Nonterminal nonterminal = grammar.getNonterminalById(node.getParent().getId());
				sb.append("PackedNode node" + count + " = new PackedNode(" +
						  "grammarGraph.getPackedNodeId(" + nonterminal + ", " +
						  CollectionsUtil.listToString(grammar.getDefinition(nonterminal, node.getId()), ", ") + "), " + 
						  node.getPivot() + ", " + node.getParent().getObject() + ");\n");
				
			} else {
				sb.append("PackedNode node" + count + " = new PackedNode(" +
						  "grammarGraph.getIntermediateNodeId(" + 
						  "list(" + CollectionsUtil.listToString(grammar.getIntermediateNodeLabel(node.getParent().getId()), ", ") + ")), " + 
						  node.getPivot() + ", " + node.getParent().getObject() + ");\n");				
			}
			
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

			sb.append("ListSymbolNode node" + count + " = factory.createListNode(" +
					grammar.getNonterminalById(node.getId()).getName() + ", " +
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
