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

package org.iguana.sppf.lookup;

import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.grammar.slot.NonterminalGrammarSlot;
import org.iguana.sppf.IntermediateNode;
import org.iguana.sppf.NonterminalNode;
import org.iguana.sppf.NonterminalOrIntermediateNode;
import org.iguana.sppf.PackedNode;
import org.iguana.sppf.TerminalNode;
import org.iguana.util.Input;
import org.iguana.util.logging.LoggerWrapper;


public abstract class AbstractSPPFLookup implements SPPFLookup {
	
	private static final LoggerWrapper log = LoggerWrapper.getLogger(AbstractSPPFLookup.class);
	
	private int countAmbiguousNodes;
	
	private int countNonterminalNodes;
	
	private int countIntermediateNodes;

	private int countPackedNodes;
	
	private int countTerminalNodes;

	private Input input;
	
	public AbstractSPPFLookup(Input input) {
		this.input = input;
	}
	
	@Override
	public void intermediateNodeAdded(IntermediateNode node) {
		log.trace("Intermediate node created: %s", node);
		countIntermediateNodes++;
	}
	
	@Override
	public void nonterminalNodeAdded(NonterminalNode node) {
		log.trace("Nonterminal node created: %s", node);
		countNonterminalNodes++;
	}
	
	@Override
	public void ambiguousNodeAdded(NonterminalOrIntermediateNode node) {
		log.trace("Ambiguous node added: %s", node);
		log.warning("Ambiguous node: %s %s", node, input.getNodeInfo(node));
//		System.out.println("Ambiguous node added: " + node.toString() + " " + input.getNodeInfo(node));
//		org.iguana.util.Visualization.generateSPPFGraph("/Users/aliafroozeh/output", node, input);
//		for (PackedNode packedNode : node.getChildren()) {
//			log.warning("   Packed node: " + packedNode.toString());
//			for (org.iguana.sppf.NonPackedNode child : packedNode.getChildren()) {
//				log.warning("       %s %s", child, input.getNodeInfo(child));
//			}
//		}
//		 System.exit(0);
		countAmbiguousNodes++;
	}
	
	@Override
	public void packedNodeAdded(PackedNode node) {
		countPackedNodes++;
	}
	
	@Override
	public void terminalNodeAdded(TerminalNode node) {
		log.trace("Terminal node created: %s", node);
		countTerminalNodes++;
	}
	
	public int getNonterminalNodesCount() {
		return countNonterminalNodes;
	}
	
	public int getIntermediateNodesCount() {
		return countIntermediateNodes;
	}
	
	public int getTerminalNodesCount() {
		return countTerminalNodes;
	}
	
	public int getPackedNodesCount() {
		return countPackedNodes;
	}
	
	public int getAmbiguousNodesCount() {
		return countAmbiguousNodes;
	}
	
	protected IntermediateNode createIntermediateNode(BodyGrammarSlot grammarSlot, int leftExtent, int rightExtent) {
		return new IntermediateNode(grammarSlot, leftExtent, rightExtent, (x, y) -> true);
	}
	
	protected NonterminalNode createNonterminalNode(NonterminalGrammarSlot grammarSlot, int leftExtent, int rightExtent) {
		return new NonterminalNode(grammarSlot, leftExtent, rightExtent, (x, y) -> true);
	}
	
	@Override
	public void reset() {
		countAmbiguousNodes = 0;
		countNonterminalNodes = 0;
		countIntermediateNodes = 0;
		countPackedNodes = 0;
		countTerminalNodes = 0;
	}
}
