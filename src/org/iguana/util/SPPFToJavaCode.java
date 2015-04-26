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

import static org.iguana.util.generator.GeneratorUtil.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.iguana.grammar.symbol.Epsilon;
import org.iguana.sppf.IntermediateNode;
import org.iguana.sppf.NonPackedNode;
import org.iguana.sppf.NonterminalNode;
import org.iguana.sppf.PackedNode;
import org.iguana.sppf.SPPFNode;
import org.iguana.sppf.TerminalNode;
import org.iguana.traversal.SPPFVisitor;

public class SPPFToJavaCode implements SPPFVisitor {
	
	private AtomicInteger id = new AtomicInteger(1);
	
	private Map<NonPackedNode, Integer> idsMap = new HashMap<>();
	
	private Map<NonPackedNode, Map<PackedNode, Integer>> packedNodeIds = new HashMap<>();
	
	private StringBuilder sb = new StringBuilder();

	public SPPFToJavaCode() {
		sb.append("SPPFNodeFactory factory = new SPPFNodeFactory(registry);\n");
	}
	
	public static String toJavaCode(NonterminalNode node) {
		SPPFToJavaCode toJavaCode = new SPPFToJavaCode();
		toJavaCode.visit(node);
		return toJavaCode.toString();
	}
	
	@Override
	public void visit(TerminalNode node) {
		
		if (idsMap.putIfAbsent(node, id.getAndIncrement()) != null)
			return;
		
		if (node.getGrammarSlot().getRegularExpression() == Epsilon.getInstance()) {
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
				"\"" + node.getGrammarSlot().getNonterminal().getName() + "\", " + 
				node.getGrammarSlot().getNonterminal().getIndex() + ", " +
				node.getLeftExtent() + ", " + 
				node.getRightExtent() + ");\n");
		
		visitChildren(node);
		
		addChildren(node);
	}

	@Override
	public void visit(IntermediateNode node) {
		
		if (idsMap.putIfAbsent(node, id.getAndIncrement()) != null) 
			return;
		
		sb.append("IntermediateNode node" + idsMap.get(node) + " = factory.createIntermediateNode(" +
				  "\"" + escape(node.getGrammarSlot().toString()) + "\", " + 
				  node.getLeftExtent() + ", " + 
				  node.getRightExtent() + ");\n");
		
		visitChildren(node);
		
		addChildren(node);
	}

	@Override
	public void visit(PackedNode node) {
		
		Map<PackedNode, Integer> map = packedNodeIds.computeIfAbsent(node.getParent(), k -> new HashMap<>() );
		map.put(node, id.getAndIncrement());
		
		sb.append("PackedNode node" + packedNodeIds.get(node.getParent()).get(node) + " = factory.createPackedNode(" +
				  "\"" + escape(node.getGrammarSlot().toString()) + "\", " + 
				  node.getPivot() + ", " + "node" + idsMap.get(node.getParent()) + ");\n");				
		
		visitChildren(node);
		
		addChildren(node);			
	}
	
	private void visitChildren(SPPFNode node) {
		for(SPPFNode child : node.getChildren()) {
			child.accept(this);
		}
	}
	
	private void addChildren(PackedNode node) {
		for(NonPackedNode child : node.getChildren()) {
			sb.append("node" + packedNodeIds.get(node.getParent()).get(node) + ".addChild(" + "node" + idsMap.get(child) + ");\n");
		}
	}
	
	private void addChildren(NonPackedNode node) {
		for(PackedNode child : node.getChildren()) {
			sb.append("node" + idsMap.get(node) + ".addChild(" + "node" + packedNodeIds.get(node).get(child)  + ");\n");
		}
	}
	
	@Override
	public String toString() {
		return sb.toString();
	}

}
