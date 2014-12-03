package org.jgll.util.visualization;

import java.util.HashMap;
import java.util.Map;

import org.jgll.sppf.DummyNode;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TokenSymbolNode;

public abstract class ToDot {
	
	private Map<SPPFNode, Integer> idsMap = new HashMap<>();
	
	protected String getId(SPPFNode node) {
		
		idsMap.putIfAbsent(node, idsMap.size());
		
		if(node instanceof NonterminalNode) {
			return "nontermianl_" + getNodeId(node);
		}
		else if (node instanceof IntermediateNode) {
			return "intermediate_" + getNodeId(node);
		}
		else if(node instanceof PackedNode) {
			SPPFNode parent = ((PackedNode) node).getParent();
			if(parent instanceof NonterminalNode) {
				return "packed_n_" + getNodeId(parent) + "_" + getNodeId((PackedNode)node);				
			} else {
				return "packed_i_" + getNodeId(parent) + "_" + getNodeId((PackedNode)node);
			}
		}
		else if(node instanceof DummyNode) {
			return "-1";
		} 
		else if(node instanceof TokenSymbolNode) {
			return "token_" + getNodeId(node);
		}
		throw new RuntimeException("Node of type " +  node.getClass() + " could not be matched.");
	}
	
	protected String getNodeId(SPPFNode node) {
		return node.getGrammarSlot().toString() + "," + node.getLeftExtent() + "," + node.getRightExtent();
	}
	
	protected String getNodeId(PackedNode node) {
		return idsMap.get(node) + "," + node.getPivot();
	}
	
}
